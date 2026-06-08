import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由懒加载
const Login = () => import('@/views/Login.vue')
const Test = () => import('@/views/Test.vue')
const NotFound = () => import('@/views/NotFound.vue')

const routes = [
  {
    path: '/',
    redirect: '/login'
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
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 如果已登录且访问登录页，重定向到测试页
    if (to.path === '/login') {
      const userStore = useUserStore()
      if (userStore.isLoggedIn) {
        next('/test')
      } else {
        next()
      }
    } else {
      next()
    }
  }
})

export default router