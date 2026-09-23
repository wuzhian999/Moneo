<script setup lang="ts">
import { ref } from 'vue'
import { useTheme } from '../../composables/useTheme'
import type { LedgerRecord, RecordGroup } from '../../types/ledger'
import { magicCategoryIcon } from '../../utils/magicIcon'
import { formatDate, formatMoney } from '../../utils/format'

defineProps<{ group: RecordGroup }>()
const emit = defineEmits<{ edit: [record: LedgerRecord]; delete: [record: LedgerRecord] }>()
const openedRecordId = ref<number | null>(null)
const touchStartX = ref(0)
const wasSwipe = ref(false)
const { theme } = useTheme()

function beginSwipe(event: PointerEvent) {
  touchStartX.value = event.clientX
}

function endSwipe(event: PointerEvent, record: LedgerRecord) {
  const distance = event.clientX - touchStartX.value
  wasSwipe.value = Math.abs(distance) > 36
  if (distance > 36) openedRecordId.value = record.id
  if (distance < -36) openedRecordId.value = null
}

function closeActions(record: LedgerRecord) {
  if (openedRecordId.value === record.id) openedRecordId.value = null
}

function handleRecordClick(record: LedgerRecord) {
  if (wasSwipe.value) {
    wasSwipe.value = false
    return
  }
  closeActions(record)
}

function edit(record: LedgerRecord) {
  openedRecordId.value = null
  emit('edit', record)
}

function remove(record: LedgerRecord) {
  openedRecordId.value = null
  emit('delete', record)
}
</script>

<template>
  <section class="record-group">
    <div class="date-row"><span>{{ formatDate(group.date) }}</span><b v-if="group.expense">支出 ¥{{ formatMoney(group.expense) }}</b></div>
    <div v-for="record in group.items" :key="record.id" class="swipe-record" @pointerdown="beginSwipe" @pointerup="endSwipe($event, record)" @pointercancel="closeActions(record)">
      <div class="record-actions"><button class="edit-action" @click.stop="edit(record)">编辑</button><button class="delete-action" @click.stop="remove(record)">删除</button></div>
      <article class="record-row" :class="{ 'actions-open': openedRecordId === record.id }" @click="handleRecordClick(record)">
        <span class="record-icon" :style="{ background: record.color }">{{ theme === 'magic' ? magicCategoryIcon(record.category, record.icon) : record.icon }}</span>
        <div class="record-info"><b>{{ record.category }}</b><span>{{ record.note }} · {{ record.time }}</span></div>
        <strong :class="record.type">{{ record.type === 'expense' ? '-' : '+' }}¥{{ formatMoney(record.amount) }}</strong>
      </article>
    </div>
  </section>
</template>
