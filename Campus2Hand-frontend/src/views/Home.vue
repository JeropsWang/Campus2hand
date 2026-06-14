<template>
  <div class="home-page">
    <!-- 顶部功能模块 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <div class="logo-icon">C2H</div>
          <span class="logo-text">Campus2Hand</span>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 搜索栏 + vlog -->
      <section class="search-vlog-section">
        <div class="search-box">
          <div class="search-icon"></div>
          <input type="text" class="search-input" placeholder="搜索二手商品..." v-model="searchKeyword" @keyup.enter="searchProducts">
          <button @click="searchProducts" class="search-btn">搜索</button>
        </div>
        <div class="vlog-area">
          <div class="vlog-play-icon"></div>
          <span>登录页面 vlog 展示区域</span>
        </div>
      </section>

      <!-- 二手商品模块 -->
      <section class="products-section">
        <div class="section-title-row">
          <h2 class="section-title">闲置二手好物</h2>
          <span class="section-more">查看更多</span>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="productsLoading" class="loading">
          <div class="loading-spinner"></div>
        </div>

        <!-- 商品网格 -->
        <div v-else-if="productsData && productsData.length > 0" class="products-grid">
          <div 
            v-for="product in productsData" 
            :key="product.id" 
            @click="handleProductClick(product.id)"
            class="product-card"
          >
            <div class="product-image">
  <img 
    v-if="product.imageUrl" 
    :src="product.imageUrl" 
    alt="商品图片"
    @error="handleProductImageError($event)"
  />
  <div v-else class="product-image-placeholder">图</div>
</div>
            <div class="product-title">{{ product.title }}</div>
            <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
            <div class="product-condition">{{ product.quality }}</div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">暂无二手商品</div>
      </section>
    </main>

    <!-- 右侧固定按钮栏 -->
    <aside class="side-bar">
      <div @click="showUserCenter" class="side-btn user-center-btn">
        <div class="btn-icon user-icon">个</div>
        <span>个人中心</span>
      </div>
      <div @click="handleAiClick" class="side-btn ai-btn">
        <div class="btn-icon ai-icon">AI</div>
        <span>AI客服</span>
      </div>
      <div @click="handlePublishClick" class="side-btn publish-btn">
        <div class="btn-icon publish-icon">+</div>
        <span>发布商品</span>
      </div>
    </aside>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">Campus2Hand © 2024</div>
    </footer>

    <!-- 个人中心弹窗 -->
    <div v-if="showUserModal" class="modal-overlay" @click="closeUserModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <span class="modal-title">个人中心</span>
          <span class="modal-close" @click="closeUserModal">×</span>
        </div>
        <div class="modal-body">
          <div v-if="userLoading" class="loading-small">
            <div class="loading-spinner"></div>
          </div>
          <div v-else-if="userData" class="user-modal-content">
            <!-- 支持图片头像展示 -->
            <div class="user-avatar-wrapper">
              <img 
                v-if="userData.avatar" 
                :src="userData.avatar" 
                class="user-avatar-img" 
                alt="头像"
                @error="handleAvatarError"
              />
              <div v-else class="user-avatar-text">
                {{ userData.nickname?.charAt(0) || userData.name?.charAt(0) || '用' }}
              </div>
            </div>
            <div class="user-name">{{ userData.nickname || userData.name }}</div>
            <div class="user-id">学号: {{ userData.studentId }}</div>
            <div class="user-college">学院: {{ userData.college }}</div>
            <div class="user-contact">联系方式: {{ userData.contact || '未设置' }}</div>
            <div class="user-verified" :class="{ verified: userData.verified }">
              {{ userData.verified ? '✓ 已认证' : '未认证' }}
            </div>
          </div>
          <div v-else class="user-fallback">
            <div class="user-avatar">用</div>
            <div class="user-name">欢迎回来，用户</div>
          </div>
          <button @click="handleOrderClick" class="order-btn">我的订单</button>
          
          <!-- 订单列表区域 -->
          <div v-if="ordersData.length > 0" class="orders-list">
            <h3 class="orders-title">我的订单</h3>
            <div v-for="order in ordersData" :key="order.orderNo" class="order-item">
              <div class="order-header">
                <span class="order-no">订单号: {{ order.orderNo }}</span>
                <span :class="['order-status', order.status]">{{ getStatusText(order.status) }}</span>
              </div>
              <div class="order-body">
                <div class="order-product">{{ order.productName }}</div>
                <div class="order-price">¥{{ order.totalAmount.toFixed(2) }}</div>
                <div class="order-method">{{ getTradeMethodText(order.tradeType) }}</div>
              </div>
            </div>
          </div>
          
          <!-- 空订单提示 -->
          <div v-else-if="ordersData === 'empty'" class="empty-orders">暂无订单</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { userApi, productApi, orderApi } from '@/services/api';

const router = useRouter();

// ==================== 页面状态 ====================
const searchKeyword = ref('');
const showUserModal = ref(false);
const ordersData = ref([]); // 订单数据

// 用户信息状态
const userLoading = ref(false);
const userData = ref(null);

// 商品列表状态
const productsLoading = ref(true);
const productsData = ref(null);

// 获取当前登录用户ID（从localStorage获取）
function getCurrentUserId() {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      // 后端使用用户ID查询
      return user.id || user.userId || '1';
    } catch {
      return '1';
    }
  }
  return '1'; // 默认用户ID
}

// ==================== 页面方法 ====================
async function loadUserInfo() {
  console.log('=====================================');
  console.log('[Home] 开始获取用户信息');
  userLoading.value = true;
  try {
    // 从后端API获取真实用户信息
    const userId = getCurrentUserId();
    console.log('[Home] 请求参数 - userId:', userId);
    console.log('[Home] 请求URL:', `/api/user/${userId}`);
    console.log('-------------------------------------');
    
    const response = await userApi.getUserInfo(userId);
    
    console.log('[Home] 响应状态:', response.status);
    console.log('[Home] 响应数据:', JSON.stringify(response.data, null, 2));
    console.log('-------------------------------------');
    
    if (response.data.code === 200 && response.data.data) {
      userData.value = response.data.data;
      // 确保userId字段存在
      if (!userData.value.userId && userData.value.id) {
        userData.value.userId = userData.value.id;
      }
      // 更新localStorage
      localStorage.setItem('user', JSON.stringify(userData.value));
      console.log('[Home] ✅ 成功获取用户信息');
      console.log('[Home] 用户数据:', JSON.stringify(userData.value, null, 2));
    } else {
      console.warn('[Home] ⚠️ 后端返回非成功状态');
      console.warn('[Home] code:', response.data?.code);
      console.warn('[Home] message:', response.data?.message);
    }
  } catch (error) {
    console.error('[Home] ❌ 获取用户信息失败');
    console.error('[Home] 错误对象:', error);
    console.error('[Home] 错误消息:', error.message);
    if (error.response) {
      console.error('[Home] 响应状态码:', error.response.status);
      console.error('[Home] 响应数据:', JSON.stringify(error.response.data, null, 2));
    }
    // 不使用mock数据，保持用户数据为空
  } finally {
    userLoading.value = false;
    console.log('=====================================');
  }
}

// 头像加载失败处理
function handleAvatarError(event) {
  console.warn('[Home] 头像加载失败，使用文字头像替代');
  event.target.style.display = 'none';
  event.target.parentElement.querySelector('.user-avatar-text')?.classList.remove('hidden');
}

// 商品图片加载失败处理
function handleProductImageError(event) {
  console.warn('[Home] 商品图片加载失败，显示占位符');
  event.target.style.display = 'none';
  const placeholder = event.target.parentElement.querySelector('.product-image-placeholder');
  if (placeholder) {
    placeholder.style.display = 'flex';
  }
}

async function loadProducts() {
  productsLoading.value = true;
  try {
    // 传递完整参数：category、title、pageNum、pageSize
    const params = {
      category: '',
      title: '',
      pageNum: 1,
      pageSize: 10
    };
    const response = await productApi.getProductList(params);
    
    console.log('[Home] 商品列表响应完整:', JSON.stringify(response.data, null, 2));
    
    // 后端统一 Result 包装：response.data = { code, message, data: { records, total, ... } }
    const resultData = response.data?.data || response.data;
    
    console.log('[Home] 解析后的数据:', JSON.stringify(resultData, null, 2));
    
    if (resultData) {
      // MyBatis Plus 的数据列表字段是 records
      productsData.value = resultData.records || resultData || [];
      console.log('[Home] 解析到商品数量:', productsData.value.length);
      console.log('[Home] 第一个商品图片字段:', productsData.value[0]?.imageUrl);
    }
  } catch (error) {
    console.error('Load products failed:', error);
    console.error('错误详情:', error.response?.data);
    productsData.value = [];
  } finally {
    productsLoading.value = false;
  }
}

async function searchProducts() {
  productsLoading.value = true;
  try {
    // 传递完整参数：category、title、pageNum、pageSize
    const params = {
      category: '',
      title: searchKeyword.value.trim() || '',
      pageNum: 1,
      pageSize: 10
    };
    const response = await productApi.getProductList(params);
    
    // MyBatis Plus 分页返回结构：{ records, total, size, current, pages }
    if (response.data) {
      productsData.value = response.data.records || [];
    }
  } catch (error) {
    console.error('Search products failed:', error);
    console.error('错误详情:', error.response?.data);
    productsData.value = [];
  } finally {
    productsLoading.value = false;
  }
}

function handleProductClick(productId) {
  // 跳转到商品详情页
  router.push(`/product/${productId}`);
}

// 状态转换函数
function getStatusText(status) {
  const statusMap = {
    PENDING_PAYMENT: '待支付',
    PENDING_DELIVERY: '待交付',
    COMPLETED: '已完成',
    CLOSED: '已关闭'
  };
  return statusMap[status] || status;
}

function getTradeMethodText(tradeType) {
  const tradeMethodMap = {
    FACE_TO_FACE: '当面交易',
    SELF_PICKUP: '自提'
  };
  return tradeMethodMap[tradeType] || tradeType;
}

async function handleOrderClick() {
  try {
    console.log('[Home] 开始获取订单信息');
    const userId = getCurrentUserId();
    const response = await orderApi.getOrderList({ userId });
    
    console.log('[Home] 订单响应:', response);
    
    if (response.data.code === 200 && response.data.data) {
      // 根据后端标准，数据结构是 { total, list }
      let orders = [];
      if (Array.isArray(response.data.data.list)) {
        orders = response.data.data.list;
      } else if (Array.isArray(response.data.data)) {
        orders = response.data.data;
      }
      
      console.log('[Home] 解析的订单列表:', orders);
      
      if (orders.length === 0) {
        ordersData.value = 'empty'; // 标记为空状态
        return;
      }
      
      // 将订单数据保存到响应式状态，用于渲染
      ordersData.value = orders;
    } else {
      ordersData.value = 'empty';
      console.warn('[Home] 获取订单失败:', response.message);
    }
  } catch (error) {
    console.error('[Home] Get orders failed:', error);
    ordersData.value = 'empty';
  }
}

function handleAiClick() {
  router.push('/ai-chat');
}

function handlePublishClick() {
  router.push('/publish');
}

function showUserCenter() {
  // 输出头像地址到控制台
  console.log('[Home] 个人中心头像地址:', userData.value?.avatar);
  // 跳转到个人中心页面
  router.push('/profile');
}

function closeUserModal() {
  showUserModal.value = false;
}

// ==================== 初始化 ====================
onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #F8F8F8;
  display: flex;
  flex-direction: column;
}

/* 离线提示 */
.offline-badge {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background-color: #1A1A1A;
  color: #FFFFFF;
  text-align: center;
  padding: 8px;
  font-size: 12px;
  z-index: 9999;
}

/* 顶部功能模块 */
.header {
  background-color: #FFFFFF;
  border-bottom: 1px solid #E0E0E0;
  padding: 16px 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background-color: #1A1A1A;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-size: 16px;
  font-weight: 600;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #1A1A1A;
}

/* 主内容区 */
.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  position: relative;
}

/* 搜索栏 + vlog区域 */
.search-vlog-section {
  background-color: #FFFFFF;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #F8F8F8;
  border-radius: 12px;
  padding: 14px 16px;
  gap: 12px;
}

.search-icon {
  width: 20px;
  height: 20px;
  border: 2px solid #666666;
  border-radius: 50%;
  position: relative;
}

.search-icon::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  width: 6px;
  height: 6px;
  border: 2px solid #666666;
  border-radius: 50%;
  transform: translate(-50%, -2px);
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #1A1A1A;
  outline: none;
}

.search-input::placeholder {
  color: #666666;
}

.search-btn {
  padding: 8px 16px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.search-btn:hover {
  background-color: #333333;
}

.vlog-area {
  margin-top: 16px;
  height: 150px;
  background-color: #F8F8F8;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #666666;
  font-size: 15px;
}

.vlog-play-icon {
  width: 48px;
  height: 36px;
  border: 2px solid #666666;
  border-radius: 8px;
  position: relative;
}

.vlog-play-icon::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-left: 10px solid #666666;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  transform: translate(-40%, -50%);
}

/* 二手商品模块 */
.products-section {
  background-color: #FFFFFF;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1A1A1A;
}

.section-more {
  font-size: 14px;
  color: #666666;
  cursor: pointer;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.product-card {
  background-color: #FFFFFF;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 150px;
  background-color: #CCCCCC;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image-placeholder {
  color: #666666;
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-title {
  padding: 12px 12px 4px;
  font-size: 14px;
  font-weight: 500;
  color: #1A1A1A;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  padding: 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #1A1A1A;
}

.product-condition {
  padding: 4px 12px 12px;
  font-size: 12px;
  color: #666666;
}

/* 右侧固定按钮栏 */
.side-bar {
  position: fixed;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 16px;
  z-index: 100;
}

.side-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.side-btn:hover {
  transform: scale(1.1);
}

.btn-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #CCCCCC;
  color: #666666;
  font-size: 14px;
}

.user-icon {
  font-size: 20px;
}

.ai-icon {
  background-color: #333333;
  color: #FFFFFF;
  font-size: 16px;
  font-weight: 600;
}

.publish-icon {
  background-color: #1a1a1a;
  color: #FFFFFF;
  font-size: 24px;
  font-weight: 600;
}

.side-btn span {
  font-size: 12px;
  color: #666666;
}

/* 底部 */
.footer {
  background-color: #FFFFFF;
  border-top: 1px solid #E0E0E0;
  padding: 20px;
  text-align: center;
}

.footer-content {
  font-size: 14px;
  color: #666666;
}

/* 个人中心弹窗 */
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
  background-color: #FFFFFF;
  border-radius: 16px;
  width: 320px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #E0E0E0;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  color: #1A1A1A;
}

.modal-close {
  font-size: 24px;
  color: #666666;
  cursor: pointer;
}

.modal-body {
  padding: 24px;
  text-align: center;
}

.loading-small {
  padding: 20px;
}

.user-modal-content {
  padding: 10px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #CCCCCC;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666666;
  font-size: 28px;
  margin: 0 auto 12px;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #1A1A1A;
}

.user-id, .user-college, .user-contact {
  font-size: 14px;
  color: #666666;
  margin-top: 4px;
}

.user-verified {
  font-size: 14px;
  color: #999999;
  margin-top: 8px;
  padding: 4px 12px;
  border-radius: 12px;
  background-color: #F8F8F8;
  display: inline-block;
}

.user-verified.verified {
  color: #2ECC71;
  background-color: #E8F8F0;
}

.user-fallback {
  padding: 10px;
}

.order-btn {
  margin-top: 20px;
  width: 100%;
  padding: 12px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.order-btn:hover {
  background-color: #333333;
}

/* 订单列表 */
.orders-list {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #E0E0E0;
}

.orders-title {
  font-size: 14px;
  font-weight: 600;
  color: #1A1A1A;
  margin-bottom: 12px;
  text-align: left;
}

.order-item {
  background-color: #F8F8F8;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.order-no {
  font-size: 12px;
  color: #666666;
}

.order-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 8px;
}

.order-status.PENDING_PAYMENT {
  color: #E74C3C;
  background-color: #FDECEA;
}

.order-status.PENDING_DELIVERY {
  color: #F39C12;
  background-color: #FEF5E0;
}

.order-status.COMPLETED {
  color: #2ECC71;
  background-color: #E8F8F0;
}

.order-status.CLOSED {
  color: #999999;
  background-color: #F5F5F5;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-product {
  font-size: 14px;
  color: #1A1A1A;
  flex: 1;
}

.order-price {
  font-size: 14px;
  font-weight: 600;
  color: #1A1A1A;
}

.order-method {
  font-size: 12px;
  color: #666666;
  margin-left: 12px;
}

.empty-orders {
  margin-top: 12px;
  font-size: 14px;
  color: #999999;
}

/* 加载动画 */
.loading {
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #E0E0E0;
  border-top-color: #1A1A1A;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666666;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .main-content {
    padding: 16px;
  }
  
  .side-bar {
    right: 16px;
  }
}

@media (max-width: 500px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .product-image {
    height: 120px;
    font-size: 24px;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .search-vlog-section {
    padding: 16px;
  }
  
  .vlog-area {
    height: 120px;
  }
  
  .modal-content {
    width: 90%;
  }
}
</style>