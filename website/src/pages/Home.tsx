import React, { useState } from 'react';
import { 
  MousePointer, Download, ShieldCheck, Zap, Lock, Wifi, Monitor, 
  Smartphone, ChevronRight, HelpCircle, Layers, PlayCircle, EyeOff, Radio
} from 'lucide-react';
import { PhoneMockup } from '../components/PhoneMockup';
import { GlassCard, GradientButton, GlassButton, GlowText, StatusBadge } from '../components/UIComponents';

interface HomeProps {
  setActivePage: (page: string) => void;
}

export const Home: React.FC<HomeProps> = ({ setActivePage }) => {
  const [openFaq, setOpenFaq] = useState<number | null>(null);

  const faqs = [
    {
      q: "Do I need an internet connection or cloud account to use Know The Mice?",
      a: "No. Know The Mice is built strictly on the No-Cloud Principle. All discovery, pairing, and input control happen entirely within your local Wi-Fi subnet. No internet access, account registration, or subscription is ever required."
    },
    {
      q: "How does the pairing process work?",
      a: "When you open the Android app, it automatically discovers your Windows PC via local UDP broadcast. Tapping 'Pair' triggers a 6-digit one-time code on your Windows screen. Entering this code on your phone generates an X25519 cryptographic keypair stored in Windows Credential Manager and Android Keystore."
    },
    {
      q: "What is the interaction latency?",
      a: "Under typical home Wi-Fi 5 or Wi-Fi 6 conditions, relative mouse movements achieve under 15ms latency thanks to our optimized 13-byte binary UDP/TCP stream framing."
    },
    {
      q: "Can anyone execute commands or scripts on my PC remotely?",
      a: "Never. The Windows Host has zero shell execution capabilities. Cmd.exe, PowerShell, and terminal piping are completely excluded from the protocol. Power controls (shutdown, sleep) are explicitly gated behind confirmation prompts."
    },
    {
      q: "Does Air Mouse work with my phone?",
      a: "Yes! If your Android phone has an accelerometer and gyroscope, the built-in Sensor Fusion engine translates your hand movements into smooth cursor control with dead-zone noise filtering."
    }
  ];

  return (
    <div className="space-y-24 pt-28 pb-16">
      {/* 1. Hero Section */}
      <section className="relative px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto text-center">
        <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-white/5 border border-orange-500/30 mb-8 backdrop-blur-md">
          <StatusBadge status="secure" label="v1.0.0 Ready" />
          <span className="text-xs text-zinc-300 font-mono">Zero Cloud · Local-First Control</span>
        </div>

        <h1 className="text-4xl sm:text-6xl lg:text-7xl font-orbitron font-extrabold tracking-tight max-w-5xl mx-auto leading-tight text-white">
          Your Phone. Your Computer. <br />
          <GlowText>One Powerful Remote.</GlowText>
        </h1>

        <p className="mt-6 text-lg sm:text-xl text-zinc-400 max-w-2xl mx-auto leading-relaxed">
          Turn your Android phone into a lightning-fast, secure wireless mouse, trackpad, keyboard, and presentation remote for Windows. Control without cables.
        </p>

        {/* CTA Button Group */}
        <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
          <GradientButton 
            onClick={() => setActivePage('download')}
            className="w-full sm:w-auto text-sm px-8 py-4"
          >
            <Download className="w-4 h-4" />
            <span>Download for Windows (.exe / .msi)</span>
          </GradientButton>

          <GlassButton 
            onClick={() => setActivePage('download')}
            className="w-full sm:w-auto text-sm px-8 py-4"
          >
            <Smartphone className="w-4 h-4 text-brand-orange" />
            <span>Get Android App (.apk)</span>
          </GlassButton>
        </div>

        {/* Sub-tagline pills */}
        <div className="mt-8 flex flex-wrap items-center justify-center gap-4 text-xs font-mono text-zinc-500">
          <span>• Precision in your hands</span>
          <span>• Private by design</span>
          <span>• Fast by nature</span>
          <span>• Move. Type. Play. Present.</span>
        </div>

        {/* Interactive Phone-to-PC Live Simulation */}
        <div className="mt-14">
          <PhoneMockup />
        </div>
      </section>

      {/* 2. 3-Step "How It Works" Section */}
      <section className="px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
            Simple 30-Second Setup
          </span>
          <h2 className="text-3xl sm:text-4xl font-orbitron font-bold text-white mt-2">
            How It Works
          </h2>
          <p className="text-zinc-400 text-sm mt-3 max-w-lg mx-auto">
            No accounts to create. No credit cards. No cloud servers. Connect your devices in three effortless steps.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <GlassCard className="relative overflow-hidden group">
            <div className="text-5xl font-orbitron font-extrabold text-orange-500/15 mb-4 group-hover:text-orange-500/30 transition-colors">
              01
            </div>
            <h3 className="text-xl font-orbitron font-semibold text-white mb-2">
              Launch on Windows
            </h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Install the lightweight Windows Host. It docks quietly into your system tray and starts broadcasting its availability securely on your local Wi-Fi.
            </p>
          </GlassCard>

          <GlassCard className="relative overflow-hidden group">
            <div className="text-5xl font-orbitron font-extrabold text-orange-500/15 mb-4 group-hover:text-orange-500/30 transition-colors">
              02
            </div>
            <h3 className="text-xl font-orbitron font-semibold text-white mb-2">
              Auto-Discover on Android
            </h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Open the Android app. Your PC appears automatically in seconds. Tap "Pair" to request an encrypted handshake without manual IP entry.
            </p>
          </GlassCard>

          <GlassCard className="relative overflow-hidden group">
            <div className="text-5xl font-orbitron font-extrabold text-orange-500/15 mb-4 group-hover:text-orange-500/30 transition-colors">
              03
            </div>
            <h3 className="text-xl font-orbitron font-semibold text-white mb-2">
              Verify PIN & Take Control
            </h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Enter the 6-digit code shown on your monitor. Both devices establish a trusted cryptographic session. Enjoy instant, low-latency control!
            </p>
          </GlassCard>
        </div>
      </section>

      {/* 3. Core Feature Grid */}
      <section className="px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
            Comprehensive Suite
          </span>
          <h2 className="text-3xl sm:text-4xl font-orbitron font-bold text-white mt-2">
            Engineered for Precision & Flow
          </h2>
          <p className="text-zinc-400 text-sm mt-3 max-w-lg mx-auto">
            From fluid multi-finger trackpad gestures to presentation slides and system controls.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <GlassCard>
            <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight mb-5">
              <MousePointer className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-orbitron font-semibold text-white mb-2">Fluid Liquid Touchpad</h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Two-finger natural scrolling, tap-to-click, drag-and-drop lock, and customizable cursor acceleration with haptic click vibration.
            </p>
          </GlassCard>

          <GlassCard>
            <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight mb-5">
              <Radio className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-orbitron font-semibold text-white mb-2">Gyroscope Air Mouse</h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Hold the Air Mouse button and point your phone like a laser wand. Perfect for couch browsing, living room TVs, and lecture halls.
            </p>
          </GlassCard>

          <GlassCard>
            <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight mb-5">
              <Layers className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-orbitron font-semibold text-white mb-2">Full Remote Keyboard</h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Complete layout with Ctrl, Alt, Shift, Windows key, function keys (F1-F12), dynamic arrow pads, and Android voice dictation.
            </p>
          </GlassCard>

          <GlassCard>
            <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight mb-5">
              <PlayCircle className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-orbitron font-semibold text-white mb-2">Media & Volume Deck</h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Native media keys for Spotify, YouTube, VLC, and Netflix. Adjust master volume, mute, skip tracks, and toggle fullscreen effortlessly.
            </p>
          </GlassCard>

          <GlassCard>
            <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight mb-5">
              <Monitor className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-orbitron font-semibold text-white mb-2">Presentation Remote</h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Advance slides in PowerPoint, Keynote, and Google Slides. Built-in stopwatch timer, black screen toggle (B), and vibration feedback.
            </p>
          </GlassCard>

          <GlassCard>
            <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-highlight mb-5">
              <Lock className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-orbitron font-semibold text-white mb-2">Confirmation-Gated Power</h3>
            <p className="text-zinc-400 text-sm leading-relaxed">
              Lock PC, Sleep, Restart, and Shutdown are strictly protected by a two-step confirmation modal naming the target computer. Never accidental.
            </p>
          </GlassCard>
        </div>
      </section>

      {/* 4. Security & Privacy Philosophy Banner */}
      <section className="px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <div className="rounded-3xl glass-panel p-8 sm:p-12 border border-orange-500/30 relative overflow-hidden">
          <div className="max-w-3xl space-y-6">
            <div className="inline-flex items-center space-x-2 text-xs font-mono text-emerald-400">
              <ShieldCheck className="w-4 h-4" />
              <span>THE NO-CLOUD COMMITMENT</span>
            </div>
            <h2 className="text-3xl sm:text-4xl font-orbitron font-bold text-white">
              Private by Design. Fast by Nature.
            </h2>
            <p className="text-zinc-400 text-base leading-relaxed">
              Unlike cloud-tethered remote control software, Know The Mice never routes your mouse coordinates, key presses, or clipboard across third-party servers. Your keystrokes never touch a cloud database. No analytics on typed content. Your network stays yours.
            </p>
            <div className="flex flex-wrap gap-4 pt-2">
              <button 
                onClick={() => setActivePage('security')}
                className="gradient-brand-button px-6 py-3 rounded-xl text-xs font-semibold uppercase text-white"
              >
                Read Security Whitepaper
              </button>
              <button 
                onClick={() => setActivePage('docs')}
                className="bg-white/5 hover:bg-white/10 border border-white/10 px-6 py-3 rounded-xl text-xs font-semibold uppercase text-zinc-300 hover:text-white"
              >
                View Architecture Docs
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* 5. FAQ Accordion */}
      <section className="px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-orbitron font-bold text-white">Frequently Asked Questions</h2>
          <p className="text-zinc-400 text-sm mt-2">Everything you need to know about setup and security.</p>
        </div>

        <div className="space-y-4">
          {faqs.map((faq, i) => (
            <div 
              key={i} 
              className="rounded-2xl glass-panel overflow-hidden border border-orange-500/20"
            >
              <button
                onClick={() => setOpenFaq(openFaq === i ? null : i)}
                className="w-full text-left px-6 py-5 flex items-center justify-between focus:outline-none"
              >
                <span className="font-orbitron text-sm font-semibold text-zinc-200">
                  {faq.q}
                </span>
                <ChevronRight 
                  className={`w-4 h-4 text-orange-400 transition-transform duration-200 ${
                    openFaq === i ? 'rotate-90' : ''
                  }`} 
                />
              </button>
              {openFaq === i && (
                <div className="px-6 pb-5 text-sm text-zinc-400 leading-relaxed border-t border-white/5 pt-3">
                  {faq.a}
                </div>
              )}
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};
