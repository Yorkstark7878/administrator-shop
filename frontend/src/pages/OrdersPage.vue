<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <router-link to="/" class="text-2xl font-bold text-blue-600">ShopHub</router-link>
          <div class="flex items-center space-x-4">
            <router-link to="/user" class="text-gray-700 hover:text-blue-600">Profile</router-link>
            <router-link to="/" class="text-gray-700 hover:text-blue-600">Shop</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-red-600">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900">My Orders</h1>
        <router-link to="/" class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700">
          Continue Shopping
        </router-link>
      </div>

      <div v-if="loading" class="text-center">
        <p class="text-gray-600">Loading orders...</p>
      </div>

      <div v-else-if="orders.length === 0" class="text-center py-12 bg-white rounded-lg shadow">
        <p class="text-gray-600 text-lg mb-4">You haven't placed any orders yet</p>
        <router-link to="/" class="text-blue-600 hover:text-blue-800 font-semibold">
          Start shopping now
        </router-link>
      </div>

      <div v-else class="space-y-6">
        <div
          v-for="order in orders"
          :key="order.id"
          class="bg-white rounded-lg shadow-lg overflow-hidden"
        >
          <div class="bg-gradient-to-r from-blue-600 to-blue-800 text-white p-6">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <p class="text-sm text-blue-100">Order ID</p>
                <p class="text-xl font-semibold">#{{ order.id }}</p>
              </div>
              <div>
                <p class="text-sm text-blue-100">Order Date</p>
                <p class="text-xl font-semibold">{{ formatDate(order.orderDate) }}</p>
              </div>
              <div>
                <p class="text-sm text-blue-100">Status</p>
                <p
                  :class="[
                    'text-xl font-semibold',
                    order.status === 'DELIVERED'
                      ? 'text-green-300'
                      : order.status === 'CANCELLED'
                        ? 'text-red-300'
                        : 'text-yellow-300',
                  ]"
                >
                  {{ order.status }}
                </p>
              </div>
              <div>
                <p class="text-sm text-blue-100">Total</p>
                <p class="text-2xl font-bold">${{ order.totalAmount.toFixed(2) }}</p>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div class="p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Items</h3>
            <div class="space-y-4">
              <div
                v-if="order.items && order.items.length > 0"
                v-for="item in order.items"
                :key="item.id"
                class="flex justify-between items-center pb-4 border-b"
              >
                <div>
                  <p class="font-semibold text-gray-900">{{ item.productName }}</p>
                  <p class="text-sm text-gray-600">Quantity: {{ item.quantity }}</p>
                </div>
                <p class="text-lg font-semibold text-gray-900">
                  ${{ (item.price * item.quantity).toFixed(2) }}
                </p>
              </div>
              <div v-else class="text-gray-600">
                <p>Product: Product #{{ order.productId }}</p>
                <p>Quantity: {{ order.quantite || 1 }}</p>
              </div>
            </div>
          </div>

          <!-- Order Actions -->
          <div class="bg-gray-50 px-6 py-4 flex gap-4">
            <button
              @click="viewOrderDetails(order)"
              class="text-blue-600 hover:text-blue-800 font-semibold"
            >
              View Details
            </button>
            <button
              v-if="order.status === 'PENDING'"
              @click="cancelOrder(order.id)"
              class="text-red-600 hover:text-red-800 font-semibold"
            >
              Cancel Order
            </button>
            <button
              @click="reorderProducts(order)"
              class="text-green-600 hover:text-green-800 font-semibold"
            >
              Reorder
            </button>
          </div>
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

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderService.getMyOrders()
    orders.value = response.data
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
}

const viewOrderDetails = (order) => {
  console.log('Order details:', order)
  // You could implement a modal or detailed view here
}

const cancelOrder = async (orderId) => {
  if (!confirm('Are you sure you want to cancel this order?')) return

  try {
    // Call API to cancel order
    await loadOrders()
  } catch (error) {
    console.error('Failed to cancel order:', error)
  }
}

const reorderProducts = async (order) => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')

  if (order.items && order.items.length > 0) {
    order.items.forEach((item) => {
      const existingItem = cart.find((c) => c.id === item.productId)
      if (existingItem) {
        existingItem.quantity += item.quantity
      } else {
        cart.push({
          id: item.productId,
          name: item.productName,
          price: item.price,
          quantity: item.quantity,
        })
      }
    })
  }

  localStorage.setItem('cart', JSON.stringify(cart))
  alert('Products added to cart!')
  router.push('/')
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadOrders()
})
</script>
