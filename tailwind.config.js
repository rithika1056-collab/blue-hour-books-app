/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        midnight: {
          50: '#eef2fb',
          100: '#d6deef',
          200: '#aebde0',
          300: '#7e96cc',
          400: '#5470b3',
          500: '#3a5396',
          600: '#2c3f78',
          700: '#233260',
          800: '#1b2749',
          900: '#141d36',
          950: '#0c1224',
        },
        lavender: {
          50: '#f6f4ff',
          100: '#ece8ff',
          200: '#d9d2ff',
          300: '#bbaeff',
          400: '#9884fb',
          500: '#7d61f4',
          600: '#6a45e8',
          700: '#5a36d4',
          800: '#4a2eae',
          900: '#3e2a8a',
        },
        cream: {
          50: '#fdfbf7',
          100: '#faf5ec',
          200: '#f4e9d4',
          300: '#ecd9b3',
          400: '#e2c389',
          500: '#d9aa63',
        },
        gold: {
          400: '#f5c451',
          500: '#e6a823',
          600: '#c7871a',
        },
      },
      fontFamily: {
        serif: ['"Cormorant Garamond"', 'Georgia', 'serif'],
        sans: ['"Plus Jakarta Sans"', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        soft: '0 10px 40px -12px rgba(12, 18, 36, 0.45)',
        glow: '0 0 24px -4px rgba(152, 132, 251, 0.5)',
        card: '0 4px 24px -8px rgba(12, 18, 36, 0.35)',
      },
      backgroundImage: {
        'blue-hour': 'radial-gradient(ellipse at top, #233260 0%, #1b2749 35%, #0c1224 100%)',
        'star-field': 'radial-gradient(2px 2px at 20% 30%, rgba(255,255,255,0.6), transparent), radial-gradient(1px 1px at 60% 70%, rgba(255,255,255,0.4), transparent), radial-gradient(1.5px 1.5px at 80% 20%, rgba(255,255,255,0.5), transparent), radial-gradient(1px 1px at 40% 85%, rgba(255,255,255,0.3), transparent)',
      },
      keyframes: {
        twinkle: {
          '0%, 100%': { opacity: '0.3' },
          '50%': { opacity: '0.9' },
        },
        'fade-in': {
          '0%': { opacity: '0', transform: 'translateY(8px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        'scale-in': {
          '0%': { opacity: '0', transform: 'scale(0.96)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
        'slide-up': {
          '0%': { opacity: '0', transform: 'translateY(100%)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
      },
      animation: {
        twinkle: 'twinkle 4s ease-in-out infinite',
        'fade-in': 'fade-in 0.4s ease-out',
        'scale-in': 'scale-in 0.2s ease-out',
        'slide-up': 'slide-up 0.3s ease-out',
      },
    },
  },
  plugins: [],
};
