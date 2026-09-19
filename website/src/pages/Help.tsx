import React from 'react';
import { HelpCircle, AlertTriangle, Wifi, ShieldAlert, Cpu, RefreshCw, Terminal } from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

export const HelpPage: React.FC = () => {
  const issues = [
    {
      title: "Android app says 'PC Not Found' or searching indefinitely",
      cause: "Both devices are not on the same network subnet, or your router has Client Isolation enabled.",
      solution: "1. Confirm both phone and PC are connected to the same Wi-Fi router (not 2.4GHz Guest vs 5GHz Main).\n2. Disable 'AP Isolation' or 'Client Isolation' in your Wi-Fi router settings.\n3. Alternatively, click 'Connect Manually' on Android and enter your PC's local IP address (found in Windows Host Dashboard or by running 'ipconfig' in command prompt)."
    },
    {
      title: "Windows Firewall blocks the connection",
      cause: "Windows Defender or third-party firewall set the network profile to 'Public' or blocked port 52841.",
      solution: "1. Open Windows Settings -> Network & Internet -> Status -> Properties.\n2. Ensure Network Profile is set to 'Private' rather than 'Public'.\n3. Ensure port 52841 (TCP) and 52840 (UDP) are permitted in Windows Firewall."
    },
    {
      title: "Cursor movement feels sluggish or jittery",
      cause: "High Wi-Fi latency, packet loss, or excessive distance from the router.",
      solution: "1. Ensure you are on a 5GHz Wi-Fi band if possible.\n2. In Android Settings -> Remote Settings, adjust the Sensitivity slider and enable Pointer Smoothing.\n3. Make sure Bluetooth audio streaming or heavy local downloads are not saturating the Wi-Fi band."
    },
    {
      title: "Pairing code expired or failed to authenticate",
      cause: "Pairing codes expire automatically after 60 seconds for security.",
      solution: "1. Return to the Windows Host Dashboard and click 'Pair New Device' to generate a fresh 6-digit code.\n2. Re-enter the code immediately on Android."
    }
  ];

  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto">
      <div className="text-center max-w-3xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
          Diagnostics & Support
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          Help & <GlowText>Troubleshooting</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Plain-language resolutions for common local-network and discovery hiccups.
        </p>
      </div>

      <div className="space-y-6">
        {issues.map((item, idx) => (
          <GlassCard key={idx} className="p-7 space-y-3">
            <div className="flex items-start space-x-3">
              <div className="w-8 h-8 rounded-lg bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight shrink-0 mt-0.5">
                <HelpCircle className="w-4 h-4" />
              </div>
              <div className="space-y-1">
                <h3 className="text-base font-orbitron font-semibold text-white">{item.title}</h3>
                <p className="text-xs text-orange-400/90 font-mono">Cause: {item.cause}</p>
              </div>
            </div>
            <div className="p-4 rounded-xl bg-black/40 border border-white/5 text-xs sm:text-sm text-zinc-300 whitespace-pre-line leading-relaxed font-sans mt-3">
              {item.solution}
            </div>
          </GlassCard>
        ))}
      </div>
    </div>
  );
};
