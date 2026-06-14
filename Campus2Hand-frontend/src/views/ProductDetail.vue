<template>
  <div class="product-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 商品详情内容 -->
    <template v-else-if="product">
      <!-- 顶部导航 -->
      <header class="detail-header">
        <div class="header-content">
          <button @click="goBack" class="back-btn">
            <span class="back-icon">←</span>
            返回
          </button>
          <h1 class="page-title">商品详情</h1>
          <div class="header-actions">
            <button @click="toggleFavorite" class="favorite-btn" :class="{ active: isFavorite }">
              <span>{{ isFavorite ? '❤️' : '🤍' }}</span>
            </button>
          </div>
        </div>
      </header>

      <!-- 主内容区 -->
      <main class="detail-content">
        <div class="product-container">
          <!-- 左侧：商品图片 -->
          <div class="image-section">
            <div class="main-image-wrapper">
              <img 
                :src="product.imageUrl" 
                :alt="product.title"
                class="main-image"
                @error="handleImageError"
              />
              <div v-if="!product.imageUrl" class="image-placeholder">
                <span class="placeholder-icon">📷</span>
                <span class="placeholder-text">暂无图片</span>
              </div>
            </div>
          </div>

          <!-- 右侧：商品信息 -->
          <div class="info-section">
            <!-- 商品标题 -->
            <h2 class="product-title">{{ product.title }}</h2>
            
            <!-- 价格和成色 -->
            <div class="price-row">
              <span class="price">¥{{ product.price?.toFixed(2) || '0.00' }}</span>
              <span class="quality-badge" :class="getQualityClass(product.quality)">
                {{ product.quality }}
              </span>
            </div>

            <!-- 商品标签 -->
            <div class="tags-row">
              <span class="category-tag">{{ product.category }}</span>
              <span class="status-tag" :class="product.status?.toLowerCase()">
                {{ getStatusText(product.status) }}
              </span>
            </div>

            <!-- 发布时间 -->
            <div class="publish-info">
              <span class="publish-label">发布时间：</span>
              <span class="publish-value">{{ formatDate(product.publishTime) }}</span>
            </div>

            <!-- 商品描述 -->
            <div class="description-section">
              <h3 class="section-title">商品描述</h3>
              <p class="description-text">{{ product.description || '暂无描述' }}</p>
            </div>

            <!-- 购买区域 -->
            <div class="purchase-section">
              <!-- 交易方式选择 -->
              <div class="trade-method">
                <span class="method-label">交易方式：</span>
                <div class="method-options">
                  <button 
                    v-for="method in tradeMethods" 
                    :key="method.value"
                    @click="selectedTradeMethod = method.value"
                    class="method-option"
                    :class="{ active: selectedTradeMethod === method.value }"
                  >
                    {{ method.label }}
                  </button>
                </div>
              </div>

              <!-- 购买按钮 -->
              <button 
                @click="handleBuy" 
                class="buy-btn"
                :disabled="isBuying"
              >
                <span class="btn-icon">🛒</span>
                {{ isBuying ? '处理中...' : '立即购买' }}
              </button>

              <!-- 联系卖家 -->
              <button @click="handleContactSeller" class="contact-btn">
                <span class="btn-icon">💬</span>
                联系卖家
              </button>
            </div>
          </div>
        </div>
      </main>

      <!-- 底部固定购买栏 -->
      <footer class="bottom-bar">
        <div class="bottom-left">
          <button @click="goBack" class="bottom-icon-btn">
            <span>🏠</span>
            <span>首页</span>
          </button>
          <button @click="toggleFavorite" class="bottom-icon-btn" :class="{ active: isFavorite }">
            <span>{{ isFavorite ? '❤️' : '🤍' }}</span>
            <span>收藏</span>
          </button>
        </div>
        <div class="bottom-right">
          <button @click="handleContactSeller" class="quick-contact">联系卖家</button>
          <button @click="handleBuy" class="quick-buy" :disabled="isBuying">
            {{ isBuying ? '处理中...' : '购买' }}
          </button>
        </div>
      </footer>

      <!-- 购买成功弹窗 -->
      <div v-if="showSuccessModal" class="modal-overlay" @click="closeSuccessModal">
        <div class="success-modal" @click.stop>
          <div class="success-icon">✅</div>
          <h3 class="success-title">购买成功</h3>
          <p class="success-message">订单已创建，等待卖家确认</p>
          <div class="success-actions">
            <button @click="goToOrders" class="success-btn primary">查看订单</button>
            <button @click="goBack" class="success-btn secondary">返回首页</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 商品不存在 -->
    <div v-else class="not-found-container">
      <div class="not-found-icon">📦</div>
      <h2 class="not-found-title">商品不存在</h2>
      <p class="not-found-message">该商品可能已被删除或下架</p>
      <button @click="goBack" class="not-found-btn">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { productApi, orderApi } from '@/services/api';

const route = useRoute();
const router = useRouter();

// 状态
const loading = ref(true);
const product = ref(null);
const isFavorite = ref(false);
const selectedTradeMethod = ref('FACE_TO_FACE');
const isBuying = ref(false);
const showSuccessModal = ref(false);

// 交易方式选项
const tradeMethods = [
  { value: 'FACE_TO_FACE', label: '当面交易' },
  { value: 'SELF_PICKUP', label: '自提' }
];

// 获取当前用户ID
function getCurrentUserId() {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      return user.id || user.userId || '1';
    } catch {
      return '1';
    }
  }
  return '1';
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '未知';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}

// 获取成色样式类
function getQualityClass(quality) {
  const qualityMap = {
    '全新': 'quality-new',
    '99新': 'quality-almost-new',
    '95新': 'quality-excellent',
    '9成新': 'quality-good',
    '8成新': 'quality-fair',
    '7成新': 'quality-poor'
  };
  return qualityMap[quality] || 'quality-other';
}

// 获取状态文本
function getStatusText(status) {
  const statusMap = {
    'ACTIVE': '在售',
    'SOLD': '已售出',
    'CLOSED': '已下架'
  };
  return statusMap[status] || status;
}

// 返回
function goBack() {
  router.back();
}

// 切换收藏
function toggleFavorite() {
  isFavorite.value = !isFavorite.value;
  console.log(`[ProductDetail] 收藏状态: ${isFavorite.value}`);
}

// 图片加载失败处理
function handleImageError(event) {
  console.warn('[ProductDetail] 图片加载失败');
  event.target.style.display = 'none';
}

// 联系卖家
function handleContactSeller() {
  alert('功能开发中，暂无法联系卖家');
}

// 购买商品
async function handleBuy() {
  if (isBuying.value) return;
  
  isBuying.value = true;
  console.log('[ProductDetail] 开始创建订单');
  
  try {
    const userId = getCurrentUserId();
    console.log('[ProductDetail] 用户ID:', userId);
    console.log('[ProductDetail] 商品ID:', product.value.id);
    console.log('[ProductDetail] 交易方式:', selectedTradeMethod.value);
    
    // 后端 OrderCreateReq 必填字段（根据后端验证规则）
    // - productName: 商品名称（@NotBlank，不能为空）
    // - price: 商品单价（@NotNull，不能为空）
    const orderData = {
      productId: product.value.id,
      productName: product.value.title,    // 必填：商品名称
      price: product.value.price,          // 必填：商品单价
      quantity: 1,
      tradeType: selectedTradeMethod.value,
      tradeLocation: '',                   // 可选：交易地点
      remark: ''                           // 可选：备注
    };
    
    console.log('[ProductDetail] 订单数据:', JSON.stringify(orderData, null, 2));
    console.log('[ProductDetail] 用户ID:', userId);
    
    // 后端接口: POST /api/orders?userId={userId}
    const response = await orderApi.createOrder(orderData, userId);
    console.log('[ProductDetail] 订单创建响应:', response);
    
    if (response.data && (response.data.code === 200 || response.status === 200)) {
      showSuccessModal.value = true;
      console.log('[ProductDetail] ✅ 订单创建成功');
    } else {
      throw new Error('订单创建失败');
    }
  } catch (error) {
    console.error('[ProductDetail] ❌ 创建订单失败:', error);
    alert('订单创建失败，请重试');
  } finally {
    isBuying.value = false;
  }
}

// 关闭成功弹窗
function closeSuccessModal() {
  showSuccessModal.value = false;
}

// 跳转到订单页面
function goToOrders() {
  showSuccessModal.value = false;
  router.push('/orders');
}

// 获取商品详情
async function loadProductDetail() {
  loading.value = true;
  try {
    const productId = route.params.productId;
    console.log('[ProductDetail] 商品ID:', productId);
    
    const response = await productApi.getProductDetail(productId);
    console.log('[ProductDetail] 商品详情响应:', response);
    
    // 处理后端返回的数据结构
    const resultData = response.data?.data || response.data;
    product.value = resultData;
    
    console.log('[ProductDetail] 商品数据:', JSON.stringify(product.value, null, 2));
  } catch (error) {
    console.error('[ProductDetail] 获取商品详情失败:', error);
    product.value = null;
  } finally {
    loading.value = false;
  }
}

// 初始化
onMounted(() => {
  loadProductDetail();
});
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 100px;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #E0E0E0;
  border-top-color: #1A1A1A;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 顶部导航 */
.detail-header {
  background-color: #FFFFFF;
  border-bottom: 1px solid #E0E0E0;
  padding: 12px 24px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: transparent;
  border: none;
  color: #1A1A1A;
  font-size: 14px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.back-btn:hover {
  background-color: #F5F5F5;
}

.back-icon {
  font-size: 18px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1A1A1A;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.favorite-btn {
  width: 40px;
  height: 40px;
  border: none;
  background-color: transparent;
  font-size: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}

.favorite-btn:hover {
  background-color: #FFF0F0;
}

/* 主内容区 */
.detail-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.product-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  background-color: #FFFFFF;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 图片区域 */
.image-section {
  display: flex;
  justify-content: center;
  align-items: center;
}

.main-image-wrapper {
  width: 100%;
  aspect-ratio: 1;
  background-color: #FAFAFA;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.main-image:hover {
  transform: scale(1.05);
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.placeholder-icon {
  font-size: 48px;
  opacity: 0.5;
}

.placeholder-text {
  color: #999999;
  font-size: 14px;
}

/* 信息区域 */
.info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  color: #1A1A1A;
  line-height: 1.4;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price {
  font-size: 32px;
  font-weight: 700;
  color: #E53935;
}

.quality-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.quality-new {
  background-color: #E3F2FD;
  color: #1976D2;
}

.quality-almost-new {
  background-color: #E8F5E9;
  color: #388E3C;
}

.quality-excellent {
  background-color: #FFF3E0;
  color: #F57C00;
}

.quality-good {
  background-color: #FCE4EC;
  color: #C2185B;
}

.quality-fair {
  background-color: #F5F5F5;
  color: #616161;
}

.quality-poor {
  background-color: #E0E0E0;
  color: #424242;
}

.quality-other {
  background-color: #F5F5F5;
  color: #616161;
}

.tags-row {
  display: flex;
  gap: 8px;
}

.category-tag, .status-tag {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
}

.category-tag {
  background-color: #F5F5F5;
  color: #616161;
}

.status-tag {
  background-color: #E8F5E9;
  color: #388E3C;
}

.status-tag.sold {
  background-color: #FFF3E0;
  color: #F57C00;
}

.status-tag.closed {
  background-color: #F5F5F5;
  color: #9E9E9E;
}

.publish-info {
  display: flex;
  gap: 8px;
  color: #999999;
  font-size: 14px;
}

.publish-label {
  color: #999999;
}

.publish-value {
  color: #666666;
}

/* 描述区域 */
.description-section {
  padding-top: 16px;
  border-top: 1px solid #F0F0F0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1A1A1A;
  margin-bottom: 12px;
}

.description-text {
  color: #666666;
  line-height: 1.8;
  font-size: 14px;
}

/* 购买区域 */
.purchase-section {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid #F0F0F0;
}

.trade-method {
  margin-bottom: 16px;
}

.method-label {
  font-size: 14px;
  color: #666666;
  margin-bottom: 8px;
  display: block;
}

.method-options {
  display: flex;
  gap: 12px;
}

.method-option {
  flex: 1;
  padding: 12px;
  border: 2px solid #E0E0E0;
  border-radius: 10px;
  background-color: #FFFFFF;
  font-size: 14px;
  color: #666666;
  cursor: pointer;
  transition: all 0.2s;
}

.method-option:hover {
  border-color: #1A1A1A;
}

.method-option.active {
  border-color: #1A1A1A;
  background-color: #F8F8F8;
  color: #1A1A1A;
  font-weight: 500;
}

.buy-btn {
  width: 100%;
  padding: 16px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
  margin-bottom: 12px;
}

.buy-btn:hover:not(:disabled) {
  background-color: #333333;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.buy-btn:active:not(:disabled) {
  transform: translateY(0);
}

.buy-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 18px;
}

.contact-btn {
  width: 100%;
  padding: 14px;
  background-color: transparent;
  color: #666666;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.contact-btn:hover {
  border-color: #1A1A1A;
  color: #1A1A1A;
}

/* 底部固定栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #FFFFFF;
  border-top: 1px solid #E0E0E0;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
}

.bottom-left {
  display: flex;
  gap: 24px;
}

.bottom-icon-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  background-color: transparent;
  border: none;
  cursor: pointer;
  color: #666666;
  font-size: 12px;
  transition: color 0.2s;
}

.bottom-icon-btn span:first-child {
  font-size: 20px;
}

.bottom-icon-btn:hover,
.bottom-icon-btn.active {
  color: #1A1A1A;
}

.bottom-right {
  display: flex;
  gap: 12px;
}

.quick-contact {
  padding: 12px 20px;
  background-color: transparent;
  border: 1px solid #1A1A1A;
  color: #1A1A1A;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-contact:hover {
  background-color: #F8F8F8;
}

.quick-buy {
  padding: 12px 32px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-buy:hover:not(:disabled) {
  background-color: #333333;
  transform: scale(1.02);
}

.quick-buy:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 成功弹窗 */
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
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.success-modal {
  background-color: #FFFFFF;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  max-width: 360px;
  width: 90%;
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.success-icon {
  width: 80px;
  height: 80px;
  background-color: #E8F5E9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin: 0 auto 24px;
}

.success-title {
  font-size: 20px;
  font-weight: 600;
  color: #1A1A1A;
  margin-bottom: 8px;
}

.success-message {
  color: #666666;
  font-size: 14px;
  margin-bottom: 32px;
}

.success-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.success-btn {
  padding: 14px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.success-btn.primary {
  background-color: #1A1A1A;
  color: #FFFFFF;
}

.success-btn.primary:hover {
  background-color: #333333;
}

.success-btn.secondary {
  background-color: #F5F5F5;
  color: #666666;
}

.success-btn.secondary:hover {
  background-color: #E8E8E8;
}

/* 商品不存在 */
.not-found-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px;
}

.not-found-icon {
  font-size: 80px;
  margin-bottom: 24px;
}

.not-found-title {
  font-size: 24px;
  font-weight: 600;
  color: #1A1A1A;
  margin-bottom: 12px;
}

.not-found-message {
  color: #999999;
  font-size: 14px;
  margin-bottom: 32px;
}

.not-found-btn {
  padding: 14px 32px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.not-found-btn:hover {
  background-color: #333333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-container {
    grid-template-columns: 1fr;
    padding: 16px;
  }

  .product-title {
    font-size: 20px;
  }

  .price {
    font-size: 28px;
  }

  .bottom-bar {
    padding: 12px 16px;
  }

  .bottom-left {
    gap: 16px;
  }

  .quick-contact {
    padding: 12px 16px;
    font-size: 12px;
  }

  .quick-buy {
    padding: 12px 24px;
    font-size: 12px;
  }
}
</style>