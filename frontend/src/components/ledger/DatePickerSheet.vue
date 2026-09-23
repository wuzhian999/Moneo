<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{ date: string }>()
const emit = defineEmits<{ close: []; confirm: [date: string] }>()
const selectedDate = ref(props.date)
const visibleMonth = ref(monthStart(props.date))
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

watch(() => props.date, (date) => {
  selectedDate.value = date
  visibleMonth.value = monthStart(date)
})

const monthLabel = computed(() => {
  const [year, month] = visibleMonth.value.split('-').map(Number)
  return `${year} 年 ${month} 月`
})

const calendarDays = computed(() => {
  const [year, month] = visibleMonth.value.split('-').map(Number)
  const firstWeekday = new Date(year, month - 1, 1).getDay()
  const totalDays = new Date(year, month, 0).getDate()
  return [
    ...Array.from({ length: firstWeekday }, () => null),
    ...Array.from({ length: totalDays }, (_, index) => index + 1),
  ]
})

function monthStart(date: string) {
  return date.slice(0, 7)
}

function moveMonth(delta: number) {
  const [year, month] = visibleMonth.value.split('-').map(Number)
  const next = new Date(year, month - 1 + delta, 1)
  visibleMonth.value = `${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, '0')}`
}

function chooseDay(day: number) {
  selectedDate.value = `${visibleMonth.value}-${String(day).padStart(2, '0')}`
}

function confirm() {
  emit('confirm', selectedDate.value)
}
</script>

<template>
  <div class="date-picker-mask" @click="emit('close')"></div>
  <section class="date-picker" role="dialog" aria-modal="true" aria-label="选择账单日期">
    <header class="date-picker-head">
      <b>{{ monthLabel }}</b>
      <div><button aria-label="上个月" @click="moveMonth(-1)">‹</button><button aria-label="下个月" @click="moveMonth(1)">›</button></div>
    </header>
    <div class="calendar-weekdays"><span v-for="weekday in weekdays" :key="weekday">{{ weekday }}</span></div>
    <div class="calendar-days">
      <div v-for="(day, index) in calendarDays" :key="`${visibleMonth}-${index}`" class="calendar-day-wrap">
        <button v-if="day" :class="{ selected: `${visibleMonth}-${String(day).padStart(2, '0')}` === selectedDate }" @click="chooseDay(day)">{{ day }}</button>
      </div>
    </div>
    <footer class="date-picker-actions"><button @click="emit('close')">取消</button><button class="confirm" @click="confirm">确定</button></footer>
  </section>
</template>
