<template>
  <div class="profile-page">
    <!-- 头部 -->
    <header class="profile-header">
      <div class="back-btn" @click="goBack">← 返回</div>
      <h1 class="page-title">个人中心</h1>
      <div class="placeholder"></div>
    </header>

    <!-- 主内容区 -->
    <main class="profile-content">
      <!-- 用户信息区域 -->
      <section class="user-info-section">
        <div class="avatar-wrapper" @click="uploadAvatar">
          <img 
            v-if="userData.avatar" 
            :src="userData.avatar" 
            class="user-avatar" 
            alt="头像"
            @error="handleAvatarError"
          />
          <div v-else class="user-avatar-placeholder">
            {{ userData.nickname?.charAt(0) || userData.name?.charAt(0) || '用' }}
          </div>
          <div class="avatar-edit-icon">+</div>
        </div>
        
        <div class="user-details">
          <div class="info-item">
            <span class="info-label">学号</span>
            <span class="info-value">{{ userData.studentId || '未设置' }}</span>
          </div>
          <div class="info-item editable">
            <span class="info-label">学院</span>
            <div class="editable-content">
              <input 
                v-if="editingField === 'college'" 
                v-model="editValue" 
                class="edit-input" 
                @keyup.enter="saveEdit('college')"
                @blur="cancelEdit"
                ref="editInputRef"
              />
              <span v-else class="info-value">{{ userData.college || '未设置' }}</span>
              <span v-if="editingField !== 'college'" class="edit-icon" @click="startEdit('college', userData.college)">✏️</span>
              <span v-else class="save-icon" @click="saveEdit('college')">✓</span>
            </div>
          </div>
          <div class="info-item editable">
            <span class="info-label">姓名</span>
            <div class="editable-content">
              <input 
                v-if="editingField === 'nickname'" 
                v-model="editValue" 
                class="edit-input" 
                @keyup.enter="saveEdit('nickname')"
                @blur="cancelEdit"
                ref="editInputRef"
              />
              <span v-else class="info-value">{{ userData.nickname || userData.name || '未设置' }}</span>
              <span v-if="editingField !== 'nickname'" class="edit-icon" @click="startEdit('nickname', userData.nickname || userData.name)">✏️</span>
              <span v-else class="save-icon" @click="saveEdit('nickname')">✓</span>
            </div>
          </div>
          <div class="info-item editable">
            <span class="info-label">联系方式</span>
            <div class="editable-content">
              <input 
                v-if="editingField === 'phone'" 
                v-model="editValue" 
                class="edit-input" 
                @keyup.enter="saveEdit('phone')"
                @blur="cancelEdit"
                ref="editInputRef"
              />
              <span v-else class="info-value">{{ userData.phone || '未设置' }}</span>
              <span v-if="editingField !== 'phone'" class="edit-icon" @click="startEdit('phone', userData.phone)">✏️</span>
              <span v-else class="save-icon" @click="saveEdit('phone')">✓</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 功能模块区域 -->
      <section class="function-section">
        <!-- 修改密码 -->
        <div class="function-card" @click="showChangePassword">
          <div class="function-icon">🔐</div>
          <span class="function-title">修改密码</span>
        </div>

        <!-- 订单详情（快捷查看） -->
        <div class="function-card order-detail-card" @click="goToOrders">
          <div class="function-icon">📋</div>
          <span class="function-title">订单详情</span>
          <div class="order-stats">
            <div class="stat-item">
              <span class="stat-value">{{ orderStats.pendingPayment }}</span>
              <span class="stat-label">待支付</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ orderStats.pendingDelivery }}</span>
              <span class="stat-label">待交付</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ orderStats.completed }}</span>
              <span class="stat-label">已完成</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 我的订单按钮 -->
      <section class="order-section">
        <button class="order-btn" @click="goToOrders">我的订单</button>
      </section>

      <!-- 退出登录按钮 -->
      <section class="logout-section">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </section>
    </main>

    <!-- 头像上传弹窗 -->
    <div v-if="showAvatarModal" class="modal-overlay" @click="closeAvatarModal">
      <div class="modal-content" @click.stop>
        <h3>上传头像</h3>
        <input type="file" accept="image/jpeg,image/png" @change="handleAvatarUpload" class="file-input" />
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeAvatarModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordModal" class="modal-overlay" @click="closePasswordModal">
      <div class="modal-content" @click.stop>
        <h3>修改密码</h3>
        <div class="form-group">
          <label>旧密码</label>
          <input type="password" v-model="passwordForm.oldPassword" placeholder="请输入旧密码" />
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </div>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closePasswordModal">取消</button>
          <button class="btn-confirm" :disabled="passwordLoading" @click="changePassword">
            {{ passwordLoading ? '修改中...' : '确认修改' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
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
  avatar: '',
  college: ''
});

// 订单统计
const orderStats = reactive({
  pendingPayment: 0,
  pendingDelivery: 0,
  completed: 0,
  closed: 0
});

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 加载状态
const passwordLoading = ref(false);

// 弹窗状态
const showAvatarModal = ref(false);
const showPasswordModal = ref(false);

// 编辑状态
const editingField = ref('');
const editValue = ref('');
const editInputRef = ref(null);

// 页面方法
async function loadUserInfo() {
  try {
    // 从后端API获取真实用户数据
    // 后端使用用户ID查询
    const userId = getCurrentUserId();
    console.log('=====================================');
    console.log('[Profile] 开始获取用户信息');
    console.log('[Profile] 请求参数 - userId:', userId);
    console.log('[Profile] 请求URL:', `/api/user/${userId}`);
    console.log('-------------------------------------');
    
    const response = await userApi.getUserInfo(userId);
    
    console.log('[Profile] 响应状态:', response.status);
    console.log('[Profile] 响应头:', response.headers);
    console.log('[Profile] 响应数据:', JSON.stringify(response.data, null, 2));
    console.log('-------------------------------------');
    
    if (response.data.code === 200 && response.data.data) {
      const rawData = response.data.data;
      // 字段映射：兼容后端可能返回的不同字段名
      userData.value = {
        userId: rawData.userId || rawData.id || '',
        studentId: rawData.studentId || rawData.student_id || '',
        name: rawData.name || '',
        nickname: rawData.nickname || '',
        phone: rawData.phone || rawData.mobile || rawData.tel || '',
        avatar: rawData.avatar || '',
        college: rawData.college || rawData.department || rawData.school || ''
      };
      console.log('[Profile] 字段映射结果:', JSON.stringify(userData.value, null, 2));
      // 同步到localStorage和userStore
      localStorage.setItem('user', JSON.stringify(userData.value));
      if (userStore.userInfo) {
        userStore.userInfo = { ...userData.value };
      }
      console.log('[Profile] ✅ 成功从后端获取用户信息');
      console.log('[Profile] 用户数据:', JSON.stringify(userData.value, null, 2));
    } else {
      console.warn('[Profile] ⚠️ 后端返回非成功状态');
      console.warn('[Profile] code:', response.data.code);
      console.warn('[Profile] message:', response.data.message);
    }
    console.log('=====================================');
  } catch (e) {
    console.log('=====================================');
    console.error('[Profile] ❌ 从后端获取用户信息失败');
    console.error('[Profile] 错误对象:', e);
    console.error('[Profile] 错误类型:', e.name);
    console.error('[Profile] 错误消息:', e.message);
    if (e.response) {
      console.error('[Profile] 响应状态码:', e.response.status);
      console.error('[Profile] 响应数据:', JSON.stringify(e.response.data, null, 2));
      console.error('[Profile] 响应头:', e.response.headers);
    } else if (e.request) {
      console.error('[Profile] 请求已发送但无响应');
      console.error('[Profile] 请求对象:', e.request);
    } else {
      console.error('[Profile] 请求配置错误:', e.message);
    }
    console.log('=====================================');
    // 不使用mock数据，保持用户数据为空
    userData.value = {
      userId: '',
      studentId: '',
      name: '',
      nickname: '',
      phone: '',
      avatar: '',
      college: ''
    };
  }
}

async function loadOrderStats() {
  console.log('=====================================');
  console.log('[Profile] 开始获取订单统计');
  try {
    const userId = userData.value.studentId || userData.value.userId || getCurrentUserId();
    console.log('[Profile] 请求参数 - userId:', userId);
    console.log('[Profile] 请求URL:', '/api/orders/list?userId=' + userId);
    console.log('-------------------------------------');
    
    const response = await orderApi.getOrderList({ userId });
    
    console.log('[Profile] 订单接口响应状态:', response.status);
    console.log('[Profile] 订单接口响应数据:', JSON.stringify(response.data, null, 2));
    console.log('-------------------------------------');
    
    if (response.data.code === 200 && response.data.data) {
      const data = response.data.data;
      // 兼容分页格式和列表格式
      const orders = data.records || data.list || (Array.isArray(data) ? data : []);
      console.log('[Profile] 数据来源:', data.records ? 'records' : (data.list ? 'list' : 'data'));
      console.log('[Profile] 解析到订单数量:', orders.length);
      console.log('[Profile] 订单列表:', JSON.stringify(orders, null, 2));
      
      orders.forEach((order, index) => {
        console.log(`[Profile] 订单${index + 1}: orderNo=${order.orderNo}, status=${order.status}, totalAmount=${order.totalAmount}`);
        switch (order.status) {
          case 'PENDING_PAYMENT':
            orderStats.pendingPayment++;
            break;
          case 'PENDING_DELIVERY':
            orderStats.pendingDelivery++;
            break;
          case 'COMPLETED':
            orderStats.completed++;
            break;
          case 'CLOSED':
            orderStats.closed++;
            break;
          default:
            console.warn(`[Profile] 未知订单状态: ${order.status}`);
        }
      });
      
      console.log('[Profile] ✅ 订单统计完成');
      console.log('[Profile] 统计结果:', JSON.stringify(orderStats, null, 2));
    } else {
      console.warn('[Profile] ⚠️ 订单接口返回非成功状态');
      console.warn('[Profile] code:', response.data?.code);
      console.warn('[Profile] message:', response.data?.message);
    }
  } catch (e) {
    console.error('[Profile] ❌ 获取订单统计失败');
    console.error('[Profile] 错误对象:', e);
    console.error('[Profile] 错误消息:', e.message);
    if (e.response) {
      console.error('[Profile] 响应状态码:', e.response.status);
      console.error('[Profile] 响应数据:', JSON.stringify(e.response.data, null, 2));
    }
  }
  console.log('=====================================');
}

// 获取当前用户ID（后端接口使用用户ID查询）
function getCurrentUserId() {
  // 优先从localStorage获取用户信息
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
  // 尝试从userStore获取
  if (userStore.userInfo) {
    return userStore.userInfo.id || userStore.userInfo.userId || userStore.userInfo.studentId || '1';
  }
  return '1';
}

function goBack() {
  router.back();
}

function uploadAvatar() {
  showAvatarModal.value = true;
}

function closeAvatarModal() {
  showAvatarModal.value = false;
}

async function handleAvatarUpload(event) {
  const file = event.target.files?.[0];
  if (!file) return;
  
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过 2MB');
    return;
  }
  
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
      avatarUrl += (avatarUrl.includes('?') ? '&' : '?') + 't=' + Date.now();
      userData.value.avatar = avatarUrl;
      localStorage.setItem('user', JSON.stringify(userData.value));
      if (userStore.userInfo) {
        userStore.userInfo.avatar = avatarUrl;
      }
      alert('头像上传成功');
    } else {
      alert(response.data.message || '上传失败');
    }
  } catch (e) {
    console.error('上传头像失败:', e);
    alert('上传失败，请稍后重试');
  }
}

function handleAvatarError(event) {
  console.warn('头像加载失败');
  event.target.style.display = 'none';
}

function showChangePassword() {
  showPasswordModal.value = true;
}

function closePasswordModal() {
  showPasswordModal.value = false;
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
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
    } else {
      alert(response.data.message || '修改失败');
    }
  } catch (e) {
    console.error('修改密码失败:', e);
    alert(e.response?.data?.message || '修改失败，请稍后重试');
  } finally {
    passwordLoading.value = false;
  }
}

// 开始编辑
function startEdit(field, value) {
  editingField.value = field;
  editValue.value = value || '';
  setTimeout(() => {
    const input = document.querySelector('.edit-input');
    if (input) {
      input.focus();
      input.select();
    }
  }, 10);
}

// 保存编辑
async function saveEdit(field) {
  const value = editValue.value.trim();
  if (!value) {
    alert('请输入内容');
    return;
  }
  
  // 验证电话号码
  if (field === 'phone' && !/^1[3-9]\d{9}$/.test(value)) {
    alert('请输入正确的手机号码');
    return;
  }
  
  try {
    const response = await userApi.updateUserInfo({
      userId: userData.value.userId,
      [field]: value
    });
    
    if (response.data.code === 200) {
      userData.value[field] = value;
      localStorage.setItem('user', JSON.stringify(userData.value));
      if (userStore.userInfo) {
        userStore.userInfo[field] = value;
      }
      alert('修改成功');
    } else {
      alert(response.data.message || '修改失败');
    }
  } catch (e) {
    console.error('修改信息失败:', e);
    alert('修改失败，请稍后重试');
  } finally {
    editingField.value = '';
    editValue.value = '';
  }
}

// 取消编辑
function cancelEdit() {
  editingField.value = '';
  editValue.value = '';
}

function goToOrders() {
  router.push('/orders');
}

function handleLogout() {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout();
    router.push('/login');
  }
}

onMounted(() => {
  loadUserInfo();
  loadOrderStats();
});
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.profile-header {
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

.profile-content {
  flex: 1;
  padding: 30px 50px;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
}

.user-info-section {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: flex-start;
  gap: 40px;
}

.avatar-wrapper {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  border: 4px solid #e0e0e0;
  flex-shrink: 0;
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e8e8e8;
  font-size: 48px;
  color: #666;
}

.avatar-edit-icon {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 36px;
  height: 36px;
  background-color: #333;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.user-details {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: #f8f8f8;
  border-radius: 12px;
}

.info-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.info-value {
  font-size: 18px;
  color: #1a1a1a;
  font-weight: 600;
}

/* 可编辑样式 */
.info-item.editable {
  cursor: pointer;
}

.editable-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.edit-input {
  flex: 1;
  padding: 10px 16px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  border: 2px solid #333;
  border-radius: 8px;
  background-color: #fff;
  outline: none;
}

.edit-input:focus {
  border-color: #666;
}

.edit-icon {
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
}

.info-item.editable:hover .edit-icon {
  opacity: 1;
}

.save-icon {
  font-size: 18px;
  color: #2e7d32;
  cursor: pointer;
  font-weight: bold;
}

.function-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 30px;
}

.function-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 40px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.function-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.function-icon {
  font-size: 48px;
}

.function-title {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.order-detail-card {
  position: relative;
}

.order-stats {
  display: flex;
  gap: 30px;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.order-section {
  margin-bottom: 24px;
}

.order-btn {
  width: 100%;
  padding: 24px;
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  background-color: #333;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-btn:hover {
  background-color: #444;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.logout-section {
  padding-bottom: 30px;
}

.logout-btn {
  width: 100%;
  padding: 20px;
  font-size: 16px;
  color: #999;
  background-color: transparent;
  border: 2px solid #e0e0e0;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  color: #ff4444;
  border-color: #ff4444;
  background-color: #fff5f5;
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
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
  box-sizing: border-box;
}

.modal-content h3 {
  margin: 0 0 20px;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 12px;
  font-size: 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #333;
}

.file-input {
  width: 100%;
  padding: 12px;
  border: 1px dashed #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn-cancel,
.btn-confirm {
  flex: 1;
  padding: 12px;
  font-size: 14px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-cancel {
  background-color: #f0f0f0;
  color: #666;
}

.btn-cancel:hover {
  background-color: #e8e8e8;
}

.btn-confirm {
  background-color: #333;
  color: #ffffff;
}

.btn-confirm:hover:not(:disabled) {
  background-color: #444;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .profile-content {
    padding: 16px;
  }
  
  .function-section {
    grid-template-columns: 1fr;
  }
  
  .order-stats {
    gap: 16px;
  }
}
</style>