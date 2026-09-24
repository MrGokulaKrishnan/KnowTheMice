import React, { useState } from 'react';
import { MousePointer, Download, Shield, BookOpen, Menu, X, Cpu, Smartphone, Github } from 'lucide-react';

interface NavbarProps {
  activePage: string;
  setActivePage: (page: string) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ activePage, setActivePage }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navItems = [
    { id: 'home', label: 'Home' },
    { id: 'features', label: 'Features' },
    { id: 'download', label: 'Download' },
    { id: 'security', label: 'Security' },
    { id: 'docs', label: 'Docs' },
    { id: 'help', label: 'Help' },
  ];

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 px-4 sm:px-6 lg:px-8 pt-4">
      <div className="max-w-7xl mx-auto rounded-2xl glass-panel px-4 py-3 flex items-center justify-between">
        {/* Brand Monogram & Name */}
        <button 
          onClick={() => setActivePage('home')}
          className="flex items-center space-x-3 group text-left focus:outline-none"
        >
          <div className="w-10 h-10 rounded-xl bg-black flex items-center justify-center p-0.5 overflow-hidden">
            <img src="/logo.png" alt="Know The Mice Logo" className="w-full h-full object-contain" />
          </div>
          <div>
            <span className="font-orbitron font-bold text-lg tracking-wider text-white group-hover:text-brand-highlight transition-colors">
              KNOW THE MICE
            </span>
            <span className="hidden sm:block text-[10px] text-zinc-400 tracking-widest uppercase">
              Low-Latency Remote Control
            </span>
          </div>
        </button>

        {/* Desktop Nav Links */}
        <div className="hidden md:flex items-center space-x-1 lg:space-x-2">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActivePage(item.id)}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-all ${
                activePage === item.id
                  ? 'text-brand-highlight bg-orange-500/10 border border-orange-500/30 shadow-sm'
                  : 'text-zinc-400 hover:text-white hover:bg-white/5'
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>

        {/* Action Button */}
        <div className="hidden sm:flex items-center space-x-3">
          <a
            href="https://github.com/MrGokulaKrishnan/KnowTheMice"
            target="_blank"
            rel="noopener noreferrer"
            className="p-2 rounded-xl text-zinc-400 hover:text-white hover:bg-white/5 border border-white/10 transition-colors"
            title="GitHub Repository"
          >
            <Github className="w-4 h-4" />
          </a>
          <button
            onClick={() => setActivePage('download')}
            className="gradient-brand-button px-4 py-2 rounded-xl text-xs font-semibold tracking-wider uppercase text-white flex items-center space-x-2"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Get Apps</span>
          </button>
        </div>

        {/* Mobile menu trigger */}
        <div className="md:hidden flex items-center">
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 text-zinc-400 hover:text-white focus:outline-none"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden mt-2 rounded-2xl glass-panel p-4 space-y-2 border border-orange-500/20 shadow-2xl">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => {
                setActivePage(item.id);
                setMobileMenuOpen(false);
              }}
              className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-all ${
                activePage === item.id
                  ? 'text-brand-highlight bg-orange-500/10 border border-orange-500/30'
                  : 'text-zinc-300 hover:bg-white/5'
              }`}
            >
              {item.label}
            </button>
          ))}
          <button
            onClick={() => {
              setActivePage('download');
              setMobileMenuOpen(false);
            }}
            className="w-full gradient-brand-button mt-3 py-2.5 rounded-xl text-xs font-semibold tracking-wider uppercase text-white flex items-center justify-center space-x-2"
          >
            <Download className="w-4 h-4" />
            <span>Download for Windows & Android</span>
          </button>
        </div>
      )}
    </nav>
  );
};
