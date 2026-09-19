import React, { useState } from 'react';
import { BookOpen, Terminal, Network, Shield, Cpu, Sliders, Smartphone, Monitor } from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

export const DocsPage: React.FC = () => {
  const [activeSection, setActiveSection] = useState('getting-started');

  const sections = [
    { id: 'getting-started', label: '1. Getting Started' },
    { id: 'windows-host', label: '2. Windows Host Setup' },
    { id: 'android-app', label: '3. Android Client Setup' },
    { id: 'air-mouse', label: '4. Air Mouse Calibration' },
    { id: 'network-firewall', label: '5. Firewall & Network Ports' },
    { id: 'protocol-spec', label: '6. Protocol Architecture' },
  ];

  return (
    <div className="space-y-12 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
      <div className="text-center max-w-3xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
          Developer & User Manual
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          Documentation <GlowText>& Guides</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Everything you need to install, configure, calibrate, and troubleshoot Know The Mice.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        {/* Sidebar */}
        <div className="space-y-2">
          <div className="p-4 rounded-2xl glass-panel space-y-1">
            <span className="text-xs font-mono text-zinc-500 uppercase tracking-wider block px-3 py-1 font-semibold">
              Sections
            </span>
            {sections.map((s) => (
              <button
                key={s.id}
                onClick={() => setActiveSection(s.id)}
                className={`w-full text-left px-3 py-2 rounded-xl text-sm font-medium transition-all ${
                  activeSection === s.id
                    ? 'text-brand-highlight bg-orange-500/10 border border-orange-500/30'
                    : 'text-zinc-400 hover:text-white hover:bg-white/5'
                }`}
              >
                {s.label}
              </button>
            ))}
          </div>
        </div>

        {/* Content Area */}
        <div className="lg:col-span-3 space-y-6">
          {activeSection === 'getting-started' && (
            <GlassCard className="p-8 space-y-6">
              <h2 className="text-2xl font-orbitron font-bold text-white">Getting Started with Know The Mice</h2>
              <p className="text-zinc-300 text-sm leading-relaxed">
                Know The Mice is designed for seamless zero-configuration local networking. To begin, both your Windows computer and your Android smartphone must be connected to the same local Wi-Fi router or access point.
              </p>
              <div className="p-4 rounded-xl bg-orange-500/10 border border-orange-500/30 text-xs text-orange-300 space-y-1 font-mono">
                <p><strong>Important Note on Guest Networks:</strong></p>
                <p>Ensure your Wi-Fi router does not have "Client Isolation" or "AP Isolation" enabled (often enabled by default on Hotel and Guest Wi-Fi networks), as this prevents devices on the same Wi-Fi from talking to each other.</p>
              </div>
              <div className="space-y-4 text-sm text-zinc-400">
                <h3 className="font-semibold text-white font-orbitron">Prerequisites:</h3>
                <ul className="list-disc list-inside space-y-2 pl-2">
                  <li>Windows 10 / 11 PC (64-bit) connected to Wi-Fi or Ethernet.</li>
                  <li>Android 10+ smartphone with Wi-Fi enabled.</li>
                  <li>Less than 30 seconds for initial pairing verification.</li>
                </ul>
              </div>
            </GlassCard>
          )}

          {activeSection === 'windows-host' && (
            <GlassCard className="p-8 space-y-6">
              <h2 className="text-2xl font-orbitron font-bold text-white">Windows Desktop Host Setup</h2>
              <p className="text-zinc-300 text-sm leading-relaxed">
                The Windows application runs as a lightweight native background daemon. Once installed, an icon appears in your Windows system tray.
              </p>
              <div className="space-y-3">
                <h3 className="font-semibold text-white font-orbitron text-sm">Installation Options:</h3>
                <div className="p-4 rounded-xl bg-black/40 border border-white/5 space-y-2">
                  <p className="text-xs font-mono text-orange-400">Option 1: MSI Package (Recommended)</p>
                  <p className="text-xs text-zinc-400">Run <code className="text-white">KnowTheMice-Setup-v1.0.0-win-x64.msi</code>. It registers Start Menu shortcuts, sets up uninstallation entries, and pre-configures the local Windows Firewall rule for port 52841.</p>
                </div>
                <div className="p-4 rounded-xl bg-black/40 border border-white/5 space-y-2">
                  <p className="text-xs font-mono text-orange-400">Option 2: Standalone Portable Executable</p>
                  <p className="text-xs text-zinc-400">Double click <code className="text-white">KnowTheMice-v1.0.0-Standalone-win-x64.exe</code>. Requires no administrator rights and leaves no registry footprints.</p>
                </div>
              </div>
            </GlassCard>
          )}

          {activeSection === 'android-app' && (
            <GlassCard className="p-8 space-y-6">
              <h2 className="text-2xl font-orbitron font-bold text-white">Android Client Setup</h2>
              <p className="text-zinc-300 text-sm leading-relaxed">
                Install <code className="text-orange-400">KnowTheMice-v1.0.0-release.apk</code> on your Android device. Follow the initial onboarding walkthrough:
              </p>
              <ol className="list-decimal list-inside space-y-3 text-sm text-zinc-400 pl-2">
                <li>Grant <strong className="text-white">Nearby Devices</strong> permission so the app can scan the local Wi-Fi subnet.</li>
                <li>When your PC appears in the discovery radar, tap <strong className="text-white">Pair</strong>.</li>
                <li>Look at your Windows monitor for the 6-digit numeric PIN code.</li>
                <li>Type the 6-digit code into your phone to generate and store your cryptographic pairing key.</li>
              </ol>
            </GlassCard>
          )}

          {activeSection === 'air-mouse' && (
            <GlassCard className="p-8 space-y-6">
              <h2 className="text-2xl font-orbitron font-bold text-white">Air Mouse Calibration & Use</h2>
              <p className="text-zinc-300 text-sm leading-relaxed">
                Air Mouse mode uses your phone's built-in IMU (Inertial Measurement Unit) to convert 3D hand rotations into 2D mouse cursor movements.
              </p>
              <div className="space-y-3 text-sm text-zinc-400">
                <h3 className="font-semibold text-white font-orbitron text-sm">How to Calibrate:</h3>
                <ul className="list-disc list-inside space-y-2 pl-2">
                  <li>Place the phone flat on a table for 2 seconds and tap <strong className="text-white">Calibrate</strong> in the settings menu. This zeros the gyroscope drift bias.</li>
                  <li>Press and hold the <strong className="text-white">Air Mouse</strong> trigger button on the RemotePad screen while pointing at your screen.</li>
                  <li>Releasing the button freezes the cursor in place, allowing you to reposition your arm comfortably (clutching mechanism).</li>
                </ul>
              </div>
            </GlassCard>
          )}

          {activeSection === 'network-firewall' && (
            <GlassCard className="p-8 space-y-6">
              <h2 className="text-2xl font-orbitron font-bold text-white">Firewall & Network Ports</h2>
              <p className="text-zinc-300 text-sm leading-relaxed">
                Know The Mice uses two dedicated ports on your local network:
              </p>
              <div className="space-y-3 font-mono text-xs">
                <div className="p-3 bg-black/40 rounded-xl border border-white/5">
                  <span className="text-orange-400 font-semibold">Port 52840 (UDP)</span>
                  <p className="text-zinc-400 mt-1">Used for local broadcast beaconing and mDNS service discovery.</p>
                </div>
                <div className="p-3 bg-black/40 rounded-xl border border-white/5">
                  <span className="text-orange-400 font-semibold">Port 52841 (TCP)</span>
                  <p className="text-zinc-400 mt-1">Used for the encrypted control channel, mouse coordinates, and keyboard scan codes.</p>
                </div>
              </div>
              <p className="text-xs text-zinc-400">
                To manually add a firewall exception in Windows PowerShell (Run as Administrator):
              </p>
              <pre className="p-3 bg-black/60 rounded-xl border border-orange-500/20 text-xs font-mono text-orange-300 overflow-x-auto">
                netsh advfirewall firewall add rule name="Know The Mice Host" dir=in action=allow protocol=TCP localport=52841 profile=private
              </pre>
            </GlassCard>
          )}

          {activeSection === 'protocol-spec' && (
            <GlassCard className="p-8 space-y-6">
              <h2 className="text-2xl font-orbitron font-bold text-white">Protocol Architecture & Framing</h2>
              <p className="text-zinc-300 text-sm leading-relaxed">
                For high-frequency input like mouse movement (which can generate 100+ events per second), JSON encoding adds substantial payload and serialization overhead. Know The Mice uses a hybrid protocol:
              </p>
              <ul className="list-disc list-inside space-y-2 text-sm text-zinc-400 pl-2">
                <li><strong className="text-white">Binary 13-Byte Framing:</strong> Magic byte, 32-bit float dx, 32-bit float dy, 16-bit sequence, 8-bit button flags, and 8-bit checksum.</li>
                <li><strong className="text-white">JSON Control Frames:</strong> Handshake, pairing, media, and presentation commands use structured JSON envelopes encrypted with AES-256-GCM.</li>
              </ul>
            </GlassCard>
          )}
        </div>
      </div>
    </div>
  );
};
