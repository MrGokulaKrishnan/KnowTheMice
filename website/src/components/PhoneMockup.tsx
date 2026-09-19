import React, { useState, useRef } from 'react';
import { MousePointer, Wifi, Volume2, Play, SkipForward, Power, ShieldCheck, Sparkles } from 'lucide-react';

export const PhoneMockup: React.FC = () => {
  // Cursor position on simulated PC screen (percentages 0-100)
  const [cursorPos, setCursorPos] = useState({ x: 50, y: 45 });
  const [isDragging, setIsDragging] = useState(false);
  const [lastTouch, setLastTouch] = useState<{ x: number; y: number } | null>(null);
  const [activeButton, setActiveButton] = useState<string | null>(null);
  const [clickEffect, setClickEffect] = useState(false);

  const handlePointerDown = (e: React.PointerEvent<HTMLDivElement>) => {
    setIsDragging(true);
    setLastTouch({ x: e.clientX, y: e.clientY });
    (e.target as HTMLElement).setPointerCapture(e.pointerId);
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLDivElement>) => {
    if (!isDragging || !lastTouch) return;
    const dx = (e.clientX - lastTouch.x) * 0.35;
    const dy = (e.clientY - lastTouch.y) * 0.35;

    setCursorPos((prev) => ({
      x: Math.max(5, Math.min(95, prev.x + dx)),
      y: Math.max(5, Math.min(92, prev.y + dy)),
    }));
    setLastTouch({ x: e.clientX, y: e.clientY });
  };

  const handlePointerUp = () => {
    setIsDragging(false);
    setLastTouch(null);
  };

  const triggerClick = (btn: string) => {
    setActiveButton(btn);
    setClickEffect(true);
    setTimeout(() => {
      setActiveButton(null);
      setClickEffect(false);
    }, 200);
  };

  return (
    <div className="relative w-full max-w-5xl mx-auto py-8 px-4 flex flex-col lg:flex-row items-center justify-center gap-8 lg:gap-12">
      {/* Background ambient radial flare */}
      <div className="absolute inset-0 bg-radial-gradient from-brand-orange/15 to-transparent blur-3xl pointer-events-none -z-10" />

      {/* 1. Simulated Android Phone Device */}
      <div className="relative w-72 sm:w-80 h-[540px] rounded-[44px] bg-[#0A0A0A] border-4 border-zinc-800 shadow-2xl p-3 flex flex-col justify-between select-none">
        {/* Outer Phone Bezel & Reflection */}
        <div className="absolute inset-0 rounded-[40px] pointer-events-none border border-orange-500/20 shadow-[inset_0_1px_2px_rgba(255,255,255,0.2)]" />
        
        {/* Dynamic Island / Speaker Pill */}
        <div className="w-24 h-4 bg-zinc-900 rounded-full mx-auto flex items-center justify-center space-x-1.5 z-20">
          <div className="w-2 h-2 rounded-full bg-zinc-950" />
          <div className="w-1.5 h-1.5 rounded-full bg-orange-500/80" />
        </div>

        {/* Mobile App UI Container */}
        <div className="relative flex-1 rounded-[32px] bg-background-amoled overflow-hidden flex flex-col justify-between p-3.5 border border-white/5 mt-1">
          {/* Header Bar */}
          <div className="flex items-center justify-between pb-2 border-b border-white/5 text-[11px] text-zinc-400">
            <div className="flex items-center space-x-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              <span className="font-medium text-white font-mono">KRISH-PC</span>
            </div>
            <div className="flex items-center space-x-2 text-orange-400 font-mono text-[10px]">
              <Wifi className="w-3 h-3" />
              <span>12ms</span>
            </div>
          </div>

          {/* Interactive Touchpad Surface */}
          <div
            onPointerDown={handlePointerDown}
            onPointerMove={handlePointerMove}
            onPointerUp={handlePointerUp}
            onPointerCancel={handlePointerUp}
            className={`relative flex-1 my-2 rounded-2xl glass-panel flex flex-col items-center justify-center cursor-grab active:cursor-grabbing transition-colors ${
              isDragging ? 'border-orange-500/70 shadow-glow-orange' : 'border-orange-500/25'
            }`}
          >
            {/* Ambient Watermark Logo */}
            <div className="w-16 h-16 opacity-10 pointer-events-none flex items-center justify-center">
              <img src="/logo_monogram.png" alt="" className="w-full h-full object-contain" />
            </div>
            <div className="absolute text-[11px] font-mono tracking-wider text-zinc-500 uppercase pointer-events-none">
              {isDragging ? 'Transmitting X/Y' : 'Swipe to Move Cursor'}
            </div>

            {/* Subtle Right Scroll Zone Indicator */}
            <div className="absolute right-1 top-4 bottom-4 w-1.5 rounded-full bg-white/5 border border-white/10 flex items-center justify-center">
              <div className="w-1 h-8 rounded-full bg-orange-500/40" />
            </div>
          </div>

          {/* Mouse Button Triggers */}
          <div className="grid grid-cols-2 gap-2 pt-1">
            <button
              onClick={() => triggerClick('left')}
              className={`py-2.5 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all ${
                activeButton === 'left'
                  ? 'bg-brand-orange text-white shadow-glow-orange scale-95'
                  : 'bg-white/5 border border-white/10 text-zinc-300 hover:bg-white/10'
              }`}
            >
              Left Click
            </button>
            <button
              onClick={() => triggerClick('right')}
              className={`py-2.5 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all ${
                activeButton === 'right'
                  ? 'bg-brand-orange text-white shadow-glow-orange scale-95'
                  : 'bg-white/5 border border-white/10 text-zinc-300 hover:bg-white/10'
              }`}
            >
              Right Click
            </button>
          </div>

          {/* Quick Mode Bar */}
          <div className="flex items-center justify-around pt-2 border-t border-white/5 text-zinc-400 text-[10px]">
            <span className="text-orange-400 font-medium">Touchpad</span>
            <span>Keyboard</span>
            <span>Media</span>
            <span>Air Mouse</span>
          </div>
        </div>
      </div>

      {/* 2. Interactive Wireless Bridge Wave Animation */}
      <div className="hidden lg:flex flex-col items-center justify-center space-y-2">
        <div className="w-16 h-0.5 bg-gradient-to-r from-orange-500 to-amber-400 relative overflow-hidden">
          <div className="absolute inset-0 bg-white/80 animate-[shimmer_1.5s_infinite] -translate-x-full" />
        </div>
        <span className="text-[10px] font-mono text-orange-400 tracking-wider">Wi-Fi &lt;15ms</span>
      </div>

      {/* 3. Simulated Windows 11 Desktop Screen */}
      <div className="relative w-full max-w-lg h-80 sm:h-[400px] rounded-2xl glass-panel p-4 flex flex-col justify-between overflow-hidden shadow-2xl border border-orange-500/20">
        {/* Desktop Titlebar */}
        <div className="flex items-center justify-between border-b border-white/10 pb-2.5">
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 rounded-full bg-red-500/80" />
            <div className="w-3 h-3 rounded-full bg-amber-500/80" />
            <div className="w-3 h-3 rounded-full bg-emerald-500/80" />
            <span className="text-xs font-mono text-zinc-400 pl-2">Windows 11 Display · 1920x1080</span>
          </div>
          <span className="text-[10px] font-mono text-emerald-400 flex items-center">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 mr-1.5 animate-pulse" />
            Host Connected
          </span>
        </div>

        {/* Windows Workspace Surface */}
        <div className="relative flex-1 my-3 bg-gradient-to-br from-zinc-950 to-zinc-900 rounded-xl border border-white/5 p-4 overflow-hidden">
          {/* Simulated App Windows */}
          <div className="w-48 h-32 rounded-lg bg-zinc-900/90 border border-white/10 p-3 shadow-lg flex flex-col justify-between">
            <div className="flex items-center justify-between text-[11px] text-zinc-300">
              <span className="font-semibold text-orange-400">Media Center</span>
              <span className="text-[10px] text-zinc-500">Spotify</span>
            </div>
            <div className="space-y-1">
              <div className="h-2 w-32 bg-white/10 rounded" />
              <div className="h-2 w-20 bg-white/5 rounded" />
            </div>
            <div className="flex items-center space-x-3 text-zinc-400">
              <Play className="w-3.5 h-3.5 text-white" />
              <SkipForward className="w-3 h-3" />
              <Volume2 className="w-3 h-3" />
            </div>
          </div>

          {/* Interactive Mouse Pointer Element */}
          <div
            className="absolute transition-transform duration-75 ease-out pointer-events-none"
            style={{
              left: `${cursorPos.x}%`,
              top: `${cursorPos.y}%`,
              transform: 'translate(-50%, -50%)',
            }}
          >
            <div className="relative">
              <MousePointer className="w-5 h-5 text-brand-highlight fill-brand-orange drop-shadow-[0_2px_8px_rgba(255,90,0,0.8)]" />
              {clickEffect && (
                <div className="absolute -inset-2 rounded-full border border-orange-400 animate-ping opacity-75" />
              )}
            </div>
          </div>
        </div>

        {/* Windows Taskbar */}
        <div className="h-10 rounded-xl bg-zinc-950/90 border border-white/10 px-3 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            {/* Windows Logo */}
            <div className="grid grid-cols-2 gap-0.5 w-3.5 h-3.5">
              <div className="bg-blue-400 rounded-sm" />
              <div className="bg-blue-400 rounded-sm" />
              <div className="bg-blue-400 rounded-sm" />
              <div className="bg-blue-400 rounded-sm" />
            </div>
            <div className="w-5 h-5 rounded bg-black border border-orange-500/40 flex items-center justify-center p-0.5 shadow-sm">
              <img src="/favicon-16x16.png" alt="" className="w-full h-full object-contain" />
            </div>
          </div>
          <div className="text-[11px] font-mono text-zinc-400">
            12:49 PM · 23H2
          </div>
        </div>
      </div>
    </div>
  );
};
