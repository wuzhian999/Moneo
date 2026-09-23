<script setup lang="ts">
import type { RecordType } from '../../types/ledger'

defineEmits<{ start: [type: RecordType] }>()
</script>

<template>
  <section class="quick-sheet sheet-panel">
    <div class="sheet-title">记一笔</div>
    <div class="quick-actions">
      <button @click="$emit('start', 'expense')"><span class="quick-icon expense">−</span><b>支出</b><small>记录一笔花费</small></button>
      <button @click="$emit('start', 'income')"><span class="quick-icon income">+</span><b>收入</b><small>记录一笔收入</small></button>
    </div>
  </section>
</template>
