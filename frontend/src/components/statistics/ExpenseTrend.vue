<script setup lang="ts">
import { computed } from 'vue'
import { useLedger } from '../../composables/useLedger'
import { formatMoney } from '../../utils/format'

const { statPeriod, trendData } = useLedger()
const chartWidth = 320
const chartHeight = 184
const plot = { left: 38, right: 10, top: 22, bottom: 36 }
const plotWidth = chartWidth - plot.left - plot.right
const plotHeight = chartHeight - plot.top - plot.bottom
const maxValue = computed(() => Math.max(...trendData.value.map((item) => item.value), 0))
const yAxisValues = computed(() => [maxValue.value, maxValue.value / 2, 0])
const points = computed(() => trendData.value.map((item, index) => {
  const x = trendData.value.length <= 1 ? plot.left + plotWidth / 2 : plot.left + index / (trendData.value.length - 1) * plotWidth
  const y = maxValue.value === 0 ? plot.top + plotHeight : plot.top + (1 - item.value / maxValue.value) * plotHeight
  return { ...item, x, y }
}))
const linePoints = computed(() => points.value.map((point) => `${point.x},${point.y}`).join(' '))
const maxPoint = computed(() => points.value.find((point) => point.value === maxValue.value && point.value > 0))
const periodUnit = computed(() => statPeriod.value === 'week' || statPeriod.value === 'month' ? '\u6bcf\u65e5' : statPeriod.value === 'year' ? '\u6bcf\u6708' : '\u6bcf\u6708')

function yForIndex(index: number) {
  return plot.top + index * plotHeight / 2
}

function showXLabel(index: number) {
  const total = points.value.length
  if (total <= 8) return true
  const interval = Math.ceil((total - 1) / 5)
  return index === 0 || index === total - 1 || index % interval === 0
}
</script>

<template>
  <section class="expense-trend">
    <div class="section-head"><h2>&#25903;&#20986;&#36235;&#21183;</h2><span>{{ periodUnit }}</span></div>
    <div class="line-chart-wrap">
      <svg class="line-chart" viewBox="0 0 320 184" role="img" aria-label="支出趋势折线图">
        <g v-for="(value, index) in yAxisValues" :key="`y-${index}`">
          <line class="chart-grid" :x1="plot.left" :x2="chartWidth - plot.right" :y1="yForIndex(index)" :y2="yForIndex(index)" />
          <text class="chart-y-label" x="0" :y="yForIndex(index) + 4">¥{{ formatMoney(value) }}</text>
        </g>
        <line class="chart-axis" :x1="plot.left" :x2="plot.left" :y1="plot.top" :y2="plot.top + plotHeight" />
        <line class="chart-axis" :x1="plot.left" :x2="chartWidth - plot.right" :y1="plot.top + plotHeight" :y2="plot.top + plotHeight" />
        <polyline v-if="linePoints" class="chart-line" :points="linePoints" />
        <circle v-for="point in points" :key="point.label" class="chart-point" :cx="point.x" :cy="point.y" r="2.8" />
        <g v-if="maxPoint" class="chart-maximum">
          <circle :cx="maxPoint.x" :cy="maxPoint.y" r="4.5" />
          <text :x="maxPoint.x" :y="Math.max(13, maxPoint.y - 9)">¥{{ formatMoney(maxPoint.value) }}</text>
        </g>
        <text v-for="(point, index) in points" v-show="showXLabel(index)" :key="`x-${point.label}`" class="chart-x-label" :x="point.x" :y="chartHeight - 11">{{ point.label }}</text>
      </svg>
    </div>
  </section>
</template>
