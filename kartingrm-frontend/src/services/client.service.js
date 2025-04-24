import http from '../http-common'

/**
 * Devuelve un cliente por ID
 * @param {number|string} id
 */
const get      = id   => http.get(`/clients/${id}`)

/**
 * Devuelve la lista completa de clientes
 */
const getAll   = ()   => http.get('/clients')

export default { get, getAll }
