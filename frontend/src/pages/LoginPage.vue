<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-black flex flex-col justify-center py-24 px-8">
    <div class="mx-auto w-full max-w-xl">
      <h2 class="mt-8 text-center text-5xl font-bold text-white">Connectez-vous à votre compte</h2>
    </div>

    <div class="mt-16 mx-auto w-full max-w-xl">
      <div class="bg-gradient-to-br from-gray-900 to-black py-16 px-12 shadow-2xl rounded-2xl border-2 border-red-600">
        <form class="space-y-8" @submit.prevent="handleLogin">
          <div>
            <label for="username" class="block text-lg font-semibold text-white">Nom d'utilisateur</label>
            <input
              v-model="form.username"
              id="username"
              name="username"
              type="text"
              required
              class="mt-3 block w-full px-5 py-4 border-2 border-red-500 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-red-400 text-lg bg-gray-900 text-white placeholder-gray-400"
            />
          </div>

          <div>
            <label for="password" class="block text-lg font-semibold text-white">Mot de passe</label>
            <input
              v-model="form.password"
              id="password"
              name="password"
              type="password"
              required
              class="mt-3 block w-full px-5 py-4 border-2 border-red-500 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-red-400 text-lg bg-gray-900 text-white placeholder-gray-400"
            />
          </div>

          <div v-if="error" class="rounded-lg bg-red-900/50 p-6 border-l-4 border-red-500">
            <p class="text-base font-semibold text-red-300">{{ error }}</p>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full flex justify-center py-4 px-6 border border-transparent rounded-lg shadow-lg text-xl font-bold text-white bg-gradient-to-r from-red-600 to-red-800 hover:from-red-700 hover:to-red-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50 transition duration-200 shadow-red-500/50"
          >
            {{ loading ? 'Connexion en cours...' : 'Se connecter' }}
          </button>
        </form>

        <div class="mt-10">
          <p class="text-center text-lg text-gray-300">
            Vous n'avez pas de compte ?
            <router-link to="/register" class="font-bold text-red-500 hover:text-red-400 text-lg">
              S'inscrire
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '../api/services'

const router = useRouter()
const form = ref({ username: '', password: '' })
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  try {
    const response = await authService.login(form.username, form.password)
    authService.setToken(response.data.token, response.data.user)
    router.push('/')
  } catch (err) {
    error.value = err.response?.data?.message || 'Échec de la connexion. Veuillez réessayer.'
  } finally {
    loading.value = false
  }
}
</script>