<script setup lang="ts">
import { ref } from 'vue'
import { useLedger } from '../../composables/useLedger'

const emit = defineEmits<{ close: [] }>()
const { selectedMonth, selectMonth } = useLedger()
const pickerYear = ref(Number(selectedMonth.value.slice(0, 4)))
function chooseMonth(month: number) {
  selectMonth(`${pickerYear.value}-${String(month).padStart(2, '0')}`)
  emit('close')
}
</script>

<template>
  <section class="month-sheet sheet-panel">
    <div class="sheet-title">选择月份</div>
    <div class="picker-year"><button @click="pickerYear -= 1">‹</button><b>{{ pickerYear }} 年</b><button @click="pickerYear += 1">›</button></div>
    <div class="month-options"><button v-for="month in 12" :key="month" :class="{ selected: selectedMonth === `${pickerYear}-${String(month).padStart(2, '0')}` }" @click="chooseMonth(month)">{{ month }} 月</button></div>
  </section>
</template>
