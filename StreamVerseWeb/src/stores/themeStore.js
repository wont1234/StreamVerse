import { defineStore } from 'pinia'

const STORAGE_KEY = 'theme'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    theme: 'light',
  }),
  getters: {
    isDark: (state) => state.theme === 'dark',
  },
  actions: {
    initFromStorage() {
      try {
        const v = localStorage.getItem(STORAGE_KEY)
        if (v === 'light' || v === 'dark') {
          this.theme = v
        }
      } catch (e) {
        // ignore
      }
    },
    setTheme(name) {
      if (name !== 'light' && name !== 'dark') return
      this.theme = name
      try {
        localStorage.setItem(STORAGE_KEY, name)
      } catch (e) {
        // ignore
      }
    },
    toggle() {
      this.setTheme(this.theme === 'dark' ? 'light' : 'dark')
    },
  },
})
