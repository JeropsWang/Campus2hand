<template>
  <header class="page-header">
    <button v-if="showBack" class="back-btn" @click="handleBack">← 返回</button>
    <h1 class="page-title">{{ title }}</h1>
    <div class="header-spacer"></div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  title: { type: String, required: true },
  showBack: { type: Boolean, default: true }
})

const emit = defineEmits(['back'])
const router = useRouter()

function handleBack() {
  emit('back')
  router.back()
}
</script>

<style scoped>
.page-header {
  background: #fff;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.back-btn {
  font-size: 16px;
  color: var(--text-primary, #333);
  cursor: pointer;
  padding: 8px 12px;
  background: none;
  border: none;
}
.back-btn:hover { color: var(--text-secondary, #666); }
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #1A1A1A);
  margin: 0;
}
.header-spacer { width: 60px; }
</style>
