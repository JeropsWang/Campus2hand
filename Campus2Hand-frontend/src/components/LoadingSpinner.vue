<template>
  <div :class="['loading-spinner-wrapper', size]">
    <div class="loading-spinner"></div>
    <span v-if="text" class="loading-text">{{ text }}</span>
  </div>
</template>

<script setup>
defineProps({
  size: { type: String, default: 'medium' },
  text: { type: String, default: '' }
})
</script>

<style scoped>
.loading-spinner-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px;
}
.loading-spinner {
  border: 3px solid var(--border-color, #E0E0E0);
  border-top-color: var(--primary-color, #1A1A1A);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
.small .loading-spinner { width: 20px; height: 20px; border-width: 2px; }
.medium .loading-spinner { width: 36px; height: 36px; border-width: 3px; }
.large .loading-spinner { width: 48px; height: 48px; border-width: 4px; }
.loading-text { font-size: 14px; color: var(--text-muted, #999); }

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
