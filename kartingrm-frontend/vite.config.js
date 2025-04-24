// vite.config.js
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    /** Proxy para evitar CORS durante el desarrollo */
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    rollupOptions: {
      output: {
        /** bundle vendor global – React, MUI… */
        manualChunks(id) {
          if (id.includes('node_modules')) return 'vendor'
        }
      }
    }
  }
})
