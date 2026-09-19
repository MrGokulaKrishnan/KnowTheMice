import React, { useState } from 'react';
import { 
  Download, Monitor, Smartphone, CheckCircle, Copy, Check, 
  FileCode, Terminal, AlertTriangle, ShieldCheck, Sparkles 
} from 'lucide-react';
import { GlassCard, GradientButton, GlassButton, GlowText } from '../components/UIComponents';

export const DownloadPage: React.FC = () => {
  const [copiedHash, setCopiedHash] = useState<string | null>(null);

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedHash(id);
    setTimeout(() => setCopiedHash(null), 2000);
  };

  const GITHUB_RELEASE_BASE = "https://github.com/MrGokulaKrishnan/KnowTheMice/releases/download/v1.0.0";

  const downloads = {
    windowsMsi: {
      filename: "KnowTheMice-Setup-v1.0.0-win-x64.msi",
      size: "54.1 MB",
      sha256: "E516E1E55C5539C76EFAC6B60B7B1A85C4F84B0874195A811275AD170F4640C1",
      type: "Windows MSI Installer (Recommended)",
      url: `${GITHUB_RELEASE_BASE}/KnowTheMice-Setup-v1.0.0-win-x64.msi`
    },
    windowsExe: {
      filename: "KnowTheMice.Host.exe",
      size: "154.2 MB",
      sha256: "8ADF8BC0BB9A27F445AAE97B0A327C456762A31E92A071630E90C237BBDD8025",
      type: "Windows Portable Executable (No install needed)",
      url: `${GITHUB_RELEASE_BASE}/KnowTheMice.Host.exe`
    },
    androidApk: {
      filename: "app-debug.apk",
      size: "51.9 MB",
      sha256: "E06DE1F0C8FA96D73669094A4F267B4504914475553C7DE43F8EDE9E85858E0F",
      type: "Android Direct APK (Universal)",
      url: `${GITHUB_RELEASE_BASE}/app-debug.apk`
    }
  };

  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
      {/* Page Title */}
      <div className="text-center max-w-3xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
          Official Release Center
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          Download <GlowText>Know The Mice</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Latest stable version 1.0.0. Free, privacy-first, and zero subscriptions forever.
        </p>
      </div>

      {/* Main Download Cards Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Windows Card */}
        <GlassCard className="p-8 space-y-6 border-orange-500/30">
          <div className="flex items-center space-x-4">
            <div className="w-14 h-14 rounded-2xl bg-orange-500/10 border border-orange-500/40 flex items-center justify-center text-brand-highlight shadow-glow-orange">
              <Monitor className="w-7 h-7" />
            </div>
            <div>
              <h2 className="text-2xl font-orbitron font-bold text-white">Windows Desktop Host</h2>
              <p className="text-xs font-mono text-zinc-400">Windows 10 / 11 (64-bit)</p>
            </div>
          </div>

          <p className="text-zinc-300 text-sm leading-relaxed">
            The native background host application. Docks into your system tray, handles Win32 input simulation, auto-discovery beaconing, and secure pairing.
          </p>

          <div className="space-y-3 pt-2">
            <a 
              href={downloads.windowsMsi.url}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full gradient-brand-button py-3.5 rounded-xl font-semibold uppercase text-xs flex items-center justify-center space-x-2 text-white block text-center"
            >
              <Download className="w-4 h-4" />
              <span>Download MSI Installer (.msi · {downloads.windowsMsi.size})</span>
            </a>

            <a 
              href={downloads.windowsExe.url}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full bg-white/5 hover:bg-white/10 border border-white/15 py-3.5 rounded-xl font-semibold uppercase text-xs flex items-center justify-center space-x-2 text-zinc-300 hover:text-white block text-center transition-colors"
            >
              <FileCode className="w-4 h-4 text-orange-400" />
              <span>Standalone Executable (.exe · {downloads.windowsExe.size})</span>
            </a>
          </div>

          {/* System requirements */}
          <div className="pt-4 border-t border-white/5 space-y-2 text-xs text-zinc-400">
            <div className="font-semibold text-zinc-300">System Requirements:</div>
            <ul className="list-disc list-inside space-y-1 text-zinc-400">
              <li>Windows 10 (Build 19041+) or Windows 11</li>
              <li>x64 Architecture (64-bit)</li>
              <li>Local Wi-Fi network interface (Private profile recommended)</li>
              <li>RAM: &lt; 25MB footprint</li>
            </ul>
          </div>
        </GlassCard>

        {/* Android Card */}
        <GlassCard className="p-8 space-y-6 border-orange-500/30">
          <div className="flex items-center space-x-4">
            <div className="w-14 h-14 rounded-2xl bg-orange-500/10 border border-orange-500/40 flex items-center justify-center text-brand-highlight shadow-glow-orange">
              <Smartphone className="w-7 h-7" />
            </div>
            <div>
              <h2 className="text-2xl font-orbitron font-bold text-white">Android Mobile Remote</h2>
              <p className="text-xs font-mono text-zinc-400">Android 10.0+ (API 29+)</p>
            </div>
          </div>

          <p className="text-zinc-300 text-sm leading-relaxed">
            The full-featured mobile client with Jetpack Compose Liquid Glass UI, high-precision touchpad, gyroscope air mouse, keyboard, media, and presentation deck.
          </p>

          <div className="space-y-3 pt-2">
            <a 
              href={downloads.androidApk.url}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full gradient-brand-button py-3.5 rounded-xl font-semibold uppercase text-xs flex items-center justify-center space-x-2 text-white block text-center"
            >
              <Download className="w-4 h-4" />
              <span>Download Direct APK (.apk · {downloads.androidApk.size})</span>
            </a>

            <button 
              onClick={() => alert("Google Play Store listing is currently in open testing review. Please download the direct signed APK above.")}
              className="w-full bg-white/5 hover:bg-white/10 border border-white/15 py-3.5 rounded-xl font-semibold uppercase text-xs flex items-center justify-center space-x-2 text-zinc-300 hover:text-white transition-colors"
            >
              <span>Get on Google Play (Pending Store Verification)</span>
            </button>
          </div>

          {/* Android requirements */}
          <div className="pt-4 border-t border-white/5 space-y-2 text-xs text-zinc-400">
            <div className="font-semibold text-zinc-300">Device Requirements:</div>
            <ul className="list-disc list-inside space-y-1 text-zinc-400">
              <li>Android 10 or newer</li>
              <li>Connected to the same Wi-Fi router as your PC</li>
              <li>Gyroscope & Accelerometer (required for Air Mouse mode)</li>
              <li>Vibration motor (for tactile haptic feedback)</li>
            </ul>
          </div>
        </GlassCard>
      </div>

      {/* Cryptographic SHA-256 Checksums */}
      <GlassCard className="p-6">
        <h3 className="text-lg font-orbitron font-bold text-white flex items-center space-x-2 mb-4">
          <ShieldCheck className="w-5 h-5 text-emerald-400" />
          <span>SHA-256 Checksums & Binary Verification</span>
        </h3>
        <p className="text-xs text-zinc-400 mb-4">
          Always verify the integrity of your downloaded files before installation using <code className="text-orange-400">CertUtil -hashfile [file] SHA256</code> or <code className="text-orange-400">sha256sum</code>.
        </p>

        <div className="space-y-3 font-mono text-xs">
          {Object.entries(downloads).map(([key, item]) => (
            <div key={key} className="p-3 rounded-xl bg-black/40 border border-white/5 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div className="overflow-hidden">
                <span className="text-orange-400 block font-semibold">{item.filename}</span>
                <span className="text-zinc-500 truncate block text-[11px]">{item.sha256}</span>
              </div>
              <button
                onClick={() => copyToClipboard(item.sha256, key)}
                className="px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 text-zinc-300 text-[11px] flex items-center space-x-1.5 self-start sm:self-auto shrink-0"
              >
                {copiedHash === key ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copiedHash === key ? "Copied" : "Copy Hash"}</span>
              </button>
            </div>
          ))}
        </div>
      </GlassCard>

      {/* Release Notes v1.0.0 */}
      <GlassCard className="p-6 space-y-4">
        <h3 className="text-lg font-orbitron font-bold text-white flex items-center space-x-2">
          <Sparkles className="w-5 h-5 text-brand-orange" />
          <span>Release Notes — Version 1.0.0 (Initial GA)</span>
        </h3>
        <div className="space-y-2 text-sm text-zinc-400">
          <p className="text-zinc-300 font-medium">New Features in this release:</p>
          <ul className="list-disc list-inside space-y-1 text-xs sm:text-sm pl-2">
            <li><strong className="text-white">Sub-15ms Motion Engine:</strong> Binary 13-byte mouse framing with hardware acceleration.</li>
            <li><strong className="text-white">Sensor Fusion Air Mouse:</strong> Hand gesture pointer using phone gyro + accelerometer.</li>
            <li><strong className="text-white">X25519 Ephemeral Handshake:</strong> Military-grade local LAN encryption with 6-digit PIN verification.</li>
            <li><strong className="text-white">Presentation & Media Suite:</strong> Dedicated slide advancing, timer, black screen, and Spotify/YouTube control.</li>
            <li><strong className="text-white">Native System Tray Host:</strong> Runs silently in Windows background with minimal RAM usage.</li>
            <li><strong className="text-white">Protected Power Gate:</strong> Confirmation-enforced shutdown, restart, and sleep commands.</li>
          </ul>
        </div>
      </GlassCard>
    </div>
  );
};
