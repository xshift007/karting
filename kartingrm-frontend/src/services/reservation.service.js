import http from '../http-common'

const list   = ()      => http.get('/reservations')
const create = payload => http.post('/reservations', payload)
const cancel = id      => http.patch(`/reservations/${id}/cancel`)
export default { list, create, cancel }
