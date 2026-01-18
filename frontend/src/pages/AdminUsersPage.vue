<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <router-link to="/" class="text-2xl font-bold text-blue-600">ShopHub Admin</router-link>
          <div class="flex items-center space-x-4">
            <router-link to="/admin" class="text-gray-700 hover:text-blue-600">Dashboard</router-link>
            <router-link to="/admin/products" class="text-gray-700 hover:text-blue-600">
              Products
            </router-link>
            <router-link to="/admin/orders" class="text-gray-700 hover:text-blue-600">Orders</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-red-600">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <h1 class="text-3xl font-bold text-gray-900 mb-8">User Management</h1>

      <div class="bg-white rounded-lg shadow-lg overflow-hidden">
        <div v-if="loading" class="p-8 text-center text-gray-600">
          Loading users...
        </div>
        <div v-else-if="users.length === 0" class="p-8 text-center text-gray-600">
          No users found
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-100">
              <tr>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Username</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Email</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Roles</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Joined</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id" class="border-t hover:bg-gray-50">
                <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ user.username }}</td>
                <td class="px-6 py-4 text-sm text-gray-600">{{ user.email }}</td>
                <td class="px-6 py-4 text-sm">
                  <div class="flex gap-2">
                    <span
                      v-for="role in user.roles"
                      :key="role"
                      :class="[
                        'px-3 py-1 rounded-full text-xs font-medium',
                        role === 'ADMIN' ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800',
                      ]"
                    >
                      {{ role }}
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 text-sm">
                  <span
                    :class="[
                      'px-3 py-1 rounded-full text-xs font-medium',
                      user.enabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                    ]"
                  >
                    {{ user.enabled ? 'Active' : 'Disabled' }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(user.createdAt) }}</td>
                <td class="px-6 py-4 text-sm flex gap-2">
                  <button
                    @click="editUser(user)"
                    class="text-blue-600 hover:text-blue-800 font-semibold"
                  >
                    Edit
                  </button>
                  <button
                    v-if="user.enabled"
                    @click="disableUser(user.id)"
                    class="text-orange-600 hover:text-orange-800 font-semibold"
                  >
                    Disable
                  </button>
                  <button
                    v-else
                    @click="enableUser(user.id)"
                    class="text-green-600 hover:text-green-800 font-semibold"
                  >
                    Enable
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Edit User Modal -->
      <div v-if="showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div class="bg-white rounded-lg shadow-lg p-8 w-full max-w-md">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">Edit User</h2>
          <form @submit.prevent="handleSaveUser" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Username</label>
              <input
                v-model="editForm.username"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Email</label>
              <input
                v-model="editForm.email"
                type="email"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Roles</label>
              <div class="space-y-2">
                <label class="flex items-center">
                  <input
                    type="checkbox"
                    value="USER"
                    v-model="editForm.roles"
                    class="rounded border-gray-300"
                  />
                  <span class="ml-2 text-gray-700">User</span>
                </label>
                <label class="flex items-center">
                  <input
                    type="checkbox"
                    value="ADMIN"
                    v-model="editForm.roles"
                    class="rounded border-gray-300"
                  />
                  <span class="ml-2 text-gray-700">Admin</span>
                </label>
              </div>
            </div>

            <div v-if="editMessage" :class="['p-4 rounded-lg', editMessageType === 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200']">
              <p :class="['text-sm', editMessageType === 'success' ? 'text-green-800' : 'text-red-800']">
                {{ editMessage }}
              </p>
            </div>

            <div class="flex gap-4">
              <button
                type="submit"
                :disabled="editLoading"
                class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {{ editLoading ? 'Saving...' : 'Save' }}
              </button>
              <button
                type="button"
                @click="showEditModal = false"
                class="flex-1 bg-gray-600 text-white py-2 rounded-lg hover:bg-gray-700"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminService, authService } from '../api/services'

const router = useRouter()
const users = ref([])
const loading = ref(false)
const showEditModal = ref(false)
const editLoading = ref(false)
const editMessage = ref('')
const editMessageType = ref('')
const editForm = ref({
  username: '',
  email: '',
  roles: [],
})
const editingUserId = ref(null)

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

const loadUsers = async () => {
  loading.value = true
  try {
    const response = await adminService.getAllUsers()
    users.value = response.data
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    loading.value = false
  }
}

const editUser = (user) => {
  editingUserId.value = user.id
  editForm.value = {
    username: user.username,
    email: user.email,
    roles: [...(user.roles || [])],
  }
  showEditModal.value = true
}

const handleSaveUser = async () => {
  editLoading.value = true
  editMessage.value = ''

  try {
    await adminService.updateUser(editingUserId.value, editForm.value)
    editMessage.value = 'User updated successfully!'
    editMessageType.value = 'success'
    setTimeout(() => {
      showEditModal.value = false
      loadUsers()
    }, 2000)
  } catch (error) {
    editMessage.value = error.response?.data?.message || 'Failed to update user'
    editMessageType.value = 'error'
  } finally {
    editLoading.value = false
  }
}

const disableUser = async (userId) => {
  if (!confirm('Are you sure you want to disable this user?')) return

  try {
    await adminService.updateUser(userId, { enabled: false })
    loadUsers()
  } catch (error) {
    console.error('Failed to disable user:', error)
  }
}

const enableUser = async (userId) => {
  try {
    await adminService.updateUser(userId, { enabled: true })
    loadUsers()
  } catch (error) {
    console.error('Failed to enable user:', error)
  }
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadUsers()
})
</script>
