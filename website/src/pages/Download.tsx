import React, { useState, useEffect } from 'react';
import { 
  Download, Monitor, Smartphone, CheckCircle, Copy, Check, 
  FileCode, Terminal, AlertTriangle, ShieldCheck, Sparkles, 
  RefreshCw, Headphones, ArrowRight, Laptop, CheckCircle2
} from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

interface DownloadInfo {
  version: string;
  name: string;
  filename: string;
  url: string;
  size: string;
  sha256: string;
  releaseDate: string;
  platform: string;
  requirements: string[];
  description: string;
}

interface DownloadsConfig {
  windows: DownloadInfo;
  android: DownloadInfo;
}

const DEFAULT_CONFIG: DownloadsConfig = {
  windows: {
    version: "1.1.0",
    name: "Know The Mice for Windows",
    filename: "KnowTheMice-Setup-x64.exe",
    url: "/downloads/windows/KnowTheMice-Setup-x64.exe",
    size: "922 KB",
    sha256: "7589C7A1B1814BA508B9005D24621F6AD6A5DCDEEBE7A5EB18E6F24BFCE36B86",
    releaseDate: "September 19, 2026",
    platform: "Windows 10 / 11 (64-bit)",
    requirements: [
      "Windows 10 (Build 19041+) or Windows 11",
      "x64 Architecture (64-bit)",
      "Local Wi-Fi network interface",
      "RAM: < 25MB footprint"
    ],
    description: "Native Windows Host installer with auto-start, system tray service, firewall rules, and desktop shortcuts."
  },
  android: {
    version: "1.1.0",
    name: "Know The Mice for Android",
    filename: "KnowTheMice-Android.apk",
    url: "/downloads/android/KnowTheMice-Android.apk",
    size: "2.11 MB",
    sha256: "18E65EAC732CC1A4FD55061400136C8E20E8F98DEB739162B9D404AB7DC4F25F",
    releaseDate: "September 19, 2026",
    platform: "Android 10.0+ (API 29+)",
    requirements: [
      "Android 10 or newer (API 29+)",
      "Connected to the same local Wi-Fi as your PC",
      "Gyroscope & Accelerometer (for Air Mouse mode)",
      "Vibration motor (for tactile haptic feedback)"
    ],
    description: "Touchpad with multi-touch gestures, Gyroscope Air Mouse with complementary filter, full virtual keyboard, universal media deck, and presentation controls."
  }
};

export const DownloadPage: React.FC = () => {
  const [copiedHash, setCopiedHash] = useState<string | null>(null);
  const [downloadToast, setDownloadToast] = useState<{ filename: string; size: string } | null>(null);
  const [config, setConfig] = useState<DownloadsConfig>(DEFAULT_CONFIG);
  const [downloadError, setDownloadError] = useState<string | null>(null);
  
  // Smart platform detection
  const [detectedPlatform, setDetectedPlatform] = useState<'windows' | 'android' | 'other'>('windows');
  const [filterPlatform, setFilterPlatform] = useState<'all' | 'windows' | 'android'>('all');

  useEffect(() => {
    // Detect user OS
    if (typeof navigator !== 'undefined') {
      const ua = navigator.userAgent.toLowerCase();
      if (/android/i.test(ua)) {
        setDetectedPlatform('android');
      } else if (/windows/i.test(ua)) {
        setDetectedPlatform('windows');
      } else {
        setDetectedPlatform('other');
      }
    }

    // Fetch live downloads.json
    fetch('/downloads.json')
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load downloads.json");
        return res.json();
      })
      .then((data: DownloadsConfig) => {
        if (data.windows && data.android) {
          setConfig(data);
        }
      })
      .catch(() => {
        // Fallback to default bundled configuration
      });
  }, []);

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedHash(id);
    setTimeout(() => setCopiedHash(null), 2000);
  };

  const handleDownloadClick = (filename: string, size: string, url: string) => {
    setDownloadError(null);
    setDownloadToast({ filename, size });
    setTimeout(() => {
      setDownloadToast(null);
    }, 5000);
  };

  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
      {/* Toast Notification */}
      {downloadToast && (
        <div className="fixed bottom-6 right-6 z-50 animate-bounce-short">
          <div className="glass-panel border-orange-500/50 p-4 rounded-2xl shadow-2xl flex items-center space-x-3 bg-black/90 backdrop-blur-xl border">
            <div className="w-9 h-9 rounded-xl bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center text-emerald-400">
              <CheckCircle2 className="w-5 h-5" />
            </div>
            <div>
              <p className="text-xs font-semibold text-white">Download Started</p>
              <p className="text-[11px] font-mono text-zinc-400">
                {downloadToast.filename} ({downloadToast.size})
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Page Title */}
      <div className="text-center max-w-3xl mx-auto space-y-3">
        <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-orange-500/10 border border-orange-500/30 text-brand-highlight text-xs font-mono font-medium tracking-wide">
          <Sparkles className="w-3.5 h-3.5" />
          <span>Official First-Party Release</span>
        </div>
        <h1 className="text-4xl sm:text-5xl font-display font-extrabold text-white tracking-tight">
          DOWNLOAD <GlowText>KNOW THE MICE</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base max-w-xl mx-auto">
          Get the latest version of Know The Mice for your devices. Fast, direct browser downloads with zero cloud tracking.
        </p>
      </div>

      {/* Smart Device Detection Banner */}
      {detectedPlatform !== 'other' && (
        <div className="max-w-3xl mx-auto glass-panel p-4 rounded-2xl border border-orange-500/30 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center space-x-3 text-left">
            <div className="w-10 h-10 rounded-xl bg-orange-500/15 border border-orange-500/30 flex items-center justify-center text-orange-400">
              {detectedPlatform === 'windows' ? <Laptop className="w-5 h-5" /> : <Smartphone className="w-5 h-5" />}
            </div>
            <div>
              <p className="text-xs font-mono text-orange-400 uppercase tracking-wider font-semibold">
                Detected Platform
              </p>
              <p className="text-sm font-semibold text-white">
                {detectedPlatform === 'windows' ? 'Windows 10 / 11 PC' : 'Android Mobile Device'}
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-3">
            <a
              href={detectedPlatform === 'windows' ? config.windows.url : config.android.url}
              download={detectedPlatform === 'windows' ? config.windows.filename : config.android.filename}
              onClick={() => handleDownloadClick(
                detectedPlatform === 'windows' ? config.windows.filename : config.android.filename,
                detectedPlatform === 'windows' ? config.windows.size : config.android.size,
                detectedPlatform === 'windows' ? config.windows.url : config.android.url
              )}
              className="gradient-brand-button px-5 py-2.5 rounded-xl text-xs font-bold uppercase tracking-wider text-white flex items-center space-x-2"
            >
              <Download className="w-4 h-4" />
              <span>
                {detectedPlatform === 'windows' ? 'Download for Windows' : 'Download APK'}
              </span>
            </a>
          </div>
        </div>
      )}

      {/* Error state if file unavailable */}
      {downloadError && (
        <div className="max-w-2xl mx-auto p-4 rounded-xl bg-red-500/10 border border-red-500/30 flex items-center justify-between text-xs text-red-200">
          <div className="flex items-center space-x-2">
            <AlertTriangle className="w-4 h-4 text-red-400 shrink-0" />
            <span>{downloadError}</span>
          </div>
          <div className="flex space-x-2">
            <button
              onClick={() => setDownloadError(null)}
              className="px-2.5 py-1 bg-white/10 hover:bg-white/20 rounded font-semibold text-white"
            >
              Try Again
            </button>
          </div>
        </div>
      )}

      {/* Platform Switcher */}
      <div className="flex justify-center items-center space-x-2">
        <span className="text-xs text-zinc-400 mr-2 font-mono">Filter by Platform:</span>
        <button
          onClick={() => setFilterPlatform('all')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
            filterPlatform === 'all'
              ? 'bg-orange-500/20 text-brand-highlight border border-orange-500/40'
              : 'text-zinc-400 hover:text-white hover:bg-white/5 border border-transparent'
          }`}
        >
          All Downloads
        </button>
        <button
          onClick={() => setFilterPlatform('windows')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
            filterPlatform === 'windows'
              ? 'bg-orange-500/20 text-brand-highlight border border-orange-500/40'
              : 'text-zinc-400 hover:text-white hover:bg-white/5 border border-transparent'
          }`}
        >
          Windows
        </button>
        <button
          onClick={() => setFilterPlatform('android')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
            filterPlatform === 'android'
              ? 'bg-orange-500/20 text-brand-highlight border border-orange-500/40'
              : 'text-zinc-400 hover:text-white hover:bg-white/5 border border-transparent'
          }`}
        >
          Android
        </button>
      </div>

      {/* Main Download Cards Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Windows Card */}
        {(filterPlatform === 'all' || filterPlatform === 'windows') && (
          <GlassCard className="p-8 space-y-6 border-orange-500/30 flex flex-col justify-between">
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <div className="w-14 h-14 rounded-2xl bg-orange-500/10 border border-orange-500/40 flex items-center justify-center text-brand-highlight shadow-glow-orange">
                    <Monitor className="w-7 h-7" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-display font-bold text-white tracking-tight">WINDOWS</h2>
                    <p className="text-xs font-mono text-zinc-400">{config.windows.name}</p>
                  </div>
                </div>
                <span className="px-2.5 py-1 rounded-full bg-orange-500/10 border border-orange-500/30 text-orange-400 font-mono text-[11px] font-semibold">
                  v{config.windows.version}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3 p-3.5 rounded-xl bg-black/40 border border-white/5 text-xs font-mono">
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">Platform</span>
                  <span className="text-zinc-300 font-medium">{config.windows.platform}</span>
                </div>
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">File Size</span>
                  <span className="text-orange-400 font-bold">{config.windows.size}</span>
                </div>
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">File Format</span>
                  <span className="text-zinc-300 font-medium">Standalone .EXE</span>
                </div>
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">Release Date</span>
                  <span className="text-zinc-300 font-medium">{config.windows.releaseDate}</span>
                </div>
              </div>

              <p className="text-zinc-300 text-sm leading-relaxed">
                {config.windows.description}
              </p>

              <div className="space-y-3 pt-2">
                <a 
                  href={config.windows.url}
                  download={config.windows.filename}
                  onClick={() => handleDownloadClick(config.windows.filename, config.windows.size, config.windows.url)}
                  className="w-full gradient-brand-button py-3.5 rounded-xl font-bold uppercase text-xs flex items-center justify-center space-x-2 text-white block text-center cursor-pointer shadow-glow-orange"
                >
                  <Download className="w-4 h-4" />
                  <span>DOWNLOAD FOR WINDOWS</span>
                </a>
              </div>

              {/* SHA-256 Snippet */}
              <div className="p-3 rounded-xl bg-black/50 border border-white/5 space-y-1">
                <div className="flex items-center justify-between text-[11px] font-mono">
                  <span className="text-zinc-400 font-semibold flex items-center space-x-1">
                    <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
                    <span>SHA-256 Checksum</span>
                  </span>
                  <button
                    onClick={() => copyToClipboard(config.windows.sha256, 'windows')}
                    className="text-orange-400 hover:text-orange-300 flex items-center space-x-1 font-sans text-xs font-semibold"
                  >
                    {copiedHash === 'windows' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{copiedHash === 'windows' ? 'COPIED' : 'COPY'}</span>
                  </button>
                </div>
                <div className="font-mono text-[10.5px] text-zinc-500 break-all select-all">
                  {config.windows.sha256}
                </div>
              </div>
            </div>

            {/* System requirements */}
            <div className="pt-4 border-t border-white/5 space-y-2 text-xs text-zinc-400">
              <div className="font-semibold text-zinc-300">System Requirements:</div>
              <ul className="list-disc list-inside space-y-1 text-zinc-400">
                {config.windows.requirements.map((req, idx) => (
                  <li key={idx}>{req}</li>
                ))}
              </ul>
            </div>
          </GlassCard>
        )}

        {/* Android Card */}
        {(filterPlatform === 'all' || filterPlatform === 'android') && (
          <GlassCard className="p-8 space-y-6 border-orange-500/30 flex flex-col justify-between">
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <div className="w-14 h-14 rounded-2xl bg-orange-500/10 border border-orange-500/40 flex items-center justify-center text-brand-highlight shadow-glow-orange">
                    <Smartphone className="w-7 h-7" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-display font-bold text-white tracking-tight">ANDROID</h2>
                    <p className="text-xs font-mono text-zinc-400">{config.android.name}</p>
                  </div>
                </div>
                <span className="px-2.5 py-1 rounded-full bg-orange-500/10 border border-orange-500/30 text-orange-400 font-mono text-[11px] font-semibold">
                  v{config.android.version}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3 p-3.5 rounded-xl bg-black/40 border border-white/5 text-xs font-mono">
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">Platform</span>
                  <span className="text-zinc-300 font-medium">{config.android.platform}</span>
                </div>
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">File Size</span>
                  <span className="text-orange-400 font-bold">{config.android.size}</span>
                </div>
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">Package Type</span>
                  <span className="text-zinc-300 font-medium">Release Signed APK</span>
                </div>
                <div>
                  <span className="text-zinc-500 block text-[10px] uppercase">Release Date</span>
                  <span className="text-zinc-300 font-medium">{config.android.releaseDate}</span>
                </div>
              </div>

              <p className="text-zinc-300 text-sm leading-relaxed">
                {config.android.description}
              </p>

              <div className="space-y-3 pt-2">
                <a 
                  href={config.android.url}
                  download={config.android.filename}
                  onClick={() => handleDownloadClick(config.android.filename, config.android.size, config.android.url)}
                  className="w-full gradient-brand-button py-3.5 rounded-xl font-bold uppercase text-xs flex items-center justify-center space-x-2 text-white block text-center cursor-pointer shadow-glow-orange"
                >
                  <Download className="w-4 h-4" />
                  <span>DOWNLOAD APK</span>
                </a>
              </div>

              {/* SHA-256 Snippet */}
              <div className="p-3 rounded-xl bg-black/50 border border-white/5 space-y-1">
                <div className="flex items-center justify-between text-[11px] font-mono">
                  <span className="text-zinc-400 font-semibold flex items-center space-x-1">
                    <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
                    <span>SHA-256 Checksum</span>
                  </span>
                  <button
                    onClick={() => copyToClipboard(config.android.sha256, 'android')}
                    className="text-orange-400 hover:text-orange-300 flex items-center space-x-1 font-sans text-xs font-semibold"
                  >
                    {copiedHash === 'android' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{copiedHash === 'android' ? 'COPIED' : 'COPY'}</span>
                  </button>
                </div>
                <div className="font-mono text-[10.5px] text-zinc-500 break-all select-all">
                  {config.android.sha256}
                </div>
              </div>
            </div>

            {/* Android requirements */}
            <div className="pt-4 border-t border-white/5 space-y-2 text-xs text-zinc-400">
              <div className="font-semibold text-zinc-300">Device Requirements:</div>
              <ul className="list-disc list-inside space-y-1 text-zinc-400">
                {config.android.requirements.map((req, idx) => (
                  <li key={idx}>{req}</li>
                ))}
              </ul>
            </div>
          </GlassCard>
        )}
      </div>

      {/* Cryptographic SHA-256 Checksums Section */}
      <GlassCard className="p-6 space-y-4">
        <h3 className="text-lg font-display font-bold text-white flex items-center space-x-2">
          <ShieldCheck className="w-5 h-5 text-emerald-400" />
          <span>Cryptographic Integrity Verification</span>
        </h3>
        <p className="text-xs text-zinc-400">
          Before launching the installer or sideloading the APK, you can verify that the downloaded file has not been altered in transit.
        </p>

        <div className="space-y-3 font-mono text-xs">
          <div className="p-3.5 rounded-xl bg-black/40 border border-white/5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="overflow-hidden space-y-1">
              <div className="flex items-center space-x-2">
                <span className="text-orange-400 font-semibold">{config.windows.filename}</span>
                <span className="text-[10px] text-zinc-500 uppercase">({config.windows.size})</span>
              </div>
              <span className="text-zinc-500 truncate block text-[11px] select-all">{config.windows.sha256}</span>
            </div>
            <button
              onClick={() => copyToClipboard(config.windows.sha256, 'full-win')}
              className="px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 text-zinc-300 text-[11px] flex items-center space-x-1.5 self-start sm:self-auto shrink-0 transition-colors"
            >
              {copiedHash === 'full-win' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copiedHash === 'full-win' ? "Copied" : "Copy SHA-256"}</span>
            </button>
          </div>

          <div className="p-3.5 rounded-xl bg-black/40 border border-white/5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="overflow-hidden space-y-1">
              <div className="flex items-center space-x-2">
                <span className="text-orange-400 font-semibold">{config.android.filename}</span>
                <span className="text-[10px] text-zinc-500 uppercase">({config.android.size})</span>
              </div>
              <span className="text-zinc-500 truncate block text-[11px] select-all">{config.android.sha256}</span>
            </div>
            <button
              onClick={() => copyToClipboard(config.android.sha256, 'full-android')}
              className="px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 text-zinc-300 text-[11px] flex items-center space-x-1.5 self-start sm:self-auto shrink-0 transition-colors"
            >
              {copiedHash === 'full-android' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copiedHash === 'full-android' ? "Copied" : "Copy SHA-256"}</span>
            </button>
          </div>
        </div>

        <div className="p-3 rounded-xl bg-white/5 border border-white/10 text-[11px] text-zinc-400 font-mono">
          <p className="text-zinc-300 font-semibold mb-1">How to verify on Windows Terminal:</p>
          <code className="text-orange-400 select-all block">CertUtil -hashfile KnowTheMice-Setup-x64.exe SHA256</code>
        </div>
      </GlassCard>

      {/* Release Notes v1.0.0 */}
      <GlassCard className="p-6 space-y-4">
        <h3 className="text-lg font-display font-bold text-white flex items-center space-x-2">
          <Sparkles className="w-5 h-5 text-brand-orange" />
          <span>Release Notes — Version 1.0.0 (Initial GA)</span>
        </h3>
        <div className="space-y-2 text-sm text-zinc-400">
          <p className="text-zinc-300 font-medium">Core Highlights in Version 1.0.0:</p>
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
