<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-black">
    <!-- Header with Navigation -->
    <nav class="bg-gradient-to-r from-red-600 to-black shadow-2xl sticky top-0 z-50">
      <div class="max-w-full px-8">
        <div class="flex justify-between h-20 items-center">
          <div class="flex items-center">
            <router-link to="/" class="text-4xl font-bold text-white">Administrator Shop</router-link>
          </div>
          <div class="flex items-center space-x-6">
            <input
              v-model="searchQuery"
              @keyup.enter="handleSearch"
              type="text"
              placeholder="Rechercher des produits..."
              class="w-96 px-6 py-3 border-2 border-red-500 rounded-full focus:outline-none focus:ring-2 focus:ring-red-400 text-lg bg-gray-900 text-white placeholder-gray-400"
            />
            <button @click="handleSearch" class="bg-red-600 text-white px-8 py-3 rounded-full hover:bg-red-700 font-semibold text-lg transition-all shadow-lg hover:shadow-red-500/50">
              Rechercher
            </button>
            <div v-if="isAuthenticated" class="flex items-center space-x-8">
              <router-link to="/user" class="text-white hover:text-red-400 font-semibold text-lg transition-colors">Profil</router-link>
              <router-link to="/user/orders" class="text-white hover:text-red-400 font-semibold text-lg transition-colors">Commandes</router-link>
              <div v-if="currentUser && currentUser.roles.includes('ADMIN')">
                <router-link to="/admin" class="text-white hover:text-red-400 font-semibold text-lg transition-colors">Admin</router-link>
              </div>
              <button @click="handleLogout" class="text-white hover:text-red-300 font-semibold text-lg transition-colors">Déconnexion</button>
            </div>
            <div v-else class="flex space-x-4">
              <router-link
                to="/login"
                class="px-8 py-3 border-2 border-white text-white rounded-full hover:bg-white/10 font-semibold text-lg transition-all"
              >
                Connexion
              </router-link>
              <router-link
                to="/register"
                class="px-8 py-3 bg-red-600 text-white rounded-full hover:bg-red-700 font-semibold text-lg transition-all shadow-lg"
              >
                S'inscrire
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <div class="bg-gradient-to-r from-black via-red-900 to-black text-white py-32 relative overflow-hidden">
      <div class="absolute inset-0 bg-red-600/10"></div>
      <div class="max-w-full px-8 text-center relative z-10">
        <h1 class="text-7xl font-extrabold mb-6 drop-shadow-2xl">Application Spring-Boot</h1>
        <p class="text-4xl text-red-200 font-light">Des super produits pour un super prix !</p>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="bg-gray-900 shadow-md border-b border-red-900">
      <div class="max-w-full px-8 py-8">
        <div class="flex gap-6 justify-center">
          <select
            v-model="selectedCategory"
            @change="loadProducts"
            class="px-8 py-4 border-2 border-red-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500 text-lg font-semibold bg-black text-white hover:border-red-500 transition-all cursor-pointer"
          >
            <option value="">Toutes les catégories</option>
            <option value="electronics">Électronique</option>
            <option value="clothing">Vêtements</option>
            <option value="books">Livres</option>
            <option value="home">Maison & Jardin</option>
          </select>

          <select
            v-model="sortBy"
            @change="loadProducts"
            class="px-8 py-4 border-2 border-red-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500 text-lg font-semibold bg-black text-white hover:border-red-500 transition-all cursor-pointer"
          >
            <option value="">Tri par défaut</option>
            <option value="price-asc">Prix: Croissant</option>
            <option value="price-desc">Prix: Décroissant</option>
            <option value="latest">Derniers produits</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Products Grid -->
    <div class="max-w-full px-8 py-16">
      <div v-if="loading" class="text-center">
        <p class="text-gray-400 text-xl">Chargement des produits...</p>
      </div>

      <div v-else-if="products.length === 0" class="text-center py-12">
        <p class="text-gray-400 text-xl">Aucun produit trouvé</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-10">
        <div
          v-for="product in products"
          :key="product.id"
          class="bg-gradient-to-br from-gray-900 to-black rounded-2xl shadow-2xl hover:shadow-red-500/50 transition-all duration-300 overflow-hidden transform hover:-translate-y-2 border-2 border-gray-800 hover:border-red-600"
        >
          <div class="h-72 bg-gradient-to-br from-gray-800 to-black flex items-center justify-center overflow-hidden">
            <img
              v-if="product.lienImage"
              :src="product.lienImage"
              :alt="product.name"
              class="w-full h-full object-cover hover:scale-110 transition-transform duration-500"
            />
            <span v-else class="text-gray-500 text-lg">Pas d'image</span>
          </div>
          <div class="p-6">
            <h3 class="text-2xl font-bold text-white mb-3 hover:text-red-500 transition-colors">{{ product.name }}</h3>
            <p class="text-base text-gray-400 mt-2 line-clamp-2 mb-5">{{ product.description }}</p>
            <div class="mt-5 flex justify-between items-center">
              <span class="text-4xl font-extrabold bg-gradient-to-r from-red-500 to-red-700 bg-clip-text text-transparent">${{ product.price }}</span>
              <span
                :class="[
                  'px-4 py-2 rounded-full text-sm font-semibold shadow-sm',
                  product.stockQuantity > 0 ? 'bg-green-900 text-green-300 border border-green-700' : 'bg-red-900 text-red-300 border border-red-700',
                ]"
              >
                {{ product.stockQuantity > 0 ? 'En stock' : 'Rupture de stock' }}
              </span>
            </div>
            <router-link
              :to="`/product/${product.id}`"
              class="w-full mt-6 inline-block text-center bg-gradient-to-r from-red-600 to-red-800 text-white py-4 rounded-xl hover:from-red-700 hover:to-red-900 transition-all duration-300 font-semibold text-lg shadow-lg hover:shadow-red-500/50"
            >
              Voir les détails
            </router-link>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="mt-20 flex justify-center gap-3">
        <button
          v-for="page in totalPages"
          :key="page"
          @click="currentPage = page; loadProducts()"
          :class="[
            'px-8 py-4 rounded-xl border-2 font-semibold text-lg transition-all duration-300',
            currentPage === page
              ? 'bg-gradient-to-r from-red-600 to-red-800 text-white border-red-600 shadow-lg shadow-red-500/50'
              : 'bg-gray-900 text-gray-300 border-gray-700 hover:border-red-500 hover:text-red-400 hover:shadow-md',
          ]"
        >
          {{ page }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productService, authService } from '../api/services'

const router = useRouter()
const products = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 12
const totalPages = ref(1)
const searchQuery = ref('')
const selectedCategory = ref('')
const sortBy = ref('')

const isAuthenticated = computed(() => authService.isAuthenticated())
const currentUser = computed(() => authService.getCurrentUser())

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productService.getAllProducts(currentPage.value - 1, pageSize)
    products.value = response.data.content || response.data
    totalPages.value = response.data.totalPages || Math.ceil(products.value.length / pageSize)
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    loadProducts()
    return
  }

  loading.value = true
  try {
    const response = await productService.searchProducts(searchQuery.value)
    products.value = response.data
  } catch (error) {
    console.error('Search failed:', error)
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  authService.logout()
  router.push('/login')
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>