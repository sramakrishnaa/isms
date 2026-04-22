/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  corePlugins: {
    preflight: false, // Disable Preflight to prevent conflicts
  },
  theme: {
    extend: {},
  },
  plugins: [],
}