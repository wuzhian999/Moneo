<script setup lang="ts">
import { ref } from 'vue'
import { useLedger } from '../../composables/useLedger'

const emit = defineEmits<{ close: []; notify: [message: string] }>()
const { monthlyBudget, selectedMonthLabel, setBudget } = useLedger()
const budgetInput = ref(String(monthlyBudget.value))
async function saveBudget() {
  const value = Number(budgetInput.value)
  if (!value || value <= 0) { emit('notify', '请输入正确预算'); return }
  try {
    await setBudget(value)
    emit('close')
    emit('notify', '本月预算已更新')
  } catch (error) {
    emit('notify', error instanceof Error ? error.message : '保存预算失败')
  }
}
</script>

<template>
  <section class="budget-sheet sheet-panel">
    <div class="sheet-title">本月预算</div>
    <p>设置 {{ selectedMonthLabel }} 的总支出预算</p>
    <label><span>¥</span><input v-model="budgetInput" inputmode="decimal" autofocus /></label>
    <button class="submit-btn" @click="saveBudget">保存预算</button>
  </section>
</template>
