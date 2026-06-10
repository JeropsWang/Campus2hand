<template>
  <div class="profile-page">
    <!-- 头部 -->
    <header class="profile-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">←</div>
        <h1 class="page-title">个人中心</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="profile-content">
      <!-- 用户信息卡片 -->
      <section class="user-card">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="uploadAvatar">
            <img 
              v-if="userData.avatar" 
              :src="userData.avatar" 
              class="user-avatar" 
              alt="头像"
            />
            <div v-else class="user-avatar-placeholder">
              {{ userData.nickname?.charAt(0) || userData.name?.charAt(0) || '用' }}
            </div>
            <div class="avatar-upload-icon">+</div>
          </div>
          <div class="user-info">
            <h2 class="user-name">{{ userData.nickname || userData.name || '用户' }}</h2>
            <p class="user-id">学号: {{ userData.studentId }}</p>
          </div>
        </div>
      </section>

      <!-- 修改个人信息表单 -->
      <section class="section-card">
        <h3 class="section-title">修改个人信息</h3>
        <form @submit.prevent="updateUserInfo" class="info-form">
          <div class="form-group">
            <label class="form-label">昵称</label>
            <input 
              type="text" 
              v-model="formData.nickname" 
              class="form-input" 
              placeholder="请输入昵称"
            />
          </div>
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input 
              type="tel" 
              v-model="formData.phone" 
              class="form-input" 
              placeholder="请输入手机号"
            />
          </div>
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '保存中...' : '保存修改' }}
          </button>
        </form>
      </section>

      <!-- 修改密码表单 -->
      <section class="section-card">
        <h3 class="section-title">修改密码</h3>
        <form @submit.prevent="changePassword" class="password-form">
          <div class="form-group">
            <label class="form-label">旧密码</label>
            <input 
              type="password" 
              v-model="passwordForm.oldPassword" 
              class="form-input" 
              placeholder="请输入旧密码"
            />
          </div>
          <div class="form-group">
            <label class="form-label">新密码</label>
            <input 
              type="password" 
              v-model="passwordForm.newPassword" 
              class="form-input" 
              placeholder="请输入新密码"
            />
          </div>
          <div class="form-group">
            <label class="form-label">确认新密码</label>
            <input 
              type="password" 
              v-model="passwordForm.confirmPassword" 
              class="form-input" 
              placeholder="请再次输入新密码"
            />
          </div>
          <button type="submit" class="submit-btn" :disabled="passwordLoading">
            {{ passwordLoading ? '修改中...' : '修改密码' }}
          </button>
        </form>
      </section>

      <!-- 我的订单 -->
      <section class="section-card">
        <h3 class="section-title">我的订单</h3>
        
        <!-- 订单统计 -->
        <div class="order-stats">
          <div class="stat-item">
            <div class="stat-count">{{ orderStats.pendingPayment }}</div>
            <div class="stat-label">待支付</div>
          </div>
          <div class="stat-item">
            <div class="stat-count">{{ orderStats.pendingDelivery }}</div>
            <div class="stat-label">待交付</div>
          </div>
          <div class="stat-item">
            <div class="stat-count">{{ orderStats.completed }}</div>
            <div class="stat-label">已完成</div>
          </div>
          <div class="stat-item">
            <div class="stat-count">{{ orderStats.closed }}</div>
            <div class="stat-label">已关闭</div>
          </div>
        </div>

        <!-- 订单列表 -->
        <div v-if="ordersData.length > 0" class="orders-list">
          <div 
            v-for="order in ordersData" 
            :key="order.orderNo" 
            class="order-item"
          >
            <div class="order-header">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span :class="['order-status', order.status]">{{ getStatusText(order.status) }}</span>
            </div>
            <div class="order-body">
              <div class="order-product">{{ order.productName }}</div>
              <div class="order-price">¥{{ order.totalAmount.toFixed(2) }}</div>
            </div>
            <div class="order-footer">
              <span class="order-method">{{ getTradeMethodText(order.tradeType) }}</span>
              <span class="order-time">{{ formatTime(order.createTime) }}</span>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">暂无订单</div>
      </section>

      <!-- 退出登录 -->
      <section class="section-card">
        <button @click="handleLogout" class="logout-btn">退出登录</button>
      </section>
    </main>

    <!-- 头像上传弹窗 -->
    <div v-if="showAvatarModal" class="modal-overlay" @click="closeAvatarModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <span class="modal-title">上传头像</span>
          <span class="modal-close" @click="closeAvatarModal">×</span>
        </div>
        <div class="modal-body">
          <input 
            type="file" 
            id="avatar-input" 
            accept="image/*" 
            class="avatar-input"
            @change="handleAvatarUpload"
          />
          <label for="avatar-input" class="upload-label">
            <div class="upload-icon">📷</div>
            <span>点击选择图片</span>
          </label>
          <p class="upload-hint">支持 JPG、PNG 格式，大小不超过 2MB</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { userApi, orderApi } from '@/services/api';
const router = useRouter();
const userStore = useUserStore();
// 用户数据
const userData = ref({
 userId: '',
 studentId: '',
 name: '',
 nickname: '',
 phone: '',
 avatar: ''
});
// 表单数据
const formData = reactive({
 nickname: '',
 phone: ''
});
// 密码表单
const passwordForm = reactive({
 oldPassword: '',
 newPassword: '',
 confirmPassword: ''
});
// 订单数据
const ordersData = ref([]);
const orderStats = reactive({
 pendingPayment: 0,
 pendingDelivery: 0,
 completed: 0,
 closed: 0
});
// 加载状态
const loading = ref(false);
const passwordLoading = ref(false);
// 弹窗状态
const showAvatarModal = ref(false);
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
function formatTime(timeStr) {
 if (!timeStr)
 return '';
 try {
 const date = new Date(timeStr);
 return date.toLocaleDateString('zh-CN');
 }
 catch (e) {
 return timeStr;
 }
}
// 页面方法
async function loadUserInfo() {
 try {
 // 从localStorage获取用户信息
 const userStr = localStorage.getItem('user');
 if (userStr) {
 userData.value = JSON.parse(userStr);
 }
 // 如果没有用户ID，尝试从userStore获取
 if (!userData.value.userId && userStore.userInfo) {
 userData.value = { ...userStore.userInfo };
 }
 // 初始化表单数据
 formData.nickname = userData.value.nickname || '';
 formData.phone = userData.value.phone || '';
 }
 catch (e) {
 console.error('加载用户信息失败:', e);
 }
}
async function loadOrders() {
 try {
 const response = await orderApi.getOrderList();
 if (response.data.code === 200 && response.data.data) {
 let orders = [];
 if (Array.isArray(response.data.data.list)) {
 orders = response.data.data.list;
 }
 else if (Array.isArray(response.data.data)) {
 orders = response.data.data;
 }
 ordersData.value = orders;
 // 统计订单状态
 orderStats.pendingPayment = orders.filter(o => o.status === 'PENDING_PAYMENT').length;
 orderStats.pendingDelivery = orders.filter(o => o.status === 'PENDING_DELIVERY').length;
 orderStats.completed = orders.filter(o => o.status === 'COMPLETED').length;
 orderStats.closed = orders.filter(o => o.status === 'CLOSED').length;
 }
 }
 catch (e) {
 console.error('加载订单失败:', e);
 }
}
async function updateUserInfo() {
 if (!formData.nickname && !formData.phone) {
 alert('请至少修改一项信息');
 return;
 }
 loading.value = true;
 try {
 const data = {
 id: userData.value.userId,
 nickname: formData.nickname || undefined,
 phone: formData.phone || undefined
 };
 const response = await userApi.updateUserInfo(data);
 if (response.data.code === 200) {
 // 更新本地用户信息
 userData.value.nickname = formData.nickname || userData.value.nickname;
 userData.value.phone = formData.phone || userData.value.phone;
 localStorage.setItem('user', JSON.stringify(userData.value));
 alert('修改成功');
 }
 else {
 alert(response.data.message || '修改失败');
 }
 }
 catch (e) {
 console.error('修改用户信息失败:', e);
 alert('修改失败，请稍后重试');
 }
 finally {
 loading.value = false;
 }
}
async function changePassword() {
 if (!passwordForm.oldPassword) {
 alert('请输入旧密码');
 return;
 }
 if (!passwordForm.newPassword) {
 alert('请输入新密码');
 return;
 }
 if (passwordForm.newPassword !== passwordForm.confirmPassword) {
 alert('两次输入的密码不一致');
 return;
 }
 passwordLoading.value = true;
 try {
 const data = {
 userId: userData.value.userId,
 oldPassword: passwordForm.oldPassword,
 newPassword: passwordForm.newPassword
 };
 const response = await userApi.changePassword(data);
 if (response.data.code === 200) {
 alert('密码修改成功，请重新登录');
 userStore.logout();
 router.push('/login');
 }
 else {
 alert(response.data.message || '修改失败');
 }
 }
 catch (e) {
 console.error('修改密码失败:', e);
 alert(e.response?.data?.message || '修改失败，请稍后重试');
 }
 finally {
 passwordLoading.value = false;
 }
}
function uploadAvatar() {
 showAvatarModal.value = true;
}
function closeAvatarModal() {
 showAvatarModal.value = false;
}
async function handleAvatarUpload(event) {
 const file = event.target.files?.[0];
 if (!file)
 return;
 // 检查文件大小（2MB）
 if (file.size > 2 * 1024 * 1024) {
 alert('图片大小不能超过 2MB');
 return;
 }
 // 检查文件类型
 const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg'];
 if (!allowedTypes.includes(file.type)) {
 alert('只支持 JPG、PNG 格式');
 return;
 }
 closeAvatarModal();
 try {
 const formData = new FormData();
 formData.append('file', file);
 const response = await userApi.uploadAvatar(userData.value.userId, formData);
 if (response.data.code === 200) {
 let avatarUrl = response.data.data.url;
 
 // 如果是绝对URL（包含http），转换为相对路径通过代理访问
 if (avatarUrl.startsWith('http')) {
 // 提取路径部分，如 /avatars/xxx.png
 const urlObj = new URL(avatarUrl);
 avatarUrl = urlObj.pathname;
 }
 
 // 添加时间戳避免浏览器缓存旧图片
 avatarUrl += (avatarUrl.includes('?') ? '&' : '?') + 't=' + Date.now();
 
 // 更新本地用户数据
 userData.value.avatar = avatarUrl;
 
 // 更新localStorage缓存
 localStorage.setItem('user', JSON.stringify(userData.value));
 
 // 更新userStore中的用户信息
 if (userStore.userInfo) {
 userStore.userInfo.avatar = avatarUrl;
 }
 
 console.log('[Profile] 头像上传成功:', avatarUrl);
 alert('头像上传成功');
 }
 else {
 alert(response.data.message || '上传失败');
 }
 }
 catch (e) {
 console.error('上传头像失败:', e);
 alert('上传失败，请稍后重试');
 }
}
function handleLogout() {
 if (confirm('确定要退出登录吗？')) {
 userStore.logout();
 router.push('/login');
 }
}
function goBack() {
 router.back();
}
// 初始化
onMounted(() => {
 loadUserInfo();
 loadOrders();
});
</script>

<style scoped>
/* 页面容器 */
.profile-page {
  min-height: 100vh;
  background-color: #F8F8F8;
}

/* 头部 */
.profile-header {
  background-color: #FFFFFF;
  padding: 16px 24px;
  border-bottom: 1px solid #E0E0E0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 600px;
  margin: 0 auto;
}

.back-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #1A1A1A;
  cursor: pointer;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1A1A1A;
}

.placeholder {
  width: 40px;
}

/* 主内容区 */
.profile-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

/* 用户卡片 */
.user-card {
  background-color: #FFFFFF;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #E0E0E0;
}

.user-avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #CCCCCC;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #666666;
  border: 2px solid #E0E0E0;
}

.avatar-upload-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #1A1A1A;
  margin-bottom: 4px;
}

.user-id {
  font-size: 14px;
  color: #666666;
}

/* 区块卡片 */
.section-card {
  background-color: #FFFFFF;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1A1A1A;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #666666;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  font-size: 15px;
  color: #1A1A1A;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  border-color: #1A1A1A;
}

.form-input::placeholder {
  color: #999999;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background-color: #1A1A1A;
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: #333333;
}

.submit-btn:disabled {
  background-color: #CCCCCC;
  cursor: not-allowed;
}

/* 订单统计 */
.order-stats {
  display: flex;
  justify-content: space-around;
  padding-bottom: 16px;
  border-bottom: 1px solid #F0F0F0;
}

.stat-item {
  text-align: center;
}

.stat-count {
  font-size: 20px;
  font-weight: 600;
  color: #1A1A1A;
}

.stat-label {
  font-size: 12px;
  color: #666666;
  margin-top: 4px;
}

/* 订单列表 */
.orders-list {
  margin-top: 16px;
}

.order-item {
  background-color: #F8F8F8;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
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
  margin-bottom: 8px;
}

.order-product {
  font-size: 14px;
  color: #1A1A1A;
}

.order-price {
  font-size: 14px;
  font-weight: 600;
  color: #1A1A1A;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666666;
}

.empty-state {
  text-align: center;
  padding: 24px;
  color: #999999;
  font-size: 14px;
}

/* 退出登录按钮 */
.logout-btn {
  width: 100%;
  padding: 14px;
  background-color: #F5F5F5;
  color: #666666;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.logout-btn:hover {
  background-color: #EEEEEE;
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
  background-color: #FFFFFF;
  border-radius: 16px;
  width: 320px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.avatar-input {
  display: none;
}

.upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px;
  border: 2px dashed #E0E0E0;
  border-radius: 16px;
  cursor: pointer;
  transition: border-color 0.2s ease;
}

.upload-label:hover {
  border-color: #1A1A1A;
}

.upload-icon {
  font-size: 48px;
}

.upload-label span {
  font-size: 14px;
  color: #666666;
}

.upload-hint {
  margin-top: 12px;
  font-size: 12px;
  color: #999999;
}
</style>