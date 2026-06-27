<template>
  <span :class="['status-badge', type, statusClass]" :style="colorStyle">
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'
import {
  ORDER_STATUS_MAP,
  ORDER_STATUS_COLORS,
  PRODUCT_STATUS_MAP,
  QUALITY_CLASS_MAP
} from '@/constants'

const props = defineProps({
  status: { type: String, required: true },
  type: { type: String, default: 'order' }
})

const label = computed(() => {
  if (props.type === 'order') return ORDER_STATUS_MAP[props.status] || props.status
  if (props.type === 'product') return PRODUCT_STATUS_MAP[props.status] || props.status
  if (props.type === 'quality') return props.status
  return props.status
})

const colorStyle = computed(() => {
  if (props.type === 'order' && ORDER_STATUS_COLORS[props.status]) {
    return {
      backgroundColor: ORDER_STATUS_COLORS[props.status].bg,
      color: ORDER_STATUS_COLORS[props.status].color
    }
  }
  return {}
})

const statusClass = computed(() => {
  if (props.type === 'order') return props.status || ''
  if (props.type === 'product') return (props.status || '').toLowerCase()
  if (props.type === 'quality') {
    return QUALITY_CLASS_MAP[props.status] || ''
  }
  return ''
})
</script>

<style scoped>
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: #F5F5F5;
  color: #666;
}

/* 订单状态 */
.status-badge.PENDING_PAYMENT { background: #FFF3E0; color: #E65100; }
.status-badge.PENDING_DELIVERY { background: #E3F2FD; color: #1976D2; }
.status-badge.COMPLETED { background: #E8F5E9; color: #2E7D32; }
.status-badge.CLOSED { background: #F5F5F5; color: #999; }

/* 商品状态 */
.status-badge.active { background: #E8F5E9; color: #388E3C; }
.status-badge.sold { background: #FFF3E0; color: #F57C00; }
.status-badge.closed { background: #F5F5F5; color: #9E9E9E; }
</style>
