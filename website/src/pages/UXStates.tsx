import React from 'react';
import { 
  Clock, WifiOff, AlertTriangle, RefreshCw, SearchX, CheckCircle2, 
  Home, ArrowLeft, ShieldAlert 
} from 'lucide-react';
import { GlassCard, GradientButton, GlassButton, GlowText } from '../components/UIComponents';

interface StateProps {
  setActivePage: (page: string) => void;
}

export const PairingExpiredState: React.FC<StateProps> = ({ setActivePage }) => {
  return (
    <div className="pt-32 pb-24 px-4 max-w-lg mx-auto text-center space-y-6">
      <div className="w-16 h-16 rounded-2xl bg-amber-500/10 border border-amber-500/40 text-amber-400 mx-auto flex items-center justify-center shadow-glow-orange">
        <Clock className="w-8 h-8" />
      </div>
      <div>
        <span className="text-xs font-mono text-amber-400 font-semibold uppercase tracking-widest">Time Expired</span>
        <h1 className="text-3xl font-orbitron font-extrabold text-white mt-1">Pairing Session Expired</h1>
        <p className="text-zinc-400 text-sm mt-3 leading-relaxed">
          For your security, one-time 6-digit PINs expire after 60 seconds to prevent unauthorized brute-force attempts on the local network.
        </p>
      </div>
      <GlassCard className="p-6 text-left space-y-3 text-xs text-zinc-300">
        <p className="font-semibold text-white">What should I do?</p>
        <ol className="list-decimal list-inside space-y-1.5 text-zinc-400 pl-1">
          <li>On your Windows computer, open the Know The Mice dashboard.</li>
          <li>Click <strong className="text-white">"Pair New Device"</strong> to generate a fresh code.</li>
          <li>Re-enter the code on your phone within 60 seconds.</li>
        </ol>
      </GlassCard>
      <div className="flex flex-col sm:flex-row gap-3 justify-center">
        <GradientButton onClick={() => setActivePage('home')}>
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Try Pairing Again</span>
        </GradientButton>
        <GlassButton onClick={() => setActivePage('help')}>
          <span>Help & Diagnostics</span>
        </GlassButton>
      </div>
    </div>
  );
};

export const OfflineState: React.FC<StateProps> = ({ setActivePage }) => {
  return (
    <div className="pt-32 pb-24 px-4 max-w-lg mx-auto text-center space-y-6">
      <div className="w-16 h-16 rounded-2xl bg-red-500/10 border border-red-500/40 text-red-400 mx-auto flex items-center justify-center shadow-lg">
        <WifiOff className="w-8 h-8" />
      </div>
      <div>
        <span className="text-xs font-mono text-red-400 font-semibold uppercase tracking-widest">Connection Lost</span>
        <h1 className="text-3xl font-orbitron font-extrabold text-white mt-1">Local Network Disconnected</h1>
        <p className="text-zinc-400 text-sm mt-3 leading-relaxed">
          Could not communicate with the Windows Host. Check that both devices are connected to the same Wi-Fi router.
        </p>
      </div>
      <GlassCard className="p-5 text-left space-y-2 text-xs text-zinc-300">
        <p className="font-semibold text-white">Troubleshooting Steps:</p>
        <ul className="list-disc list-inside space-y-1 text-zinc-400">
          <li>Check that your PC has not entered Sleep mode.</li>
          <li>Verify your phone is not on cellular mobile data.</li>
          <li>Confirm Windows Firewall is set to "Private Network".</li>
        </ul>
      </GlassCard>
      <GradientButton onClick={() => setActivePage('home')}>
        <RefreshCw className="w-3.5 h-3.5" />
        <span>Scan Local Network</span>
      </GradientButton>
    </div>
  );
};

export const NotFoundState: React.FC<StateProps> = ({ setActivePage }) => {
  return (
    <div className="pt-32 pb-24 px-4 max-w-lg mx-auto text-center space-y-6">
      <div className="text-7xl font-orbitron font-black text-orange-500/20">404</div>
      <div>
        <h1 className="text-3xl font-orbitron font-bold text-white">Page Not Found</h1>
        <p className="text-zinc-400 text-sm mt-3">The documentation or page you are looking for does not exist or has been relocated.</p>
      </div>
      <GradientButton onClick={() => setActivePage('home')}>
        <Home className="w-3.5 h-3.5" />
        <span>Return to Home</span>
      </GradientButton>
    </div>
  );
};

export const ErrorState: React.FC<StateProps> = ({ setActivePage }) => {
  return (
    <div className="pt-32 pb-24 px-4 max-w-lg mx-auto text-center space-y-6">
      <div className="w-16 h-16 rounded-2xl bg-red-500/10 border border-red-500/40 text-red-400 mx-auto flex items-center justify-center">
        <AlertTriangle className="w-8 h-8" />
      </div>
      <div>
        <h1 className="text-3xl font-orbitron font-bold text-white">Application Exception (500)</h1>
        <p className="text-zinc-400 text-sm mt-3">An unexpected client error occurred. Diagnostics have been sanitized with no sensitive data logged.</p>
      </div>
      <GradientButton onClick={() => setActivePage('home')}>
        <RefreshCw className="w-3.5 h-3.5" />
        <span>Reload Experience</span>
      </GradientButton>
    </div>
  );
};
