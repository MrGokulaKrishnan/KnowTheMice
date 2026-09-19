using System.IO;
using System.Windows;
using System.Windows.Input;
using Forms = System.Windows.Forms;

namespace KnowTheMice.Setup;

public partial class InstallerWindow : Window
{
    public InstallerWindow()
    {
        InitializeComponent();
        TxtInstallDir.Text = Program.GetInstallDir();
    }

    private void TitleBar_MouseDown(object sender, MouseButtonEventArgs e)
    {
        if (e.LeftButton == MouseButtonState.Pressed)
        {
            DragMove();
        }
    }

    private void BtnClose_Click(object sender, RoutedEventArgs e)
    {
        Close();
    }

    private void BtnWelcomeNext_Click(object sender, RoutedEventArgs e)
    {
        StepWelcome.Visibility = Visibility.Collapsed;
        StepOptions.Visibility = Visibility.Visible;
    }

    private void BtnOptionsBack_Click(object sender, RoutedEventArgs e)
    {
        StepOptions.Visibility = Visibility.Collapsed;
        StepWelcome.Visibility = Visibility.Visible;
    }

    private void BtnBrowse_Click(object sender, RoutedEventArgs e)
    {
        using var dlg = new Forms.FolderBrowserDialog();
        dlg.Description = "Select Installation Folder for Know The Mice";
        dlg.SelectedPath = TxtInstallDir.Text;
        if (dlg.ShowDialog() == Forms.DialogResult.OK)
        {
            TxtInstallDir.Text = dlg.SelectedPath;
        }
    }

    private async void BtnOptionsInstall_Click(object sender, RoutedEventArgs e)
    {
        StepOptions.Visibility = Visibility.Collapsed;
        StepInstalling.Visibility = Visibility.Visible;

        bool createDesktop = ChkDesktop.IsChecked == true;
        bool createStartMenu = ChkStartMenu.IsChecked == true;
        bool startup = ChkStartup.IsChecked == true;
        bool firewall = ChkFirewall.IsChecked == true;
        string customDir = TxtInstallDir.Text.Trim();

        await Task.Run(() =>
        {
            Program.PerformInstallCustom(
                customDir,
                createDesktop,
                createStartMenu,
                startup,
                firewall,
                (status, pct) =>
                {
                    Dispatcher.Invoke(() =>
                    {
                        TxtProgressStatus.Text = status;
                        PrgInstall.Value = pct;
                        TxtProgressPct.Text = $"{pct}%";
                    });
                }
            );
        });

        StepInstalling.Visibility = Visibility.Collapsed;
        StepCompleted.Visibility = Visibility.Visible;
    }

    private void BtnLaunch_Click(object sender, RoutedEventArgs e)
    {
        Program.LaunchHost(TxtInstallDir.Text.Trim());
        Close();
    }
}
