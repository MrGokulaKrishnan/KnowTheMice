using System.Diagnostics;
using System.IO;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Windows;

namespace KnowTheMice.Host;

public enum UpdateState
{
    Idle,
    Checking,
    UpToDate,
    UpdateAvailable,
    Downloading,
    Ready, // Staged, verified SHA-256, Restart Required
    Failed
}

public class UpdateMetadata
{
    [JsonPropertyName("latestVersion")]
    public string LatestVersion { get; set; } = string.Empty;

    [JsonPropertyName("releaseDate")]
    public string ReleaseDate { get; set; } = string.Empty;

    [JsonPropertyName("windows")]
    public PlatformUpdate Windows { get; set; } = new();

    [JsonPropertyName("android")]
    public PlatformUpdate Android { get; set; } = new();
}

public class PlatformUpdate
{
    [JsonPropertyName("version")]
    public string Version { get; set; } = string.Empty;

    [JsonPropertyName("versionCode")]
    public int VersionCode { get; set; }

    [JsonPropertyName("downloadUrl")]
    public string DownloadUrl { get; set; } = string.Empty;

    [JsonPropertyName("sha256")]
    public string Sha256 { get; set; } = string.Empty;

    [JsonPropertyName("changelog")]
    public string Changelog { get; set; } = string.Empty;

    [JsonPropertyName("mandatory")]
    public bool Mandatory { get; set; }
}

public class StagedUpdateInfo
{
    public string StagedVersion { get; set; } = string.Empty;
    public string InstallerPath { get; set; } = string.Empty;
    public string Sha256 { get; set; } = string.Empty;
    public string Changelog { get; set; } = string.Empty;
    public DateTime StagedAt { get; set; } = DateTime.UtcNow;
}

public class UpdateService
{
    public const string UPDATE_METADATA_URL = "https://knowthemice.web.app/downloads.json";
    private readonly HttpClient _http = new() { Timeout = TimeSpan.FromSeconds(15) };
    private readonly string _persistencePath;

    public UpdateState CurrentState { get; private set; } = UpdateState.Idle;
    public PlatformUpdate? AvailableUpdate { get; private set; }
    public string? StagedInstallerPath { get; private set; }
    public string? ErrorMessage { get; private set; }
    public int DownloadProgress { get; private set; }

    public event Action<UpdateState, PlatformUpdate?, string?>? OnStateChanged;

    public UpdateService()
    {
        string localAppData = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
        string appDir = Path.Combine(localAppData, "KnowTheMice");
        Directory.CreateDirectory(appDir);
        _persistencePath = Path.Combine(appDir, "update_staged.json");
    }

    public void InitializeState(string currentVersion)
    {
        try
        {
            if (File.Exists(_persistencePath))
            {
                string json = File.ReadAllText(_persistencePath);
                var staged = JsonSerializer.Deserialize<StagedUpdateInfo>(json);

                if (staged != null &&
                    IsNewerVersion(staged.StagedVersion, currentVersion) &&
                    File.Exists(staged.InstallerPath))
                {
                    // Verify hash
                    if (string.IsNullOrEmpty(staged.Sha256) || VerifySha256(staged.InstallerPath, staged.Sha256))
                    {
                        StagedInstallerPath = staged.InstallerPath;
                        AvailableUpdate = new PlatformUpdate
                        {
                            Version = staged.StagedVersion,
                            Changelog = staged.Changelog,
                            Sha256 = staged.Sha256
                        };
                        SetState(UpdateState.Ready, AvailableUpdate, null);
                        return;
                    }
                }

                // If running version is equal or newer, or staged file corrupted, clean up
                CleanStagedPersistence();
            }
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[UpdateService] Error initializing state: {ex.Message}");
        }

        SetState(UpdateState.Idle, null, null);
    }

    public async Task<PlatformUpdate?> CheckForUpdatesAsync(string currentVersion)
    {
        // If an update is already staged and ready for restart, retain Ready state
        if (CurrentState == UpdateState.Ready && AvailableUpdate != null)
        {
            return AvailableUpdate;
        }

        SetState(UpdateState.Checking, null, null);

        try
        {
            string url = $"{UPDATE_METADATA_URL}?t={DateTimeOffset.UtcNow.ToUnixTimeSeconds()}";
            string json = await _http.GetStringAsync(url);
            var meta = JsonSerializer.Deserialize<UpdateMetadata>(json);

            if (meta?.Windows != null && !string.IsNullOrWhiteSpace(meta.Windows.Version))
            {
                if (IsNewerVersion(meta.Windows.Version, currentVersion))
                {
                    AvailableUpdate = meta.Windows;
                    SetState(UpdateState.UpdateAvailable, AvailableUpdate, null);
                    return AvailableUpdate;
                }
            }

            SetState(UpdateState.UpToDate, null, null);
            return null;
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[UpdateService] Check failed: {ex.Message}");
            SetState(UpdateState.Failed, null, $"Check failed: {ex.Message}");
            return null;
        }
    }

    public async Task<bool> DownloadUpdateAsync(Action<int>? onProgress = null)
    {
        if (AvailableUpdate == null || string.IsNullOrWhiteSpace(AvailableUpdate.DownloadUrl))
        {
            SetState(UpdateState.Failed, null, "No download URL available.");
            return false;
        }

        SetState(UpdateState.Downloading, AvailableUpdate, null);
        DownloadProgress = 0;

        try
        {
            string tempDir = Path.GetTempPath();
            string tempInstaller = Path.Combine(tempDir, $"KnowTheMice-Setup-v{AvailableUpdate.Version}.exe");

            using (var response = await _http.GetAsync(AvailableUpdate.DownloadUrl, HttpCompletionOption.ResponseHeadersRead))
            {
                response.EnsureSuccessStatusCode();
                long totalBytes = response.Content.Headers.ContentLength ?? -1L;

                await using var contentStream = await response.Content.ReadAsStreamAsync();
                await using var fileStream = new FileStream(tempInstaller, FileMode.Create, FileAccess.Write, FileShare.None, 8192, true);

                byte[] buffer = new byte[8192];
                long totalRead = 0;
                int bytesRead;

                while ((bytesRead = await contentStream.ReadAsync(buffer, 0, buffer.Length)) > 0)
                {
                    await fileStream.WriteAsync(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    if (totalBytes > 0)
                    {
                        int pct = (int)((totalRead * 100) / totalBytes);
                        DownloadProgress = pct;
                        onProgress?.Invoke(pct);
                    }
                }
            }

            // Verify SHA-256 Checksum if present
            if (!string.IsNullOrEmpty(AvailableUpdate.Sha256))
            {
                if (!VerifySha256(tempInstaller, AvailableUpdate.Sha256))
                {
                    throw new InvalidOperationException("Checksum validation failed for downloaded update file.");
                }
            }

            StagedInstallerPath = tempInstaller;

            // Persist staged state to disk
            var stagedInfo = new StagedUpdateInfo
            {
                StagedVersion = AvailableUpdate.Version,
                InstallerPath = tempInstaller,
                Sha256 = AvailableUpdate.Sha256,
                Changelog = AvailableUpdate.Changelog,
                StagedAt = DateTime.UtcNow
            };
            File.WriteAllText(_persistencePath, JsonSerializer.Serialize(stagedInfo, new JsonSerializerOptions { WriteIndented = true }));

            SetState(UpdateState.Ready, AvailableUpdate, null);
            return true;
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[UpdateService] Download failed: {ex.Message}");
            SetState(UpdateState.Failed, AvailableUpdate, $"Download error: {ex.Message}");
            return false;
        }
    }

    public void ApplyRestartToUpdate()
    {
        if (CurrentState != UpdateState.Ready || string.IsNullOrEmpty(StagedInstallerPath) || !File.Exists(StagedInstallerPath))
        {
            SetState(UpdateState.Failed, AvailableUpdate, "Cannot restart: update installer not staged.");
            return;
        }

        try
        {
            Process.Start(new ProcessStartInfo
            {
                FileName = StagedInstallerPath,
                Arguments = "/S",
                UseShellExecute = true
            });

            System.Windows.Application.Current?.Dispatcher.Invoke(() =>
            {
                System.Windows.Application.Current.Shutdown();
            });
            Environment.Exit(0);
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[UpdateService] Restart launch failed: {ex.Message}");
            SetState(UpdateState.Failed, AvailableUpdate, $"Failed to launch installer: {ex.Message}");
        }
    }

    public void ClearFailure()
    {
        if (AvailableUpdate != null)
        {
            SetState(UpdateState.UpdateAvailable, AvailableUpdate, null);
        }
        else
        {
            SetState(UpdateState.Idle, null, null);
        }
    }

    private void CleanStagedPersistence()
    {
        try
        {
            if (File.Exists(_persistencePath))
            {
                string json = File.ReadAllText(_persistencePath);
                var staged = JsonSerializer.Deserialize<StagedUpdateInfo>(json);
                if (staged != null && !string.IsNullOrEmpty(staged.InstallerPath) && File.Exists(staged.InstallerPath))
                {
                    try { File.Delete(staged.InstallerPath); } catch { }
                }
                File.Delete(_persistencePath);
            }
        }
        catch { }
    }

    private void SetState(UpdateState newState, PlatformUpdate? update, string? error)
    {
        CurrentState = newState;
        if (update != null) AvailableUpdate = update;
        ErrorMessage = error;
        OnStateChanged?.Invoke(newState, AvailableUpdate, error);
    }

    private static bool VerifySha256(string filePath, string expectedSha256)
    {
        try
        {
            using var sha = SHA256.Create();
            using var stream = File.OpenRead(filePath);
            byte[] hash = sha.ComputeHash(stream);
            string hex = BitConverter.ToString(hash).Replace("-", "").ToUpperInvariant();
            return string.Equals(hex, expectedSha256.Replace("-", "").ToUpperInvariant(), StringComparison.OrdinalIgnoreCase);
        }
        catch
        {
            return false;
        }
    }

    public static bool IsNewerVersion(string remote, string current)
    {
        try
        {
            var vRemote = new Version(remote.TrimStart('v'));
            var vCurrent = new Version(current.TrimStart('v'));
            return vRemote > vCurrent;
        }
        catch
        {
            return string.Compare(remote, current, StringComparison.OrdinalIgnoreCase) > 0;
        }
    }
}
