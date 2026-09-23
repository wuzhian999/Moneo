<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterView } from 'vue-router'
import AppNavigation from './components/layout/AppNavigation.vue'
import MonthPickerSheet from './components/ledger/MonthPickerSheet.vue'
import BudgetSheet from './components/ledger/BudgetSheet.vue'
import QuickEntrySheet from './components/ledger/QuickEntrySheet.vue'
import RecordEntrySheet from './components/ledger/RecordEntrySheet.vue'
import type { RecordType } from './types/ledger'
import { useLedger } from './composables/useLedger'

const activeSheet = ref<'month' | 'budget' | 'quick' | 'record' | null>(null)
const entryType = ref<RecordType>('expense')
const toastMessage = ref('')
const { initializeLedger } = useLedger()

onMounted(() => { void initializeLedger() })

function startRecord(type: RecordType) {
  entryType.value = type
  activeSheet.value = 'record'
}

function notify(message: string) {
  toastMessage.value = message
  window.setTimeout(() => { toastMessage.value = '' }, 1800)
}
</script>

<template>
  <main class="app-shell">
    <RouterView v-slot="{ Component }">
      <component :is="Component" @open-month="activeSheet = 'month'" @open-budget="activeSheet = 'budget'" />
    </RouterView>
    <button class="fab" aria-label="新增账单" @click="activeSheet = 'quick'">+</button>
    <AppNavigation />

    <div v-if="activeSheet" class="sheet-mask" @click="activeSheet = null"></div>
    <MonthPickerSheet v-if="activeSheet === 'month'" @close="activeSheet = null" />
    <BudgetSheet v-if="activeSheet === 'budget'" @close="activeSheet = null" @notify="notify" />
    <QuickEntrySheet v-if="activeSheet === 'quick'" @start="startRecord" />
    <RecordEntrySheet v-if="activeSheet === 'record'" :initial-type="entryType" @close="activeSheet = null" @notify="notify" />
    <transition name="toast"><span v-if="toastMessage" class="toast">{{ toastMessage }}</span></transition>
  </main>
</template>
