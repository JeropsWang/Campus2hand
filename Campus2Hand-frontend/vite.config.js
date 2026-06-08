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
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
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