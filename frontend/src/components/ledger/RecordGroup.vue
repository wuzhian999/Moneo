<script setup lang="ts">
import type { RecordGroup } from '../../types/ledger'
import { formatDate, formatMoney } from '../../utils/format'

defineProps<{ group: RecordGroup }>()
</script>

<template>
  <section class="record-group">
    <div class="date-row"><span>{{ formatDate(group.date) }}</span><b v-if="group.expense">支出 ¥{{ formatMoney(group.expense) }}</b></div>
    <article v-for="record in group.items" :key="record.id" class="record-row">
      <span class="record-icon" :style="{ background: record.color }">{{ record.icon }}</span>
      <div class="record-info"><b>{{ record.category }}</b><span>{{ record.note }} · {{ record.time }}</span></div>
      <strong :class="record.type">{{ record.type === 'expense' ? '-' : '+' }}¥{{ formatMoney(record.amount) }}</strong>
    </article>
  </section>
</template>
