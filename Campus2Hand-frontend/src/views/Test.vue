<template>
  <div class="test-page">
    <!-- 头部导航 -->
    <header class="header">
      <div class="logo">
        <svg class="logo-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 4C12.954 4 4 12.954 4 24s8.954 20 20 20 20-8.954 20-20S35.046 4 24 4zm0 36c-8.837 0-16-7.163-16-16S15.163 8 24 8s16 7.163 16 16-7.163 16-16 16z" fill="#ffffff"/>
          <path d="M16 20v16l12-8-12-8z" fill="#1a1a1a"/>
        </svg>
        <span class="logo-text">Campus2Hand</span>
      </div>
      <div class="user-info">
        <span class="user-name">{{ userStore.userInfo?.name || '用户' }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="container">
      <!-- 用户信息卡片 -->
      <section class="section">
        <h2 class="section-title">用户信息</h2>
        <div class="user-card">
          <div class="avatar">
            <svg class="avatar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="user-details">
            <div class="detail-row">
              <span class="detail-label">学号：</span>
              <span class="detail-value">{{ userStore.userInfo?.studentId || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">姓名：</span>
              <span class="detail-value">{{ userStore.userInfo?.name || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">{{ userStore.userInfo?.email || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">手机号：</span>
              <span class="detail-value">{{ userStore.userInfo?.phone || '-' }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- API测试区 -->
      <section class="section">
        <h2 class="section-title">API 测试</h2>
        <div class="api-test-section">
          <div class="api-controls">
            <button class="api-btn" @click="testApi('/api/user/info')">获取用户信息</button>
            <button class="api-btn" @click="testApi('/api/user/list')">获取用户列表</button>
            <button class="api-btn secondary" @click="clearResponse">清空结果</button>
          </div>

          <div class="response-container">
            <div class="response-header">
              <span>{{ responseStatus }}</span>
            </div>
            <pre class="response-body">{{ responseContent }}</pre>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/services/api'

const router = useRouter()
const userStore = useUserStore()

const responseStatus = ref('等待测试...')
const responseContent = ref('点击上方按钮测试 API')

async function testApi(url) {
  responseStatus.value = `请求: ${url}`
  responseContent.value = '加载中...'

  try {
    const response = await userApi.getUserInfo()
    responseStatus.value = `请求成功 (${response.status})`
    responseContent.value = JSON.stringify(response.data, null, 2)
  } catch (error) {
    responseStatus.value = `请求失败 (${error.response?.status || '未知'})`
    responseContent.value = JSON.stringify(error.response?.data || error.message, null, 2)
  }
}

function clearResponse() {
  responseStatus.value = '等待测试...'
  responseContent.value = '点击上方按钮测试 API'
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.test-page {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  background: #f8f9fa;
}

.header {
  background: #1a1a1a;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
}

.logo-text {
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  color: #ffffff;
  font-size: 14px;
}

.logout-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.container {
  max-width: 1000px;
  margin: 32px auto;
  padding: 0 24px;
}

.section {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  animation: fadeIn 0.3s ease;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.user-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1a1a1a 0%, #333333 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-icon {
  width: 40px;
  height: 40px;
  color: #ffffff;
}

.user-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.detail-row {
  display: flex;
  gap: 24px;
}

.detail-label {
  color: #999999;
  font-size: 14px;
}

.detail-value {
  color: #1a1a1a;
  font-size: 14px;
  font-weight: 500;
}

.api-test-section {
  margin-top: 24px;
}

.api-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.api-btn {
  padding: 10px 20px;
  background: #1a1a1a;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.api-btn:hover {
  background: #333333;
  transform: translateY(-1px);
}

.api-btn:active {
  transform: translateY(0);
}

.api-btn.secondary {
  background: #f0f0f0;
  color: #1a1a1a;
}

.api-btn.secondary:hover {
  background: #e0e0e0;
}

.response-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.response-header {
  font-size: 12px;
  color: #666666;
  margin-bottom: 12px;
}

.response-body {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  color: #333333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 640px) {
  .header {
    padding: 12px 16px;
  }

  .logo-text {
    font-size: 16px;
  }

  .user-name {
    display: none;
  }

  .container {
    margin: 16px auto;
  }

  .section {
    padding: 16px;
  }

  .user-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .detail-row {
    justify-content: center;
  }

  .api-controls {
    flex-direction: column;
  }

  .api-btn {
    width: 100%;
  }
}
</style>