import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/services/api'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const loading = ref(false)

  // 计算属性 - 只要有token就认为是登录状态
  const isLoggedIn = computed(() => !!token.value)

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return null

    loading.value = true
    try {
      // 获取当前用户ID（优先从已存储的用户信息中获取）
      let userId = '1';
      if (userInfo.value) {
        userId = userInfo.value.id || userInfo.value.userId || userInfo.value.studentId || '1';
      } else {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          const user = JSON.parse(storedUser);
          userId = user.id || user.userId || user.studentId || '1';
        }
      }
      
      console.log('[UserStore] fetchUserInfo, userId:', userId);
      const response = await userApi.getUserInfo(userId)
      if (response.data.code === 200) {
        userInfo.value = response.data.data
        // 确保userId字段存在
        if (!userInfo.value.userId && userInfo.value.id) {
          userInfo.value.userId = userInfo.value.id;
        }
        localStorage.setItem('user', JSON.stringify(userInfo.value));
        return userInfo.value
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      console.error('错误详情:', error.response?.data);
      // 如果获取失败，清除登录状态
      logout()
    } finally {
      loading.value = false
    }
    return null
  }

  // 登录
  async function login(studentId, password) {
    loading.value = true
    try {
      const response = await userApi.login({ studentId, password })
      console.log('[UserStore] 登录响应:', response)
      
      if (response.data.code === 200) {
        // 根据后端标准，登录返回: { token, id, userId, nickname, avatar, name, studentId }
        const loginData = response.data.data
        const newToken = loginData.token
        
        // 构建完整的用户信息对象，包含后端返回的所有字段
        const userData = {
          id: loginData.id || loginData.userId, // 后端可能返回id或userId
          userId: loginData.userId || loginData.id,
          studentId: loginData.studentId || loginData.userId || loginData.id, // 学号
          nickname: loginData.nickname || '',
          name: loginData.name || '',
          avatar: loginData.avatar || '',
          phone: loginData.phone || '',
          college: loginData.college || '',
          // 保留后端返回的其他字段
          ...loginData
        }
        
        userInfo.value = userData
        token.value = newToken
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(userData))
        
        console.log('[UserStore] 登录成功，存储用户信息:', userData)
        return { success: true, data: userData }
      }
      return { success: false, message: response.data.message || '登录失败' }
    } catch (error) {
      console.error('[UserStore] 登录失败:', error)
      return {
        success: false,
        message: error.response?.data?.message || '登录失败，请稍后重试'
      }
    } finally {
      loading.value = false
    }
  }

  // 登出
  function logout() {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 初始化 - 从localStorage恢复登录状态
  function initFromStorage() {
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')

    if (storedUser && storedToken) {
      try {
        userInfo.value = JSON.parse(storedUser)
        token.value = storedToken
      } catch (e) {
        console.error('解析存储的用户信息失败:', e)
        logout()
      }
    }
  }

  return {
    // 状态
    userInfo,
    token,
    loading,
    // 计算属性
    isLoggedIn,
    // 方法
    fetchUserInfo,
    login,
    logout,
    initFromStorage
  }
}, {
  // Pinia持久化配置
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'user',
        storage: localStorage,
        paths: ['token', 'userInfo']
      }
    ]
  }
})