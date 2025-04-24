import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Para que History API fallback redirija al index.html
    historyApiFallback: true
  },
  build: {
    rollupOptions: {
      output: {
        // Crea un bundle vendor separado con React y MUI
        manualChunks: {
          vendor: ['react', 'react-dom', '@mui/material', '@mui/icons-material']
        }
      }
    }
  }
})
