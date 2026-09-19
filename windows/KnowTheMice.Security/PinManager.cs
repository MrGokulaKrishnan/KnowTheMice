using System.Security.Cryptography;

namespace KnowTheMice.Security;

public class PinManager
{
    private string? _currentPin;
    private DateTime _expiresAtUtc;
    private int _failedAttempts;
    private DateTime _lockoutUntilUtc;
    private readonly object _lock = new();

    public const int PIN_TTL_SECONDS = 60;
    public const int MAX_ATTEMPTS = 3;
    public const int LOCKOUT_MINUTES = 5;

    public bool IsLockedOut
    {
        get
        {
            lock (_lock)
            {
                return DateTime.UtcNow < _lockoutUntilUtc;
            }
        }
    }

    public string GenerateNewPin()
    {
        lock (_lock)
        {
            if (IsLockedOut)
            {
                throw new InvalidOperationException($"Pairing is temporarily locked out due to multiple failed attempts. Try again in {Math.Ceiling((_lockoutUntilUtc - DateTime.UtcNow).TotalMinutes)} minutes.");
            }

            // Cryptographically secure 6-digit numeric PIN (100000 - 999999)
            int randomVal = RandomNumberGenerator.GetInt32(100000, 1000000);
            _currentPin = randomVal.ToString();
            _expiresAtUtc = DateTime.UtcNow.AddSeconds(PIN_TTL_SECONDS);
            _failedAttempts = 0;
            return _currentPin;
        }
    }

    public bool ValidatePin(string candidate)
    {
        lock (_lock)
        {
            if (IsLockedOut)
                return false;

            if (_currentPin == null || DateTime.UtcNow > _expiresAtUtc)
            {
                _currentPin = null;
                return false; // Expired
            }

            bool match = CryptographicOperations.FixedTimeEquals(
                System.Text.Encoding.UTF8.GetBytes(_currentPin),
                System.Text.Encoding.UTF8.GetBytes(candidate)
            );

            if (match)
            {
                _currentPin = null; // Single-use consumption
                _failedAttempts = 0;
                return true;
            }
            else
            {
                _failedAttempts++;
                if (_failedAttempts >= MAX_ATTEMPTS)
                {
                    _lockoutUntilUtc = DateTime.UtcNow.AddMinutes(LOCKOUT_MINUTES);
                    _currentPin = null;
                }
                return false;
            }
        }
    }

    public int GetRemainingSeconds()
    {
        lock (_lock)
        {
            if (_currentPin == null) return 0;
            int remaining = (int)(_expiresAtUtc - DateTime.UtcNow).TotalSeconds;
            return Math.Max(0, remaining);
        }
    }
}
