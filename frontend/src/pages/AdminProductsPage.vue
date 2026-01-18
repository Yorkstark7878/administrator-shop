<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <router-link to="/" class="text-2xl font-bold text-blue-600">ShopHub Admin</router-link>
          <div class="flex items-center space-x-4">
            <router-link to="/admin" class="text-gray-700 hover:text-blue-600">Dashboard</router-link>
            <router-link to="/admin/users" class="text-gray-700 hover:text-blue-600">Users</router-link>
            <router-link to="/admin/orders" class="text-gray-700 hover:text-blue-600">Orders</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-red-600">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Product Management</h1>
        <button
          @click="showAddForm = true"
          class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
        >
          Add New Product
        </button>
      </div>

      <!-- Add/Edit Product Form -->
      <div v-if="showAddForm" class="bg-white rounded-lg shadow-lg p-8 mb-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">
          {{ editingProduct ? 'Edit Product' : 'Add New Product' }}
        </h2>
        <form @submit.prevent="handleSaveProduct" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Product Name</label>
              <input
                v-model="productForm.name"
                type="text"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Price</label>
              <input
                v-model.number="productForm.price"
                type="number"
                step="0.01"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Category</label>
              <select
                v-model="productForm.category"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select a category</option>
                <option value="electronics">Electronics</option>
                <option value="clothing">Clothing</option>
                <option value="books">Books</option>
                <option value="home">Home & Garden</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Stock Quantity</label>
              <input
                v-model.number="productForm.stockQuantity"
                type="number"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
            <textarea
              v-model="productForm.description"
              rows="4"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Image URL</label>
            <input
              v-model="productForm.lienImage"
              type="url"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div v-if="formMessage" :class="['p-4 rounded-lg', formMessageType === 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200']">
            <p :class="['text-sm', formMessageType === 'success' ? 'text-green-800' : 'text-red-800']">
              {{ formMessage }}
            </p>
          </div>

          <div class="flex gap-4">
            <button
              type="submit"
              :disabled="formLoading"
              class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50"
            >
              {{ formLoading ? 'Saving...' : 'Save Product' }}
            </button>
            <button
              type="button"
              @click="resetForm"
              class="bg-gray-600 text-white px-6 py-2 rounded-lg hover:bg-gray-700"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>

      <!-- Products Table -->
      <div class="bg-white rounded-lg shadow-lg overflow-hidden">
        <div v-if="loading" class="p-8 text-center text-gray-600">
          Loading products...
        </div>
        <div v-else-if="products.length === 0" class="p-8 text-center text-gray-600">
          No products found
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-100">
              <tr>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Product Name</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Category</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Price</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Stock</th>
                <th class="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in products" :key="product.id" class="border-t hover:bg-gray-50">
                <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ product.name }}</td>
                <td class="px-6 py-4 text-sm text-gray-600">{{ product.category }}</td>
                <td class="px-6 py-4 text-sm font-semibold text-gray-900">${{ product.price }}</td>
                <td class="px-6 py-4 text-sm">
                  <span
                    :class="[
                      'px-3 py-1 rounded-full text-xs font-medium',
                      product.stockQuantity > 0 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                    ]"
                  >
                    {{ product.stockQuantity }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm flex gap-2">
                  <button
                    @click="editProduct(product)"
                    class="text-blue-600 hover:text-blue-800 font-semibold"
                  >
                    Edit
                  </button>
                  <button
                    @click="deleteProduct(product.id)"
                    class="text-red-600 hover:text-red-800 font-semibold"
                  >
                    Delete
                  </button>
                </td>
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
import { adminService, authService } from '../api/services'

const router = useRouter()
const products = ref([])
const loading = ref(false)
const showAddForm = ref(false)
const editingProduct = ref(null)
const formLoading = ref(false)
const formMessage = ref('')
const formMessageType = ref('')

const productForm = ref({
  name: '',
  description: '',
  price: 0,
  category: '',
  stockQuantity: 0,
  lienImage: '',
})

const loadProducts = async () => {
  loading.value = true
  try {
    // Mock data - replace with actual API call
    const mockProducts = [
      {
        id: 1,
        name: 'Laptop Pro',
        description: 'High performance laptop',
        price: 1299.99,
        category: 'electronics',
        stockQuantity: 15,
      },
      {
        id: 2,
        name: 'Wireless Mouse',
        description: 'Ergonomic wireless mouse',
        price: 29.99,
        category: 'electronics',
        stockQuantity: 50,
      },
    ]
    products.value = mockProducts
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loading.value = false
  }
}

const handleSaveProduct = async () => {
  formLoading.value = true
  formMessage.value = ''

  try {
    if (editingProduct.value) {
      await adminService.updateProduct(editingProduct.value.id, productForm.value)
      formMessage.value = 'Product updated successfully!'
    } else {
      await adminService.createProduct(productForm.value)
      formMessage.value = 'Product created successfully!'
    }
    formMessageType.value = 'success'
    setTimeout(() => {
      resetForm()
      loadProducts()
    }, 2000)
  } catch (error) {
    formMessage.value = error.response?.data?.message || 'Failed to save product'
    formMessageType.value = 'error'
  } finally {
    formLoading.value = false
  }
}

const editProduct = (product) => {
  editingProduct.value = product
  productForm.value = { ...product }
  showAddForm.value = true
}

const deleteProduct = async (id) => {
  if (!confirm('Are you sure you want to delete this product?')) return

  try {
    await adminService.deleteProduct(id)
    loadProducts()
  } catch (error) {
    console.error('Failed to delete product:', error)
  }
}

const resetForm = () => {
  showAddForm.value = false
  editingProduct.value = null
  productForm.value = {
    name: '',
    description: '',
    price: 0,
    category: '',
    stockQuantity: 0,
    lienImage: '',
  }
  formMessage.value = ''
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadProducts()
})
</script>
