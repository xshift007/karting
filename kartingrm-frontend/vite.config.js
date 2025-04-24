import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // necesario para que el history fallback de React Router funcione
    historyApiFallback: true
  },
  build: {
    rollupOptions: {
      output: {
        // separa un bundle ‘vendor’ con React y MUI
        manualChunks: {
          vendor: ['react', 'react-dom', '@mui/material']
        }
      }
    }
  }
})
