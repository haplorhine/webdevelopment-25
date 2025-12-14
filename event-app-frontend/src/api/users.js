import { http } from './http'

/**
 * Registers a new user (payload must match backend UserDto).
 */
export async function registerUser(payload) {
  const res = await http.post('/users/register', payload)
  return res.data
}

