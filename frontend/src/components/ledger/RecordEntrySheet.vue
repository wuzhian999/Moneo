<script setup lang="ts">
import { computed, ref } from 'vue'
import { useLedger } from '../../composables/useLedger'
import { useTheme } from '../../composables/useTheme'
import type { LedgerRecord, RecordType } from '../../types/ledger'
import DateTimePickerSheet from './DateTimePickerSheet.vue'
import { magicCategoryIcon } from '../../utils/magicIcon'

const props = defineProps<{ initialType: RecordType; record?: LedgerRecord | null }>()
const emit = defineEmits<{ close: []; notify: [message: string] }>()
const { addRecord, updateRecord, expenseCategories, incomeCategories, selectedMonth } = useLedger()
const now = new Date()
const recordType = ref<RecordType>(props.record?.type ?? props.initialType)
const selectedCategoryId = ref<number | null>(props.record?.categoryId ?? null)
const amount = ref(props.record ? String(props.record.amount) : '')
const note = ref(props.record?.note ?? '')
const selectedDate = ref(props.record?.date ?? initialDate())
const selectedTime = ref(props.record?.time ?? `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`)
const isSaving = ref(false)
const isDateTimePickerOpen = ref(false)
const categoryOptions = computed(() => recordType.value === 'expense' ? expenseCategories.value : incomeCategories.value)
const isEditing = computed(() => Boolean(props.record))
const { theme } = useTheme()

function initialDate() {
  const [year, month] = selectedMonth.value.split('-').map(Number)
  const day = Math.min(now.getDate(), new Date(year, month, 0).getDate())
  return `${selectedMonth.value}-${String(day).padStart(2, '0')}`
}

function changeType(type: RecordType) {
  recordType.value = type
  selectedCategoryId.value = categoryOptions.value[0]?.id ?? null
}

function updateDateTime(date: string, time: string) {
  selectedDate.value = date
  selectedTime.value = time
  isDateTimePickerOpen.value = false
}

async function saveRecord() {
  const value = Number(amount.value)
  if (!value || value <= 0) { emit('notify', '请输入正确金额'); return }
  const categoryId = selectedCategoryId.value ?? categoryOptions.value[0]?.id
  if (!categoryId) { emit('notify', '分类加载中，请稍后重试'); return }
  isSaving.value = true
  try {
    if (props.record) await updateRecord(props.record.id, recordType.value, categoryId, props.record.accountId, value, note.value, selectedDate.value, selectedTime.value)
    else await addRecord(recordType.value, categoryId, value, note.value, selectedDate.value, selectedTime.value)
    emit('close')
    emit('notify', isEditing.value ? '账单已更新' : '已记一笔')
  } catch (error) {
    emit('notify', error instanceof Error ? error.message : '保存失败，请重试')
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <section class="record-sheet sheet-panel">
    <div class="record-sheet-head"><button aria-label="关闭" @click="$emit('close')">×</button><div class="type-switch"><button :class="{ selected: recordType === 'expense' }" @click="changeType('expense')">支出</button><button :class="{ selected: recordType === 'income' }" @click="changeType('income')">收入</button></div><span></span></div>
    <div class="amount-input"><span>¥</span><input v-model="amount" inputmode="decimal" autofocus placeholder="0.00" /></div>
    <div class="category-grid"><button v-for="category in categoryOptions" :key="category.id" :class="{ selected: (selectedCategoryId ?? categoryOptions[0]?.id) === category.id }" @click="selectedCategoryId = category.id"><span :style="{ background: category.color }">{{ theme === 'magic' ? magicCategoryIcon(category.name, category.icon) : category.icon }}</span>{{ category.name }}</button></div>
    <label class="note-field"><span>备注</span><input v-model="note" placeholder="写点什么" /></label>
    <button class="datetime-field" @click="isDateTimePickerOpen = true"><span>日期与时间</span><div><b>{{ selectedDate }}</b><b>{{ selectedTime }}</b></div></button>
    <button class="submit-btn" :disabled="isSaving" @click="saveRecord">{{ isSaving ? '保存中…' : isEditing ? '保存修改' : '保存账单' }}</button>
    <DateTimePickerSheet v-if="isDateTimePickerOpen" :date="selectedDate" :time="selectedTime" @close="isDateTimePickerOpen = false" @confirm="updateDateTime" />
  </section>
</template>
