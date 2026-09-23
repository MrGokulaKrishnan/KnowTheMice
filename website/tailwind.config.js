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
      backgroundImage: {
        'gradient-primary': 'linear-gradient(135deg, #FF8A00 0%, #FF5A00 50%, #D93600 100%)',
        'gradient-secondary': 'linear-gradient(135deg, rgba(255, 138, 0, 0.16) 0%, rgba(217, 54, 0, 0.05) 100%)',
        'gradient-accent': 'linear-gradient(135deg, #FFA333 0%, #FF5A00 100%)',
        'gradient-text': 'linear-gradient(135deg, #FF8A00 0%, #FF5A00 50%, #D93600 100%)',
        'gradient-border': 'linear-gradient(135deg, rgba(255, 138, 0, 0.45) 0%, rgba(255, 90, 0, 0.20) 50%, rgba(217, 54, 0, 0.35) 100%)',
        'gradient-surface': 'linear-gradient(180deg, rgba(20, 20, 20, 0.70) 0%, rgba(10, 10, 10, 0.90) 100%)',
        'gradient-hover': 'linear-gradient(135deg, #FFA033 0%, #FF6B1A 50%, #E64000 100%)',
        'gradient-active': 'linear-gradient(135deg, #E65000 0%, #CC4500 50%, #B32D00 100%)',
        'gradient-disabled': 'linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, rgba(255, 255, 255, 0.03) 100%)',
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
