import React, { useState } from 'react';
import { ShieldCheck, Lock, FileText, CheckCircle, Sliders } from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

export const PrivacyPolicyPage: React.FC = () => {
  return (
    <div className="space-y-12 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto text-zinc-300 text-sm leading-relaxed">
      <div className="text-center">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">Legal Compliance</span>
        <h1 className="text-4xl font-orbitron font-extrabold text-white mt-2">Privacy <GlowText>Policy</GlowText></h1>
        <p className="text-xs text-zinc-500 font-mono mt-2">Effective Date: September 2026</p>
      </div>

      <GlassCard className="p-8 space-y-6">
        <h2 className="text-xl font-orbitron font-bold text-white">1. The No-Cloud & Local-Only Philosophy</h2>
        <p>
          Know The Mice operates on an uncompromising local-first model. Core functionalities—including touchpad coordinates, keyboard scancodes, media commands, and pairing secrets—are strictly transmitted between your Android device and Windows PC across your local Wi-Fi subnet. No data is relayed through, processed by, or stored on external cloud servers.
        </p>

        <h2 className="text-xl font-orbitron font-bold text-white">2. What Data We Never Collect</h2>
        <ul className="list-disc list-inside space-y-1 text-zinc-400 pl-2">
          <li><strong>Keystrokes:</strong> We never log, record, or transmit what you type.</li>
          <li><strong>Clipboard Contents:</strong> Clipboard sync is opt-in and transmitted exclusively on your local network.</li>
          <li><strong>Screen Activity or Files:</strong> We do not capture or index your files.</li>
          <li><strong>Passwords & Master Keys:</strong> Ephemeral X25519 session keys never leave local device memory.</li>
        </ul>

        <h2 className="text-xl font-orbitron font-bold text-white">3. Local Device Storage</h2>
        <p>
          On Windows, paired device identities are saved via the Windows Data Protection API (DPAPI). On Android, tokens are secured within the Android Keystore.
        </p>

        <h2 className="text-xl font-orbitron font-bold text-white">4. Website Analytics</h2>
        <p>
          This website uses zero tracking pixels and zero cross-site marketing cookies.
        </p>
      </GlassCard>
    </div>
  );
};

export const TermsPage: React.FC = () => {
  return (
    <div className="space-y-12 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto text-zinc-300 text-sm leading-relaxed">
      <div className="text-center">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">Legal Agreement</span>
        <h1 className="text-4xl font-orbitron font-extrabold text-white mt-2">Terms of <GlowText>Service</GlowText></h1>
        <p className="text-xs text-zinc-500 font-mono mt-2">Last Updated: September 2026</p>
      </div>

      <GlassCard className="p-8 space-y-6">
        <h2 className="text-xl font-orbitron font-bold text-white">1. Software License & Permitted Use</h2>
        <p>
          Know The Mice grants you a personal, non-exclusive, revocable license to install and execute the Windows Host and Android Remote software on your personal devices.
        </p>

        <h2 className="text-xl font-orbitron font-bold text-white">2. Acceptable Use</h2>
        <p>
          You agree not to modify the Windows host to bypass safety confirmation gates or attempt unauthorized access on computer systems you do not own or have explicit administrative permission to control.
        </p>

        <h2 className="text-xl font-orbitron font-bold text-white">3. Disclaimer of Warranties</h2>
        <p>
          The software is provided "as is" without warranty of any kind. The creators are not liable for accidental inputs or shutdowns resulting from wireless interference or misconfiguration.
        </p>
      </GlassCard>
    </div>
  );
};

export const CookiesPage: React.FC = () => {
  const [pref, setPref] = useState({ essential: true, performance: false });

  return (
    <div className="space-y-12 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto text-zinc-300 text-sm leading-relaxed">
      <div className="text-center">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">Cookie Control</span>
        <h1 className="text-4xl font-orbitron font-extrabold text-white mt-2">Cookie <GlowText>Preferences</GlowText></h1>
      </div>

      <GlassCard className="p-8 space-y-6">
        <h2 className="text-xl font-orbitron font-bold text-white">Zero Tracking Commitment</h2>
        <p>
          We do not use advertising, marketing, or behavioral tracking cookies. Only essential localStorage state (like your UI preference) is maintained locally in your browser.
        </p>

        <div className="space-y-4 pt-4 border-t border-white/5">
          <div className="flex items-center justify-between p-4 bg-black/40 rounded-xl border border-white/5">
            <div>
              <span className="font-semibold text-white block">Strictly Necessary Storage</span>
              <span className="text-xs text-zinc-400">Maintains active page routing and theme preferences.</span>
            </div>
            <span className="text-xs font-mono text-emerald-400 font-bold uppercase">Always Active</span>
          </div>

          <div className="flex items-center justify-between p-4 bg-black/40 rounded-xl border border-white/5">
            <div>
              <span className="font-semibold text-white block">Anonymous Diagnostics (Opt-In)</span>
              <span className="text-xs text-zinc-400">Aggregated client load time metrics to optimize page speed.</span>
            </div>
            <input 
              type="checkbox" 
              checked={pref.performance}
              onChange={(e) => setPref({ ...pref, performance: e.target.checked })}
              className="accent-brand-orange w-5 h-5 cursor-pointer"
            />
          </div>
        </div>
      </GlassCard>
    </div>
  );
};

export const AccessibilityPage: React.FC = () => {
  return (
    <div className="space-y-12 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto text-zinc-300 text-sm leading-relaxed">
      <div className="text-center">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">Inclusive Design</span>
        <h1 className="text-4xl font-orbitron font-extrabold text-white mt-2">Accessibility <GlowText>Statement</GlowText></h1>
      </div>

      <GlassCard className="p-8 space-y-6">
        <p>
          Know The Mice is committed to ensuring full digital accessibility for all users, including individuals with motor disabilities, low vision, and sensory sensitivities.
        </p>
        <ul className="list-disc list-inside space-y-2 text-zinc-400 pl-2">
          <li><strong>High Contrast Ratio:</strong> All text elements achieve WCAG AAA contrast ratio (&gt;7:1) against AMOLED black backdrops.</li>
          <li><strong>Large Touch Targets:</strong> Touchpad triggers and presentation buttons exceed the 48x48dp touch recommendation.</li>
          <li><strong>Screen Reader Labels:</strong> All interactive icons and buttons carry descriptive aria-label tags.</li>
          <li><strong>Reduced Motion:</strong> Respects the system-level prefers-reduced-motion media query.</li>
          <li><strong>Haptic & Audio Independence:</strong> Visual status indicators are paired with color-independent icons and shapes.</li>
        </ul>
      </GlassCard>
    </div>
  );
};
