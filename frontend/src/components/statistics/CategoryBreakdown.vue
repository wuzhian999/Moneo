<script setup lang="ts">
import { useLedger } from '../../composables/useLedger'
import { formatDate, formatMoney } from '../../utils/format'

const { categoryKind, expandedCategory, categoryStats, categoryTotal, donutStyle, toggleCategory, categoryRecords } = useLedger()
</script>

<template>
  <section class="category-detail">
    <div class="section-head"><h2>{{ categoryKind === 'expense' ? '支出' : '收入' }}分类详情</h2></div>
    <div class="income-expense-switch"><button :class="{ active: categoryKind === 'expense' }" @click="categoryKind = 'expense'; expandedCategory = null">支出</button><button :class="{ active: categoryKind === 'income' }" @click="categoryKind = 'income'; expandedCategory = null">收入</button></div>
    <div v-if="categoryStats.length" class="category-chart"><div class="donut stat-donut" :style="{ background: donutStyle }"><span>{{ categoryKind === 'expense' ? '总支出' : '总收入' }}<br><b>¥{{ formatMoney(categoryTotal) }}</b></span></div><div class="chart-key"><p v-for="item in categoryStats.slice(0, 4)" :key="item.name"><i :style="{ background: item.color }"></i>{{ item.name }} <b>{{ item.percent }}%</b></p></div></div>
    <div v-else class="empty-state">这个周期还没有{{ categoryKind === 'expense' ? '支出' : '收入' }}</div>
    <div class="category-list"><div v-for="item in categoryStats" :key="item.name" class="category-item"><article @click="toggleCategory(item.name)"><span class="record-icon" :style="{ background: item.color }">{{ item.icon }}</span><div><b>{{ item.name }} <em>{{ item.percent }}%</em></b><i><span :style="{ width: `${item.percent}%`, background: item.color }"></span></i></div><strong>¥{{ formatMoney(item.amount) }}<small>{{ item.count }} 笔</small></strong><button class="detail-chevron" :class="{ expanded: expandedCategory === item.name }" :aria-label="`展开${item.name}账单`">›</button></article><div v-if="expandedCategory === item.name" class="record-details"><p v-for="record in categoryRecords(item.name)" :key="record.id"><span>{{ formatDate(record.date) }} · {{ record.time }} · {{ record.note }}</span><strong :class="record.type">{{ record.type === 'expense' ? '-' : '+' }}¥{{ formatMoney(record.amount) }}</strong></p></div></div></div>
  </section>
</template>
