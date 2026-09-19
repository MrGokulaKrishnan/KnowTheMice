import React from 'react';
import { 
  MousePointer, Radio, Keyboard, Music, Presentation, Rocket, 
  BatteryCharging, Zap, Shield, Cpu, Sliders, Smartphone, Copy
} from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

export const FeaturesPage: React.FC = () => {
  const featureList = [
    {
      icon: <MousePointer className="w-8 h-8 text-brand-orange" />,
      title: "Precision Multi-Touch Trackpad",
      tagline: "Fluid, desktop-grade touch response.",
      description: "Supports natural two-finger vertical and horizontal scrolling, tap-to-click, secondary two-finger right click, drag-and-drop hold locking, and custom mouse acceleration curves."
    },
    {
      icon: <Radio className="w-8 h-8 text-brand-orange" />,
      title: "6-DOF Gyroscope Air Mouse",
      tagline: "Point in the air like a laser pointer.",
      description: "Combines the Android gyroscope and accelerometer through a low-pass complementary sensor fusion filter. Hold the Air Mouse button and wave your phone to move the PC cursor smoothly from across the room."
    },
    {
      icon: <Keyboard className="w-8 h-8 text-brand-orange" />,
      title: "Full Physical & Voice Keyboard",
      tagline: "Every key from Escape to PrintScreen.",
      description: "Features a dynamic QWERTY keyboard with full modifier support (Ctrl, Alt, Shift, Win key), function keys (F1 through F12), arrow navigation pad, and integration with Android voice-to-text dictation."
    },
    {
      icon: <Music className="w-8 h-8 text-brand-orange" />,
      title: "Universal Media Controller",
      tagline: "Sit back and control your entertainment.",
      description: "Sends standard Windows multimedia scan codes for play, pause, next track, previous track, mute, and master volume control. Works seamlessly with Spotify, YouTube, Netflix, VLC, and Windows Media Player."
    },
    {
      icon: <Presentation className="w-8 h-8 text-brand-orange" />,
      title: "PowerPoint & Keynote Deck",
      tagline: "Master your presentations without bulky clickers.",
      description: "Equipped with large high-contrast slide advance buttons, an interactive elapsed-time presenter stopwatch, a Black Screen (B) privacy toggle, and haptic vibrations on every slide transition."
    },
    {
      icon: <Rocket className="w-8 h-8 text-brand-orange" />,
      title: "Safe Whitelisted App Launcher",
      tagline: "Launch your essential tools in one tap.",
      description: "The Windows host detects installed software and exposes safe AppUserModelIds. Browse favorites and recent apps directly on Android. Never exposes raw shell commands or scripts."
    },
    {
      icon: <Sliders className="w-8 h-8 text-brand-orange" />,
      title: "Customizable Quick Shortcuts",
      tagline: "Your favorite Windows key combos at your fingertips.",
      description: "Trigger complex macros with a single tap: Alt+Tab task switching, Win+D desktop toggle, Ctrl+Shift+Esc Task Manager, Win+L lock workstation, or create your own custom key combination."
    },
    {
      icon: <Copy className="w-8 h-8 text-brand-orange" />,
      title: "Opt-In Local Clipboard Bridge",
      tagline: "Paste text between devices privately.",
      description: "Disabled by default for maximum privacy. When explicitly toggled on, lets you securely sync clipboard text between your phone and PC over the local encrypted channel with zero cloud relay."
    },
    {
      icon: <Zap className="w-8 h-8 text-brand-orange" />,
      title: "Sub-15ms Local Wi-Fi Latency",
      tagline: "Engineered for real-time responsiveness.",
      description: "Packets are framed in a compact 13-byte binary payload with intelligent batching and throttling, avoiding TCP buffer bloat and ensuring your cursor feels wired to your fingertips."
    }
  ];

  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
      <div className="text-center max-w-3xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
          Deep Dive
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          Feature <GlowText>Ecosystem</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Every tool you need to control, present, type, and manage your Windows PC without ever leaving your seat.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {featureList.map((item, idx) => (
          <GlassCard key={idx} className="p-7 space-y-4">
            <div className="w-14 h-14 rounded-2xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center shadow-glow-orange">
              {item.icon}
            </div>
            <div>
              <h3 className="text-lg font-orbitron font-bold text-white">{item.title}</h3>
              <p className="text-xs font-mono text-orange-400/90 mt-1">{item.tagline}</p>
            </div>
            <p className="text-zinc-400 text-sm leading-relaxed">
              {item.description}
            </p>
          </GlassCard>
        ))}
      </div>
    </div>
  );
};
