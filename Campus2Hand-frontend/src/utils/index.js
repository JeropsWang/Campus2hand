// 防抖函数
export function debounce(func, wait = 300) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

// 节流函数
export function throttle(func, limit = 300) {
  let inThrottle
  return function executedFunction(...args) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => (inThrottle = false), limit)
    }
  }
}

// 格式化日期
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''

  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

// 格式化金额
export function formatMoney(amount) {
  if (!amount && amount !== 0) return '¥0.00'
  return `¥${parseFloat(amount).toFixed(2)}`
}

// 手机号格式化
export function formatPhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3')
}

// 邮箱格式化
export function formatEmail(email) {
  if (!email) return ''
  const [name, domain] = email.split('@')
  if (name.length <= 3) {
    return `${name}***@${domain}`
  }
  return `${name.slice(0, 3)}***@${domain}`
}

// 生成随机ID
export function generateId() {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
}

// 深拷贝
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (obj instanceof Object) {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

// 本地存储操作
export const storage = {
  get(key, defaultValue = null) {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : defaultValue
    } catch (error) {
      console.error(`Error getting ${key} from localStorage:`, error)
      return defaultValue
    }
  },

  set(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
      return true
    } catch (error) {
      console.error(`Error setting ${key} to localStorage:`, error)
      return false
    }
  },

  remove(key) {
    try {
      localStorage.removeItem(key)
      return true
    } catch (error) {
      console.error(`Error removing ${key} from localStorage:`, error)
      return false
    }
  },

  clear() {
    try {
      localStorage.clear()
      return true
    } catch (error) {
      console.error('Error clearing localStorage:', error)
      return false
    }
  }
}

// 验证工具
export const validators = {
  // 学号验证
  isStudentId(studentId) {
    return /^\d{8,12}$/.test(studentId)
  },

  // 手机号验证
  isPhone(phone) {
    return /^1[3-9]\d{9}$/.test(phone)
  },

  // 邮箱验证
  isEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  },

  // 密码强度验证（至少8位，包含字母和数字）
  isPassword(password) {
    return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(password)
  }
}

// 导出所有工具函数
export default {
  debounce,
  throttle,
  formatDate,
  formatMoney,
  formatPhone,
  formatEmail,
  generateId,
  deepClone,
  storage,
  validators
}