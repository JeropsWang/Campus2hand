<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 装饰角 -->
      <div class="decoration-corner decoration-top"></div>
      <div class="decoration-corner decoration-bottom"></div>

      <!-- Logo区域 -->
      <div class="logo-section">
        <svg class="logo-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 4C12.954 4 4 12.954 4 24s8.954 20 20 20 20-8.954 20-20S35.046 4 24 4zm0 36c-8.837 0-16-7.163-16-16S15.163 8 24 8s16 7.163 16 16-7.163 16-16 16z" fill="#1a1a1a"/>
          <path d="M16 20v16l12-8-12-8z" fill="#ffffff"/>
        </svg>
        <h1 class="logo-title">Campus2Hand</h1>
        <p class="logo-subtitle">校园二手交易平台</p>
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="studentId" class="form-label">学号</label>
          <div class="input-wrapper">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            <input
              type="text"
              id="studentId"
              v-model="formData.studentId"
              class="form-input"
              placeholder="请输入学号"
              autocomplete="off"
              required
            />
          </div>
          <span v-if="errors.studentId" class="error-message">{{ errors.studentId }}</span>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">密码</label>
          <div class="input-wrapper">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="formData.password"
              class="form-input"
              placeholder="请输入密码"
              required
            />
            <button type="button" class="toggle-password" @click="togglePassword">
              <svg v-if="!showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                <line x1="15" y1="12" x2="9" y2="12"/>
              </svg>
            </button>
          </div>
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>

        <button type="submit" class="login-button" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          <span>{{ loading ? '登录中...' : '登录' }}</span>
        </button>
      </form>

      <!-- 额外链接 -->
      <div class="extra-links">
        <a href="#" class="link">忘记密码?</a>
        <span class="divider"></span>
        <a href="#" class="link">注册账号</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { validators } from '@/utils'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单数据
const formData = reactive({
  studentId: '',
  password: ''
})

// 错误信息
const errors = reactive({
  studentId: '',
  password: ''
})

// 状态
const loading = ref(false)
const showPassword = ref(false)

// 切换密码可见性
function togglePassword() {
  showPassword.value = !showPassword.value
}

// 表单验证
function validateForm() {
  let isValid = true
  errors.studentId = ''
  errors.password = ''

  if (!formData.studentId.trim()) {
    errors.studentId = '请输入学号'
    isValid = false
  }

  if (!formData.password) {
    errors.password = '请输入密码'
    isValid = false
  }

  return isValid
}

// 处理登录
async function handleLogin() {
  if (!validateForm()) return

  loading.value = true

  try {
    const result = await userStore.login(formData.studentId, formData.password)

    if (result.success) {
      // 登录成功，直接跳转到首页
      console.log('[Login] 登录成功，跳转到首页')
      window.location.href = '/'
    } else {
      // 登录失败，显示错误信息
      alert(result.message)
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
  z-index: 1;
}

.login-card {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05), 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 40px;
  position: relative;
  width: 100%;
  max-width: 420px;
  animation: fadeIn 0.3s ease;
}

.decoration-corner {
  position: absolute;
  width: 100px;
  height: 100px;
  border: 2px solid rgba(26, 26, 26, 0.1);
}

.decoration-top {
  top: -2px;
  right: -2px;
  border-bottom: none;
  border-left: none;
  border-radius: 0 24px 0 0;
}

.decoration-bottom {
  bottom: -2px;
  left: -2px;
  border-top: none;
  border-right: none;
  border-radius: 0 0 0 24px;
}

.logo-section {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  transition: transform 0.3s ease;
}

.logo-icon:hover {
  transform: scale(1.05);
}

.logo-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.logo-subtitle {
  font-size: 14px;
  color: #999999;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  width: 18px;
  height: 18px;
  color: #999999;
  transition: color 0.3s ease;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 14px 16px 14px 50px;
  font-size: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  background: #fafafa;
  color: #1a1a1a;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #1a1a1a;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(26, 26, 26, 0.05);
}

.form-input:focus + .input-icon {
  color: #1a1a1a;
}

.form-input::placeholder {
  color: #cccccc;
}

.toggle-password {
  position: absolute;
  right: 16px;
  background: none;
  border: none;
  cursor: pointer;
  color: #999999;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s ease;
}

.toggle-password:hover {
  color: #1a1a1a;
}

.toggle-password svg {
  width: 18px;
  height: 18px;
}

.error-message {
  font-size: 12px;
  color: #f44336;
}

.login-button {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  background: #1a1a1a;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-button:hover:not(:disabled) {
  background: #333333;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.extra-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.link {
  font-size: 14px;
  color: #666666;
  text-decoration: none;
  transition: color 0.3s ease;
}

.link:hover {
  color: #1a1a1a;
  text-decoration: underline;
}

.divider {
  width: 1px;
  height: 16px;
  background: #e0e0e0;
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

@media (max-width: 480px) {
  .login-card {
    padding: 30px 24px;
  }

  .logo-title {
    font-size: 24px;
  }

  .form-input {
    padding: 12px 14px 12px 46px;
  }
}
</style>