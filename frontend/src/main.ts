import { createApp } from 'vue'
import { router } from './router'
import './styles/theme.css'
import './styles/magic/tokens.css'
import './styles/base.css'
import './styles/overlays.css'
import './styles/navigation.css'
import './styles/magic/effects.css'
import './styles/magic/components.css'
import App from './App.vue'
import { initializeTheme } from './composables/useTheme'

initializeTheme()
createApp(App).use(router).mount('#app')
