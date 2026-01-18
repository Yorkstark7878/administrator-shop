<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <router-link to="/" class="text-2xl font-bold text-blue-600">ShopHub Admin</router-link>
          <div class="flex items-center space-x-4">
            <router-link to="/admin/products" class="text-gray-700 hover:text-blue-600">
              Products
            </router-link>
            <router-link to="/admin/users" class="text-gray-700 hover:text-blue-600">Users</router-link>
            <router-link to="/admin/orders" class="text-gray-700 hover:text-blue-600">Orders</router-link>
            <router-link to="/" class="text-gray-700 hover:text-blue-600">Shop</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-red-600">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <h1 class="text-4xl font-bold text-gray-900 mb-8">Admin Dashboard</h1>

      <!-- Statistics Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow-lg p-6">
          <h3 class="text-gray-600 font-semibold mb-2">Total Products</h3>
          <p class="text-4xl font-bold text-blue-600">{{ stats.totalProducts }}</p>
        </div>
        <div class="bg-white rounded-lg shadow-lg p-6">
          <h3 class="text-gray-600 font-semibold mb-2">Total Users</h3>
          <p class="text-4xl font-bold text-green-600">{{ stats.totalUsers }}</p>
        </div>
        <div class="bg-white rounded-lg shadow-lg p-6">
          <h3 class="text-gray-600 font-semibold mb-2">Total Orders</h3>
          <p class="text-4xl font-bold text-purple-600">{{ stats.totalOrders }}</p>
        </div>
        <div class="bg-white rounded-lg shadow-lg p-6">
          <h3 class="text-gray-600 font-semibold mb-2">Revenue</h3>
          <p class="text-3xl font-bold text-orange-600">${{ stats.totalRevenue.toFixed(2) }}</p>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow-lg p-6">
          <h3 class="text-2xl font-bold text-gray-900 mb-4">Quick Actions</h3>
          <div class="space-y-3">
            <router-link
              to="/admin/products"
              class="block text-center bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700"
            >
              Manage Products
            </router-link>
            <router-link
              to="/admin/users"
              class="block text-center bg-green-600 text-white py-2 rounded-lg hover:bg-green-700"
            >
              Manage Users
            </router-link>
            <router-link
              to="/admin/orders"
              class="block text-center bg-purple-600 text-white py-2 rounded-lg hover:bg-purple-700"
            >
              View Orders
            </router-link>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-lg p-6">
          <h3 class="text-2xl font-bold text-gray-900 mb-4">Recent Activity</h3>
          <div class="space-y-2 text-sm text-gray-600">
            <p>✓ 15 new users registered today</p>
            <p>✓ 8 orders placed in the last hour</p>
            <p>✓ 3 products out of stock</p>
            <p>✓ 2 pending user verifications</p>
          </div>
        </div>
      </div>

      <!-- Recent Orders Preview -->
      <div class="bg-white rounded-lg shadow-lg p-6">
        <h3 class="text-2xl font-bold text-gray-900 mb-4">Recent Orders</h3>
        <div v-if="recentOrders.length === 0" class="text-gray-600 text-center py-8">
          No recent orders
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-100">
              <tr>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Order ID</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Customer</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Amount</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Date</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in recentOrders" :key="order.id" class="border-t">
                <td class="px-6 py-4 text-sm text-gray-900">#{{ order.id }}</td>
                <td class="px-6 py-4 text-sm text-gray-900">{{ order.userName || 'N/A' }}</td>
                <td class="px-6 py-4 text-sm font-semibold text-gray-900">
                  ${{ order.totalAmount.toFixed(2) }}
                </td>
                <td class="px-6 py-4 text-sm">
                  <span
                    :class="[
                      'px-3 py-1 rounded-full text-xs font-medium',
                      order.status === 'DELIVERED'
                        ? 'bg-green-100 text-green-800'
                        : order.status === 'PENDING'
                          ? 'bg-yellow-100 text-yellow-800'
                          : 'bg-blue-100 text-blue-800',
                    ]"
                  >
                    {{ order.status }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(order.orderDate) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminService, orderService, authService } from '../api/services'

const router = useRouter()
const stats = ref({
  totalProducts: 0,
  totalUsers: 0,
  totalOrders: 0,
  totalRevenue: 0,
})
const recentOrders = ref([])

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

const loadDashboardData = async () => {
  try {
    // Load users for count
    const usersResponse = await adminService.getAllUsers()
    stats.value.totalUsers = usersResponse.data.length

    // Load orders
    const ordersResponse = await orderService.getMyOrders()
    const orders = ordersResponse.data
    stats.value.totalOrders = orders.length
    stats.value.totalRevenue = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
    recentOrders.value = orders.slice(0, 5)

    // Mock data for products (you would load from API)
    stats.value.totalProducts = 42
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadDashboardData()
})
</script>
