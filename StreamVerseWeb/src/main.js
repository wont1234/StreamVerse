import { createApp } from 'vue'
import { createPinia } from 'pinia'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import App from './App.vue'
import router from './router'
import HttpFetch from './utils/fetch'
import { useThemeStore } from '@/stores/themeStore'

import './assets/css/Main.css'

const app = createApp(App)

const pinia = createPinia()

const themeStore = useThemeStore(pinia)
themeStore.initFromStorage()

app.config.globalProperties.SERVER_API_URL = '/api'

if (import.meta.env.DEV) {
  const originalLog = console.log
  console.log = (...args) => {
    const first = args[0]
    if (typeof first === 'string') {
      const s = first.toLowerCase()
      if (s.includes('store installed') || s.includes('🍍')) {
        return
      }
    }
    return originalLog(...args)
  }
}

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
  },
  theme: {
    defaultTheme: themeStore.theme,
    themes: {
      light: {
        dark: false,
        colors: {
          background: '#FFFFFF',
          surface: '#F3F4F6',
          'on-background': '#111827',
          'on-surface': '#111827',
          primary: '#E50914',
          'on-primary': '#FFFFFF',
          secondary: '#111827',
          'on-secondary': '#FFFFFF',
          error: '#DC2626',
          'on-error': '#FFFFFF',
          info: '#2563EB',
          'on-info': '#FFFFFF',
          success: '#16A34A',
          'on-success': '#FFFFFF',
          warning: '#D97706',
          'on-warning': '#111827',
        },
      },
      dark: {
        dark: true,
        colors: {
          background: '#0B1220',
          surface: '#111827',
          'on-background': '#E5E7EB',
          'on-surface': '#E5E7EB',
          primary: '#E50914',
          'on-primary': '#FFFFFF',
          secondary: '#334155',
          'on-secondary': '#E5E7EB',
          error: '#F87171',
          'on-error': '#0B1220',
          info: '#60A5FA',
          'on-info': '#0B1220',
          success: '#34D399',
          'on-success': '#0B1220',
          warning: '#FBBF24',
          'on-warning': '#0B1220',
        },
      },
    },
  },
  defaults: {
    VBtn: {
      rounded: 'xl',
      variant: 'flat',
    },
    VCard: {
      rounded: 'xl',
      elevation: 1,
    },
    VTextField: {
      density: 'comfortable',
      variant: 'solo-filled',
      rounded: 'xl',
    },
    VTextarea: {
      density: 'comfortable',
      variant: 'solo-filled',
      rounded: 'xl',
    },
    VSelect: {
      density: 'comfortable',
      variant: 'solo-filled',
      rounded: 'xl',
    },
  },
})

app.use(pinia)
app.use(router)
app.use(vuetify)
app.use(HttpFetch)
app.mount('#app')
