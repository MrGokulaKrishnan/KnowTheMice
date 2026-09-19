using System.Text;
using KnowTheMice.Security;
using KnowTheMice.SystemControllers;
using Xunit;

namespace KnowTheMice.Tests;

public class SecurityTests
{
    [Fact]
    public void PinManager_GeneratesValid6DigitPin_AndValidatesCorrectly()
    {
        var pinMgr = new PinManager();
        string pin = pinMgr.GenerateNewPin();

        Assert.Equal(6, pin.Length);
        Assert.True(int.TryParse(pin, out _));

        // Wrong pin should fail
        Assert.False(pinMgr.ValidatePin("000000"));

        // Correct pin should succeed
        Assert.True(pinMgr.ValidatePin(pin));

        // Single-use: after successful validation, should not be valid again
        Assert.False(pinMgr.ValidatePin(pin));
    }

    [Fact]
    public void PinManager_ThreeFailedAttempts_TriggersLockout()
    {
        var pinMgr = new PinManager();
        string pin = pinMgr.GenerateNewPin();

        Assert.False(pinMgr.ValidatePin("111111"));
        Assert.False(pinMgr.ValidatePin("222222"));
        Assert.False(pinMgr.ValidatePin("333333")); // 3rd failure

        Assert.True(pinMgr.IsLockedOut);
        // Even the right pin should now be rejected during lockout
        Assert.False(pinMgr.ValidatePin(pin));
    }

    [Fact]
    public void ReplayProtection_RejectsDuplicateAndOldPackets()
    {
        var replay = new ReplayProtection(windowSize: 32);

        Assert.True(replay.IsValidAndAdvance(100));
        Assert.True(replay.IsValidAndAdvance(101));

        // Duplicate packet should be rejected
        Assert.False(replay.IsValidAndAdvance(100));

        // Stale packet outside window (101 - 32 = 69)
        Assert.False(replay.IsValidAndAdvance(50));

        // Future packet advances window
        Assert.True(replay.IsValidAndAdvance(105));
    }

    [Fact]
    public void CryptoEngine_AesGcm_EncryptAndDecrypt_Roundtrips()
    {
        byte[] key = new byte[32];
        System.Security.Cryptography.RandomNumberGenerator.Fill(key);

        byte[] plaintext = Encoding.UTF8.GetBytes("KnowTheMiceSecurePayload-12345");

        var (ciphertext, nonce, tag) = CryptoEngine.EncryptAesGcm(plaintext, key);
        byte[] decrypted = CryptoEngine.DecryptAesGcm(ciphertext, nonce, tag, key);

        Assert.Equal(plaintext, decrypted);
        Assert.Equal("KnowTheMiceSecurePayload-12345", Encoding.UTF8.GetString(decrypted));
    }

    [Fact]
    public void AppLauncher_RejectsShellExecutablesAndUnapprovedApps()
    {
        // 1. Shell commands must throw UnauthorizedAccessException
        Assert.Throws<UnauthorizedAccessException>(() => AppLauncher.LaunchApp("cmd.exe"));
        Assert.Throws<UnauthorizedAccessException>(() => AppLauncher.LaunchApp("powershell"));
        Assert.Throws<UnauthorizedAccessException>(() => AppLauncher.LaunchApp("pwsh"));
        Assert.Throws<UnauthorizedAccessException>(() => AppLauncher.LaunchApp("calc.exe & echo pwned"));

        // 2. Unapproved random app must throw UnauthorizedAccessException
        Assert.Throws<UnauthorizedAccessException>(() => AppLauncher.LaunchApp("malicious_tool.exe"));
    }

    [Fact]
    public void PowerController_UnconfirmedAction_ThrowsException()
    {
        Assert.Throws<InvalidOperationException>(() =>
            PowerController.ExecutePowerAction(Core.PowerAction.SHUTDOWN, isConfirmed: false, Environment.MachineName));
    }

    [Fact]
    public void PowerController_WrongTargetPc_ThrowsException()
    {
        Assert.Throws<InvalidOperationException>(() =>
            PowerController.ExecutePowerAction(Core.PowerAction.RESTART, isConfirmed: true, "WRONG-PC-NAME"));
    }
}
