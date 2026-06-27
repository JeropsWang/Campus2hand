import { reactive, readonly } from 'vue'

const toasts = reactive([])
let nextId = 1

function addToast(message, type = 'info', duration = 3000) {
  const id = nextId++
  toasts.push({ id, message, type, visible: true })
  setTimeout(() => {
    const idx = toasts.findIndex(t => t.id === id)
    if (idx !== -1) {
      toasts[idx].visible = false
      setTimeout(() => {
        const idx2 = toasts.findIndex(t => t.id === id)
        if (idx2 !== -1) toasts.splice(idx2, 1)
      }, 300)
    }
  }, duration)
}

export const toast = {
  success(msg, duration) {
    addToast(msg, 'success', duration)
  },
  error(msg, duration) {
    addToast(msg, 'error', duration)
  },
  info(msg, duration) {
    addToast(msg, 'info', duration)
  },
  warning(msg, duration) {
    addToast(msg, 'warning', duration)
  }
}

export function useToast() {
  return {
    toasts: readonly(toasts),
    toast
  }
}
