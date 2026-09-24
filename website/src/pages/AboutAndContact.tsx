import React, { useState } from 'react';
import { Users, Award, Shield, Heart, Terminal, Sparkles, Check } from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

export const AboutPage: React.FC = () => {
  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto">
      <div className="text-center max-w-3xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
          Our Mission
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          About <GlowText>Know The Mice</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Built by engineers who believe essential computer control utilities should be fast, private, and independent of remote cloud servers.
        </p>
      </div>

      <GlassCard className="p-8 sm:p-10 space-y-6">
        <h2 className="text-2xl font-orbitron font-bold text-white">Why We Built Know The Mice</h2>
        <p className="text-zinc-300 text-sm sm:text-base leading-relaxed">
          Traditional remote control utilities have increasingly turned into bloated cloud services requiring accounts, persistent internet connections, paid subscriptions, and opaque telemetry that tracks your every action.
        </p>
        <p className="text-zinc-300 text-sm sm:text-base leading-relaxed">
          We built <strong>Know The Mice</strong> to bring back the purity of local network tools. When you want to move your cursor or skip a music track from your couch, you shouldn't have to route that request across a data center 3,000 miles away. Your phone talks directly to your computer over local Wi-Fi with sub-15ms response times and cryptographic privacy.
        </p>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-4 border-t border-white/5">
          <div className="p-4 rounded-xl bg-black/40 border border-white/5 text-center">
            <span className="text-2xl font-orbitron font-bold text-brand-orange">100%</span>
            <span className="text-xs text-zinc-400 block mt-1">Local & Offline</span>
          </div>
          <div className="p-4 rounded-xl bg-black/40 border border-white/5 text-center">
            <span className="text-2xl font-orbitron font-bold text-brand-orange">&lt;15ms</span>
            <span className="text-xs text-zinc-400 block mt-1">Average Latency</span>
          </div>
          <div className="p-4 rounded-xl bg-black/40 border border-white/5 text-center">
            <span className="text-2xl font-orbitron font-bold text-brand-orange">0</span>
            <span className="text-xs text-zinc-400 block mt-1">Cloud Dependencies</span>
          </div>
        </div>
      </GlassCard>
    </div>
  );
};

export const ContactPage: React.FC = () => {
  const [submitted, setSubmitted] = useState(false);

  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-3xl mx-auto">
      <div className="text-center max-w-2xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-brand-orange">
          Direct Line
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          Contact <GlowText>Engineering</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Have feedback, feature requests, or discovered a network bug? Let us know.
        </p>
      </div>

      <GlassCard className="p-8 space-y-6">
        {submitted ? (
          <div className="p-6 text-center space-y-3">
            <div className="w-12 h-12 rounded-full bg-emerald-500/20 text-emerald-400 mx-auto flex items-center justify-center">
              <Check className="w-6 h-6 text-emerald-400" />
            </div>
            <h3 className="text-xl font-orbitron font-bold text-white">Message Received</h3>
            <p className="text-zinc-400 text-sm">Thank you for helping us improve Know The Mice!</p>
          </div>
        ) : (
          <form 
            onSubmit={(e) => { e.preventDefault(); setSubmitted(true); }}
            className="space-y-4"
          >
            <div>
              <label className="block text-xs font-mono text-zinc-300 uppercase mb-1">Your Name</label>
              <input 
                required
                type="text" 
                placeholder="Alex Mercer"
                className="w-full px-4 py-3 rounded-xl bg-black/50 border border-white/10 text-white text-sm focus:outline-none focus:border-orange-500/60"
              />
            </div>
            <div>
              <label className="block text-xs font-mono text-zinc-300 uppercase mb-1">Your Email</label>
              <input 
                required
                type="email" 
                placeholder="alex@example.com"
                className="w-full px-4 py-3 rounded-xl bg-black/50 border border-white/10 text-white text-sm focus:outline-none focus:border-orange-500/60"
              />
            </div>
            <div>
              <label className="block text-xs font-mono text-zinc-300 uppercase mb-1">Inquiry Topic</label>
              <select className="w-full px-4 py-3 rounded-xl bg-black/50 border border-white/10 text-white text-sm focus:outline-none focus:border-orange-500/60">
                <option>General Feedback</option>
                <option>Windows Host Network Issue</option>
                <option>Android App / Sensor Calibration</option>
                <option>Security Vulnerability Report</option>
              </select>
            </div>
            <div>
              <label className="block text-xs font-mono text-zinc-300 uppercase mb-1">Message</label>
              <textarea 
                required
                rows={4}
                placeholder="Describe your question or diagnostic details..."
                className="w-full px-4 py-3 rounded-xl bg-black/50 border border-white/10 text-white text-sm focus:outline-none focus:border-orange-500/60"
              ></textarea>
            </div>
            <button 
              type="submit"
              className="w-full gradient-brand-button py-3.5 rounded-xl font-semibold uppercase text-xs text-white tracking-wider"
            >
              Send Message
            </button>
          </form>
        )}
      </GlassCard>
    </div>
  );
};
