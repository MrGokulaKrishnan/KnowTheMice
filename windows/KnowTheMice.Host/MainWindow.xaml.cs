using System.Net;
using System.Net.Sockets;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Threading;
using KnowTheMice.Network;
using KnowTheMice.Security;
using Button = System.Windows.Controls.Button;
using Color = System.Windows.Media.Color;

namespace KnowTheMice.Host;

public partial class MainWindow : Window
{
    private readonly DiscoveryBeacon _beacon = new();
    private readonly NetworkServer _server = new();
    private readonly UpdateService _updateService = new();
    private PlatformUpdate? _availableUpdate;
    private DispatcherTimer? _pinCountdownTimer;
    private int _remainingSeconds = 0;
    private bool _isPaused = false;

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

    public void NavigateToTab(string tabName)
    {
        Dispatcher.Invoke(() =>
        {
            // Reset nav button styles
            NavDashboard.Style = (Style)FindResource("NavButton");
            NavDevices.Style = (Style)FindResource("NavButton");
            NavRemote.Style = (Style)FindResource("NavButton");
            NavKeyboard.Style = (Style)FindResource("NavButton");
            NavMedia.Style = (Style)FindResource("NavButton");
            NavShortcuts.Style = (Style)FindResource("NavButton");
            NavSettings.Style = (Style)FindResource("NavButton");

            // Hide all views
            ViewDashboard.Visibility = Visibility.Collapsed;
            ViewDevices.Visibility = Visibility.Collapsed;
            ViewRemote.Visibility = Visibility.Collapsed;
            ViewKeyboard.Visibility = Visibility.Collapsed;
            ViewMedia.Visibility = Visibility.Collapsed;
            ViewSettings.Visibility = Visibility.Collapsed;

            // Show selected view
            switch (tabName)
            {
                case "Dashboard":
                    ViewDashboard.Visibility = Visibility.Visible;
                    NavDashboard.Style = (Style)FindResource("ActiveNavButton");
                    break;
                case "Devices":
                    ViewDevices.Visibility = Visibility.Visible;
                    NavDevices.Style = (Style)FindResource("ActiveNavButton");
                    break;
                case "Remote":
                    ViewRemote.Visibility = Visibility.Visible;
                    NavRemote.Style = (Style)FindResource("ActiveNavButton");
                    break;
                case "Keyboard":
                    ViewKeyboard.Visibility = Visibility.Visible;
                    NavKeyboard.Style = (Style)FindResource("ActiveNavButton");
                    break;
                case "Media":
                    ViewMedia.Visibility = Visibility.Visible;
                    NavMedia.Style = (Style)FindResource("ActiveNavButton");
                    break;
                case "Shortcuts":
                    ViewMedia.Visibility = Visibility.Visible;
                    NavShortcuts.Style = (Style)FindResource("ActiveNavButton");
                    break;
                case "Settings":
                    ViewSettings.Visibility = Visibility.Visible;
                    NavSettings.Style = (Style)FindResource("ActiveNavButton");
                    break;
            }
        });
    }

    private void Nav_Click(object sender, RoutedEventArgs e)
    {
        if (sender is Button btn && btn.Tag is string tag)
        {
            NavigateToTab(tag);
        }
    }

    private void QuickAction_Click(object sender, RoutedEventArgs e)
    {
        if (sender is Button btn && btn.Tag is string tag)
        {
            NavigateToTab(tag);
        }
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
            TxtConnectedDevice.Text = name;
            TxtConnectionHeader.Text = $"CONNECTED TO {name.ToUpper()}";
            DotConnection.Fill = new SolidColorBrush(Color.FromRgb(0x22, 0xC5, 0x5E));
            TxtLatency.Text = "< 15 ms";
            TxtClientCountSidebar.Text = $"{_server.ActiveClientCount} Connected ({name})";
            TxtStatusLog.Text = $"● Connected: {name} ({DateTime.Now:HH:mm:ss})";
            PinDisplayBox.Visibility = Visibility.Collapsed;
            _pinCountdownTimer?.Stop();
            RefreshTrustedDevices();
            App.Instance.UpdateTrayStatus($"Connected: {name}", true, name);
        });
    }

    private void HandleClientDisconnected(string clientId)
    {
        Dispatcher.Invoke(() =>
        {
            TxtConnectedDevice.Text = "None (Waiting for phone)";
            TxtConnectionHeader.Text = "HOST READY FOR CONNECTIONS";
            DotConnection.Fill = new SolidColorBrush(Color.FromRgb(0x22, 0xC5, 0x5E));
            TxtLatency.Text = "-- ms";
            TxtClientCountSidebar.Text = $"{_server.ActiveClientCount} Devices Connected";
            TxtStatusLog.Text = $"● Device disconnected ({DateTime.Now:HH:mm:ss})";
            App.Instance.UpdateTrayStatus("Ready for connections", false);
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
        if (TxtSensitivityVal != null)
        {
            TxtSensitivityVal.Text = $"{e.NewValue:0.0}x";
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

    private void ChkPauseControl_Checked(object sender, RoutedEventArgs e)
    {
        SetRemoteControlPaused(true);
    }

    private void ChkPauseControl_Unchecked(object sender, RoutedEventArgs e)
    {
        SetRemoteControlPaused(false);
    }

    public void SetRemoteControlPaused(bool isPaused)
    {
        _isPaused = isPaused;
        if (ChkPauseControl != null && ChkPauseControl.IsChecked != isPaused)
        {
            ChkPauseControl.IsChecked = isPaused;
        }
        TxtStatusLog.Text = isPaused ? "● Remote control PAUSED" : "● Remote control ACTIVE";
    }

    private void BtnReleaseKeys_Click(object sender, RoutedEventArgs e)
    {
        _server.InputInjector.ReleaseAllKeys();
        TxtStatusLog.Text = "● All held keys and modifiers released.";
        System.Windows.MessageBox.Show("All held keys and modifiers have been released successfully.", "Keyboard Safety", MessageBoxButton.OK, MessageBoxImage.Information);
    }

    private void ChkDirectConnect_Checked(object sender, RoutedEventArgs e)
    {
        if (_server != null) _server.AllowDirectConnectOnLan = true;
    }

    private void ChkDirectConnect_Unchecked(object sender, RoutedEventArgs e)
    {
        if (_server != null) _server.AllowDirectConnectOnLan = false;
    }

    private void ChkNotifications_Checked(object sender, RoutedEventArgs e)
    {
        App.Instance.NotificationsEnabled = true;
    }

    private void ChkNotifications_Unchecked(object sender, RoutedEventArgs e)
    {
        App.Instance.NotificationsEnabled = false;
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
        if (ChkMinimizeToTray != null && ChkMinimizeToTray.IsChecked == true)
        {
            // Minimize to system tray instead of closing completely
            e.Cancel = true;
            Hide();
        }
    }
}
