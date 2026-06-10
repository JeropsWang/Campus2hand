import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: '', // 空baseURL，路径直接写完整
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 添加Token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 统一错误处理
    const message = error.response?.data?.message || error.message || '请求失败'

    // 根据状态码处理不同错误
    switch (error.response?.status) {
      case 401:
        // 未授权，清除登录状态
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
        break
      case 403:
        console.error('没有权限访问')
        break
      case 404:
        console.error('请求的资源不存在')
        break
      case 500:
        console.error('服务器错误，请稍后重试')
        break
      default:
        if (message !== '取消请求') {
          console.error(message)
        }
    }

    return Promise.reject(error)
  }
)

// 用户API
export const userApi = {
  // 登录
  login(data) {
    return apiClient.post('/api/user/login', data)
  },

  // 获取用户信息 - 后端使用id查询
  getUserInfo(userId) {
    return apiClient.get(`/api/user/${userId}`)
  },

  // 修改用户信息 - 通过userId标识用户
  updateUserInfo(data) {
    return apiClient.put('/api/user/info', data)
  },

  // 上传头像
  uploadAvatar(userId, formData) {
    return apiClient.post(`/api/user/${userId}/avatar`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 修改密码
  changePassword(data) {
    return apiClient.put('/api/user/password', data)
  }
}

// 商品API
export const productApi = {
  // 获取商品列表 - 后端路径是 /product/list（单数）
  getProductList(params) {
    return apiClient.get('/api/product/list', { params })
  },

  // 获取商品详情
  getProductDetail(productId) {
    return apiClient.get(`/api/product/${productId}`)
  },

  // 发布商品
  createProduct(data) {
    return apiClient.post('/api/product', data)
  },

  // 修改商品
  updateProduct(data) {
    return apiClient.put('/api/product', data)
  },

  // 下架/删除商品
  deleteProduct(productId) {
    return apiClient.delete(`/api/product/${productId}`)
  }
}

// AI客服API
export const aiApi = {
  // 发送聊天消息
  chat(data) {
    return apiClient.post('/api/ai/chat', data)
  }
}

// 订单API
export const orderApi = {
  // 获取订单列表
  getOrderList(params) {
    return apiClient.get('/api/orders/list', { params })
  },

  // 获取订单详情
  getOrderDetail(orderId) {
    return apiClient.get(`/api/orders/${orderId}`)
  },

  // 创建订单
  createOrder(data) {
    return apiClient.post('/api/orders', data)
  },

  // 取消订单
  cancelOrder(orderId) {
    return apiClient.put('/api/orders/cancel', { orderId })
  }
}

export default {
  userApi,
  productApi,
  orderApi,
  aiApi,
  apiClient
}