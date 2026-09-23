<script setup lang="ts">
import { computed, ref } from 'vue'
import { useLedger } from '../../composables/useLedger'
import type { RecordType } from '../../types/ledger'

const props = defineProps<{ initialType: RecordType }>()
const emit = defineEmits<{ close: []; notify: [message: string] }>()
const { addRecord, expenseCategories, incomeCategories } = useLedger()
const recordType = ref<RecordType>(props.initialType)
const selectedCategoryId = ref<number | null>(null)
const amount = ref('')
const note = ref('')
const isSaving = ref(false)
const categoryOptions = computed(() => recordType.value === 'expense' ? expenseCategories.value : incomeCategories.value)

function changeType(type: RecordType) {
  recordType.value = type
  selectedCategoryId.value = categoryOptions.value[0]?.id ?? null
}
async function saveRecord() {
  const value = Number(amount.value)
  if (!value || value <= 0) { emit('notify', '请输入正确金额'); return }
  const categoryId = selectedCategoryId.value ?? categoryOptions.value[0]?.id
  if (!categoryId) { emit('notify', '分类加载中，请稍后重试'); return }
  isSaving.value = true
  try {
    await addRecord(recordType.value, categoryId, value, note.value)
    emit('close')
    emit('notify', '已记一笔')
  } catch (error) {
    emit('notify', error instanceof Error ? error.message : '保存失败，请重试')
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <section class="record-sheet sheet-panel">
    <div class="record-sheet-head"><button @click="$emit('close')">×</button><div class="type-switch"><button :class="{ selected: recordType === 'expense' }" @click="changeType('expense')">支出</button><button :class="{ selected: recordType === 'income' }" @click="changeType('income')">收入</button></div><span></span></div>
    <div class="amount-input"><span>¥</span><input v-model="amount" inputmode="decimal" autofocus placeholder="0.00" /></div>
    <div class="category-grid"><button v-for="category in categoryOptions" :key="category.id" :class="{ selected: (selectedCategoryId ?? categoryOptions[0]?.id) === category.id }" @click="selectedCategoryId = category.id"><span :style="{ background: category.color }">{{ category.icon }}</span>{{ category.name }}</button></div>
    <label class="note-field"><span>备注</span><input v-model="note" placeholder="写点什么" /></label>
    <button class="submit-btn" :disabled="isSaving" @click="saveRecord">{{ isSaving ? '保存中…' : '保存账单' }}</button>
  </section>
</template>
