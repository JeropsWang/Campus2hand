<template>
  <Teleport to="body">
    <div class="toast-container">
      <ToastItem
        v-for="t in toasts"
        :key="t.id"
        :message="t.message"
        :type="t.type"
        :visible="t.visible"
      />
    </div>
  </Teleport>
</template>

<script setup>
import { useToast } from '@/composables/useToast'
import ToastItem from './ToastItem.vue'

const { toasts } = useToast()
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
}
.toast-container > * {
  pointer-events: auto;
}
</style>
