import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './assets/styles/main.css'

const app = createApp(App)

// 挂载Pinia状态管理
const pinia = createPinia()
app.use(pinia)

// 挂载路由
app.use(router)

// 挂载到DOM
app.mount('#app')