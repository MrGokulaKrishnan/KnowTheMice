using System.Diagnostics;
using System.Runtime.InteropServices;
using KnowTheMice.Core;

namespace KnowTheMice.SystemControllers;

public class PowerController
{
    [DllImport("PowrProf.dll", SetLastError = true)]
    private static extern bool SetSuspendState(bool hibernate, bool forceCritical, bool disableWakeEvent);

    public static bool ExecutePowerAction(PowerAction action, bool isConfirmed, string targetPcName)
    {
        // 1. Mandatory confirmation validation
        if (!isConfirmed)
        {
            throw new InvalidOperationException("Power action was rejected: Confirmation flag is false.");
        }

        // 2. PC name matching validation (prevents misdirected power commands)
        if (!string.Equals(targetPcName, Environment.MachineName, StringComparison.OrdinalIgnoreCase))
        {
            throw new InvalidOperationException($"Target PC name '{targetPcName}' does not match local host '{Environment.MachineName}'.");
        }

        switch (action)
        {
            case PowerAction.LOCK:
                Input.NativeMethods.LockWorkStation();
                return true;

            case PowerAction.SLEEP:
                return SetSuspendState(false, false, false);

            case PowerAction.HIBERNATE:
                return SetSuspendState(true, false, false);

            case PowerAction.RESTART:
                Process.Start(new ProcessStartInfo
                {
                    FileName = "shutdown.exe",
                    Arguments = "/r /t 0",
                    CreateNoWindow = true,
                    UseShellExecute = false
                });
                return true;

            case PowerAction.SHUTDOWN:
                Process.Start(new ProcessStartInfo
                {
                    FileName = "shutdown.exe",
                    Arguments = "/s /t 0",
                    CreateNoWindow = true,
                    UseShellExecute = false
                });
                return true;

            default:
                return false;
        }
    }
}
