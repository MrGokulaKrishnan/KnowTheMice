import React from 'react';

interface GlassCardProps {
  children: React.ReactNode;
  className?: string;
  hoverEffect?: boolean;
}

export const GlassCard: React.FC<GlassCardProps> = ({ 
  children, 
  className = '', 
  hoverEffect = true 
}) => {
  return (
    <div 
      className={`rounded-2xl ${
        hoverEffect ? 'glass-panel-interactive' : 'glass-panel'
      } p-6 ${className}`}
    >
      {children}
    </div>
  );
};

export const GradientButton: React.FC<React.ButtonHTMLAttributes<HTMLButtonElement>> = ({ 
  children, 
  className = '', 
  ...props 
}) => {
  return (
    <button
      className={`gradient-brand-button px-6 py-3 rounded-xl font-semibold text-white tracking-wide uppercase text-xs flex items-center justify-center space-x-2 transition-all disabled:opacity-50 ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};

export const GlassButton: React.FC<React.ButtonHTMLAttributes<HTMLButtonElement>> = ({ 
  children, 
  className = '', 
  ...props 
}) => {
  return (
    <button
      className={`bg-white/5 hover:bg-white/10 border border-orange-500/25 hover:border-orange-500/50 px-6 py-3 rounded-xl font-semibold text-zinc-200 hover:text-white tracking-wide uppercase text-xs flex items-center justify-center space-x-2 transition-all ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};

export const GlowText: React.FC<{ children: React.ReactNode; className?: string }> = ({ 
  children, 
  className = '' 
}) => {
  return (
    <span className={`gradient-orange-text drop-shadow-[0_0_20px_rgba(255,90,0,0.3)] ${className}`}>
      {children}
    </span>
  );
};

export const StatusBadge: React.FC<{ 
  status: 'online' | 'pairing' | 'offline' | 'secure'; 
  label?: string 
}> = ({ status, label }) => {
  const configs = {
    online: { color: 'bg-emerald-500', text: label || 'Host Online', ring: 'border-emerald-500/30' },
    pairing: { color: 'bg-amber-500', text: label || 'Pairing Active', ring: 'border-amber-500/30' },
    offline: { color: 'bg-zinc-600', text: label || 'Disconnected', ring: 'border-zinc-700' },
    secure: { color: 'bg-brand-orange', text: label || 'X25519 Encrypted', ring: 'border-orange-500/30' }
  };
  const c = configs[status];
  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-mono bg-white/5 border ${c.ring} text-zinc-200`}>
      <span className={`w-2 h-2 rounded-full mr-2 ${c.color} animate-pulse`} />
      {c.text}
    </span>
  );
};
