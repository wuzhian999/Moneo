import { createApp } from 'vue'
import { router } from './router'
import './styles/base.css'
import './styles/overlays.css'
import './styles/navigation.css'
import App from './App.vue'

createApp(App).use(router).mount('#app')
