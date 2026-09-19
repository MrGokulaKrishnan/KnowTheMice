using System;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.IO.Compression;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Microsoft.Win32;

namespace KnowTheMice.Setup
{
    static class Program
    {
        private const string AppName = "Know The Mice";
        private const string AppVersion = "1.0.0";
        private const string AppPublisher = "Know The Mice Team";
        private const string ExeName = "KnowTheMice.Host.exe";

        [STAThread]
        static void Main(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            bool isUninstall = false;
            bool isSilent = false;

            foreach (var arg in args)
            {
                if (arg.Equals("/uninstall", StringComparison.OrdinalIgnoreCase) || 
                    arg.Equals("--uninstall", StringComparison.OrdinalIgnoreCase))
                {
                    isUninstall = true;
                }
                if (arg.Equals("/S", StringComparison.OrdinalIgnoreCase) || 
                    arg.Equals("--silent", StringComparison.OrdinalIgnoreCase))
                {
                    isSilent = true;
                }
            }

            if (isUninstall)
            {
                PerformUninstall(isSilent);
                return;
            }

            if (isSilent)
            {
                PerformInstall(null);
                LaunchHost();
                return;
            }

            Application.Run(new InstallerForm());
        }

        public static string GetInstallDir()
        {
            string localAppData = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
            return Path.Combine(localAppData, "Programs", "KnowTheMice");
        }

        public static void PerformInstall(Action<string, int>? progressCallback)
        {
            string installDir = GetInstallDir();
            progressCallback?.Invoke("Preparing installation directory...", 15);

            if (!Directory.Exists(installDir))
            {
                Directory.CreateDirectory(installDir);
            }

            progressCallback?.Invoke("Extracting application components...", 35);
            var assembly = Assembly.GetExecutingAssembly();
            using (var stream = assembly.GetManifestResourceStream("KnowTheMice.Setup.payload.zip"))
            {
                if (stream != null)
                {
                    using (var archive = new ZipArchive(stream, ZipArchiveMode.Read))
                    {
                        foreach (var entry in archive.Entries)
                        {
                            if (string.IsNullOrEmpty(entry.Name)) continue;
                            string destPath = Path.Combine(installDir, entry.FullName);
                            string? dir = Path.GetDirectoryName(destPath);
                            if (!string.IsNullOrEmpty(dir) && !Directory.Exists(dir))
                            {
                                Directory.CreateDirectory(dir);
                            }
                            entry.ExtractToFile(destPath, true);
                        }
                    }
                }
            }

            // Copy self as uninstaller
            progressCallback?.Invoke("Configuring uninstaller...", 65);
            try
            {
                string selfPath = Process.GetCurrentProcess().MainModule?.FileName ?? "";
                if (File.Exists(selfPath))
                {
                    string uninstallerPath = Path.Combine(installDir, "KnowTheMice-Setup-x64.exe");
                    if (!selfPath.Equals(uninstallerPath, StringComparison.OrdinalIgnoreCase))
                    {
                        File.Copy(selfPath, uninstallerPath, true);
                    }
                }
            }
            catch { }

            // Create shortcuts
            progressCallback?.Invoke("Creating desktop and start menu shortcuts...", 80);
            string hostExe = Path.Combine(installDir, ExeName);
            string startMenuDir = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Programs), "Know The Mice");
            if (!Directory.Exists(startMenuDir))
            {
                Directory.CreateDirectory(startMenuDir);
            }
            string startMenuLnk = Path.Combine(startMenuDir, "Know The Mice.lnk");
            string desktopLnk = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory), "Know The Mice.lnk");

            CreateShortcut(startMenuLnk, hostExe, "Wireless Remote Control for PC");
            CreateShortcut(desktopLnk, hostExe, "Wireless Remote Control for PC");

            // Register in Windows Add/Remove Programs
            progressCallback?.Invoke("Registering in Windows Control Panel...", 95);
            RegisterUninstallEntry(installDir, hostExe);

            progressCallback?.Invoke("Installation Complete!", 100);
        }

        public static void LaunchHost()
        {
            try
            {
                string hostExe = Path.Combine(GetInstallDir(), ExeName);
                if (File.Exists(hostExe))
                {
                    Process.Start(new ProcessStartInfo
                    {
                        FileName = hostExe,
                        UseShellExecute = true,
                        WorkingDirectory = GetInstallDir()
                    });
                }
            }
            catch { }
        }

        public static void PerformUninstall(bool silent)
        {
            try
            {
                // Kill running host processes
                foreach (var p in Process.GetProcessesByName("KnowTheMice.Host"))
                {
                    try { p.Kill(); p.WaitForExit(2000); } catch { }
                }

                // Remove shortcuts
                string startMenuDir = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Programs), "Know The Mice");
                if (Directory.Exists(startMenuDir))
                {
                    Directory.Delete(startMenuDir, true);
                }

                string desktopLnk = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory), "Know The Mice.lnk");
                if (File.Exists(desktopLnk))
                {
                    File.Delete(desktopLnk);
                }

                // Remove registry key
                using (var uninstallKey = Registry.CurrentUser.OpenSubKey(@"Software\Microsoft\Windows\CurrentVersion\Uninstall", true))
                {
                    uninstallKey?.DeleteSubKeyTree("KnowTheMice", false);
                }

                if (!silent)
                {
                    MessageBox.Show("Know The Mice has been successfully uninstalled from your computer.", 
                        "Uninstall Complete", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }

                // Delete install dir via self-deleting cmd
                string installDir = GetInstallDir();
                Process.Start(new ProcessStartInfo
                {
                    FileName = "cmd.exe",
                    Arguments = $"/c timeout /t 2 /nobreak > NUL & rmdir /s /q \"{installDir}\"",
                    CreateNoWindow = true,
                    UseShellExecute = false
                });
            }
            catch (Exception ex)
            {
                if (!silent)
                {
                    MessageBox.Show("Error during uninstallation: " + ex.Message, "Uninstall Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
        }

        private static void CreateShortcut(string shortcutPath, string targetPath, string description)
        {
            try
            {
                Type? shellType = Type.GetTypeFromProgID("WScript.Shell");
                if (shellType != null)
                {
                    dynamic shell = Activator.CreateInstance(shellType)!;
                    dynamic shortcut = shell.CreateShortcut(shortcutPath);
                    shortcut.TargetPath = targetPath;
                    shortcut.WorkingDirectory = Path.GetDirectoryName(targetPath);
                    shortcut.Description = description;
                    shortcut.IconLocation = targetPath + ",0";
                    shortcut.Save();
                }
            }
            catch { }
        }

        private static void RegisterUninstallEntry(string installDir, string hostExe)
        {
            try
            {
                using var key = Registry.CurrentUser.CreateSubKey(@"Software\Microsoft\Windows\CurrentVersion\Uninstall\KnowTheMice");
                if (key != null)
                {
                    key.SetValue("DisplayName", AppName);
                    key.SetValue("DisplayVersion", AppVersion);
                    key.SetValue("Publisher", AppPublisher);
                    key.SetValue("InstallLocation", installDir);
                    key.SetValue("DisplayIcon", $"{hostExe},0");
                    key.SetValue("UninstallString", $"\"{Path.Combine(installDir, "KnowTheMice-Setup-x64.exe")}\" --uninstall");
                    key.SetValue("NoModify", 1, RegistryValueKind.DWord);
                    key.SetValue("NoRepair", 1, RegistryValueKind.DWord);
                    key.SetValue("EstimatedSize", 5000, RegistryValueKind.DWord);
                }
            }
            catch { }
        }
    }

    public class InstallerForm : Form
    {
        private ProgressBar progressBar;
        private Label lblStatus;
        private Label lblTitle;
        private Label lblSubtitle;
        private Button btnAction;
        private CheckBox chkLaunch;
        private bool isFinished = false;

        public InstallerForm()
        {
            Text = "Know The Mice Setup — v1.0.0";
            Size = new Size(520, 360);
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            StartPosition = FormStartPosition.CenterScreen;
            BackColor = Color.FromArgb(10, 10, 10);
            ForeColor = Color.White;

            lblTitle = new Label
            {
                Text = "KNOW THE MICE",
                Font = new Font("Segoe UI", 18, FontStyle.Bold),
                ForeColor = Color.FromArgb(255, 138, 0),
                Location = new Point(30, 25),
                AutoSize = true
            };

            lblSubtitle = new Label
            {
                Text = "Windows Desktop Remote Host Setup (v1.0.0)",
                Font = new Font("Segoe UI", 9.5f, FontStyle.Regular),
                ForeColor = Color.FromArgb(170, 170, 170),
                Location = new Point(32, 62),
                AutoSize = true
            };

            var pnlBox = new Panel
            {
                Location = new Point(30, 105),
                Size = new Size(445, 135),
                BackColor = Color.FromArgb(18, 18, 18),
                BorderStyle = BorderStyle.FixedSingle
            };

            lblStatus = new Label
            {
                Text = "Click 'Install Now' to install Know The Mice to your PC.",
                Font = new Font("Segoe UI", 9, FontStyle.Regular),
                ForeColor = Color.FromArgb(220, 220, 220),
                Location = new Point(15, 20),
                Size = new Size(415, 30)
            };

            progressBar = new ProgressBar
            {
                Location = new Point(15, 60),
                Size = new Size(415, 18),
                Style = ProgressBarStyle.Continuous,
                Value = 0
            };

            chkLaunch = new CheckBox
            {
                Text = "Launch Know The Mice in system tray after installation",
                Checked = true,
                ForeColor = Color.FromArgb(200, 200, 200),
                Font = new Font("Segoe UI", 8.5f),
                Location = new Point(15, 95),
                AutoSize = true
            };

            pnlBox.Controls.Add(lblStatus);
            pnlBox.Controls.Add(progressBar);
            pnlBox.Controls.Add(chkLaunch);

            btnAction = new Button
            {
                Text = "Install Now",
                Font = new Font("Segoe UI", 10, FontStyle.Bold),
                BackColor = Color.FromArgb(255, 90, 0),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Location = new Point(335, 260),
                Size = new Size(140, 40),
                Cursor = Cursors.Hand
            };
            btnAction.FlatAppearance.BorderSize = 0;
            btnAction.Click += BtnAction_Click;

            Controls.Add(lblTitle);
            Controls.Add(lblSubtitle);
            Controls.Add(pnlBox);
            Controls.Add(btnAction);
        }

        private async void BtnAction_Click(object? sender, EventArgs e)
        {
            if (isFinished)
            {
                if (chkLaunch.Checked)
                {
                    Program.LaunchHost();
                }
                Close();
                return;
            }

            btnAction.Enabled = false;
            btnAction.Text = "Installing...";

            await System.Threading.Tasks.Task.Run(() =>
            {
                Program.PerformInstall((msg, pct) =>
                {
                    Invoke(new Action(() =>
                    {
                        lblStatus.Text = msg;
                        progressBar.Value = Math.Min(100, pct);
                    }));
                });
            });

            isFinished = true;
            btnAction.Enabled = true;
            btnAction.Text = "Finish";
            btnAction.BackColor = Color.FromArgb(0, 160, 80);
            lblStatus.Text = "Installation succeeded! Know The Mice is ready to use.";
        }
    }
}
