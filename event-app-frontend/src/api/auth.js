import { http } from './http'


export async function login(username, password) {
  const response = await http.post('/auth/token', { username, password })
  return response.data
}