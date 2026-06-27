// 订单状态映射
export const ORDER_STATUS_MAP = {
  PENDING_PAYMENT: '待支付',
  PENDING_DELIVERY: '待交付',
  COMPLETED: '已完成',
  CLOSED: '已关闭'
}

// 交易方式映射
export const TRADE_TYPE_MAP = {
  FACE_TO_FACE: '当面交易',
  SELF_PICKUP: '自提'
}

// 商品状态映射
export const PRODUCT_STATUS_MAP = {
  ACTIVE: '在售',
  SOLD: '已售出',
  CLOSED: '已下架'
}

// 商品成色 → CSS class 映射
export const QUALITY_CLASS_MAP = {
  '全新': 'quality-new',
  '99新': 'quality-almost-new',
  '95新': 'quality-excellent',
  '9成新': 'quality-good',
  '8成新': 'quality-fair',
  '7成新及以下': 'quality-poor'
}

// 商品分类选项
export const CATEGORIES = [
  { label: '数码电子', value: '数码电子' },
  { label: '书籍教材', value: '书籍教材' },
  { label: '生活用品', value: '生活用品' },
  { label: '衣物鞋帽', value: '衣物鞋帽' },
  { label: '交通工具', value: '交通工具' },
  { label: '其他', value: '其他' }
]

// 商品成色选项
export const QUALITIES = [
  { label: '全新', value: '全新' },
  { label: '99新', value: '99新' },
  { label: '95新', value: '95新' },
  { label: '9成新', value: '9成新' },
  { label: '8成新', value: '8成新' },
  { label: '7成新及以下', value: '7成新及以下' }
]

// 交易方式选项
export const TRADE_METHODS = [
  { value: 'FACE_TO_FACE', label: '当面交易' },
  { value: 'SELF_PICKUP', label: '自提' }
]

// 获取状态文本
export function getOrderStatusText(status) {
  return ORDER_STATUS_MAP[status] || status
}

export function getTradeTypeText(type) {
  return TRADE_TYPE_MAP[type] || type
}

export function getProductStatusText(status) {
  return PRODUCT_STATUS_MAP[status] || status
}

export function getQualityClass(quality) {
  return QUALITY_CLASS_MAP[quality] || 'quality-other'
}

// 订单状态颜色映射（用于 StatusBadge）
export const ORDER_STATUS_COLORS = {
  PENDING_PAYMENT: { bg: '#FFF3E0', color: '#E65100' },
  PENDING_DELIVERY: { bg: '#E3F2FD', color: '#1976D2' },
  COMPLETED: { bg: '#E8F5E9', color: '#2E7D32' },
  CLOSED: { bg: '#F5F5F5', color: '#999999' }
}
