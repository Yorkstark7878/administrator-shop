import apiClient from './client'

export const authService = {
  register(username, email, password) {
    return apiClient.post('/auth/register', { username, email, password })
  },

  login(username, password) {
    return apiClient.post('/auth/login', { username, password })
  },

  logout() {
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('user')
  },

  getCurrentUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  },

  isAuthenticated() {
    return !!localStorage.getItem('jwt_token')
  },

  setToken(token, user) {
    localStorage.setItem('jwt_token', token)
    localStorage.setItem('user', JSON.stringify(user))
  },
}

export const productService = {
  getAllProducts(page = 0, size = 10) {
    return apiClient.get('/products', { params: { page, size } })
  },

  getProductById(id) {
    return apiClient.get(`/products/${id}`)
  },

  searchProducts(query) {
    return apiClient.get('/products/search', { params: { q: query } })
  },
}

export const adminService = {
  createProduct(productData) {
    return apiClient.post('/admin/products', productData)
  },

  updateProduct(id, productData) {
    return apiClient.put(`/admin/products/${id}`, productData)
  },

  deleteProduct(id) {
    return apiClient.delete(`/admin/products/${id}`)
  },

  getAllUsers() {
    return apiClient.get('/admin/users')
  },

  updateUser(id, userData) {
    return apiClient.put(`/admin/users/${id}`, userData)
  },
}

export const orderService = {
  createOrder(orderData) {
    return apiClient.post('/orders', orderData)
  },

  getMyOrders() {
    return apiClient.get('/orders/my-orders')
  },
}
