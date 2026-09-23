<script setup lang="ts">
import { useTheme } from '../composables/useTheme'

const { theme } = useTheme()
</script>

<template>
  <section class="page ai-page">
    <header class="topbar"><div><p class="eyebrow">{{ theme === 'magic' ? '魔法顾问' : 'AI 财务助手' }}</p><h1>{{ theme === 'magic' ? '翻阅你的账簿' : '聊聊你的消费' }}</h1></div><span class="spark">✦</span></header>
    <div class="ai-orb">✦</div>
    <p class="ai-intro">{{ theme === 'magic' ? '账簿会记录你花掉的每一笔钱，而我负责告诉你它们去了哪里。' : '问我本月花了什么，或者让 AI 帮你看看消费习惯。' }}</p>
    <div class="suggestion-list">
      <button>我这个月花得最多的是什么？</button>
      <button>帮我总结一下最近的消费</button>
      <button>下个月预算怎么分配更合理？</button>
    </div>
    <div class="ai-input"><span>问一点和钱有关的事</span><button>↑</button></div>
  </section>
</template>
