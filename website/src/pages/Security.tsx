import React from 'react';
import { 
  ShieldCheck, Lock, Key, ServerOff, EyeOff, AlertOctagon, 
  CheckCircle2, Terminal, RefreshCw 
} from 'lucide-react';
import { GlassCard, GlowText } from '../components/UIComponents';

export const SecurityPage: React.FC = () => {
  return (
    <div className="space-y-16 pt-28 pb-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
      <div className="text-center max-w-3xl mx-auto">
        <span className="text-xs font-mono font-semibold uppercase tracking-widest text-emerald-400">
          Zero-Trust Local Cryptography
        </span>
        <h1 className="text-4xl sm:text-5xl font-orbitron font-extrabold text-white mt-2">
          Security & <GlowText>Privacy</GlowText>
        </h1>
        <p className="text-zinc-400 text-sm sm:text-base mt-4">
          Know The Mice was built with a strict threat model: local Wi-Fi networks must not be trusted implicitly. Every session is cryptographically isolated and authenticated.
        </p>
      </div>

      {/* Pillars Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <GlassCard className="p-7 space-y-4 border-emerald-500/30">
          <div className="w-12 h-12 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
            <ServerOff className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-orbitron font-bold text-white">The No-Cloud Guarantee</h3>
          <p className="text-zinc-400 text-sm leading-relaxed">
            There are no intermediate servers, no cloud relays, and no user accounts. Your devices communicate exclusively point-to-point within your home or office router. If the internet goes down, Know The Mice continues to work flawlessly.
          </p>
        </GlassCard>

        <GlassCard className="p-7 space-y-4 border-orange-500/30">
          <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-orange">
            <Key className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-orbitron font-bold text-white">X25519 & AES-256-GCM</h3>
          <p className="text-zinc-400 text-sm leading-relaxed">
            Device pairing uses Diffie-Hellman elliptic curve key exchange (X25519) salted with a 6-digit one-time code. Transport frames are sealed using AES-256-GCM authenticated encryption, preventing packet tampering and eavesdropping.
          </p>
        </GlassCard>

        <GlassCard className="p-7 space-y-4 border-emerald-500/30">
          <div className="w-12 h-12 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
            <Lock className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-orbitron font-bold text-white">Replay & Flood Protection</h3>
          <p className="text-zinc-400 text-sm leading-relaxed">
            Every network message contains a strictly monotonic 32-bit sequence counter. Stale or duplicate packets outside the sliding replay window are dropped immediately. 3 incorrect PIN attempts trigger a 5-minute lockout.
          </p>
        </GlassCard>

        <GlassCard className="p-7 space-y-4 border-orange-500/30">
          <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-orange">
            <AlertOctagon className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-orbitron font-bold text-white">Zero Shell / Cmd Access</h3>
          <p className="text-zinc-400 text-sm leading-relaxed">
            The Windows Host explicitly forbids shell commands. There is no code path that allows running <code className="text-orange-400">cmd.exe</code>, <code className="text-orange-400">powershell.exe</code>, or arbitrary binaries. The application launcher only triggers user-approved Start Menu programs.
          </p>
        </GlassCard>

        <GlassCard className="p-7 space-y-4 border-emerald-500/30">
          <div className="w-12 h-12 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
            <EyeOff className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-orbitron font-bold text-white">No Keystroke / Clipboard Logging</h3>
          <p className="text-zinc-400 text-sm leading-relaxed">
            Diagnostics logs record only connection states, pairing milestones, and network latency metrics. Keystrokes, clipboard text, passwords, and cryptographic keys are strictly excluded from logging at the compiler level.
          </p>
        </GlassCard>

        <GlassCard className="p-7 space-y-4 border-orange-500/30">
          <div className="w-12 h-12 rounded-xl bg-orange-500/10 border border-orange-500/30 flex items-center justify-center text-brand-orange">
            <CheckCircle2 className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-orbitron font-bold text-white">OS Hardware Vaults</h3>
          <p className="text-zinc-400 text-sm leading-relaxed">
            Trusted device pairing tokens are never stored in plaintext config files. On Windows, credentials are encrypted with DPAPI (Data Protection API). On Android, identities are sealed within the Android hardware Keystore.
          </p>
        </GlassCard>
      </div>

      {/* Security Testing Verification */}
      <GlassCard className="p-8 space-y-6">
        <h2 className="text-2xl font-orbitron font-bold text-white flex items-center space-x-3">
          <ShieldCheck className="w-7 h-7 text-emerald-400" />
          <span>Security Hardening Matrix</span>
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs font-mono text-zinc-300">
          <div className="p-3 bg-black/40 rounded-xl border border-white/5 flex items-center space-x-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Unauthorized Pairing Attempts: Gated by 6-digit PIN + 60s TTL</span>
          </div>
          <div className="p-3 bg-black/40 rounded-xl border border-white/5 flex items-center space-x-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Replay Attack Resistance: 128-packet sliding sequence cache</span>
          </div>
          <div className="p-3 bg-black/40 rounded-xl border border-white/5 flex items-center space-x-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Malformed Packet Handling: Strict bounds checking & exception drop</span>
          </div>
          <div className="p-3 bg-black/40 rounded-xl border border-white/5 flex items-center space-x-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Command Injection Prevention: No shell pipe or process spawn APIs</span>
          </div>
          <div className="p-3 bg-black/40 rounded-xl border border-white/5 flex items-center space-x-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Destructive Action Safeguards: 2-step confirmation with PC name match</span>
          </div>
          <div className="p-3 bg-black/40 rounded-xl border border-white/5 flex items-center space-x-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Firewall Isolation: Restricts binding strictly to Private Network profile</span>
          </div>
        </div>
      </GlassCard>
    </div>
  );
};
