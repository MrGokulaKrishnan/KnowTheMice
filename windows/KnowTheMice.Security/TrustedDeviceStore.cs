using System.Security.Cryptography;
using System.Text;
using System.Text.Json;

namespace KnowTheMice.Security;

public class TrustedDevice
{
    public string ClientId { get; set; } = string.Empty;
    public string FriendlyName { get; set; } = string.Empty;
    public string AuthTokenHash { get; set; } = string.Empty;
    public DateTime PairedAtUtc { get; set; } = DateTime.UtcNow;
    public DateTime LastSeenUtc { get; set; } = DateTime.UtcNow;
}

public class TrustedDeviceStore
{
    private readonly string _storagePath;
    private readonly Dictionary<string, TrustedDevice> _devices = new();
    private readonly object _lock = new();

    public TrustedDeviceStore(string? customPath = null)
    {
        if (string.IsNullOrEmpty(customPath))
        {
            string appData = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
            string dir = Path.Combine(appData, "KnowTheMice");
            Directory.CreateDirectory(dir);
            _storagePath = Path.Combine(dir, "trusted_devices.dat");
        }
        else
        {
            _storagePath = customPath;
        }

        Load();
    }

    public void SaveDevice(string clientId, string friendlyName, string authToken)
    {
        lock (_lock)
        {
            using var sha = SHA256.Create();
            string tokenHash = Convert.ToBase64String(sha.ComputeHash(Encoding.UTF8.GetBytes(authToken)));

            _devices[clientId] = new TrustedDevice
            {
                ClientId = clientId,
                FriendlyName = friendlyName,
                AuthTokenHash = tokenHash,
                PairedAtUtc = DateTime.UtcNow,
                LastSeenUtc = DateTime.UtcNow
            };

            Persist();
        }
    }

    public bool ValidateDevice(string clientId, string authToken)
    {
        lock (_lock)
        {
            if (!_devices.TryGetValue(clientId, out var dev))
                return false;

            using var sha = SHA256.Create();
            string tokenHash = Convert.ToBase64String(sha.ComputeHash(Encoding.UTF8.GetBytes(authToken)));

            if (CryptographicOperations.FixedTimeEquals(
                Encoding.UTF8.GetBytes(dev.AuthTokenHash),
                Encoding.UTF8.GetBytes(tokenHash)))
            {
                dev.LastSeenUtc = DateTime.UtcNow;
                Persist();
                return true;
            }

            return false;
        }
    }

    public bool RevokeDevice(string clientId)
    {
        lock (_lock)
        {
            if (_devices.Remove(clientId))
            {
                Persist();
                return true;
            }
            return false;
        }
    }

    public List<TrustedDevice> GetAllDevices()
    {
        lock (_lock)
        {
            return _devices.Values.ToList();
        }
    }

    private void Persist()
    {
        try
        {
            string json = JsonSerializer.Serialize(_devices);
            byte[] plaintext = Encoding.UTF8.GetBytes(json);
            // DPAPI encryption (CurrentUser scope)
            byte[] encrypted = ProtectedData.Protect(plaintext, null, DataProtectionScope.CurrentUser);
            File.WriteAllBytes(_storagePath, encrypted);
        }
        catch { }
    }

    private void Load()
    {
        lock (_lock)
        {
            if (!File.Exists(_storagePath)) return;
            try
            {
                byte[] encrypted = File.ReadAllBytes(_storagePath);
                byte[] plaintext = ProtectedData.Unprotect(encrypted, null, DataProtectionScope.CurrentUser);
                string json = Encoding.UTF8.GetString(plaintext);
                var loaded = JsonSerializer.Deserialize<Dictionary<string, TrustedDevice>>(json);
                if (loaded != null)
                {
                    _devices.Clear();
                    foreach (var kvp in loaded)
                    {
                        _devices[kvp.Key] = kvp.Value;
                    }
                }
            }
            catch { }
        }
    }
}
