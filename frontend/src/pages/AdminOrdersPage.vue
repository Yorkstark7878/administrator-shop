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
            <router-link to="/admin/users" class="text-gray-700 hover:text-blue-600">Users</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-red-600">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Order Management</h1>
        <div class="flex gap-2">
          <select
            v-model="filterStatus"
            @change="loadOrders"
            class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="PROCESSING">Processing</option>
            <option value="SHIPPED">Shipped</option>
            <option value="DELIVERED">Delivered</option>
          </select>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-lg overflow-hidden">
        <div v-if="loading" class="p-8 text-center text-gray-600">
          Loading orders...
        </div>
        <div v-else-if="orders.length === 0" class="p-8 text-center text-gray-600">
          No orders found
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-100">
              <tr>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Order ID</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Customer</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Amount</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Items</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Date</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id" class="border-t hover:bg-gray-50">
                <td class="px-6 py-4 text-sm font-medium text-gray-900">#{{ order.id }}</td>
                <td class="px-6 py-4 text-sm text-gray-600">{{ order.userName || 'N/A' }}</td>
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
                          : order.status === 'SHIPPED'
                            ? 'bg-blue-100 text-blue-800'
                            : 'bg-purple-100 text-purple-800',
                    ]"
                  >
                    {{ order.status }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">
                  {{ order.items?.length || order.quantite || 1 }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(order.orderDate) }}</td>
                <td class="px-6 py-4 text-sm flex gap-2">
                  <button
                    @click="viewOrderDetails(order)"
                    class="text-blue-600 hover:text-blue-800 font-semibold"
                  >
                    Details
                  </button>
                  <button
                    @click="updateOrderStatus(order)"
                    class="text-purple-600 hover:text-purple-800 font-semibold"
                  >
                    Update Status
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Order Details Modal -->
      <div v-if="showDetailsModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div class="bg-white rounded-lg shadow-lg p-8 w-full max-w-2xl max-h-96 overflow-y-auto">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Order #{{ selectedOrder.id }}</h2>
            <button
              @click="showDetailsModal = false"
              class="text-gray-500 hover:text-gray-700 text-2xl"
            >
              ×
            </button>
          </div>

          <div class="space-y-4 mb-6">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-600">Customer</p>
                <p class="font-semibold">{{ selectedOrder.userName }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Total Amount</p>
                <p class="font-semibold">${{ selectedOrder.totalAmount.toFixed(2) }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Status</p>
                <p class="font-semibold">{{ selectedOrder.status }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Order Date</p>
                <p class="font-semibold">{{ formatDate(selectedOrder.orderDate) }}</p>
              </div>
            </div>

            <div>
              <h3 class="font-semibold text-gray-900 mb-2">Order Items</h3>
              <div v-if="selectedOrder.items && selectedOrder.items.length > 0" class="space-y-2">
                <div
                  v-for="item in selectedOrder.items"
                  :key="item.id"
                  class="flex justify-between items-center pb-2 border-b"
                >
                  <div>
                    <p class="font-semibold">{{ item.productName }}</p>
                    <p class="text-sm text-gray-600">Qty: {{ item.quantity }}</p>
                  </div>
                  <p class="font-semibold">${{ (item.price * item.quantity).toFixed(2) }}</p>
                </div>
              </div>
              <div v-else class="text-gray-600">
                <p>Product: Product #{{ selectedOrder.productId }}</p>
                <p>Quantity: {{ selectedOrder.quantite || 1 }}</p>
              </div>
            </div>
          </div>

          <button
            @click="showDetailsModal = false"
            class="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700"
          >
            Close
          </button>
        </div>
      </div>

      <!-- Update Status Modal -->
      <div v-if="showStatusModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div class="bg-white rounded-lg shadow-lg p-8 w-full max-w-md">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">Update Order Status</h2>
          <form @submit.prevent="handleUpdateStatus" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">New Status</label>
              <select
                v-model="newStatus"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select status</option>
                <option value="PENDING">Pending</option>
                <option value="PROCESSING">Processing</option>
                <option value="SHIPPED">Shipped</option>
                <option value="DELIVERED">Delivered</option>
              </select>
            </div>

            <div v-if="statusMessage" :class="['p-4 rounded-lg', statusMessageType === 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200']">
              <p :class="['text-sm', statusMessageType === 'success' ? 'text-green-800' : 'text-red-800']">
                {{ statusMessage }}
              </p>
            </div>

            <div class="flex gap-4">
              <button
                type="submit"
                :disabled="statusLoading"
                class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {{ statusLoading ? 'Updating...' : 'Update' }}
              </button>
              <button
                type="button"
                @click="showStatusModal = false"
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
import { orderService, authService } from '../api/services'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const filterStatus = ref('')
const showDetailsModal = ref(false)
const showStatusModal = ref(false)
const selectedOrder = ref(null)
const newStatus = ref('')
const statusLoading = ref(false)
const statusMessage = ref('')
const statusMessageType = ref('')

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderService.getMyOrders()
    let allOrders = response.data

    if (filterStatus.value) {
      allOrders = allOrders.filter((order) => order.status === filterStatus.value)
    }

    orders.value = allOrders.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate))
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
}

const viewOrderDetails = (order) => {
  selectedOrder.value = order
  showDetailsModal.value = true
}

const updateOrderStatus = (order) => {
  selectedOrder.value = order
  newStatus.value = order.status
  showStatusModal.value = true
}

const handleUpdateStatus = async () => {
  statusLoading.value = true
  statusMessage.value = ''

  try {
    // Call API to update order status
    // For now, just update locally
    selectedOrder.value.status = newStatus.value
    statusMessage.value = 'Order status updated successfully!'
    statusMessageType.value = 'success'
    setTimeout(() => {
      showStatusModal.value = false
      loadOrders()
    }, 2000)
  } catch (error) {
    statusMessage.value = 'Failed to update order status'
    statusMessageType.value = 'error'
  } finally {
    statusLoading.value = false
  }
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadOrders()
})
</script>
