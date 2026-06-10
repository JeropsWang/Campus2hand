<template>
  <div class="orders-page">
    <!-- 头部 -->
    <header class="orders-header">
      <div class="back-btn" @click="goBack">← 返回</div>
      <h1 class="page-title">我的订单</h1>
      <div class="placeholder"></div>
    </header>

    <!-- 主内容区 -->
    <main class="orders-content">
      <!-- 订单统计卡片 -->
      <section class="stats-section">
        <div class="stats-grid">
          <div class="stat-card" :class="{ active: currentStatus === '' }" @click="filterOrders('')">
            <span class="stat-value">{{ stats.all }}</span>
            <span class="stat-label">全部</span>
          </div>
          <div class="stat-card" :class="{ active: currentStatus === 'PENDING_PAYMENT' }" @click="filterOrders('PENDING_PAYMENT')">
            <span class="stat-value">{{ stats.pendingPayment }}</span>
            <span class="stat-label">待支付</span>
          </div>
          <div class="stat-card" :class="{ active: currentStatus === 'PENDING_DELIVERY' }" @click="filterOrders('PENDING_DELIVERY')">
            <span class="stat-value">{{ stats.pendingDelivery }}</span>
            <span class="stat-label">待交付</span>
          </div>
          <div class="stat-card" :class="{ active: currentStatus === 'COMPLETED' }" @click="filterOrders('COMPLETED')">
            <span class="stat-value">{{ stats.completed }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
      </section>

      <!-- 订单列表 -->
      <section class="orders-list-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span class="loading-text">加载中...</span>
        </div>

        <!-- 订单列表 -->
        <div v-else-if="filteredOrders.length > 0" class="orders-list">
          <div 
            v-for="order in filteredOrders" 
            :key="order.orderNo" 
            class="order-card"
            @click="showOrderDetail(order)"
          >
            <!-- 订单头部 -->
            <div class="order-header">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span :class="['order-status', order.status]">{{ getStatusText(order.status) }}</span>
            </div>

            <!-- 订单商品信息 -->
            <div class="order-body">
              <div class="product-info">
                <div class="product-image">
                  <span class="image-placeholder">商</span>
                </div>
                <div class="product-details">
                  <h3 class="product-name">{{ order.productName || '未知商品' }}</h3>
                  <p class="product-spec">数量: {{ order.quantity || 1 }}</p>
                </div>
                <div class="product-price">¥{{ formatPrice(order.totalAmount) }}</div>
              </div>
            </div>

            <!-- 订单底部 -->
            <div class="order-footer">
              <div class="trade-info">
                <span class="trade-type">{{ getTradeTypeText(order.tradeType) }}</span>
                <span class="trade-location">{{ order.tradeLocation }}</span>
              </div>
              <span class="create-time">{{ formatTime(order.createTime) }}</span>
            </div>

            <!-- 操作按钮 -->
            <div class="order-actions">
              <button 
                v-if="order.status === 'PENDING_PAYMENT'" 
                class="action-btn primary"
                @click.stop="handlePayment(order)"
              >
                立即支付
              </button>
              <button 
                v-if="order.status === 'PENDING_DELIVERY'" 
                class="action-btn success"
                @click.stop="handleConfirm(order)"
              >
                确认收货
              </button>
              <button 
                v-if="order.status === 'CLOSED'" 
                class="action-btn disabled"
                disabled
              >
                已关闭
              </button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <div class="empty-icon">📦</div>
          <p class="empty-text">暂无订单</p>
          <button class="empty-btn" @click="goShopping">去逛逛</button>
        </div>
      </section>
    </main>

    <!-- 订单详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content detail-modal" @click.stop>
        <div class="modal-header">
          <span class="modal-title">订单详情</span>
          <span class="modal-close" @click="closeDetailModal">×</span>
        </div>
        <div v-if="selectedOrder" class="modal-body">
          <div class="detail-section">
            <h4>基本信息</h4>
            <div class="detail-item">
              <span class="detail-label">订单编号</span>
              <span class="detail-value">{{ selectedOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态</span>
              <span :class="['detail-value', selectedOrder.status]">{{ getStatusText(selectedOrder.status) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatTime(selectedOrder.createTime) }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>商品信息</h4>
            <div class="detail-item">
              <span class="detail-label">商品名称</span>
              <span class="detail-value">{{ selectedOrder.productName || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">商品ID</span>
              <span class="detail-value">{{ selectedOrder.productId || selectedOrder.product_id || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">单价</span>
              <span class="detail-value">¥{{ formatPrice(selectedOrder.price) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">数量</span>
              <span class="detail-value">{{ selectedOrder.quantity || 1 }}</span>
            </div>
            <div class="detail-item total-row">
              <span class="detail-label">订单总额</span>
              <span class="detail-value total">¥{{ formatPrice(selectedOrder.totalAmount) }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>交易方式</h4>
            <div class="detail-item">
              <span class="detail-label">交易类型</span>
              <span class="detail-value">{{ getTradeTypeText(selectedOrder.tradeType) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">交易地点</span>
              <span class="detail-value">{{ selectedOrder.tradeLocation }}</span>
            </div>
            <div class="detail-item" v-if="selectedOrder.tradeTime">
              <span class="detail-label">交易时间</span>
              <span class="detail-value">{{ formatTime(selectedOrder.tradeTime) }}</span>
            </div>
          </div>

          <div v-if="selectedOrder.remark" class="detail-section">
            <h4>备注</h4>
            <p class="remark-text">{{ selectedOrder.remark }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-confirm" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { orderApi } from '@/services/api';

const router = useRouter();

// 订单数据
const orders = ref([]);
const loading = ref(true);
const currentStatus = ref('');
const showDetailModal = ref(false);
const selectedOrder = ref(null);

// 订单统计
const stats = ref({
  all: 0,
  pendingPayment: 0,
  pendingDelivery: 0,
  completed: 0,
  closed: 0
});

// 筛选后的订单
const filteredOrders = computed(() => {
  if (!currentStatus.value) {
    return orders.value;
  }
  return orders.value.filter(order => order.status === currentStatus.value);
});

// 获取用户ID（后端使用用户ID查询）
function getCurrentUserId() {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      // 后端使用用户ID查询（优先取id，其次取userId）
      return user.id || user.userId || user.studentId || '1';
    } catch {
      return '1';
    }
  }
  return '1';
}

// 加载订单数据
async function loadOrders() {
  console.log('=====================================');
  console.log('[Orders] 开始加载订单数据');
  loading.value = true;
  try {
    const userId = getCurrentUserId();
    console.log('[Orders] 请求参数 - userId:', userId);
    console.log('[Orders] 请求URL:', '/api/orders/list?userId=' + userId);
    console.log('-------------------------------------');
    
    // 后端订单接口使用用户ID查询
    const response = await orderApi.getOrderList({ userId });
    
    console.log('[Orders] 响应状态:', response.status);
    console.log('[Orders] 响应数据:', JSON.stringify(response.data, null, 2));
    console.log('-------------------------------------');
    
    if (response.data.code === 200 && response.data.data) {
      const data = response.data.data;
      // 兼容分页格式和列表格式
      // 后端返回的是分页数据：data.records 包含订单列表
      orders.value = data.records || data.list || (Array.isArray(data) ? data : []);
      console.log('[Orders] ✅ 成功获取订单数据');
      console.log('[Orders] 数据来源:', data.records ? 'records' : (data.list ? 'list' : 'data'));
      console.log('[Orders] 订单数量:', orders.value.length);
      console.log('[Orders] 订单列表:', JSON.stringify(orders.value, null, 2));
      
      // 统计订单数量
      calculateStats();
      console.log('[Orders] 订单统计:', JSON.stringify(stats.value, null, 2));
    } else {
      console.warn('[Orders] ⚠️ 后端返回非成功状态');
      console.warn('[Orders] code:', response.data?.code);
      console.warn('[Orders] message:', response.data?.message);
      orders.value = [];
      calculateStats();
    }
  } catch (error) {
    console.log('-------------------------------------');
    console.error('[Orders] ❌ 加载订单失败');
    console.error('[Orders] 错误对象:', error);
    console.error('[Orders] 错误类型:', error.name);
    console.error('[Orders] 错误消息:', error.message);
    if (error.response) {
      console.error('[Orders] 响应状态码:', error.response.status);
      console.error('[Orders] 响应数据:', JSON.stringify(error.response.data, null, 2));
      console.error('[Orders] 响应头:', error.response.headers);
    } else if (error.request) {
      console.error('[Orders] 请求已发送但无响应');
    } else {
      console.error('[Orders] 请求配置错误:', error.message);
    }
    // 不使用mock数据，保持订单列表为空
    orders.value = [];
    calculateStats();
  } finally {
    loading.value = false;
    console.log('=====================================');
  }
}

// 计算订单统计
function calculateStats() {
  stats.value = {
    all: orders.value.length,
    pendingPayment: orders.value.filter(o => o.status === 'PENDING_PAYMENT').length,
    pendingDelivery: orders.value.filter(o => o.status === 'PENDING_DELIVERY').length,
    completed: orders.value.filter(o => o.status === 'COMPLETED').length,
    closed: orders.value.filter(o => o.status === 'CLOSED').length
  };
}

// 筛选订单
function filterOrders(status) {
  currentStatus.value = status;
}

// 获取状态文本
function getStatusText(status) {
  const statusMap = {
    PENDING_PAYMENT: '待支付',
    PENDING_DELIVERY: '待交付',
    COMPLETED: '已完成',
    CLOSED: '已关闭'
  };
  return statusMap[status] || status;
}

// 获取交易类型文本
function getTradeTypeText(type) {
  const typeMap = {
    FACE_TO_FACE: '当面交易',
    SELF_PICKUP: '自提'
  };
  return typeMap[type] || type;
}

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr) return '未设置';
  try {
    const date = new Date(timeStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return timeStr;
  }
}

// 格式化价格（处理数字类型）
function formatPrice(price) {
  if (price === null || price === undefined) return '0.00';
  // 确保是数字类型
  const numPrice = typeof price === 'number' ? price : parseFloat(price);
  if (isNaN(numPrice)) return '0.00';
  return numPrice.toFixed(2);
}

// 显示订单详情
function showOrderDetail(order) {
  selectedOrder.value = order;
  showDetailModal.value = true;
}

// 关闭详情弹窗
function closeDetailModal() {
  showDetailModal.value = false;
  selectedOrder.value = null;
}

// 处理支付
function handlePayment(order) {
  alert(`正在支付订单 ${order.orderNo}，金额 ¥${order.totalAmount.toFixed(2)}`);
}

// 处理确认收货
function handleConfirm(order) {
  alert(`已确认收货，订单 ${order.orderNo}`);
}

// 返回
function goBack() {
  router.back();
}

// 去购物
function goShopping() {
  router.push('/');
}

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.orders-header {
  background-color: #ffffff;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  font-size: 16px;
  color: #333;
  cursor: pointer;
  padding: 8px 12px;
}

.back-btn:hover {
  color: #666;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.placeholder {
  width: 60px;
}

.orders-content {
  flex: 1;
  padding: 30px 50px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  border: 3px solid transparent;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-card.active {
  border-color: #333;
  background-color: #f8f8f8;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 16px;
  color: #666;
}

/* 订单列表 */
.orders-list-section {
  flex: 1;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e0e0e0;
  border-top-color: #333;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 16px;
  color: #999;
  font-size: 14px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.order-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 2px solid #f5f5f5;
  margin-bottom: 20px;
}

.order-no {
  font-size: 16px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: 600;
  padding: 8px 20px;
  border-radius: 25px;
}

.order-status.PENDING_PAYMENT {
  background-color: #fff3e0;
  color: #e65100;
}

.order-status.PENDING_DELIVERY {
  background-color: #e3f2fd;
  color: #1976d2;
}

.order-status.COMPLETED {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.order-status.CLOSED {
  background-color: #f5f5f5;
  color: #999;
}

.order-body {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.product-image {
  width: 120px;
  height: 120px;
  background-color: #f5f5f5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.image-placeholder {
  font-size: 40px;
  color: #999;
}

.product-details {
  flex: 1;
}

.product-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
}

.product-spec {
  font-size: 14px;
  color: #999;
  margin: 0 0 10px 0;
}

.product-price {
  font-size: 24px;
  font-weight: 700;
  color: #e65100;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 2px solid #f5f5f5;
}

.trade-info {
  display: flex;
  gap: 24px;
}

.trade-type,
.trade-location {
  font-size: 14px;
  color: #666;
}

.create-time {
  font-size: 12px;
  color: #999;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 20px;
}

.action-btn {
  padding: 14px 30px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn.primary {
  background-color: #333;
  color: #ffffff;
}

.action-btn.primary:hover {
  background-color: #444;
  transform: translateY(-2px);
}

.action-btn.success {
  background-color: #2e7d32;
  color: #ffffff;
}

.action-btn.success:hover {
  background-color: #388e3c;
  transform: translateY(-2px);
}

.action-btn.disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
}

.empty-icon {
  font-size: 72px;
  margin-bottom: 24px;
}

.empty-text {
  font-size: 18px;
  color: #999;
  margin: 0 0 30px 0;
}

.empty-btn {
  padding: 16px 48px;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  background-color: #333;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.empty-btn:hover {
  background-color: #444;
  transform: translateY(-2px);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 0;
  width: 90%;
  max-width: 700px;
  max-height: 85vh;
  overflow-y: auto;
  box-sizing: border-box;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 30px;
  border-bottom: 2px solid #f5f5f5;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.modal-close {
  font-size: 32px;
  color: #999;
  cursor: pointer;
  line-height: 1;
  padding: 8px;
}

.modal-close:hover {
  color: #333;
}

.modal-body {
  padding: 30px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #f5f5f5;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
}

.detail-item.total-row {
  background-color: #fff8f0;
  padding: 20px;
  margin-top: 12px;
  border-radius: 12px;
}

.detail-label {
  font-size: 15px;
  color: #666;
}

.detail-value {
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.detail-value.total {
  font-size: 24px;
  color: #e65100;
  font-weight: 700;
}

.detail-value.PENDING_PAYMENT {
  color: #e65100;
}

.detail-value.PENDING_DELIVERY {
  color: #1976d2;
}

.detail-value.COMPLETED {
  color: #2e7d32;
}

.detail-value.CLOSED {
  color: #999;
}

.remark-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}

.btn-confirm {
  padding: 10px 32px;
  font-size: 14px;
  color: #ffffff;
  background-color: #333;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-confirm:hover {
  background-color: #444;
}

@media (max-width: 600px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .product-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .product-image {
    width: 100%;
    height: 120px;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>