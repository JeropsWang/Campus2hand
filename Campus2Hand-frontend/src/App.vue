<template>
  <div id="app" class="app-container">
    <!-- 背景装饰 -->
    <BackgroundDecoration />

    <!-- 路由视图 -->
    <router-view v-slot="{ Component, route }">
      <transition name="fade" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import BackgroundDecoration from '@/components/BackgroundDecoration.vue'
</script>

<style scoped>
.app-container {
  position: relative;
  min-height: 100vh;
  width: 100%;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>