<template>
  <div class="publish-page">
    <!-- 头部 -->
    <header class="publish-header">
      <button class="back-btn" @click="goBack" aria-label="返回">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 class="page-title">发布二手商品</h1>
      <div class="header-spacer"></div>
    </header>

    <!-- 表单 -->
    <main class="publish-content">
      <form class="publish-form" @submit.prevent="submitProduct">
        <!-- 商品图片 -->
        <div class="form-section">
          <label class="section-label">商品图片</label>
          <div class="image-upload-area" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleDrop">
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              multiple
              class="hidden-input"
              @change="handleFileChange"
            />
            <div v-if="previewImages.length === 0" class="upload-placeholder">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                <circle cx="8.5" cy="8.5" r="1.5"/>
                <polyline points="21 15 16 10 5 21"/>
              </svg>
              <span class="upload-text">点击或拖拽上传图片</span>
              <span class="upload-hint">最多5张</span>
            </div>
            <div v-else class="image-preview-grid">
              <div v-for="(img, idx) in previewImages" :key="idx" class="preview-item">
                <img :src="img" alt="预览" />
                <button type="button" class="remove-img" @click.stop="removeImage(idx)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <line x1="18" y1="6" x2="6" y2="18"/>
                    <line x1="6" y1="6" x2="18" y2="18"/>
                  </svg>
                </button>
              </div>
              <div v-if="previewImages.length < 5" class="add-more" @click.stop="triggerFileInput">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品标题 -->
        <div class="form-group">
          <label class="form-label">
            商品标题
            <span class="required">*</span>
          </label>
          <input
            v-model="form.title"
            type="text"
            class="form-input"
            placeholder="给你的商品起个吸引人的标题"
            maxlength="50"
            required
          />
          <span class="char-count">{{ form.title.length }}/50</span>
        </div>

        <!-- 商品分类 -->
        <div class="form-group">
          <label class="form-label">
            商品分类
            <span class="required">*</span>
          </label>
          <div class="category-grid">
            <button
              v-for="cat in categories"
              :key="cat.value"
              type="button"
              class="category-btn"
              :class="{ active: form.category === cat.value }"
              @click="form.category = cat.value"
            >
              {{ cat.label }}
            </button>
          </div>
        </div>

        <!-- 商品价格 -->
        <div class="form-group">
          <label class="form-label">
            价格
            <span class="required">*</span>
          </label>
          <div class="price-input-wrap">
            <span class="price-symbol">¥</span>
            <input
              v-model.number="form.price"
              type="number"
              class="form-input price-input"
              placeholder="0.00"
              min="0"
              step="0.01"
              required
            />
          </div>
        </div>

        <!-- 商品成色 -->
        <div class="form-group">
          <label class="form-label">
            成色
            <span class="required">*</span>
          </label>
          <div class="quality-options">
            <button
              v-for="q in qualities"
              :key="q.value"
              type="button"
              class="quality-btn"
              :class="{ active: form.quality === q.value }"
              @click="form.quality = q.value"
            >
              {{ q.label }}
            </button>
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="form-group">
          <label class="form-label">商品描述</label>
          <textarea
            v-model="form.description"
            class="form-textarea"
            placeholder="描述一下商品的品牌、型号、使用时长、瑕疵等详细信息..."
            rows="5"
            maxlength="500"
          ></textarea>
          <span class="char-count">{{ form.description.length }}/500</span>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="btn-secondary" @click="goBack">取消</button>
          <button
            type="submit"
            class="btn-primary"
            :disabled="isSubmitting || !isFormValid"
          >
            <span v-if="isSubmitting" class="btn-spinner"></span>
            <span v-else>发布商品</span>
          </button>
        </div>
      </form>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { productApi } from '@/services/api';

const router = useRouter();
const fileInput = ref(null);
const isSubmitting = ref(false);
const previewImages = ref([]);
const uploadedFiles = ref([]);

const categories = [
  { label: '数码电子', value: '数码电子' },
  { label: '书籍教材', value: '书籍教材' },
  { label: '生活用品', value: '生活用品' },
  { label: '衣物鞋帽', value: '衣物鞋帽' },
  { label: '交通工具', value: '交通工具' },
  { label: '其他', value: '其他' }
];

const qualities = [
  { label: '全新', value: '全新' },
  { label: '99新', value: '99新' },
  { label: '95新', value: '95新' },
  { label: '9成新', value: '9成新' },
  { label: '8成新', value: '8成新' },
  { label: '7成新及以下', value: '7成新及以下' }
];

const form = ref({
  title: '',
  category: '',
  price: null,
  quality: '',
  description: ''
});

const isFormValid = computed(() => {
  return form.value.title &&
         form.value.category &&
         form.value.price !== null &&
         form.value.price >= 0 &&
         form.value.quality;
});

function triggerFileInput() {
  fileInput.value?.click();
}

function handleFileChange(e) {
  const files = Array.from(e.target.files);
  addImages(files);
}

function handleDrop(e) {
  const files = Array.from(e.dataTransfer.files).filter(f => f.type.startsWith('image/'));
  addImages(files);
}

function addImages(files) {
  const remaining = 5 - previewImages.value.length;
  const toAdd = files.slice(0, remaining);

  toAdd.forEach(file => {
    const reader = new FileReader();
    reader.onload = (e) => {
      previewImages.value.push(e.target.result);
    };
    reader.readAsDataURL(file);
    uploadedFiles.value.push(file);
  });
}

function removeImage(idx) {
  previewImages.value.splice(idx, 1);
  uploadedFiles.value.splice(idx, 1);
}

async function submitProduct() {
  if (!isFormValid.value) return;

  isSubmitting.value = true;
  try {
    // 根据后端 ProductCreateDTO 的字段结构
    const data = {
      title: form.value.title,
      category: form.value.category,
      price: form.value.price,
      quality: form.value.quality,
      description: form.value.description || '',
      imageUrl: '' // 暂时为空，后续可通过上传接口补充
    };

    console.log('[Publish] 提交商品数据:', JSON.stringify(data, null, 2));

    const response = await productApi.createProduct(data);

    console.log('[Publish] 后端响应:', JSON.stringify(response.data, null, 2));

    if (response.data) {
      alert('商品发布成功！');
      router.push('/');
    }
  } catch (error) {
    console.error('发布商品失败:', error);
    console.error('错误详情:', error.response?.data);
    alert('发布失败，请稍后重试');
  } finally {
    isSubmitting.value = false;
  }
}

function goBack() {
  router.back();
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: #000;
  color: #fff;
  display: flex;
  flex-direction: column;
}

/* 头部 */
.publish-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  background: rgba(0,0,0,0.8);
  backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  width: 36px; height: 36px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.12);
  background: rgba(255,255,255,0.04);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.25s ease;
}

.back-btn:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(255,255,255,0.25);
}

.page-title {
  flex: 1;
  text-align: center;
  font-size: 17px;
  font-weight: 500;
  letter-spacing: 0.5px;
  margin: 0;
}

.header-spacer { width: 36px; }

/* 表单内容 */
.publish-content {
  flex: 1;
  padding: 24px 20px 40px;
  max-width: 680px;
  margin: 0 auto;
  width: 100%;
}

.publish-form {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* 图片上传 */
.form-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-label {
  font-size: 14px;
  font-weight: 500;
  color: rgba(255,255,255,0.85);
}

.image-upload-area {
  border: 1.5px dashed rgba(255,255,255,0.15);
  border-radius: 16px;
  padding: 32px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255,255,255,0.02);
}

.image-upload-area:hover {
  border-color: rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.04);
}

.hidden-input { display: none; }

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: rgba(255,255,255,0.4);
}

.upload-placeholder svg {
  stroke: rgba(255,255,255,0.3);
}

.upload-text {
  font-size: 14px;
  color: rgba(255,255,255,0.6);
}

.upload-hint {
  font-size: 12px;
  color: rgba(255,255,255,0.3);
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.preview-item {
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  background: rgba(255,255,255,0.05);
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-img {
  position: absolute;
  top: 6px; right: 6px;
  width: 24px; height: 24px;
  border-radius: 50%;
  background: rgba(0,0,0,0.6);
  border: none;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.remove-img:hover {
  background: rgba(0,0,0,0.8);
}

.add-more {
  aspect-ratio: 1;
  border-radius: 12px;
  border: 1.5px dashed rgba(255,255,255,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255,255,255,0.3);
  cursor: pointer;
  transition: all 0.25s;
}

.add-more:hover {
  border-color: rgba(255,255,255,0.3);
  color: rgba(255,255,255,0.6);
}

/* 表单组 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: rgba(255,255,255,0.85);
  display: flex;
  align-items: center;
  gap: 4px;
}

.required {
  color: #ff4444;
  font-size: 12px;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.04);
  color: #fff;
  font-size: 15px;
  transition: all 0.25s ease;
  outline: none;
}

.form-input::placeholder {
  color: rgba(255,255,255,0.3);
}

.form-input:focus {
  border-color: rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.06);
}

.price-input-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-symbol {
  font-size: 18px;
  font-weight: 600;
  color: rgba(255,255,255,0.7);
}

.price-input {
  flex: 1;
}

/* 分类选择 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.category-btn {
  padding: 12px 8px;
  border-radius: 10px;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.03);
  color: rgba(255,255,255,0.6);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.25s ease;
  text-align: center;
}

.category-btn:hover {
  border-color: rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.06);
}

.category-btn.active {
  border-color: #fff;
  background: rgba(255,255,255,0.12);
  color: #fff;
  font-weight: 500;
}

/* 成色选择 */
.quality-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quality-btn {
  padding: 10px 18px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.03);
  color: rgba(255,255,255,0.6);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.quality-btn:hover {
  border-color: rgba(255,255,255,0.2);
}

.quality-btn.active {
  border-color: #fff;
  background: rgba(255,255,255,0.12);
  color: #fff;
  font-weight: 500;
}

/* 文本域 */
.form-textarea {
  width: 100%;
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.04);
  color: #fff;
  font-size: 15px;
  resize: vertical;
  min-height: 120px;
  transition: all 0.25s ease;
  outline: none;
  font-family: inherit;
  line-height: 1.6;
}

.form-textarea::placeholder {
  color: rgba(255,255,255,0.3);
}

.form-textarea:focus {
  border-color: rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.06);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: rgba(255,255,255,0.3);
}

/* 按钮 */
.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 12px;
}

.btn-secondary {
  flex: 1;
  padding: 14px 24px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.15);
  background: transparent;
  color: rgba(255,255,255,0.7);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
}

.btn-secondary:hover {
  background: rgba(255,255,255,0.06);
  border-color: rgba(255,255,255,0.25);
}

.btn-primary {
  flex: 2;
  padding: 14px 24px;
  border-radius: 12px;
  border: none;
  background: #fff;
  color: #000;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-primary:hover:not(:disabled) {
  background: rgba(255,255,255,0.9);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-spinner {
  width: 18px; height: 18px;
  border: 2px solid rgba(0,0,0,0.2);
  border-top-color: #000;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 480px) {
  .publish-content {
    padding: 16px 16px 32px;
  }

  .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .image-preview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>