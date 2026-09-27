using System;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Windows;
using Forms = System.Windows.Forms;
using Drawing = System.Drawing;

namespace KnowTheMice.Host;

public partial class App : System.Windows.Application
{
    private static Mutex? _singleInstanceMutex;
    private static bool _ownsMutex = false;
    private Forms.NotifyIcon? _trayIcon;
    private Forms.ToolStripMenuItem? _statusMenuItem;
    private Forms.ToolStripMenuItem? _pauseMenuItem;

    public static App Instance => (App)Current;
    public bool NotificationsEnabled { get; set; } = true;
    public bool IsRemoteControlPaused { get; set; } = false;

    public static void Log(string msg)
    {
        try
        {
            string path = Path.Combine(Path.GetTempPath(), "ktm_host.log");
            File.AppendAllText(path, $"[{DateTime.Now:HH:mm:ss.fff}] {msg}\n");
        }
        catch { }
    }

    protected override void OnStartup(StartupEventArgs e)
    {
        Log("OnStartup started");
        AppDomain.CurrentDomain.UnhandledException += (s, args) => Log($"UnhandledException: {args.ExceptionObject}");
        DispatcherUnhandledException += (s, args) => Log($"DispatcherUnhandledException: {args.Exception}");
        TaskScheduler.UnobservedTaskException += (s, args) => Log($"UnobservedTaskException: {args.Exception}");

        const string mutexName = "KnowTheMice_SingleInstance_Mutex";
        try
        {
            _singleInstanceMutex = new Mutex(true, mutexName, out bool createdNew);
            _ownsMutex = createdNew;
            Log($"Mutex createdNew={createdNew}");

            if (!_ownsMutex)
            {
                Log("Another instance is running. Shutting down.");
                Forms.MessageBox.Show(
                    "Know The Mice Host is already running in your system tray.\nLook for the orange KM logo near your taskbar clock.",
                    "Know The Mice",
                    Forms.MessageBoxButtons.OK,
                    Forms.MessageBoxIcon.Information);
                Current.Shutdown();
                return;
            }
        }
        catch (Exception ex)
        {
            Log($"Mutex error: {ex.Message}");
        }

        base.OnStartup(e);
        Log("base.OnStartup completed. Initializing tray icon...");
        InitializeTrayIcon();
        Log("InitializeTrayIcon completed.");
        // Create and show main window with validated bounds
        var mainWindow = new MainWindow();
        this.MainWindow = mainWindow;
        EnsureWindowWithinWorkArea(mainWindow);
        mainWindow.Show();
    }

    private Drawing.Icon LoadTrayIcon()
    {
        Log("LoadTrayIcon: entering");
        var targetSize = Forms.SystemInformation.SmallIconSize;
        string baseDir = AppDomain.CurrentDomain.BaseDirectory;
        Log($"LoadTrayIcon: targetSize={targetSize}, baseDir={baseDir}");

        // 1. Try disk tray.ico (high-contrast squircle badge with exact DPI size)
        string trayPath = Path.Combine(baseDir, "tray.ico");
        if (File.Exists(trayPath))
        {
            try
            {
                Log($"LoadTrayIcon: loading disk {trayPath}");
                var ico = new Drawing.Icon(trayPath, targetSize);
                Log($"LoadTrayIcon: loaded disk icon successfully (size={ico.Size})");
                return ico;
            }
            catch (Exception ex)
            {
                Log($"LoadTrayIcon disk error: {ex.Message}");
            }
        }

        // 2. Try embedded resource tray.ico
        try
        {
            var asm = Assembly.GetExecutingAssembly();
            using var stream = asm.GetManifestResourceStream("KnowTheMice.Host.tray.ico");
            if (stream != null)
            {
                Log("LoadTrayIcon: loading from embedded stream");
                return new Drawing.Icon(stream, targetSize);
            }
        }
        catch (Exception ex)
        {
            Log($"LoadTrayIcon embedded error: {ex.Message}");
        }

        // 3. Try disk icon.ico
        string iconPath = Path.Combine(baseDir, "icon.ico");
        if (File.Exists(iconPath))
        {
            try { return new Drawing.Icon(iconPath, targetSize); } catch { }
        }

        return Drawing.SystemIcons.Application;
    }

    private void InitializeTrayIcon()
    {
        try
        {
            Log("InitializeTrayIcon: creating NotifyIcon instance");
            _trayIcon = new Forms.NotifyIcon();
            _trayIcon.Icon = LoadTrayIcon();
            Log("InitializeTrayIcon: icon set successfully");

            _trayIcon.Text = "Know The Mice Host — Ready";
            _trayIcon.Visible = true;
            Log("InitializeTrayIcon: Visible set to true");
        }
        catch (Exception ex)
        {
            Log($"InitializeTrayIcon failed: {ex}");
            if (_trayIcon != null)
            {
                _trayIcon.Icon = Drawing.SystemIcons.Application;
                _trayIcon.Visible = true;
            }
        }

        var contextMenu = new Forms.ContextMenuStrip();

        // Brand Title Header
        var titleItem = new Forms.ToolStripMenuItem("KNOW THE MICE (v1.1.0)") { Enabled = false };
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

        // Check for Updates
        contextMenu.Items.Add("Check for Updates", null, (s, e) => {
            ShowMainWindow();
            (MainWindow as MainWindow)?.NavigateToTab("Settings");
        });

        // About
        contextMenu.Items.Add("About Know The Mice", null, (s, e) => {
            Forms.MessageBox.Show(
                "Know The Mice — Wireless PC Remote Control Host\nVersion 1.1.0\n\nYour phone. Your PC. Connected.\nhttps://knowthemice.web.app",
                "About Know The Mice",
                Forms.MessageBoxButtons.OK,
                Forms.MessageBoxIcon.Information);
        });

        contextMenu.Items.Add(new Forms.ToolStripSeparator());

        // Exit
        var exitItem = new Forms.ToolStripMenuItem("Exit Know The Mice", null, (s, e) => ExitApplication());
        contextMenu.Items.Add(exitItem);

        _trayIcon.ContextMenuStrip = contextMenu;

        // Both Left-Click and Double-Click restore the dashboard
        _trayIcon.MouseClick += (s, e) =>
        {
            if (e.Button == Forms.MouseButtons.Left)
            {
                ShowMainWindow();
            }
        };
        _trayIcon.DoubleClick += (s, e) => ShowMainWindow();

        // Immediate user feedback after dispatcher initializes
        Task.Delay(1200).ContinueWith(_ =>
        {
            ShowTrayNotification(
                "Know The Mice Host Active",
                "Running in your Windows system tray. Ready for phone connection.");
        });
    }

    public void ShowTrayNotification(string title, string message, Forms.ToolTipIcon icon = Forms.ToolTipIcon.Info)
    {
        Log($"ShowTrayNotification requested: {title} - {message} (skipping native balloon to protect shell stability)");
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
                ShowTrayNotification("Device Connected", $"Connected to {clientName}", Forms.ToolTipIcon.Info);
            }
            else if (!isConnected)
            {
                ShowTrayNotification("Device Disconnected", "Mobile device disconnected.", Forms.ToolTipIcon.Info);
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
        Log("ExitApplication called");
        try
        {
            if (_trayIcon != null)
            {
                _trayIcon.Visible = false;
                _trayIcon.Dispose();
                _trayIcon = null;
            }
        }
        catch { }

        ReleaseMutexSafely();
        Current.Shutdown();
    }

    protected override void OnExit(ExitEventArgs e)
    {
        Log($"OnExit called with code {e.ApplicationExitCode}");
        try
        {
            if (_trayIcon != null)
            {
                _trayIcon.Visible = false;
                _trayIcon.Dispose();
                _trayIcon = null;
            }
        }
        catch { }

        ReleaseMutexSafely();
        base.OnExit(e);
    }

    private void ReleaseMutexSafely()
    {
        if (_ownsMutex && _singleInstanceMutex != null)
        {
            try
            {
                _singleInstanceMutex.ReleaseMutex();
            }
            catch { }
            _ownsMutex = false;
        }
        try
        {
            _singleInstanceMutex?.Dispose();
            _singleInstanceMutex = null;
        }
        catch { }
    }
        /// <summary>
        /// Ensures the given window is positioned within the current monitor's working area.
        /// If saved bounds are invalid or off-screen, they are clamped and the window is centered.
        /// </summary>
        private void EnsureWindowWithinWorkArea(Window window)
        {
            try
            {
                // Retrieve saved geometry; fallback to defaults if not set
                double left = Properties.Settings.Default["WindowLeft"] as double? ?? window.Left;
                double top = Properties.Settings.Default["WindowTop"] as double? ?? window.Top;
                double width = Properties.Settings.Default["WindowWidth"] as double? ?? window.Width;
                double height = Properties.Settings.Default["WindowHeight"] as double? ?? window.Height;

                // If width/height are unreasonable, use current size
                if (width <= 0) width = window.Width;
                if (height <= 0) height = window.Height;

                // Get the screen that currently contains the window (or primary if none)
                var helper = new System.Windows.Interop.WindowInteropHelper(window);
                var screen = Forms.Screen.FromHandle(helper.Handle);
                var workArea = screen.WorkingArea; // device pixels

                // Determine DPI scaling for the target screen
                var source = PresentationSource.FromVisual(window);
                double dpiX = 96.0, dpiY = 96.0;
                if (source != null)
                {
                    dpiX = 96.0 * source.CompositionTarget.TransformToDevice.M11;
                    dpiY = 96.0 * source.CompositionTarget.TransformToDevice.M22;
                }

                // Convert logical (WPF units) to device pixels
                int devLeft = (int)Math.Round(left * dpiX / 96.0);
                int devTop = (int)Math.Round(top * dpiY / 96.0);
                int devWidth = (int)Math.Round(width * dpiX / 96.0);
                int devHeight = (int)Math.Round(height * dpiY / 96.0);

                // Clamp to work area
                if (devLeft < workArea.Left) devLeft = workArea.Left;
                if (devTop < workArea.Top) devTop = workArea.Top;
                if (devLeft + devWidth > workArea.Right) devLeft = workArea.Right - devWidth;
                if (devTop + devHeight > workArea.Bottom) devTop = workArea.Bottom - devHeight;

                // If still off‑screen after clamping (e.g., window larger than work area), center it
                if (devLeft < workArea.Left) devLeft = workArea.Left;
                if (devTop < workArea.Top) devTop = workArea.Top;

                // Convert back to logical units
                double newLeft = devLeft * 96.0 / dpiX;
                double newTop = devTop * 96.0 / dpiY;
                double newWidth = devWidth * 96.0 / dpiX;
                double newHeight = devHeight * 96.0 / dpiY;

                window.Left = newLeft;
                window.Top = newTop;
                window.Width = newWidth;
                window.Height = newHeight;
            }
            catch (Exception ex)
            {
                // If anything goes wrong, fall back to centering the window on the primary screen
                App.Log($"EnsureWindowWithinWorkArea failed: {ex.Message}. Centering window.");
                window.WindowStartupLocation = WindowStartupLocation.CenterScreen;
            }
        }
        }
