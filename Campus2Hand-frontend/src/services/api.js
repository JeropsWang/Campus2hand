import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: '/api', // API基础路径，会通过Vite代理转发
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
    return apiClient.post('/user/login', data)
  },

  // 获取用户信息
  getUserInfo() {
    return apiClient.get('/user/info')
  },

  // 获取用户列表
  getUserList(params) {
    return apiClient.get('/user/list', { params })
  },

  // 注册
  register(data) {
    return apiClient.post('/user/register', data)
  },

  // 更新用户信息
  updateUser(data) {
    return apiClient.put('/user/update', data)
  },

  // 删除用户
  deleteUser(id) {
    return apiClient.delete(`/user/delete/${id}`)
  }
}

// 商品API
export const productApi = {
  // 获取商品列表
  getProductList(params) {
    return apiClient.get('/product/list', { params })
  },

  // 获取商品详情
  getProductDetail(id) {
    return apiClient.get(`/product/detail/${id}`)
  },

  // 发布商品
  createProduct(data) {
    return apiClient.post('/product/create', data)
  },

  // 更新商品
  updateProduct(id, data) {
    return apiClient.put(`/product/update/${id}`, data)
  },

  // 删除商品
  deleteProduct(id) {
    return apiClient.delete(`/product/delete/${id}`)
  }
}

// 订单API
export const orderApi = {
  // 获取订单列表
  getOrderList(params) {
    return apiClient.get('/order/list', { params })
  },

  // 获取订单详情
  getOrderDetail(id) {
    return apiClient.get(`/order/detail/${id}`)
  },

  // 创建订单
  createOrder(data) {
    return apiClient.post('/order/create', data)
  },

  // 取消订单
  cancelOrder(id) {
    return apiClient.put(`/order/cancel/${id}`)
  },

  // 完成订单
  completeOrder(id) {
    return apiClient.put(`/order/complete/${id}`)
  }
}

export default {
  userApi,
  productApi,
  orderApi,
  apiClient
}