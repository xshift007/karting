// kartingrm-frontend/src/http-common.js
import axios from 'axios'

// En dev apuntará a /api y Vite lo reenvía.
// En prod (build) React se sirve desde Spring (8080) y /api está en la misma URL.
const base = '/api'

const http = axios.create({
  baseURL  : base,
  headers  : { 'Content-Type': 'application/json' },
  timeout  : 10000            // opcional
})

export default http
