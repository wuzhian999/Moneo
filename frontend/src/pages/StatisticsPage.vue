<script setup lang="ts">
import CategoryBreakdown from '../components/statistics/CategoryBreakdown.vue'
import ExpenseTrend from '../components/statistics/ExpenseTrend.vue'
import { useLedger } from '../composables/useLedger'
import type { StatisticsPeriod } from '../types/ledger'
import { formatMoney } from '../utils/format'

const { statPeriod, statRangeLabel, statExpense, statIncome, statAverageLabel, statAverage, selectStatPeriod, moveStatPeriod } = useLedger()
const periods: { key: StatisticsPeriod; text: string }[] = [
  { key: 'week', text: '周' }, { key: 'month', text: '月' }, { key: 'year', text: '年' }, { key: 'all', text: '全部' },
]
</script>

<template>
  <section class="page statistics-page">
    <header class="statistics-header"><h1>统计</h1></header>
    <div class="period-tabs"><button v-for="period in periods" :key="period.key" :class="{ active: statPeriod === period.key }" @click="selectStatPeriod(period.key)">{{ period.text }}</button></div>
    <div class="range-line"><button aria-label="上一个周期" :disabled="statPeriod === 'all'" @click="moveStatPeriod(-1)">‹</button><span>{{ statRangeLabel }}</span><button aria-label="下一个周期" :disabled="statPeriod === 'all'" @click="moveStatPeriod(1)">›</button></div>
    <section class="overview-panel"><div class="overview-title"><span class="overview-dot">¥</span><h2>收支</h2></div><div class="stat-grid"><div><span>支出</span><b>¥{{ formatMoney(statExpense) }}</b></div><div><span>收入</span><b>¥{{ formatMoney(statIncome) }}</b></div><div><span>结余</span><b>¥{{ formatMoney(statIncome - statExpense) }}</b></div><div><span>{{ statAverageLabel }}</span><b>¥{{ formatMoney(statAverage) }}</b></div></div></section>
    <ExpenseTrend />
    <CategoryBreakdown />
  </section>
</template>
