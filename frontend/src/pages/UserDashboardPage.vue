<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg sticky top-0 z-50">
      <div class="max-w-full px-8">
        <div class="flex justify-between h-20 items-center">
          <router-link to="/" class="text-4xl font-bold text-blue-600">ShopHub</router-link>
          <div class="flex items-center space-x-8">
            <router-link to="/user/orders" class="text-gray-700 hover:text-blue-600 font-semibold text-lg">My Orders</router-link>
            <router-link to="/" class="text-gray-700 hover:text-blue-600 font-semibold text-lg">Shop</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-red-600 font-semibold text-lg">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-full px-8 py-20">
      <div class="grid grid-cols-3 gap-8 mb-12">
        <!-- User Profile Card -->
        <div class="col-span-2">
          <div class="bg-white rounded-2xl shadow-xl p-12">
            <h1 class="text-4xl font-bold text-gray-900 mb-8">User Profile</h1>
            <div class="space-y-6">
              <div class="flex justify-between items-center pb-6 border-b-2">
                <span class="text-gray-600 font-bold text-lg">Username:</span>
                <span class="text-gray-900 text-lg">{{ currentUser.username }}</span>
              </div>
              <div class="flex justify-between items-center pb-6 border-b-2">
                <span class="text-gray-600 font-bold text-lg">Email:</span>
                <span class="text-gray-900 text-lg">{{ currentUser.email }}</span>
              </div>
              <div class="flex justify-between items-center pb-6 border-b-2">
                <span class="text-gray-600 font-bold text-lg">Roles:</span>
                <div class="flex gap-3">
                  <span
                    v-for="role in currentUser.roles"
                    :key="role"
                    :class="[
                      'px-5 py-2 rounded-full text-base font-bold',
                      role === 'ADMIN' ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800',
                    ]"
                  >
                    {{ role }}
                  </span>
                </div>
              </div>
              <div v-if="currentUser.createdAt" class="flex justify-between items-center">
                <span class="text-gray-600 font-bold text-lg">Member Since:</span>
                <span class="text-gray-900 text-lg">{{ formatDate(currentUser.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Stats -->
        <div class="space-y-6">
          <div class="bg-white rounded-2xl shadow-xl p-8">
            <h3 class="text-2xl font-bold text-gray-900 mb-4">Total Orders</h3>
            <p class="text-5xl font-bold text-blue-600">{{ totalOrders }}</p>
          </div>
          <div class="bg-white rounded-2xl shadow-xl p-8">
            <h3 class="text-2xl font-bold text-gray-900 mb-4">Total Spent</h3>
            <p class="text-4xl font-bold text-green-600">${{ totalSpent.toFixed(2) }}</p>
          </div>
        </div>
      </div>

      <!-- Edit Profile Section -->
      <div class="bg-white rounded-2xl shadow-xl p-12">
        <h2 class="text-4xl font-bold text-gray-900 mb-8">Edit Profile</h2>
        <form @submit.prevent="handleUpdateProfile" class="space-y-6">
          <div class="grid grid-cols-2 gap-8">
            <div>
              <label class="block text-lg font-bold text-gray-800 mb-3">Username</label>
              <input
                v-model="editForm.username"
                type="text"
                class="w-full px-6 py-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-lg"
              />
            </div>
            <div>
              <label class="block text-lg font-bold text-gray-800 mb-3">Email</label>
              <input
                v-model="editForm.email"
                type="email"
                class="w-full px-6 py-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-lg"
              />
            </div>
          </div>

          <div v-if="message" :class="['p-6 rounded-lg border-l-4', messageType === 'success' ? 'bg-green-50 border-green-500' : 'bg-red-50 border-red-500']">
            <p :class="['text-base font-semibold', messageType === 'success' ? 'text-green-800' : 'text-red-800']">
              {{ message }}
            </p>
          </div>

          <button
            type="submit"
            :disabled="updateLoading"
            class="bg-blue-600 text-white px-10 py-4 rounded-lg hover:bg-blue-700 disabled:opacity-50 font-bold text-xl transition duration-200"
          >
            {{ updateLoading ? 'Updating...' : 'Update Profile' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authService, orderService } from '../api/services'

const router = useRouter()
const currentUser = ref(authService.getCurrentUser())
const editForm = ref({
  username: currentUser.value.username,
  email: currentUser.value.email,
})
const totalOrders = ref(0)
const totalSpent = ref(0)
const message = ref('')
const messageType = ref('')
const updateLoading = ref(false)

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const loadUserStats = async () => {
  try {
    const response = await orderService.getMyOrders()
    const orders = response.data
    totalOrders.value = orders.length
    totalSpent.value = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
  } catch (error) {
    console.error('Failed to load user stats:', error)
  }
}

const handleUpdateProfile = async () => {
  updateLoading.value = true
  message.value = ''

  try {
    // In a real application, you would call an API to update the user
    // For now, we'll just update the local storage
    const updatedUser = { ...currentUser.value, ...editForm.value }
    authService.setToken(localStorage.getItem('jwt_token'), updatedUser)
    currentUser.value = updatedUser
    message.value = 'Profile updated successfully!'
    messageType.value = 'success'
    setTimeout(() => (message.value = ''), 3000)
  } catch (error) {
    message.value = 'Failed to update profile'
    messageType.value = 'error'
  } finally {
    updateLoading.value = false
  }
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadUserStats()
})
</script>
