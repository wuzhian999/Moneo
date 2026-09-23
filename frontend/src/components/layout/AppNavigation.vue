<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <nav class="tabbar">
    <RouterLink to="/ledger" :class="{ active: route.name === 'ledger' }"><span>▣</span>账本</RouterLink>
    <RouterLink to="/ai" :class="{ active: route.name === 'ai' }"><span>✦</span>AI</RouterLink>
    <RouterLink to="/statistics" :class="{ active: route.name === 'statistics' }"><span>▥</span>统计</RouterLink>
  </nav>
</template>
