<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterView } from 'vue-router'
import AppNavigation from './components/layout/AppNavigation.vue'
import ThemeMenu from './components/layout/ThemeMenu.vue'
import MonthPickerSheet from './components/ledger/MonthPickerSheet.vue'
import BudgetSheet from './components/ledger/BudgetSheet.vue'
import QuickEntrySheet from './components/ledger/QuickEntrySheet.vue'
import RecordEntrySheet from './components/ledger/RecordEntrySheet.vue'
import type { LedgerRecord, RecordType } from './types/ledger'
import { useLedger } from './composables/useLedger'

const activeSheet = ref<'month' | 'budget' | 'quick' | 'record' | 'theme' | null>(null)
const entryType = ref<RecordType>('expense')
const editingRecord = ref<LedgerRecord | null>(null)
const toastMessage = ref('')
const { initializeLedger, removeRecord } = useLedger()

onMounted(() => { void initializeLedger() })

function startRecord(type: RecordType) {
  entryType.value = type
  editingRecord.value = null
  activeSheet.value = 'record'
}

function startEdit(record: LedgerRecord) {
  entryType.value = record.type
  editingRecord.value = record
  activeSheet.value = 'record'
}

function closeRecord() {
  activeSheet.value = null
  editingRecord.value = null
}

async function deleteRecord(record: LedgerRecord) {
  if (!window.confirm(`删除“${record.category}”这笔账单？`)) return
  try {
    await removeRecord(record.id)
    notify('账单已删除')
  } catch (error) {
    notify(error instanceof Error ? error.message : '删除失败，请重试')
  }
}

function notify(message: string) {
  toastMessage.value = message
  window.setTimeout(() => { toastMessage.value = '' }, 1800)
}
</script>

<template>
  <main class="app-shell">
    <RouterView v-slot="{ Component }">
      <component :is="Component" @open-month="activeSheet = 'month'" @open-budget="activeSheet = 'budget'" @open-theme="activeSheet = 'theme'" @edit-record="startEdit" @delete-record="deleteRecord" />
    </RouterView>
    <button class="fab" aria-label="新增账单" @click="activeSheet = 'quick'">+</button>
    <AppNavigation />

    <div v-if="activeSheet" class="sheet-mask" @click="activeSheet = null"></div>
    <MonthPickerSheet v-if="activeSheet === 'month'" @close="activeSheet = null" />
    <BudgetSheet v-if="activeSheet === 'budget'" @close="activeSheet = null" @notify="notify" />
    <ThemeMenu v-if="activeSheet === 'theme'" @close="activeSheet = null" />
    <QuickEntrySheet v-if="activeSheet === 'quick'" @start="startRecord" />
    <RecordEntrySheet v-if="activeSheet === 'record'" :initial-type="entryType" :record="editingRecord" @close="closeRecord" @notify="notify" />
    <transition name="toast"><span v-if="toastMessage" class="toast">{{ toastMessage }}</span></transition>
  </main>
</template>
