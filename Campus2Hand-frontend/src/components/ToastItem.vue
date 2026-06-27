<template>
  <Transition name="toast">
    <div v-if="visible" :class="['toast-item', type]">
      <span class="toast-icon">{{ icon }}</span>
      <span class="toast-message">{{ message }}</span>
    </div>
  </Transition>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: { type: String, required: true },
  type: { type: String, default: 'info' },
  visible: { type: Boolean, default: true }
})

const icon = computed(() => ({
  success: '✓',
  error: '✕',
  info: 'ℹ',
  warning: '⚠'
})[props.type] || 'ℹ')
</script>

<style scoped>
.toast-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  border-radius: 12px;
  font-size: 14px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  min-width: 200px;
  max-width: 400px;
  word-break: break-word;
}
.toast-item.success {
  background: #E8F5E9;
  color: #2E7D32;
  border: 1px solid #C8E6C9;
}
.toast-item.error {
  background: #FFEBEE;
  color: #C62828;
  border: 1px solid #FFCDD2;
}
.toast-item.info {
  background: #E3F2FD;
  color: #1565C0;
  border: 1px solid #BBDEFB;
}
.toast-item.warning {
  background: #FFF3E0;
  color: #E65100;
  border: 1px solid #FFE0B2;
}
.toast-icon {
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
}
.toast-message {
  flex: 1;
}

.toast-enter-active { transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.toast-leave-active { transition: all 0.2s ease-in; }
.toast-enter-from { opacity: 0; transform: translateX(40px) scale(0.9); }
.toast-leave-to { opacity: 0; transform: translateX(40px) scale(0.9); }
</style>
