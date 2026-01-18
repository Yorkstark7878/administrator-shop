<template>
  <div class="min-h-screen bg-white">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg sticky top-0 z-50">
      <div class="max-w-full px-8">
        <div class="flex justify-between h-20 items-center">
          <router-link to="/" class="text-4xl font-bold text-blue-600">ShopHub</router-link>
          <router-link
            to="/"
            class="text-blue-600 hover:text-blue-800 flex items-center gap-2 font-semibold text-xl"
          >
            ← Back to Products
          </router-link>
        </div>
      </div>
    </nav>

    <div class="max-w-full px-8 py-20">
      <div v-if="loading" class="text-center">
        <p class="text-gray-600">Loading product...</p>
      </div>

      <div v-else-if="product" class="grid grid-cols-2 gap-16">
        <!-- Product Image -->
        <div class="flex items-center justify-center">
          <div class="w-full h-96 bg-gray-200 rounded-xl flex items-center justify-center">
            <img
              v-if="product.lienImage"
              :src="product.lienImage"
              :alt="product.name"
              class="w-full h-full object-cover rounded-lg"
            />
            <span v-else class="text-gray-400 text-lg">No image available</span>
          </div>
        </div>

        <!-- Product Info -->
        <div class="flex flex-col justify-between">
          <div>
            <h1 class="text-5xl font-bold text-gray-900 mb-6">{{ product.name }}</h1>
            <p class="text-gray-600 text-xl mb-8">{{ product.description }}</p>

            <div class="mb-10">
              <p class="text-gray-700 mb-3 text-lg">
                <span class="font-semibold">Category:</span> {{ product.category }}
              </p>
              <p class="text-gray-700 mb-4 text-lg">
                <span class="font-semibold">Stock:</span>
                <span
                  :class="[
                    'px-3 py-1 rounded-full text-sm font-medium ml-2',
                    product.stockQuantity > 0 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                  ]"
                >
                  {{ product.stockQuantity > 0 ? `${product.stockQuantity} in stock` : 'Out of Stock' }}
                </span>
              </p>
            </div>

            <!-- Price -->
            <div class="mb-10">
              <p class="text-6xl font-bold text-blue-600">${{ product.price }}</p>
            </div>

            <!-- Quantity Selector -->
            <div class="mb-10">
              <label class="block text-lg font-semibold text-gray-700 mb-4">Quantity</label>
              <div class="flex items-center gap-6">
                <button
                  @click="decreaseQuantity"
                  :disabled="quantity <= 1"
                  class="px-6 py-3 border-2 border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 font-semibold text-lg"
                >
                  −
                </button>
                <input
                  v-model="quantity"
                  type="number"
                  min="1"
                  :max="product.stockQuantity"
                  class="w-24 px-4 py-3 border-2 border-gray-300 rounded-lg text-center text-lg font-semibold"
                />
                <button
                  @click="increaseQuantity"
                  :disabled="quantity >= product.stockQuantity"
                  class="px-6 py-3 border-2 border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 font-semibold text-lg"
                >
                  +
                </button>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-6">
            <button
              @click="handleAddToCart"
              :disabled="product.stockQuantity <= 0"
              class="flex-1 bg-blue-600 text-white py-4 rounded-lg font-bold text-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Add to Cart
            </button>
            <button
              @click="handleBuyNow"
              :disabled="product.stockQuantity <= 0 || !isAuthenticated"
              class="flex-1 bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Buy Now
            </button>
          </div>

          <div v-if="!isAuthenticated" class="mt-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
            <p class="text-sm text-yellow-800">
              <router-link to="/login" class="font-semibold text-yellow-900 hover:underline">
                Sign in
              </router-link>
              to purchase this product.
            </p>
          </div>

          <div v-if="message" :class="['mt-4 p-4 rounded-lg', messageType === 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200']">
            <p :class="['text-sm', messageType === 'success' ? 'text-green-800' : 'text-red-800']">
              {{ message }}
            </p>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-12">
        <p class="text-gray-600 text-lg">Product not found</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productService, authService, orderService } from '../api/services'

const router = useRouter()
const route = useRoute()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)
const message = ref('')
const messageType = ref('')

const isAuthenticated = computed(() => authService.isAuthenticated())

const loadProduct = async () => {
  loading.value = true
  try {
    const response = await productService.getProductById(route.params.id)
    product.value = response.data
  } catch (error) {
    console.error('Failed to load product:', error)
  } finally {
    loading.value = false
  }
}

const increaseQuantity = () => {
  if (quantity.value < product.value.stockQuantity) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const handleAddToCart = () => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const existingItem = cart.find((item) => item.id === product.value.id)

  if (existingItem) {
    existingItem.quantity += quantity.value
  } else {
    cart.push({
      id: product.value.id,
      name: product.value.name,
      price: product.value.price,
      quantity: quantity.value,
    })
  }

  localStorage.setItem('cart', JSON.stringify(cart))
  message.value = 'Product added to cart!'
  messageType.value = 'success'
  setTimeout(() => (message.value = ''), 3000)
}

const handleBuyNow = async () => {
  if (!isAuthenticated.value) {
    router.push('/login')
    return
  }

  try {
    await orderService.createOrder({
      productId: product.value.id,
      quantity: quantity.value,
    })
    message.value = 'Order created successfully!'
    messageType.value = 'success'
    setTimeout(() => router.push('/user/orders'), 2000)
  } catch (error) {
    message.value = error.response?.data?.message || 'Failed to create order'
    messageType.value = 'error'
  }
}

onMounted(() => {
  loadProduct()
})
</script>
