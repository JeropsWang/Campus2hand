import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由懒加载
const Login = () => import('@/views/Login.vue')
const Home = () => import('@/views/Home.vue')
const Test = () => import('@/views/Test.vue')
const NotFound = () => import('@/views/NotFound.vue')
const Profile = () => import('@/views/Profile.vue')

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: '首页',
      requiresAuth: false // 暂时关闭登录验证，确保能访问首页
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/test',
    name: 'Test',
    component: Test,
    meta: {
      title: '测试页面',
      requiresAuth: true
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: {
      title: '个人中心',
      requiresAuth: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: {
      title: '404 - 页面不存在'
    }
  }
]

const router = createRouter({
  // 使用HTML5 History模式，支持前端路由
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - Campus2Hand` : 'Campus2Hand'

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    console.log('[Router] 检查登录状态:', {
      isLoggedIn: userStore.isLoggedIn,
      hasToken: !!userStore.token,
      hasUserInfo: !!userStore.userInfo
    })
    
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      console.log('[Router] 未登录，跳转到登录页')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 不需要登录的页面直接放行，登录后的跳转由Login.vue处理
    next()
  }
})

export default router