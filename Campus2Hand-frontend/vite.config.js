import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import os from 'os'

// 使用用户目录作为缓存目录，避免权限问题
const cacheDir = resolve(os.homedir(), '.vite-cache', 'campus2hand')

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  cacheDir: cacheDir,
  server: {
    port: 5173,
    host: true,
    // API代理配置
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
        // 不配置rewrite，保留/api前缀由后端网关的StripPrefix=1处理
      },
      // 头像静态资源代理
      '/avatars': {
        target: 'http://localhost:8081', // user-service端口
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/avatars/, '/avatars')
      },
      // 商品图片静态资源代理 - 直接请求后端端口
      '/products': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    // 路由懒加载优化
    rollupOptions: {
      output: {
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          'axios-vendor': ['axios']
        }
      }
    }
  },
  // 依赖优化配置
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios']
  }
})