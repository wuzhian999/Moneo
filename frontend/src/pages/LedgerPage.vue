<script setup lang="ts">
import RecordGroup from '../components/ledger/RecordGroup.vue'
import { useLedger } from '../composables/useLedger'
import { formatMoney } from '../utils/format'

defineEmits<{ 'open-month': []; 'open-budget': [] }>()
const { selectedMonthLabel, selectedRecords, recordGroups, monthExpense, monthIncome, monthlyBudget, leftBudget, budgetUsagePercent, moveMonth, isLoading, loadError } = useLedger()
</script>

<template>
  <section class="page ledger-page">
    <header class="ledger-header">
      <div class="month-nav">
        <button aria-label="上一个月" @click="moveMonth(-1)">‹</button>
        <button class="month-picker-trigger" @click="$emit('open-month')">{{ selectedMonthLabel }}</button>
        <button aria-label="下一个月" @click="moveMonth(1)">›</button>
      </div>
      <button class="avatar" aria-label="个人中心">安</button>
    </header>
    <section class="hero-summary">
      <p class="summary-label">本月支出</p><strong>¥{{ formatMoney(monthExpense) }}</strong>
      <div class="summary-foot"><span>收入 <b>¥{{ formatMoney(monthIncome) }}</b></span><span>结余 <b>¥{{ formatMoney(monthIncome - monthExpense) }}</b></span></div>
    </section>
    <button class="budget-line" @click="$emit('open-budget')">
      <div><span>预算还剩</span><b v-if="monthlyBudget > 0">¥{{ formatMoney(leftBudget) }}</b><b v-else>未设置</b></div>
      <span class="budget-percent">{{ monthlyBudget > 0 ? `已用 ${budgetUsagePercent}%` : '设置本月预算' }} ›</span>
      <div class="budget-progress"><i :style="{ width: `${budgetUsagePercent}%` }"></i></div>
    </button>
    <section class="section-head"><h2>本月账单</h2><span>{{ selectedRecords.length }} 笔</span></section>
    <p v-if="isLoading" class="empty-state">正在加载账单…</p>
    <p v-else-if="loadError" class="empty-state">{{ loadError }}</p>
    <RecordGroup v-for="group in recordGroups" :key="group.date" :group="group" />
    <p v-if="!isLoading && !loadError && !recordGroups.length" class="empty-state">这个月还没有账单</p>
  </section>
</template>
