using System.Windows;
using Forms = System.Windows.Forms;
using Drawing = System.Drawing;

namespace KnowTheMice.Host;

public partial class App : System.Windows.Application
{
    private static Mutex? _singleInstanceMutex;
    private Forms.NotifyIcon? _trayIcon;

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

        _trayIcon.Text = "Know The Mice Host";
        _trayIcon.Visible = true;

        var contextMenu = new Forms.ContextMenuStrip();
        contextMenu.Items.Add("Open Dashboard", null, (s, e) => ShowMainWindow());
        contextMenu.Items.Add(new Forms.ToolStripSeparator());
        contextMenu.Items.Add("Host Online (Port 52841)").Enabled = false;
        contextMenu.Items.Add(new Forms.ToolStripSeparator());
        contextMenu.Items.Add("Quit Know The Mice", null, (s, e) => ExitApplication());

        _trayIcon.ContextMenuStrip = contextMenu;
        _trayIcon.DoubleClick += (s, e) => ShowMainWindow();
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
