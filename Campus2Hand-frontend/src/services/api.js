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
    
    // 如果是 FormData，不设置 Content-Type，让浏览器自动处理
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
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
  // 获取商品列表 - 后端路径是 /products/list（复数）
  getProductList(params) {
    return apiClient.get('/api/products/list', { params })
  },

  // 获取商品详情
  getProductDetail(productId) {
    return apiClient.get(`/api/products/${productId}`)
  },

  // 发布商品
  createProduct(data) {
    return apiClient.post('/api/products', data)
  },

  // 修改商品
  updateProduct(data) {
    return apiClient.put('/api/products', data)
  },

  // 下架/删除商品
  deleteProduct(productId) {
    return apiClient.delete(`/api/products/${productId}`)
  },

  // 上传商品头像
  uploadAvatar(productId, formData) {
    return apiClient.post(`/api/products/${productId}/avatar`, formData)
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

  // 创建订单 - 后端需要 userId 作为 URL 参数
  createOrder(data, userId) {
    return apiClient.post(`/api/orders?userId=${userId}`, data)
  },

  // 取消订单 - 后端需要 userId 作为 URL 参数
  cancelOrder(orderId, userId, cancelReason = '') {
    return apiClient.post(`/api/orders/cancel?userId=${userId}`, { 
      orderId, 
      cancelReason 
    })
  },

  // 更新订单状态
  updateOrderStatus(orderId, status, tradeLocation = '') {
    return apiClient.put(`/api/orders/status?orderId=${orderId}&status=${status}&tradeLocation=${tradeLocation}`)
  },

  // 我的订单
  getMyOrders(userId, status = '', pageNum = 1, pageSize = 10) {
    const params = { userId, pageNum, pageSize }
    if (status) params.status = status
    return apiClient.get('/api/orders/my', { params })
  }
}

export default {
  userApi,
  productApi,
  orderApi,
  aiApi,
  apiClient
}