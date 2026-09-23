import { readonly, ref } from 'vue'

export type ThemeName = 'default' | 'magic'

const storageKey = 'moneo-theme'
const theme = ref<ThemeName>('default')

function isThemeName(value: string | null): value is ThemeName {
  return value === 'default' || value === 'magic'
}

function applyTheme(nextTheme: ThemeName) {
  theme.value = nextTheme
  document.documentElement.dataset.theme = nextTheme
  window.localStorage.setItem(storageKey, nextTheme)
}

export function initializeTheme() {
  const savedTheme = window.localStorage.getItem(storageKey)
  applyTheme(isThemeName(savedTheme) ? savedTheme : 'default')
}

export function useTheme() {
  return {
    theme: readonly(theme),
    setTheme: applyTheme,
  }
}
