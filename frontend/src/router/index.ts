import { createRouter, createWebHistory } from 'vue-router'
import LedgerPage from '../pages/LedgerPage.vue'
import AiPage from '../pages/AiPage.vue'
import StatisticsPage from '../pages/StatisticsPage.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/ledger' },
    { path: '/ledger', name: 'ledger', component: LedgerPage },
    { path: '/ai', name: 'ai', component: AiPage },
    { path: '/statistics', name: 'statistics', component: StatisticsPage },
  ],
})
