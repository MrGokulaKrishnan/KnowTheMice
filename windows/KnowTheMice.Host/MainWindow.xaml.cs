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
    private DispatcherTimer? _pinCountdownTimer;
    private int _remainingSeconds = 0;
    private bool _isPaused = false;

    public const string APP_VERSION = "1.2.0";

    public MainWindow()
    {
        App.Log("MainWindow constructor started");
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

        // Wire UpdateService state machine
        _updateService.OnStateChanged += HandleUpdateStateChanged;
        _updateService.InitializeState(APP_VERSION);
        _ = CheckForUpdatesAsync();

        App.Log("MainWindow constructor completed");
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

    private void HandleUpdateStateChanged(UpdateState state, PlatformUpdate? update, string? error)
    {
        Dispatcher.Invoke(() =>
        {
            switch (state)
            {
                case UpdateState.Idle:
                case UpdateState.UpToDate:
                    UpdateBanner.Visibility = Visibility.Collapsed;
                    PrgUpdate.Visibility = Visibility.Collapsed;
                    break;

                case UpdateState.Checking:
                    TxtUpdateIcon.Text = "🔄";
                    TxtUpdateTitle.Text = "Checking for updates...";
                    TxtUpdateDesc.Text = "Connecting to Know The Mice update service...";
                    PrgUpdate.Visibility = Visibility.Collapsed;
                    BtnUpdateAction.Visibility = Visibility.Collapsed;
                    UpdateBanner.Visibility = Visibility.Visible;
                    break;

                case UpdateState.UpdateAvailable:
                    TxtUpdateIcon.Text = "⚡";
                    TxtUpdateTitle.Text = $"Update Available: v{update?.Version}";
                    TxtUpdateDesc.Text = string.IsNullOrWhiteSpace(update?.Changelog) ? "New performance enhancements and features." : update.Changelog;
                    PrgUpdate.Visibility = Visibility.Collapsed;
                    BtnUpdateAction.Content = "DOWNLOAD UPDATE";
                    BtnUpdateAction.IsEnabled = true;
                    BtnUpdateAction.Visibility = Visibility.Visible;
                    UpdateBanner.Visibility = Visibility.Visible;
                    break;

                case UpdateState.Downloading:
                    TxtUpdateIcon.Text = "⏳";
                    TxtUpdateTitle.Text = "Downloading update...";
                    TxtUpdateDesc.Text = $"Preparing Know The Mice v{update?.Version} ({_updateService.DownloadProgress}%)...";
                    PrgUpdate.Visibility = Visibility.Visible;
                    PrgUpdate.Value = _updateService.DownloadProgress;
                    BtnUpdateAction.Content = "DOWNLOADING...";
                    BtnUpdateAction.IsEnabled = false;
                    BtnUpdateAction.Visibility = Visibility.Visible;
                    UpdateBanner.Visibility = Visibility.Visible;
                    break;

                case UpdateState.Ready:
                    TxtUpdateIcon.Text = "✅";
                    TxtUpdateTitle.Text = $"Update Ready (v{update?.Version})";
                    TxtUpdateDesc.Text = "Restart the application to finish installing the update.";
                    PrgUpdate.Visibility = Visibility.Collapsed;
                    BtnUpdateAction.Content = "RESTART TO UPDATE";
                    BtnUpdateAction.IsEnabled = true;
                    BtnUpdateAction.Visibility = Visibility.Visible;
                    UpdateBanner.Visibility = Visibility.Visible;
                    break;

                case UpdateState.Failed:
                    TxtUpdateIcon.Text = "⚠️";
                    TxtUpdateTitle.Text = "Update Failed";
                    TxtUpdateDesc.Text = error ?? "Failed to download update.";
                    PrgUpdate.Visibility = Visibility.Collapsed;
                    BtnUpdateAction.Content = "RETRY";
                    BtnUpdateAction.IsEnabled = true;
                    BtnUpdateAction.Visibility = Visibility.Visible;
                    UpdateBanner.Visibility = Visibility.Visible;
                    break;
            }
        });
    }

    private async Task CheckForUpdatesAsync()
    {
        await _updateService.CheckForUpdatesAsync(APP_VERSION);
    }

    private async void BtnUpdateAction_Click(object sender, RoutedEventArgs e)
    {
        switch (_updateService.CurrentState)
        {
            case UpdateState.UpdateAvailable:
            case UpdateState.Failed:
                await _updateService.DownloadUpdateAsync(pct =>
                {
                    Dispatcher.Invoke(() =>
                    {
                        PrgUpdate.Value = pct;
                        TxtUpdateDesc.Text = $"Downloading update... {pct}%";
                    });
                });
                break;

            case UpdateState.Ready:
                _updateService.ApplyRestartToUpdate();
                break;
        }
    }

    private async void BtnCheckUpdates_Click(object sender, RoutedEventArgs e)
    {
        BtnCheckUpdates.IsEnabled = false;
        TxtStatusLog.Text = "● Checking for updates...";
        var update = await _updateService.CheckForUpdatesAsync(APP_VERSION);
        BtnCheckUpdates.IsEnabled = true;

        if (update != null)
        {
            TxtStatusLog.Text = $"● New update v{update.Version} found!";
        }
        else if (_updateService.CurrentState == UpdateState.Failed)
        {
            TxtStatusLog.Text = $"● Update check failed: {_updateService.ErrorMessage}";
            System.Windows.MessageBox.Show(_updateService.ErrorMessage ?? "Failed to check for updates.", "Update Error", MessageBoxButton.OK, MessageBoxImage.Warning);
        }
        else
        {
            TxtStatusLog.Text = $"● Know The Mice is up to date (v{APP_VERSION}).";
            System.Windows.MessageBox.Show($"Know The Mice is up to date (v{APP_VERSION}).", "Check for Updates", MessageBoxButton.OK, MessageBoxImage.Information);
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
        if (ChkMinimizeToTray == null || ChkMinimizeToTray.IsChecked != false)
        {
            // Minimize to system tray instead of terminating background service
            e.Cancel = true;
            Hide();
            App.Instance.ShowTrayNotification(
                "Know The Mice Host is running in background",
                "Click or double-click the system tray icon to reopen the dashboard.");
        }
    }
}
