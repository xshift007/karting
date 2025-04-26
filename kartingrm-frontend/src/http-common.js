// src/http-common.js
import axios from 'axios'

/**
 * ▸ En desarrollo => sólo '/api' para que el proxy de Vite actúe
 * ▸ En producción  => VITE_API_BASE debe apuntar al backend público
 */
const isDev  = import.meta.env.DEV
const baseURL = isDev
  ? '/api'
  : import.meta.env.VITE_API_BASE || '/api'

const http = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' }
})

/* ------------------------------------------------------------------ */
/*  Interceptor global                                                */
/*  – Ignora cancelaciones explícitas (ERR_CANCELED)                  */
/*  – Emite un evento “httpError” sólo para errores REALES            */
/* ------------------------------------------------------------------ */
http.interceptors.response.use(
  response => response,
  error => {
    /* axios cancela peticiones con este flag/code ------------------- */
    const wasCanceled =
      axios.isCancel?.(error) ||
      error.code === 'ERR_CANCELED' ||
      error.message?.toLowerCase().includes('canceled')

    if (wasCanceled) {
      /* silenciamos cancelaciones: no alert, no console.error */
      return Promise.resolve({ __CANCELED__: true })
    }

    /* errores genuinos → aviso centralizado ------------------------ */
    const msg = error.response?.data?.message || error.message
    window.dispatchEvent(new CustomEvent('httpError', { detail: msg }))

    return Promise.reject(error)
  }
)

export default http
