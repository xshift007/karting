// src/http-common.js
import axios from 'axios'

/**
 *   Puedes definir VITE_API_BASE=http://host:port/api
 *   (se usa en despliegue) o bien VITE_BACKEND_SERVER / VITE_BACKEND_PORT
 */
const baseURL =
  import.meta.env.VITE_API_BASE ??
  `http://${import.meta.env.VITE_BACKEND_SERVER}:${import.meta.env.VITE_BACKEND_PORT}/api`

export default axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' }
})
