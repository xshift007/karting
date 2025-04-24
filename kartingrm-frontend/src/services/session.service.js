import http from '../http-common'

/**
 * Obtiene la disponibilidad de sesiones entre dos fechas
 * @param {string} from  — fecha ISO 'yyyy-MM-dd'
 * @param {string} to    — fecha ISO 'yyyy-MM-dd'
 */
const weekly = (from, to) =>
  http.get('/sessions/availability', {
    params: { from, to }
  })

export default { weekly }
