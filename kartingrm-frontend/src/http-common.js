import axios from 'axios'

const server = import.meta.env.VITE_BACKEND_SERVER
const port   = import.meta.env.VITE_BACKEND_PORT

export default axios.create({
  baseURL: `http://${server}:${port}/api`,
  headers: { 'Content-Type':'application/json' }
})
