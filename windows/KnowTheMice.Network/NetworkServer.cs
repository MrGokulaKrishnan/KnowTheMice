using System.Buffers.Binary;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using KnowTheMice.Core;
using KnowTheMice.Input;
using KnowTheMice.Security;
using KnowTheMice.SystemControllers;

namespace KnowTheMice.Network;

public class ClientSession
{
    public TcpClient Client { get; }
    public NetworkStream Stream { get; }
    public string ClientId { get; set; } = string.Empty;
    public string ClientName { get; set; } = string.Empty;
    public bool IsAuthenticated { get; set; }
    public ReplayProtection Replay { get; } = new();
    public DateTime ConnectedAtUtc { get; } = DateTime.UtcNow;

    public ClientSession(TcpClient client)
    {
        Client = client;
        Stream = client.GetStream();
    }
}

public class NetworkServer : IDisposable
{
    public const int DEFAULT_PORT = 52841;

    private TcpListener? _listener;
    private CancellationTokenSource? _cts;
    private bool _isRunning;

    private readonly InputInjector _inputInjector = new();
    private readonly PinManager _pinManager = new();
    private readonly TrustedDeviceStore _deviceStore = new();
    private readonly List<ClientSession> _activeSessions = new();
    private readonly object _sessionLock = new();

    public event Action<string, string>? OnClientConnected;
    public event Action<string>? OnClientDisconnected;
    public event Action<string, string>? OnPairingRequested; // ClientName, PIN
    public event Action<string>? OnStatusChanged;

    public PinManager PinManager => _pinManager;
    public TrustedDeviceStore DeviceStore => _deviceStore;
    public InputInjector InputInjector => _inputInjector;
    public bool AllowDirectConnectOnLan { get; set; } = true;

    public int ActiveClientCount
    {
        get
        {
            lock (_sessionLock) return _activeSessions.Count(s => s.IsAuthenticated);
        }
    }

    public void Start(int port = DEFAULT_PORT)
    {
        if (_isRunning) return;
        _isRunning = true;
        _cts = new CancellationTokenSource();

        try
        {
            _listener = new TcpListener(IPAddress.Any, port);
            _listener.Server.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress, true);
            _listener.Start(10);
            OnStatusChanged?.Invoke($"Listening on port {port}");

            Task.Run(() => AcceptLoopAsync(_cts.Token));
        }
        catch (Exception ex)
        {
            OnStatusChanged?.Invoke($"Error starting server: {ex.Message}");
        }
    }

    private async Task AcceptLoopAsync(CancellationToken token)
    {
        while (!token.IsCancellationRequested && _listener != null)
        {
            try
            {
                var tcpClient = await _listener.AcceptTcpClientAsync(token);
                tcpClient.NoDelay = true; // Disable Nagle's algorithm for sub-15ms input latency!
                var session = new ClientSession(tcpClient);

                lock (_sessionLock)
                {
                    _activeSessions.Add(session);
                }

                _ = Task.Run(() => HandleClientAsync(session, token));
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"[Server] Accept error: {ex.Message}");
            }
        }
    }

    private async Task HandleClientAsync(ClientSession session, CancellationToken token)
    {
        byte[] buffer = new byte[8192];

        try
        {
            while (!token.IsCancellationRequested && session.Client.Connected)
            {
                // Read frame length header (4 bytes, Little Endian)
                byte[] lengthHeader = new byte[4];
                int bytesRead = await ReadExactAsync(session.Stream, lengthHeader, 4, token);
                if (bytesRead < 4) break;

                int packetLength = BinaryPrimitives.ReadInt32LittleEndian(lengthHeader);
                if (packetLength <= 0 || packetLength > 65536) break; // Protection against oversized frames

                byte[] packetBody = new byte[packetLength];
                bytesRead = await ReadExactAsync(session.Stream, packetBody, packetLength, token);
                if (bytesRead < packetLength) break;

                // Check if packet is high-frequency Binary Motion Frame (Magic 0x01)
                if (packetBody[0] == BinaryMotionFrame.MAGIC_BYTE && packetLength == BinaryMotionFrame.FRAME_SIZE)
                {
                    if (session.IsAuthenticated && BinaryMotionFrame.TryParse(packetBody, out var motion))
                    {
                        if (session.Replay.IsValidAndAdvance(motion.Sequence))
                        {
                            _inputInjector.MoveMouseRelative(motion.Dx, motion.Dy);
                        }
                    }
                    continue;
                }

                // Otherwise, parse as JSON Control Envelope
                string json = Encoding.UTF8.GetString(packetBody);
                await ProcessJsonMessageAsync(session, json);
            }
        }
        catch { }
        finally
        {
            lock (_sessionLock)
            {
                _activeSessions.Remove(session);
            }
            session.Client.Dispose();
            OnClientDisconnected?.Invoke(session.ClientId);
        }
    }

    private async Task ProcessJsonMessageAsync(ClientSession session, string json)
    {
        try
        {
            using var doc = JsonDocument.Parse(json);
            var root = doc.RootElement;
            if (!root.TryGetProperty("type", out var typeProp)) return;
            string type = typeProp.GetString() ?? "";

            switch (type)
            {
                case "PAIR_REQUEST":
                    var pairReq = JsonSerializer.Deserialize<PairRequestMessage>(json);
                    if (pairReq != null)
                    {
                        session.ClientId = pairReq.ClientId;
                        session.ClientName = pairReq.ClientName;

                        if (AllowDirectConnectOnLan)
                        {
                            string authToken = Guid.NewGuid().ToString("N");
                            _deviceStore.SaveDevice(session.ClientId, session.ClientName, authToken);
                            session.IsAuthenticated = true;
                            OnClientConnected?.Invoke(session.ClientId, session.ClientName);

                            var resp = new PairResponseMessage
                            {
                                Success = true,
                                AuthToken = authToken,
                                Message = "Direct connection approved."
                            };
                            await SendJsonAsync(session.Stream, resp);
                        }
                        else
                        {
                            string pin = _pinManager.GenerateNewPin();
                            OnPairingRequested?.Invoke(pairReq.ClientName, pin);

                            var resp = new PairResponseMessage
                            {
                                Success = false,
                                Message = "PIN_REQUIRED"
                            };
                            await SendJsonAsync(session.Stream, resp);
                        }
                    }
                    break;

                case "PAIR_VERIFY":
                    var pairVerify = JsonSerializer.Deserialize<PairVerifyMessage>(json);
                    if (pairVerify != null)
                    {
                        bool validPin = _pinManager.ValidatePin(pairVerify.Pin);
                        if (validPin)
                        {
                            string authToken = Guid.NewGuid().ToString("N");
                            _deviceStore.SaveDevice(session.ClientId, session.ClientName, authToken);
                            session.IsAuthenticated = true;
                            OnClientConnected?.Invoke(session.ClientId, session.ClientName);

                            var resp = new PairResponseMessage
                            {
                                Success = true,
                                AuthToken = authToken,
                                Message = "Pairing successful and device trusted."
                            };
                            await SendJsonAsync(session.Stream, resp);
                        }
                        else
                        {
                            var resp = new PairResponseMessage
                            {
                                Success = false,
                                Message = "Invalid or expired PIN code."
                            };
                            await SendJsonAsync(session.Stream, resp);
                        }
                    }
                    break;

                case "AUTH":
                    var auth = JsonSerializer.Deserialize<AuthMessage>(json);
                    if (auth != null)
                    {
                        if (_deviceStore.ValidateDevice(auth.ClientId, auth.AuthToken))
                        {
                            session.ClientId = auth.ClientId;
                            session.IsAuthenticated = true;
                            OnClientConnected?.Invoke(auth.ClientId, "Trusted Mobile Device");
                            await SendJsonAsync(session.Stream, new BaseMessage { Type = "AUTH_SUCCESS" });
                        }
                        else
                        {
                            await SendJsonAsync(session.Stream, new BaseMessage { Type = "AUTH_FAILED" });
                            session.Client.Close();
                        }
                    }
                    break;

                case "MOUSE_CLICK":
                    if (session.IsAuthenticated)
                    {
                        var click = JsonSerializer.Deserialize<MouseClickMessage>(json);
                        if (click != null)
                        {
                            Enum.TryParse<MouseButton>(click.Button, true, out var btn);
                            Enum.TryParse<MouseAction>(click.Action, true, out var act);
                            _inputInjector.MouseClick(btn, act);
                        }
                    }
                    break;

                case "MOUSE_SCROLL":
                    if (session.IsAuthenticated)
                    {
                        var scroll = JsonSerializer.Deserialize<MouseScrollMessage>(json);
                        if (scroll != null)
                        {
                            _inputInjector.Scroll(scroll.Dx, scroll.Dy);
                        }
                    }
                    break;

                case "KEY_INPUT":
                    if (session.IsAuthenticated)
                    {
                        var keyMsg = JsonSerializer.Deserialize<KeyInputMessage>(json);
                        if (keyMsg != null)
                        {
                            if (!string.IsNullOrEmpty(keyMsg.Key) && keyMsg.Key.Length == 1)
                            {
                                _inputInjector.TypeUnicodeChar(keyMsg.Key[0]);
                            }
                            else if (keyMsg.Code > 0)
                            {
                                Enum.TryParse<KeyAction>(keyMsg.Action, true, out var act);
                                _inputInjector.KeyStroke((ushort)keyMsg.Code, act);
                            }
                        }
                    }
                    break;

                case "MEDIA_CMD":
                    if (session.IsAuthenticated)
                    {
                        var media = JsonSerializer.Deserialize<MediaMessage>(json);
                        if (media != null && Enum.TryParse<MediaAction>(media.Action, true, out var act))
                        {
                            _inputInjector.MediaControl(act);
                        }
                    }
                    break;

                case "PRESENTATION_CMD":
                    if (session.IsAuthenticated)
                    {
                        var pres = JsonSerializer.Deserialize<PresentationMessage>(json);
                        if (pres != null && Enum.TryParse<PresentationAction>(pres.Action, true, out var act))
                        {
                            _inputInjector.PresentationControl(act);
                        }
                    }
                    break;

                case "POWER_CMD":
                    if (session.IsAuthenticated)
                    {
                        var pwr = JsonSerializer.Deserialize<PowerMessage>(json);
                        if (pwr != null && Enum.TryParse<PowerAction>(pwr.Action, true, out var act))
                        {
                            PowerController.ExecutePowerAction(act, pwr.Confirmed, pwr.TargetPc);
                        }
                    }
                    break;

                case "APP_LAUNCH":
                    if (session.IsAuthenticated)
                    {
                        var app = JsonSerializer.Deserialize<AppLaunchMessage>(json);
                        if (app != null)
                        {
                            AppLauncher.LaunchApp(app.AppId);
                        }
                    }
                    break;

                case "PING":
                    await SendJsonAsync(session.Stream, new PongMessage());
                    break;
            }
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine($"[Server] Error processing message: {ex.Message}");
        }
    }

    private static async Task<int> ReadExactAsync(NetworkStream stream, byte[] buffer, int count, CancellationToken token)
    {
        int totalRead = 0;
        while (totalRead < count)
        {
            int read = await stream.ReadAsync(buffer.AsMemory(totalRead, count - totalRead), token);
            if (read == 0) return totalRead;
            totalRead += read;
        }
        return totalRead;
    }

    public static async Task SendJsonAsync<T>(NetworkStream stream, T message)
    {
        byte[] jsonBytes = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(message));
        byte[] header = new byte[4];
        BinaryPrimitives.WriteInt32LittleEndian(header, jsonBytes.Length);

        await stream.WriteAsync(header);
        await stream.WriteAsync(jsonBytes);
        await stream.FlushAsync();
    }

    public void Stop()
    {
        _isRunning = false;
        _cts?.Cancel();
        _listener?.Stop();
        lock (_sessionLock)
        {
            foreach (var s in _activeSessions)
            {
                s.Client.Dispose();
            }
            _activeSessions.Clear();
        }
    }

    public void Dispose()
    {
        Stop();
    }
}
