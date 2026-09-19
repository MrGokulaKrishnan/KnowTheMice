using System.Windows;
using Forms = System.Windows.Forms;
using Drawing = System.Drawing;

namespace KnowTheMice.Host;

public partial class App : System.Windows.Application
{
    private static Mutex? _singleInstanceMutex;
    private Forms.NotifyIcon? _trayIcon;
    private Forms.ToolStripMenuItem? _statusMenuItem;
    private Forms.ToolStripMenuItem? _pauseMenuItem;

    public static App Instance => (App)Current;
    public bool NotificationsEnabled { get; set; } = true;
    public bool IsRemoteControlPaused { get; set; } = false;

    protected override void OnStartup(StartupEventArgs e)
    {
        const string mutexName = "KnowTheMice_SingleInstance_Mutex";
        _singleInstanceMutex = new Mutex(true, mutexName, out bool createdNew);

        if (!createdNew)
        {
            Forms.MessageBox.Show("Know The Mice Host is already running in your system tray.", "Know The Mice", Forms.MessageBoxButtons.OK, Forms.MessageBoxIcon.Information);
            Current.Shutdown();
            return;
        }

        base.OnStartup(e);
        InitializeTrayIcon();
    }

    private void InitializeTrayIcon()
    {
        _trayIcon = new Forms.NotifyIcon();
        try
        {
            string iconPath = System.IO.Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "icon.ico");
            if (System.IO.File.Exists(iconPath))
            {
                _trayIcon.Icon = new Drawing.Icon(iconPath);
            }
            else
            {
                _trayIcon.Icon = Drawing.SystemIcons.Application;
            }
        }
        catch
        {
            _trayIcon.Icon = Drawing.SystemIcons.Application;
        }

        _trayIcon.Text = "Know The Mice Host — Ready (Port 52841)";
        _trayIcon.Visible = true;

        var contextMenu = new Forms.ContextMenuStrip();

        // Brand Title
        var titleItem = new Forms.ToolStripMenuItem("KNOW THE MICE") { Enabled = false };
        titleItem.Font = new Drawing.Font(titleItem.Font, Drawing.FontStyle.Bold);
        contextMenu.Items.Add(titleItem);

        // Status Item
        _statusMenuItem = new Forms.ToolStripMenuItem("● Ready (0 Devices Connected)") { Enabled = false };
        contextMenu.Items.Add(_statusMenuItem);
        contextMenu.Items.Add(new Forms.ToolStripSeparator());

        // Open Dashboard
        var openItem = new Forms.ToolStripMenuItem("Open Dashboard", null, (s, e) => ShowMainWindow());
        openItem.Font = new Drawing.Font(openItem.Font, Drawing.FontStyle.Bold);
        contextMenu.Items.Add(openItem);

        // Remote Status
        contextMenu.Items.Add("Remote Status", null, (s, e) => {
            ShowMainWindow();
            (MainWindow as MainWindow)?.NavigateToTab("Dashboard");
        });

        // Connected Devices
        contextMenu.Items.Add("Connected Devices", null, (s, e) => {
            ShowMainWindow();
            (MainWindow as MainWindow)?.NavigateToTab("Devices");
        });

        // Pause / Resume Remote Control
        _pauseMenuItem = new Forms.ToolStripMenuItem("Pause Remote Control", null, (s, e) => {
            TogglePauseRemoteControl();
        });
        contextMenu.Items.Add(_pauseMenuItem);

        // Settings
        contextMenu.Items.Add("Settings", null, (s, e) => {
            ShowMainWindow();
            (MainWindow as MainWindow)?.NavigateToTab("Settings");
        });

        // About
        contextMenu.Items.Add("About Know The Mice", null, (s, e) => {
            Forms.MessageBox.Show(
                "Know The Mice — Local Wireless Remote Control Host\nVersion 1.1.0\n\nYour phone. Your PC. Connected.\nhttps://knowthemice.web.app",
                "About Know The Mice",
                Forms.MessageBoxButtons.OK,
                Forms.MessageBoxIcon.Information);
        });

        contextMenu.Items.Add(new Forms.ToolStripSeparator());

        // Exit
        var exitItem = new Forms.ToolStripMenuItem("Exit Know The Mice", null, (s, e) => ExitApplication());
        contextMenu.Items.Add(exitItem);

        _trayIcon.ContextMenuStrip = contextMenu;
        _trayIcon.DoubleClick += (s, e) => ShowMainWindow();
    }

    public void UpdateTrayStatus(string text, bool isConnected, string? clientName = null)
    {
        if (_trayIcon == null) return;

        string displayStatus = isConnected
            ? $"● Connected: {clientName ?? "Mobile Device"}"
            : "● Ready (0 Devices Connected)";

        if (_statusMenuItem != null)
        {
            _statusMenuItem.Text = displayStatus;
        }

        string tooltip = $"Know The Mice — {text}";
        if (tooltip.Length > 63) tooltip = tooltip.Substring(0, 60) + "...";
        _trayIcon.Text = tooltip;

        if (NotificationsEnabled)
        {
            if (isConnected && !string.IsNullOrEmpty(clientName))
            {
                _trayIcon.ShowBalloonTip(3000, "Know The Mice", $"Connected to {clientName}", Forms.ToolTipIcon.Info);
            }
            else if (!isConnected)
            {
                _trayIcon.ShowBalloonTip(3000, "Know The Mice", "Device disconnected", Forms.ToolTipIcon.Info);
            }
        }
    }

    public void TogglePauseRemoteControl()
    {
        IsRemoteControlPaused = !IsRemoteControlPaused;
        if (_pauseMenuItem != null)
        {
            _pauseMenuItem.Text = IsRemoteControlPaused ? "Resume Remote Control" : "Pause Remote Control";
        }
        (MainWindow as MainWindow)?.SetRemoteControlPaused(IsRemoteControlPaused);
    }

    public void ShowMainWindow()
    {
        if (MainWindow != null)
        {
            MainWindow.Show();
            MainWindow.WindowState = WindowState.Normal;
            MainWindow.Activate();
        }
    }

    public void ExitApplication()
    {
        _trayIcon?.Dispose();
        _singleInstanceMutex?.ReleaseMutex();
        Current.Shutdown();
    }

    protected override void OnExit(ExitEventArgs e)
    {
        _trayIcon?.Dispose();
        _singleInstanceMutex?.ReleaseMutex();
        base.OnExit(e);
    }
}
