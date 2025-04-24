import http from '../http-common'

const get = id  => http.get(`/clients/${id}`)
export default { get }
