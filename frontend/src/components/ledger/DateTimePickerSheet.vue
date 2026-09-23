<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{ date: string; time: string }>()
const emit = defineEmits<{ close: []; confirm: [date: string, time: string] }>()

const selectedDate = ref(props.date)
const selectedTime = ref(props.time)
const visibleMonth = ref(monthStart(props.date))
const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const hours = Array.from({ length: 24 }, (_, index) => String(index).padStart(2, '0'))
const minutes = Array.from({ length: 60 }, (_, index) => String(index).padStart(2, '0'))

watch(() => props.date, (date) => {
  selectedDate.value = date
  visibleMonth.value = monthStart(date)
})
watch(() => props.time, (time) => { selectedTime.value = time })

const monthLabel = computed(() => {
  const [year, month] = visibleMonth.value.split('-').map(Number)
  return `${year} 年 ${month} 月`
})
const hour = computed({
  get: () => selectedTime.value.slice(0, 2),
  set: (value: string) => { selectedTime.value = `${value}:${selectedTime.value.slice(3, 5)}` },
})
const minute = computed({
  get: () => selectedTime.value.slice(3, 5),
  set: (value: string) => { selectedTime.value = `${selectedTime.value.slice(0, 2)}:${value}` },
})
const calendarDays = computed(() => {
  const [year, month] = visibleMonth.value.split('-').map(Number)
  const firstWeekday = new Date(year, month - 1, 1).getDay()
  const totalDays = new Date(year, month, 0).getDate()
  return Array.from({ length: firstWeekday + totalDays }, (_, index) => index < firstWeekday ? '' : index - firstWeekday + 1)
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
  emit('confirm', selectedDate.value, selectedTime.value)
}
</script>

<template>
  <div class="datetime-picker-mask" @click="emit('close')"></div>
  <section class="datetime-picker" role="dialog" aria-modal="true" aria-label="选择账单日期和时间">
    <header class="datetime-picker-head">
      <b>{{ monthLabel }}</b>
      <div><button aria-label="上个月" @click="moveMonth(-1)">‹</button><button aria-label="下个月" @click="moveMonth(1)">›</button></div>
    </header>
    <div class="calendar-weekdays"><span v-for="weekday in weekdays" :key="weekday">周{{ weekday }}</span></div>
    <div class="calendar-days">
      <span v-for="(day, index) in calendarDays" :key="`${visibleMonth}-${index}`" class="calendar-day-wrap">
        <button v-if="day" :class="{ selected: selectedDate === `${visibleMonth}-${String(day).padStart(2, '0')}` }" @click="chooseDay(day)">{{ day }}</button>
      </span>
    </div>
    <div class="picker-time-row">
      <strong>时间</strong>
      <div class="picker-time-controls">
        <select v-model="hour" aria-label="小时"><option v-for="value in hours" :key="value" :value="value">{{ value }}</option></select>
        <span>:</span>
        <select v-model="minute" aria-label="分钟"><option v-for="value in minutes" :key="value" :value="value">{{ value }}</option></select>
      </div>
    </div>
    <footer class="datetime-picker-actions"><button @click="emit('close')">取消</button><button class="confirm" @click="confirm">确定</button></footer>
  </section>
</template>
