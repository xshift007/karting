import http from '../http-common'

const weekly = monday => http.get('/sessions', { params:{
  from: monday, to: new Date(monday).setDate(new Date(monday).getDate()+6)
}})
export default { weekly }
