<template>
  <v-tooltip location="bottom" :text="isDark ? '切换浅色模式' : '切换深色模式'">
    <template #activator="{ props }">
      <v-btn v-bind="props" icon variant="text" @click="toggleTheme">
        <v-icon>{{ isDark ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
      </v-btn>
    </template>
  </v-tooltip>
</template>

<script setup>
import { computed } from 'vue'
import { useTheme } from 'vuetify'
import { useThemeStore } from '@/stores/themeStore'

const themeStore = useThemeStore()
const theme = useTheme()

const isDark = computed(() => themeStore.isDark)

function toggleTheme() {
  themeStore.toggle()
  if (theme && theme.global && theme.global.name) {
    theme.global.name.value = themeStore.theme
  }
}
</script>

<style scoped>
</style>
