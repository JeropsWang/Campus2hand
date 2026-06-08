<template>
  <div class="not-found-page">
    <h1 class="not-found-title">404</h1>
    <p class="not-found-text">页面不存在</p>
    <button class="back-btn" @click="goHome">返回首页</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/login')
}
</script>

<style scoped>
.not-found-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  text-align: center;
  padding: 20px;
  position: relative;
  z-index: 1;
}

.not-found-title {
  font-size: 120px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1;
  margin-bottom: 16px;
}

.not-found-text {
  font-size: 18px;
  color: #666666;
  margin-bottom: 32px;
}

.back-btn {
  padding: 12px 32px;
  background: #1a1a1a;
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #333333;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

@media (max-width: 640px) {
  .not-found-title {
    font-size: 80px;
  }
}
</style>