import React from 'react';
import { ShieldCheck, Heart, Github, Terminal, AlertTriangle, WifiOff, Clock } from 'lucide-react';

interface FooterProps {
  setActivePage: (page: string) => void;
}

export const Footer: React.FC<FooterProps> = ({ setActivePage }) => {
  return (
    <footer className="bg-background-surface/80 backdrop-blur-xl mt-24">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10">
          {/* Col 1: Brand Info */}
          <div className="lg:col-span-2 space-y-4">
            <div className="flex items-center space-x-3">
              <div className="w-9 h-9 rounded-lg bg-black border border-orange-500/40 flex items-center justify-center p-0.5 shadow-glow-orange overflow-hidden">
                <img src="/logo.png" alt="Know The Mice Logo" className="w-full h-full object-contain rounded-md" />
              </div>
              <span className="font-orbitron font-bold text-xl tracking-wider text-white">
                KNOW THE MICE
              </span>
            </div>
            <p className="text-zinc-400 text-sm leading-relaxed max-w-sm">
              Your phone. Your computer. One powerful remote. High-precision wireless control over local Wi-Fi with end-to-end X25519 cryptography and zero cloud dependency.
            </p>
            <div className="flex items-center space-x-2 text-xs text-orange-400/90 font-mono pt-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              <span>Zero-Cloud · 100% Local LAN Control</span>
            </div>
          </div>

          {/* Col 2: Product & Features */}
          <div className="space-y-3">
            <h4 className="text-white text-xs font-semibold tracking-wider uppercase font-orbitron">Product</h4>
            <ul className="space-y-2 text-sm text-zinc-400">
              <li><button onClick={() => setActivePage('features')} className="hover:text-brand-highlight transition-colors">Touchpad & Gestures</button></li>
              <li><button onClick={() => setActivePage('features')} className="hover:text-brand-highlight transition-colors">Virtual Keyboard</button></li>
              <li><button onClick={() => setActivePage('features')} className="hover:text-brand-highlight transition-colors">Gyro Air Mouse</button></li>
              <li><button onClick={() => setActivePage('features')} className="hover:text-brand-highlight transition-colors">Media & Presentation</button></li>
              <li><button onClick={() => setActivePage('features')} className="hover:text-brand-highlight transition-colors">Safe App Launcher</button></li>
              <li><button onClick={() => setActivePage('download')} className="hover:text-brand-highlight transition-colors">Download Center</button></li>
            </ul>
          </div>

          {/* Col 3: Resources & Docs */}
          <div className="space-y-3">
            <h4 className="text-white text-xs font-semibold tracking-wider uppercase font-orbitron">Resources</h4>
            <ul className="space-y-2 text-sm text-zinc-400">
              <li><button onClick={() => setActivePage('docs')} className="hover:text-brand-highlight transition-colors">Documentation</button></li>
              <li><button onClick={() => setActivePage('security')} className="hover:text-brand-highlight transition-colors">Security Architecture</button></li>
              <li><button onClick={() => setActivePage('help')} className="hover:text-brand-highlight transition-colors">Troubleshooting Guide</button></li>
              <li><button onClick={() => setActivePage('about')} className="hover:text-brand-highlight transition-colors">About the Project</button></li>
              <li><button onClick={() => setActivePage('contact')} className="hover:text-brand-highlight transition-colors">Contact Support</button></li>
            </ul>
          </div>

          {/* Col 4: Legal & System States */}
          <div className="space-y-3">
            <h4 className="text-white text-xs font-semibold tracking-wider uppercase font-orbitron">Legal & Status</h4>
            <ul className="space-y-2 text-sm text-zinc-400">
              <li><button onClick={() => setActivePage('privacy')} className="hover:text-brand-highlight transition-colors">Privacy Policy</button></li>
              <li><button onClick={() => setActivePage('terms')} className="hover:text-brand-highlight transition-colors">Terms of Service</button></li>
              <li><button onClick={() => setActivePage('cookies')} className="hover:text-brand-highlight transition-colors">Cookie Policy</button></li>
              <li><button onClick={() => setActivePage('accessibility')} className="hover:text-brand-highlight transition-colors">Accessibility Statement</button></li>
              <li><button onClick={() => setActivePage('security-policy')} className="hover:text-brand-highlight transition-colors">Security Disclosure</button></li>
              <li className="pt-2">
                <span className="text-xs text-zinc-500 uppercase tracking-wider block font-semibold mb-1">UX States:</span>
                <div className="flex flex-wrap gap-2 text-xs">
                  <button onClick={() => setActivePage('state-expired')} className="text-orange-400/80 hover:text-orange-300">Pairing Expired</button>
                  <span>·</span>
                  <button onClick={() => setActivePage('state-offline')} className="text-orange-400/80 hover:text-orange-300">Offline</button>
                  <span>·</span>
                  <button onClick={() => setActivePage('state-404')} className="text-orange-400/80 hover:text-orange-300">404</button>
                </div>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="mt-12 pt-8 flex flex-col sm:flex-row items-center justify-between text-xs text-zinc-400 gap-4">
          <p>© 2026 Know The Mice Ecosystem. All rights reserved. Open-standard local control.</p>
          <div className="flex items-center space-x-4">
            <a
              href="https://github.com/MrGokulaKrishnan/KnowTheMice"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center space-x-1.5 text-zinc-400 hover:text-white transition-colors"
            >
              <Github className="w-4 h-4" />
              <span>GitHub</span>
            </a>
            <span className="text-zinc-600">|</span>
            <span className="inline-flex items-center text-emerald-400">
              <span className="w-2 h-2 rounded-full bg-emerald-400 mr-2 animate-pulse"></span>
              Protocol v1.0.0 Active
            </span>
          </div>
        </div>
      </div>
    </footer>
  );
};
