using System.Diagnostics;

namespace KnowTheMice.SystemControllers;

public class SafeApp
{
    public string Id { get; set; } = string.Empty;
    public string Name { get; set; } = string.Empty;
    public string Category { get; set; } = "General";
    public string Executable { get; set; } = string.Empty;
}

public class AppLauncher
{
    // Explicit whitelist of user-approved safe applications
    private static readonly Dictionary<string, SafeApp> ApprovedApps = new(StringComparer.OrdinalIgnoreCase)
    {
        ["calc"] = new() { Id = "calc", Name = "Calculator", Category = "Utilities", Executable = "calc.exe" },
        ["notepad"] = new() { Id = "notepad", Name = "Notepad", Category = "Utilities", Executable = "notepad.exe" },
        ["mspaint"] = new() { Id = "mspaint", Name = "Paint", Category = "Creativity", Executable = "mspaint.exe" },
        ["explorer"] = new() { Id = "explorer", Name = "File Explorer", Category = "System", Executable = "explorer.exe" },
        ["taskmgr"] = new() { Id = "taskmgr", Name = "Task Manager", Category = "System", Executable = "taskmgr.exe" },
        ["msedge"] = new() { Id = "msedge", Name = "Microsoft Edge", Category = "Browser", Executable = "msedge.exe" },
        ["chrome"] = new() { Id = "chrome", Name = "Google Chrome", Category = "Browser", Executable = "chrome.exe" },
        ["spotify"] = new() { Id = "spotify", Name = "Spotify", Category = "Media", Executable = "spotify.exe" },
        ["powerpnt"] = new() { Id = "powerpnt", Name = "PowerPoint", Category = "Office", Executable = "powerpnt.exe" },
        ["winword"] = new() { Id = "winword", Name = "Microsoft Word", Category = "Office", Executable = "winword.exe" },
        ["excel"] = new() { Id = "excel", Name = "Microsoft Excel", Category = "Office", Executable = "excel.exe" },
    };

    public static List<SafeApp> GetApprovedApps()
    {
        return ApprovedApps.Values.ToList();
    }

    public static bool LaunchApp(string appId)
    {
        if (string.IsNullOrWhiteSpace(appId))
            return false;

        // Security check 1: Reject shell executables unconditionally
        string sanitized = appId.Trim().ToLowerInvariant();
        if (sanitized.Contains("cmd") || sanitized.Contains("powershell") || sanitized.Contains("pwsh") ||
            sanitized.Contains("bash") || sanitized.Contains("wscript") || sanitized.Contains("cscript") ||
            sanitized.Contains("&") || sanitized.Contains("|") || sanitized.Contains(">") || sanitized.Contains("<"))
        {
            throw new UnauthorizedAccessException("Security Violation: Shell executables and command chaining are strictly prohibited.");
        }

        // Security check 2: Must be in the explicitly approved whitelist
        if (!ApprovedApps.TryGetValue(sanitized, out var app))
        {
            throw new UnauthorizedAccessException($"Application '{appId}' is not in the approved whitelist.");
        }

        try
        {
            Process.Start(new ProcessStartInfo
            {
                FileName = app.Executable,
                UseShellExecute = true,
                CreateNoWindow = false
            });
            return true;
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"Failed to launch app {app.Name}: {ex.Message}");
            return false;
        }
    }
}
