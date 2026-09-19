using System.Net;
using System.Net.Sockets;
using System.Windows;
using System.Windows.Threading;
using KnowTheMice.Network;
using KnowTheMice.Security;

namespace KnowTheMice.Host;

public partial class MainWindow : Window
{
    private readonly DiscoveryBeacon _beacon = new();
    private readonly NetworkServer _server = new();
    private readonly UpdateService _updateService = new();
    private PlatformUpdate? _availableUpdate;
    private DispatcherTimer? _pinCountdownTimer;
    private int _remainingSeconds = 0;

    public MainWindow()
    {
        InitializeComponent();

        TxtPcName.Text = Environment.MachineName;
        TxtIpAddress.Text = GetLocalIpAddress();

        // Server event wiring
        _server.OnClientConnected += HandleClientConnected;
        _server.OnClientDisconnected += HandleClientDisconnected;
        _server.OnPairingRequested += HandlePairingRequested;
        _server.OnStatusChanged += HandleStatusChanged;

        // Default to direct connect for zero-friction local Wi-Fi control
        _server.AllowDirectConnectOnLan = true;

        // Start services
        _server.Start();
        _beacon.Start();

        RefreshTrustedDevices();
        _ = CheckForUpdatesAsync();
    }

    private string GetLocalIpAddress()
    {
        try
        {
            using var socket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, 0);
            socket.Connect("8.8.8.8", 65530);
            if (socket.LocalEndPoint is IPEndPoint endPoint)
            {
                return endPoint.Address.ToString();
            }
        }
        catch
        {
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork && !IPAddress.IsLoopback(ip))
                {
                    return ip.ToString();
                }
            }
        }
        return "127.0.0.1";
    }

    private void HandleClientConnected(string clientId, string name)
    {
        Dispatcher.Invoke(() =>
        {
            TxtClientCount.Text = $"{_server.ActiveClientCount} Connected ({name})";
            TxtStatusLog.Text = $"● Connected: {name} ({DateTime.Now:HH:mm:ss})";
            PinDisplayBox.Visibility = Visibility.Collapsed;
            _pinCountdownTimer?.Stop();
            RefreshTrustedDevices();
        });
    }

    private void HandleClientDisconnected(string clientId)
    {
        Dispatcher.Invoke(() =>
        {
            TxtClientCount.Text = $"{_server.ActiveClientCount} Connected";
            TxtStatusLog.Text = $"● Client disconnected ({DateTime.Now:HH:mm:ss})";
        });
    }

    private void HandlePairingRequested(string clientName, string pin)
    {
        Dispatcher.Invoke(() =>
        {
            TxtPinCode.Text = string.Join(" ", pin.ToCharArray());
            PinDisplayBox.Visibility = Visibility.Visible;
            _remainingSeconds = 60;
            StartPinCountdown();
        });
    }

    private void HandleStatusChanged(string status)
    {
        Dispatcher.Invoke(() =>
        {
            TxtStatusLog.Text = $"● {status}";
        });
    }

    private void BtnPair_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            string pin = _server.PinManager.GenerateNewPin();
            TxtPinCode.Text = string.Join(" ", pin.ToCharArray());
            PinDisplayBox.Visibility = Visibility.Visible;
            _remainingSeconds = 60;
            StartPinCountdown();
        }
        catch (Exception ex)
        {
            System.Windows.MessageBox.Show(ex.Message, "Pairing Notice", MessageBoxButton.OK, MessageBoxImage.Warning);
        }
    }

    private void StartPinCountdown()
    {
        _pinCountdownTimer?.Stop();
        _pinCountdownTimer = new DispatcherTimer
        {
            Interval = TimeSpan.FromSeconds(1)
        };
        _pinCountdownTimer.Tick += (s, e) =>
        {
            _remainingSeconds--;
            if (_remainingSeconds <= 0)
            {
                _pinCountdownTimer.Stop();
                PinDisplayBox.Visibility = Visibility.Collapsed;
                TxtStatusLog.Text = "● Pairing code expired.";
            }
        };
        _pinCountdownTimer.Start();
    }

    private void SliderSensitivity_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
    {
        if (_server != null)
        {
            _server.InputInjector.Sensitivity = (float)e.NewValue;
        }
    }

    private void ChkNaturalScroll_Checked(object sender, RoutedEventArgs e)
    {
        if (_server != null) _server.InputInjector.NaturalScrolling = true;
    }

    private void ChkNaturalScroll_Unchecked(object sender, RoutedEventArgs e)
    {
        if (_server != null) _server.InputInjector.NaturalScrolling = false;
    }

    private void ChkDirectConnect_Checked(object sender, RoutedEventArgs e)
    {
        if (_server != null) _server.AllowDirectConnectOnLan = true;
    }

    private void ChkDirectConnect_Unchecked(object sender, RoutedEventArgs e)
    {
        if (_server != null) _server.AllowDirectConnectOnLan = false;
    }

    private async Task CheckForUpdatesAsync()
    {
        var update = await _updateService.CheckForUpdatesAsync("1.1.0");
        if (update != null)
        {
            Dispatcher.Invoke(() =>
            {
                _availableUpdate = update;
                TxtUpdateTitle.Text = $"Update Available: v{update.Version}";
                TxtUpdateDesc.Text = string.IsNullOrWhiteSpace(update.Changelog) ? "New performance enhancements and features." : update.Changelog;
                UpdateBanner.Visibility = Visibility.Visible;
            });
        }
    }

    private async void BtnRestartUpdate_Click(object sender, RoutedEventArgs e)
    {
        if (_availableUpdate == null) return;
        BtnRestartUpdate.IsEnabled = false;
        BtnRestartUpdate.Content = "DOWNLOADING...";
        PrgUpdate.Visibility = Visibility.Visible;
        PrgUpdate.Value = 0;

        try
        {
            await _updateService.DownloadAndRestartAsync(_availableUpdate.DownloadUrl, pct =>
            {
                Dispatcher.Invoke(() => PrgUpdate.Value = pct);
            });
        }
        catch (Exception ex)
        {
            System.Windows.MessageBox.Show($"Failed to download update: {ex.Message}", "Update Error", MessageBoxButton.OK, MessageBoxImage.Error);
            BtnRestartUpdate.IsEnabled = true;
            BtnRestartUpdate.Content = "RESTART TO UPDATE";
            PrgUpdate.Visibility = Visibility.Collapsed;
        }
    }

    private async void BtnCheckUpdates_Click(object sender, RoutedEventArgs e)
    {
        BtnCheckUpdates.IsEnabled = false;
        TxtStatusLog.Text = "● Checking for updates...";
        var update = await _updateService.CheckForUpdatesAsync("1.1.0");
        BtnCheckUpdates.IsEnabled = true;

        if (update != null)
        {
            _availableUpdate = update;
            TxtUpdateTitle.Text = $"Update Available: v{update.Version}";
            TxtUpdateDesc.Text = update.Changelog;
            UpdateBanner.Visibility = Visibility.Visible;
            TxtStatusLog.Text = $"● New update v{update.Version} found!";
        }
        else
        {
            TxtStatusLog.Text = "● Know The Mice is up to date (v1.1.0).";
            System.Windows.MessageBox.Show("Know The Mice is up to date (v1.1.0).", "Check for Updates", MessageBoxButton.OK, MessageBoxImage.Information);
        }
    }

    private void RefreshTrustedDevices()
    {
        ListTrustedDevices.ItemsSource = null;
        ListTrustedDevices.ItemsSource = _server.DeviceStore.GetAllDevices();
    }

    private void BtnRevoke_Click(object sender, RoutedEventArgs e)
    {
        if (ListTrustedDevices.SelectedItem is TrustedDevice dev)
        {
            var result = System.Windows.MessageBox.Show(
                $"Revoke pairing trust for '{dev.FriendlyName}'? This device will need to be re-paired.",
                "Confirm Revoke",
                MessageBoxButton.YesNo,
                MessageBoxImage.Question);

            if (result == MessageBoxResult.Yes)
            {
                _server.DeviceStore.RevokeDevice(dev.ClientId);
                RefreshTrustedDevices();
            }
        }
        else
        {
            System.Windows.MessageBox.Show("Please select a device to revoke.", "Revoke", MessageBoxButton.OK, MessageBoxImage.Information);
        }
    }

    protected override void OnClosing(System.ComponentModel.CancelEventArgs e)
    {
        // Minimize to system tray instead of closing completely
        e.Cancel = true;
        Hide();
    }
}
