import http from '../http-common'

const getAll = ()            => http.get('/clients')
const get    = id            => http.get(`/clients/${id}`)
const create = payload       => http.post('/clients', payload).then(r => r.data)
const update = (id,payload)  => http.put(`/clients/${id}`, payload).then(r => r.data)

export default { getAll, get, create, update }
