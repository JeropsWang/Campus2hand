<template>
  <div class="ai-chat-page">
    <!-- 流动背景层 -->
    <div class="flow-bg">
      <div class="flow-line line-1"></div>
      <div class="flow-line line-2"></div>
      <div class="flow-line line-3"></div>
      <div class="flow-line line-4"></div>
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
    </div>

    <!-- 头部 -->
    <header class="chat-header">
      <button class="back-btn" @click="goBack" aria-label="返回">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
      </button>
      <div class="header-center">
        <div class="avatar-ring">
          <div class="ai-header-avatar"><img src="/ai-avatar.png" alt="AI助手" /></div>
          <div class="ring-pulse"></div>
        </div>
        <div class="header-text">
          <h1 class="page-title">AI 客服助手</h1>
          <span class="online-status"><span class="status-pulse"></span>在线服务中</span>
        </div>
      </div>
      <div class="header-spacer"></div>
    </header>

    <!-- 聊天内容区 -->
    <main class="chat-content" ref="chatContainer">
      <Transition name="fade-up">
        <div v-if="showWelcome" class="welcome-section">
          <div class="ai-message-row">
            <div class="avatar-col"><div class="ai-avatar-img"><img src="/ai-avatar.png" alt="AI助手" /></div></div>
            <div class="bubble-col">
              <div class="message-bubble ai-bubble welcome-bubble">
                <p class="welcome-greeting">你好！我是 Campus2Hand 的 AI 客服助手</p>
                <p class="welcome-sub">有任何问题都可以随时问我</p>
                <div class="quick-tags">
                  <button v-for="tag in quickTags" :key="tag" class="tag-btn" @click="sendQuickMessage(tag)">{{ tag }}</button>
                </div>
              </div>
              <span class="msg-time">{{ currentTime }}</span>
            </div>
          </div>
        </div>
      </Transition>

      <!-- 消息列表 -->
      <TransitionGroup name="msg" tag="div" class="messages-wrapper">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['msg-row', msg.type]">
          <template v-if="msg.type === 'ai'">
            <div class="avatar-col"><div class="ai-avatar-img"><img src="/ai-avatar.png" alt="AI助手" /></div></div>
            <div class="bubble-col">
              <div class="message-bubble ai-bubble" :class="{ 'is-loading': msg.loading }">
                <div v-if="msg.loading" class="flow-typing"><span class="ft-dot"></span><span class="ft-dot"></span><span class="ft-dot"></span></div>
                <div v-else class="msg-body" v-html="formatMessage(msg.content)"></div>
              </div>
              <span class="msg-time">{{ msg.time }}</span>
            </div>
          </template>

          <template v-else>
            <div class="bubble-col user-col">
              <div class="message-bubble user-bubble">{{ msg.content }}</div>
              <span class="msg-time user-time">{{ msg.time }}</span>
            </div>
            <div class="avatar-col user-avatar-col">
              <div class="user-avatar-img">
                <img v-if="userAvatar" :src="userAvatar" alt="用户头像" />
                <span v-else class="user-avatar-placeholder">{{ getUserInitial() }}</span>
              </div>
            </div>
          </template>
        </div>
      </TransitionGroup>
    </main>

    <!-- 输入区域 -->
    <footer class="chat-footer">
      <div class="input-glass">
        <div class="input-inner">
          <textarea
            v-model="inputMessage" class="msg-input" placeholder="输入你的问题..." rows="1"
            @keyup.enter.exact.prevent="sendMessage" @input="autoResize" ref="inputRef"
          ></textarea>
          <button
            class="send-circle" :class="{ active: inputMessage.trim() && !isLoading }"
            :disabled="!inputMessage.trim() || isLoading" @click="sendMessage" aria-label="发送"
          >
            <svg v-if="!isLoading" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/></svg>
            <div v-else class="send-spinner"></div>
          </button>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { aiApi } from '@/services/api'
import { useUser } from '@/composables/useUser'
import { toast } from '@/composables/useToast'

const router = useRouter()
const { getUserAvatar, getUserInitial } = useUser()

const messages = ref([])
const inputMessage = ref('')
const inputRef = ref(null)
const chatContainer = ref(null)
const isLoading = ref(false)
const sessionId = ref('')
const showWelcome = ref(true)
const userAvatar = ref('')

const currentTime = computed(() => new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }))
const quickTags = ['商品退换货', '发布二手商品', '交易安全', '联系卖家']

function formatMessage(content) {
  if (!content) return ''
  return content.replace(/\n/g, '<br>').replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
}

function sendQuickMessage(text) { inputMessage.value = text; sendMessage() }

async function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text || isLoading.value) return
  showWelcome.value = false

  const now = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  messages.value.push({ type: 'user', content: text, time: now })
  inputMessage.value = ''
  if (inputRef.value) inputRef.value.style.height = 'auto'
  await nextTick(); scrollToBottom()

  messages.value.push({ type: 'ai', content: '', time: '', loading: true })
  await nextTick(); scrollToBottom()
  isLoading.value = true

  try {
    const res = await aiApi.chat({ message: text, sessionId: sessionId.value || undefined })
    const data = res.data || {}
    const last = messages.value[messages.value.length - 1]
    if (data.sessionId) sessionId.value = data.sessionId
    last.time = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    if (data.success && data.response) { last.content = data.response; last.loading = false }
    else if (data.code === 200 && data.data) { last.content = data.data; last.loading = false }
    else if (data.message) { last.content = data.message; last.loading = false }
    else { last.content = data.error || '抱歉，暂时无法回答。'; last.loading = false }
  } catch (err) {
    const last = messages.value[messages.value.length - 1]
    last.content = '网络异常，请稍后重试。'
    last.loading = false
    last.time = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } finally {
    isLoading.value = false
    await nextTick(); scrollToBottom()
  }
}

function scrollToBottom() {
  if (chatContainer.value) chatContainer.value.scrollTo({ top: chatContainer.value.scrollHeight, behavior: 'smooth' })
}
function autoResize() {
  const el = inputRef.value; if (!el) return
  el.style.height = 'auto'; el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}
function goBack() { router.back() }

onMounted(() => {
  userAvatar.value = getUserAvatar()
  sessionId.value = 'sess-' + Date.now() + '-' + Math.random().toString(36).slice(2, 8)
  setTimeout(() => inputRef.value?.focus(), 200)
})
</script>

<style scoped>
.ai-chat-page {
  min-height: 100vh; background: #000; display: flex; flex-direction: column;
  position: relative; overflow: hidden; color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
.flow-bg { position: fixed; inset: 0; pointer-events: none; z-index: 0; overflow: hidden; }
.flow-line { position: absolute; width: 1px; height: 200%; background: linear-gradient(to bottom, transparent, rgba(255,255,255,0.04) 30%, rgba(255,255,255,0.08) 50%, rgba(255,255,255,0.04) 70%, transparent); animation: flowDown 10s linear infinite; }
.line-1 { left: 15%; animation-delay: 0s; }
.line-2 { left: 40%; animation-delay: -3s; animation-duration: 12s; }
.line-3 { left: 65%; animation-delay: -6s; animation-duration: 14s; }
.line-4 { left: 88%; animation-delay: -9s; animation-duration: 11s; }
@keyframes flowDown { 0% { transform: translateY(-50%); } 100% { transform: translateY(0); } }
.glow-orb { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.06; animation: orbFloat 15s ease-in-out infinite; }
.orb-1 { width: 300px; height: 300px; background: #fff; top: 10%; left: -5%; }
.orb-2 { width: 250px; height: 250px; background: #888; bottom: 15%; right: -5%; animation-delay: -7s; }
@keyframes orbFloat { 0%,100% { transform: translate(0,0) scale(1); } 33% { transform: translate(30px,-20px) scale(1.1); } 66% { transform: translate(-20px,15px) scale(0.95); } }

.chat-header { position: relative; z-index: 10; display: flex; align-items: center; justify-content: space-between; padding: 14px 20px; background: linear-gradient(180deg, rgba(0,0,0,0.9), rgba(0,0,0,0.6)); backdrop-filter: blur(20px); border-bottom: 1px solid rgba(255,255,255,0.06); }
.back-btn { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 50%; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1); color: #fff; cursor: pointer; }
.header-center { display: flex; align-items: center; gap: 14px; }
.avatar-ring { position: relative; width: 44px; height: 44px; }
.ai-header-avatar { width: 44px; height: 44px; border-radius: 50%; background: rgba(255,255,255,0.1); display: flex; align-items: center; justify-content: center; z-index: 2; border: 2px solid rgba(255,255,255,0.15); overflow: hidden; }
.ai-header-avatar img { width: 100%; height: 100%; object-fit: cover; }
.ring-pulse { position: absolute; inset: -4px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.2); animation: ringPulse 2.5s ease-out infinite; }
@keyframes ringPulse { 0% { transform: scale(1); opacity: 0.6; } 100% { transform: scale(1.4); opacity: 0; } }
.header-text { display: flex; flex-direction: column; gap: 2px; }
.page-title { font-size: 16px; font-weight: 600; color: #fff; margin: 0; }
.online-status { font-size: 12px; color: rgba(255,255,255,0.5); display: flex; align-items: center; gap: 6px; }
.status-pulse { width: 6px; height: 6px; border-radius: 50%; background: #4ade80; box-shadow: 0 0 8px #4ade80; animation: statusBlink 2s ease-in-out infinite; }
@keyframes statusBlink { 0%,100% { opacity: 1; } 50% { opacity: 0.4; } }
.header-spacer { width: 36px; }

.chat-content { flex: 1; position: relative; z-index: 5; overflow-y: auto; padding: 24px 20px 80px; }
.chat-content::-webkit-scrollbar { width: 4px; } .chat-content::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); }
.messages-wrapper { display: flex; flex-direction: column; gap: 20px; }

.welcome-section { margin-bottom: 8px; }
.ai-message-row { display: flex; gap: 12px; align-items: flex-start; }
.avatar-col { flex-shrink: 0; }
.ai-avatar-img, .user-avatar-img { width: 38px; height: 38px; border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; }
.ai-avatar-img { background: rgba(255,255,255,0.08); border: 1.5px solid rgba(255,255,255,0.12); }
.user-avatar-img { background: rgba(255,255,255,0.9); border: 1.5px solid rgba(255,255,255,0.3); }
.avatar-img img, .user-avatar-img img { width: 100%; height: 100%; object-fit: cover; }
.user-avatar-placeholder { font-size: 16px; font-weight: 600; color: #333; }
.bubble-col { display: flex; flex-direction: column; gap: 4px; max-width: 75%; }
.user-col { margin-left: auto; align-items: flex-end; }
.message-bubble { padding: 14px 18px; font-size: 14.5px; line-height: 1.7; word-wrap: break-word; }
.ai-bubble { background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.92); border: 1px solid rgba(255,255,255,0.08); border-radius: 18px 18px 18px 4px; backdrop-filter: blur(10px); }
.user-bubble { background: rgba(255,255,255,0.95); color: #000; border-radius: 18px 18px 4px 18px; font-weight: 450; }
.welcome-bubble { padding: 18px 20px; }
.welcome-greeting { font-size: 15px; font-weight: 500; margin: 0 0 4px; color: #fff; }
.welcome-sub { font-size: 13px; color: rgba(255,255,255,0.5); margin: 0 0 14px; }
.quick-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-btn { padding: 7px 14px; border-radius: 20px; border: 1px solid rgba(255,255,255,0.12); background: rgba(255,255,255,0.04); color: rgba(255,255,255,0.7); font-size: 13px; cursor: pointer; }
.tag-btn:hover { background: rgba(255,255,255,0.12); border-color: rgba(255,255,255,0.25); color: #fff; }
.msg-time { font-size: 11px; color: rgba(255,255,255,0.3); padding: 0 4px; }
.user-time { text-align: right; }
.msg-row { display: flex; gap: 12px; align-items: flex-start; }
.msg-row.ai { justify-content: flex-start; }
.msg-row.user { justify-content: flex-end; }

.flow-typing { display: flex; gap: 5px; align-items: center; padding: 4px 8px; }
.ft-dot { width: 7px; height: 7px; border-radius: 50%; background: rgba(255,255,255,0.5); animation: flowDot 1.4s cubic-bezier(0.45,0.05,0.55,0.95) infinite; }
.ft-dot:nth-child(1) { animation-delay: 0s; } .ft-dot:nth-child(2) { animation-delay: 0.18s; } .ft-dot:nth-child(3) { animation-delay: 0.36s; }
@keyframes flowDot { 0%,100% { transform: translateY(0) scale(1); opacity: 0.4; } 40% { transform: translateY(-6px) scale(1.15); opacity: 1; background: rgba(255,255,255,0.9); } 80% { transform: translateY(2px) scale(0.9); opacity: 0.5; } }

.chat-footer { position: relative; z-index: 10; padding: 12px 20px 24px; background: linear-gradient(0deg, rgba(0,0,0,0.95), rgba(0,0,0,0.7) 60%, transparent); }
.input-glass { background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.08); border-radius: 28px; padding: 4px; backdrop-filter: blur(20px); }
.input-glass:focus-within { border-color: rgba(255,255,255,0.2); }
.input-inner { display: flex; align-items: flex-end; gap: 8px; padding: 4px 4px 4px 16px; }
.msg-input { flex: 1; background: transparent; border: none; outline: none; color: #fff; font-size: 15px; line-height: 1.5; padding: 10px 0; resize: none; max-height: 120px; font-family: inherit; }
.msg-input::placeholder { color: rgba(255,255,255,0.3); }
.send-circle { width: 40px; height: 40px; border-radius: 50%; border: none; background: rgba(255,255,255,0.1); color: rgba(255,255,255,0.4); display: flex; align-items: center; justify-content: center; cursor: pointer; flex-shrink: 0; }
.send-circle.active { background: #fff; color: #000; box-shadow: 0 0 20px rgba(255,255,255,0.15); }
.send-circle:disabled { opacity: 0.5; cursor: not-allowed; }
.send-spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.2); border-top-color: #fff; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.msg-enter-active, .msg-leave-active { transition: all 0.45s cubic-bezier(0.34,1.56,0.64,1); }
.msg-enter-from { opacity: 0; transform: translateY(24px) scale(0.94); }
.fade-up-enter-active { transition: all 0.6s cubic-bezier(0.34,1.56,0.64,1); }
.fade-up-enter-from { opacity: 0; transform: translateY(30px); }

@media (max-width: 480px) {
  .chat-header { padding: 12px 16px; }
  .chat-content { padding: 16px 14px 70px; }
  .chat-footer { padding: 10px 14px 20px; }
}
</style>
