<script setup lang="ts">
import { useTheme, type ThemeName } from '../../composables/useTheme'

const emit = defineEmits<{ close: [] }>()
const { theme, setTheme } = useTheme()

const options: { value: ThemeName; name: string; description: string; swatch: string }[] = [
  { value: 'default', name: '默认', description: '简洁明亮，专注账本', swatch: 'default' },
  { value: 'magic', name: '魔法学院', description: '羊皮纸与金色星光', swatch: 'magic' },
]

function selectTheme(value: ThemeName) {
  setTheme(value)
}
</script>

<template>
  <aside class="theme-menu" role="dialog" aria-modal="true" aria-label="主题设置">
    <header class="theme-menu-head">
      <div><p>设置</p><h2>主题外观</h2></div>
      <button aria-label="关闭主题设置" @click="emit('close')">×</button>
    </header>
    <p class="theme-menu-intro">选择你喜欢的记账氛围</p>
    <div class="theme-options">
      <button v-for="option in options" :key="option.value" class="theme-option" :class="{ selected: theme === option.value }" @click="selectTheme(option.value)">
        <span class="theme-swatch" :class="option.swatch"><i></i><b>✦</b></span>
        <span><strong>{{ option.name }}</strong><small>{{ option.description }}</small></span>
        <em v-if="theme === option.value">✓</em>
      </button>
    </div>
  </aside>
</template>
