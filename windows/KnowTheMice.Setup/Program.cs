using System;
using System.Diagnostics;
using System.IO;
using System.IO.Compression;
using System.Reflection;
using System.Windows;
using Microsoft.Win32;

namespace KnowTheMice.Setup
{
    public static class Program
    {
        public const string AppName = "Know The Mice";
        public const string AppVersion = "1.1.0";
        public const string AppPublisher = "Know The Mice Team";
        public const string ExeName = "KnowTheMice.Host.exe";

        [STAThread]
        public static void Main(string[] args)
        {
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
                PerformInstallCustom(GetInstallDir(), true, true, true, true, null);
                LaunchHost();
                return;
            }

            // Launch WPF Liquid Glass Installer
            var app = new System.Windows.Application();
            app.Run(new InstallerWindow());
        }

        public static string GetInstallDir()
        {
            string localAppData = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
            return Path.Combine(localAppData, "Programs", "KnowTheMice");
        }

        public static void PerformInstallCustom(
            string installDir,
            bool createDesktop,
            bool createStartMenu,
            bool startup,
            bool firewall,
            Action<string, int>? progressCallback)
        {
            progressCallback?.Invoke("Preparing installation directory...", 10);
            if (!Directory.Exists(installDir))
            {
                Directory.CreateDirectory(installDir);
            }

            progressCallback?.Invoke("Extracting application components...", 30);
            var assembly = Assembly.GetExecutingAssembly();
            using (var stream = assembly.GetManifestResourceStream("KnowTheMice.Setup.payload.zip"))
            {
                if (stream != null)
                {
                    using (var archive = new ZipArchive(stream, ZipArchiveMode.Read))
                    {
                        int totalEntries = archive.Entries.Count;
                        int current = 0;
                        foreach (var entry in archive.Entries)
                        {
                            current++;
                            if (string.IsNullOrEmpty(entry.Name)) continue;
                            string destPath = Path.Combine(installDir, entry.FullName);
                            string? dir = Path.GetDirectoryName(destPath);
                            if (!string.IsNullOrEmpty(dir) && !Directory.Exists(dir))
                            {
                                Directory.CreateDirectory(dir);
                            }
                            entry.ExtractToFile(destPath, true);
                            
                            int pct = 30 + (int)(35.0 * current / Math.Max(1, totalEntries));
                            progressCallback?.Invoke($"Extracting {entry.Name}...", pct);
                        }
                    }
                }
            }

            // Copy self as uninstaller
            progressCallback?.Invoke("Configuring uninstaller...", 70);
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

            string hostExe = Path.Combine(installDir, ExeName);

            // Shortcuts
            progressCallback?.Invoke("Configuring shortcuts...", 80);
            if (createStartMenu)
            {
                try
                {
                    string startMenuDir = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Programs), "Know The Mice");
                    if (!Directory.Exists(startMenuDir))
                    {
                        Directory.CreateDirectory(startMenuDir);
                    }
                    string startMenuLnk = Path.Combine(startMenuDir, "Know The Mice.lnk");
                    CreateShortcut(startMenuLnk, hostExe, "Wireless Remote Control for PC");
                }
                catch { }
            }

            if (createDesktop)
            {
                try
                {
                    string desktopLnk = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory), "Know The Mice.lnk");
                    CreateShortcut(desktopLnk, hostExe, "Wireless Remote Control for PC");
                }
                catch { }
            }

            // Startup Registry
            if (startup)
            {
                try
                {
                    using var runKey = Registry.CurrentUser.OpenSubKey(@"Software\Microsoft\Windows\CurrentVersion\Run", true);
                    runKey?.SetValue("KnowTheMice", $"\"{hostExe}\"");
                }
                catch { }
            }

            // Firewall rule
            if (firewall)
            {
                progressCallback?.Invoke("Registering firewall rules...", 90);
                try
                {
                    var psi = new ProcessStartInfo
                    {
                        FileName = "netsh",
                        Arguments = $"advfirewall firewall add rule name=\"Know The Mice Host\" dir=in action=allow program=\"{hostExe}\" enable=yes profile=private",
                        CreateNoWindow = true,
                        UseShellExecute = false
                    };
                    Process.Start(psi)?.WaitForExit(3000);
                }
                catch { }
            }

            // Register in Windows Control Panel
            progressCallback?.Invoke("Registering in Windows Control Panel...", 95);
            RegisterUninstallEntry(installDir, hostExe);

            progressCallback?.Invoke("Installation Complete!", 100);
        }

        public static void LaunchHost(string? customDir = null)
        {
            try
            {
                string targetDir = string.IsNullOrWhiteSpace(customDir) ? GetInstallDir() : customDir;
                string hostExe = Path.Combine(targetDir, ExeName);
                if (File.Exists(hostExe))
                {
                    Process.Start(new ProcessStartInfo
                    {
                        FileName = hostExe,
                        UseShellExecute = true,
                        WorkingDirectory = targetDir
                    });
                }
            }
            catch { }
        }

        public static void PerformUninstall(bool silent)
        {
            try
            {
                // Terminate running host
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

                // Remove startup run key
                try
                {
                    using var runKey = Registry.CurrentUser.OpenSubKey(@"Software\Microsoft\Windows\CurrentVersion\Run", true);
                    runKey?.DeleteValue("KnowTheMice", false);
                }
                catch { }

                // Remove uninstall registry key
                using (var uninstallKey = Registry.CurrentUser.OpenSubKey(@"Software\Microsoft\Windows\CurrentVersion\Uninstall", true))
                {
                    uninstallKey?.DeleteSubKeyTree("KnowTheMice", false);
                }

                if (!silent)
                {
                    System.Windows.MessageBox.Show(
                        "Know The Mice has been successfully uninstalled from your computer.", 
                        "Uninstall Complete", 
                        MessageBoxButton.OK, 
                        MessageBoxImage.Information);
                }

                // Delete install directory via command
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
                    System.Windows.MessageBox.Show(
                        "Error during uninstallation: " + ex.Message, 
                        "Uninstall Error", 
                        MessageBoxButton.OK, 
                        MessageBoxImage.Error);
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
}
