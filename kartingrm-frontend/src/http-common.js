// src/http-common.js
import axios from 'axios'

/**
 *   ▸ En desarrollo => sólo '/api' para que el proxy de Vite actúe
 *   ▸ En producción => VITE_API_BASE debe apuntar al backend público
 */
const isDev = import.meta.env.DEV
const baseURL = isDev
  ? '/api'
  : import.meta.env.VITE_API_BASE || '/api'

const http = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' }
})

// Interceptor global para mostrar errores sin romper toda la app
http.interceptors.response.use(
  response => response,
  error => {
    const msg = error.response?.data?.message || error.message
    window.dispatchEvent(new CustomEvent('httpError', { detail: msg }))
    return Promise.reject(error)
  }
)

export default http
