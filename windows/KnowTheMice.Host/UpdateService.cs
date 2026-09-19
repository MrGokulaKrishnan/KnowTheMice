using System.Diagnostics;
using System.IO;
using System.Net.Http;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Windows;

namespace KnowTheMice.Host;

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

public class UpdateService
{
    public const string UPDATE_METADATA_URL = "https://knowthemice.web.app/downloads.json";
    private readonly HttpClient _http = new() { Timeout = TimeSpan.FromSeconds(10) };

    public event Action<PlatformUpdate>? OnUpdateFound;

    public async Task<PlatformUpdate?> CheckForUpdatesAsync(string currentVersion = "1.1.0")
    {
        try
        {
            string json = await _http.GetStringAsync(UPDATE_METADATA_URL + "?t=" + DateTimeOffset.UtcNow.ToUnixTimeSeconds());
            var meta = JsonSerializer.Deserialize<UpdateMetadata>(json);
            if (meta?.Windows != null && !string.IsNullOrWhiteSpace(meta.Windows.Version))
            {
                if (IsNewerVersion(meta.Windows.Version, currentVersion))
                {
                    OnUpdateFound?.Invoke(meta.Windows);
                    return meta.Windows;
                }
            }
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[UpdateService] Check failed: {ex.Message}");
        }
        return null;
    }

    public async Task DownloadAndRestartAsync(string downloadUrl, Action<int>? onProgress = null)
    {
        string tempDir = Path.GetTempPath();
        string tempInstaller = Path.Combine(tempDir, "KnowTheMice-Setup-Update.exe");

        using (var response = await _http.GetAsync(downloadUrl, HttpCompletionOption.ResponseHeadersRead))
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
                if (totalBytes > 0 && onProgress != null)
                {
                    int pct = (int)((totalRead * 100) / totalBytes);
                    onProgress(pct);
                }
            }
        }

        // Launch installer and cleanly exit current process
        Process.Start(new ProcessStartInfo
        {
            FileName = tempInstaller,
            Arguments = "/S",
            UseShellExecute = true
        });

        System.Windows.Application.Current?.Dispatcher.Invoke(() =>
        {
            System.Windows.Application.Current.Shutdown();
        });
        Environment.Exit(0);
    }

    private static bool IsNewerVersion(string remote, string current)
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
