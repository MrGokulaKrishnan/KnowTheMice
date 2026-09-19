/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        background: {
          amoled: "#000000",
          surface: "#050505",
          card: "#080808",
          cardHover: "#0E0E0E"
        },
        brand: {
          orange: "#FF5A00",
          highlight: "#FF8A00",
          deep: "#D93600",
          amber: "#FFB347",
          glow: "rgba(255, 90, 0, 0.25)"
        },
        glass: {
          panel: "rgba(255, 255, 255, 0.04)",
          panelHover: "rgba(255, 255, 255, 0.07)",
          border: "rgba(255, 165, 80, 0.28)",
          borderHover: "rgba(255, 165, 80, 0.50)",
          highlight: "rgba(255, 255, 255, 0.16)"
        }
      },
      boxShadow: {
        'glass': 'inset 0 1px 0 rgba(255, 255, 255, 0.14), 0 20px 60px rgba(0, 0, 0, 0.40), 0 0 35px rgba(255, 90, 0, 0.10)',
        'glass-hover': 'inset 0 1px 0 rgba(255, 255, 255, 0.22), 0 24px 70px rgba(0, 0, 0, 0.50), 0 0 45px rgba(255, 90, 0, 0.22)',
        'glow-orange': '0 0 25px rgba(255, 90, 0, 0.35)',
        'glow-orange-lg': '0 0 50px rgba(255, 90, 0, 0.50)',
      },
      fontFamily: {
        display: ['"Plus Jakarta Sans"', 'Inter', 'system-ui', 'sans-serif'],
        sans: ['Inter', '"Plus Jakarta Sans"', 'system-ui', '-apple-system', 'sans-serif'],
        orbitron: ['"Plus Jakarta Sans"', 'Inter', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace']
      }
    },
  },
  plugins: [],
}
