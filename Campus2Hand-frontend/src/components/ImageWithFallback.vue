<template>
  <div :class="['image-wrapper', customClass]">
    <img
      v-if="src"
      :src="src"
      :alt="alt"
      class="image-real"
      @error="handleError"
      v-show="!errored"
    />
    <div v-if="!src || errored" class="image-placeholder">
      <span>{{ fallbackText }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  fallbackText: { type: String, default: '图' },
  customClass: { type: String, default: '' }
})

const errored = ref(false)

function handleError() {
  errored.value = true
}
</script>

<style scoped>
.image-wrapper {
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: inherit;
  width: 100%;
  height: 100%;
}
.image-real {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted, #999);
  font-size: 24px;
}
</style>
