using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using KnowTheMice.Core;

namespace KnowTheMice.Network;

public class DiscoveryBeacon : IDisposable
{
    public const int DISCOVERY_PORT = 52840;
    public const int CONTROL_PORT = 52841;

    private UdpClient? _udpBroadcaster;
    private UdpClient? _udpListener;
    private CancellationTokenSource? _cts;
    private bool _isRunning;
    private readonly string _instanceId = Guid.NewGuid().ToString();

    public void Start()
    {
        if (_isRunning) return;
        _isRunning = true;
        _cts = new CancellationTokenSource();

        try
        {
            _udpBroadcaster = new UdpClient();
            _udpBroadcaster.EnableBroadcast = true;

            _udpListener = new UdpClient(new IPEndPoint(IPAddress.Any, DISCOVERY_PORT));

            // Background broadcast task
            Task.Run(() => BroadcastLoopAsync(_cts.Token));
            // Background unicast probe listener
            Task.Run(() => ListenProbeLoopAsync(_cts.Token));
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine($"[Discovery] Error starting UDP beacon: {ex.Message}");
        }
    }

    private async Task BroadcastLoopAsync(CancellationToken token)
    {
        var targetEndpoint = new IPEndPoint(IPAddress.Broadcast, DISCOVERY_PORT);

        while (!token.IsCancellationRequested)
        {
            try
            {
                var beacon = new Core.DiscoveryBeacon
                {
                    Id = _instanceId,
                    Name = Environment.MachineName,
                    Port = CONTROL_PORT,
                    State = "READY"
                };

                string json = JsonSerializer.Serialize(beacon);
                byte[] bytes = Encoding.UTF8.GetBytes(json);

                if (_udpBroadcaster != null)
                {
                    await _udpBroadcaster.SendAsync(bytes, bytes.Length, targetEndpoint);
                }
            }
            catch { }

            try
            {
                await Task.Delay(2000, token);
            }
            catch (OperationCanceledException)
            {
                break;
            }
        }
    }

    private async Task ListenProbeLoopAsync(CancellationToken token)
    {
        while (!token.IsCancellationRequested && _udpListener != null)
        {
            try
            {
                var result = await _udpListener.ReceiveAsync(token);
                string text = Encoding.UTF8.GetString(result.Buffer);

                if (text.Contains("\"DISCOVER\""))
                {
                    // Respond directly to the requesting client endpoint
                    var beacon = new Core.DiscoveryBeacon
                    {
                        Id = _instanceId,
                        Name = Environment.MachineName,
                        Port = CONTROL_PORT,
                        State = "READY"
                    };

                    byte[] response = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(beacon));
                    await _udpListener.SendAsync(response, response.Length, result.RemoteEndPoint);
                }
            }
            catch { }
        }
    }

    public void Stop()
    {
        _isRunning = false;
        _cts?.Cancel();
        _udpBroadcaster?.Dispose();
        _udpListener?.Dispose();
    }

    public void Dispose()
    {
        Stop();
    }
}
