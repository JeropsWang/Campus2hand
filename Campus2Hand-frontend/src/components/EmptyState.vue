<template>
  <div class="empty-state">
    <span class="empty-icon">{{ icon }}</span>
    <p class="empty-text">{{ text }}</p>
    <button v-if="actionText" class="empty-btn" @click="handleAction">{{ actionText }}</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  icon: { type: String, default: '📦' },
  text: { type: String, default: '暂无数据' },
  actionText: { type: String, default: '' },
  actionLink: { type: String, default: '' }
})

const emit = defineEmits(['action'])
const router = useRouter()

function handleAction() {
  if (props.actionLink) {
    router.push(props.actionLink)
  }
  emit('action')
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}
.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-text { font-size: 16px; color: var(--text-muted, #999); margin: 0 0 24px; }
.empty-btn {
  padding: 12px 32px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: var(--primary-color, #1A1A1A);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s;
}
.empty-btn:hover { background: var(--primary-hover, #333); }
</style>
