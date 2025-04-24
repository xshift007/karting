import http from '../http-common'

const weekly = (from, to)    =>
  http.get('/sessions/availability', { params:{ from, to } })
const getAll = ()            => http.get('/sessions')
const create = payload       => http.post('/sessions', payload).then(r => r.data)
const update = (id, payload) => http.put(`/sessions/${id}`, payload).then(r => r.data)
const remove = id            => http.delete(`/sessions/${id}`)

export default { weekly, getAll, create, update, delete: remove }
