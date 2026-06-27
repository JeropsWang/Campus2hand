import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

export function useUser() {
  const userStore = useUserStore()

  const currentUser = computed(() => {
    return userStore.userInfo
  })

  const isLoggedIn = computed(() => userStore.isLoggedIn)

  const userId = computed(() => {
    return userStore.userInfo?.id || userStore.userInfo?.userId || null
  })

  function getCurrentUserId() {
    // 优先从 store，其次从 localStorage，不使用硬编码 fallback
    if (userStore.userInfo) {
      return userStore.userInfo.id || userStore.userInfo.userId || null
    }
    const stored = localStorage.getItem('user')
    if (stored) {
      try {
        const user = JSON.parse(stored)
        return user.id || user.userId || null
      } catch {
        return null
      }
    }
    return null
  }

  function getUserInitial() {
    const user = currentUser.value
    if (!user) return '用'
    return (user.nickname || user.name || '用').charAt(0)
  }

  function getUserAvatar() {
    return currentUser.value?.avatar || ''
  }

  return {
    currentUser,
    isLoggedIn,
    userId,
    getCurrentUserId,
    getUserInitial,
    getUserAvatar,
    userStore
  }
}
