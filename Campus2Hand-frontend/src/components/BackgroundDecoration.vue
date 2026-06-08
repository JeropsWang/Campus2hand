<template>
  <div class="background-decoration">
    <div class="pattern-grid"></div>
    <div class="circle circle-1"></div>
    <div class="circle circle-2"></div>
    <div class="circle circle-3"></div>
    <div class="line line-1"></div>
    <div class="line line-2"></div>
    <div class="square square-1"></div>
    <div class="square square-2"></div>
  </div>
</template>

<style scoped>
.background-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.pattern-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(26, 26, 26, 0.05);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: -100px;
  left: -100px;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: -75px;
  right: -75px;
  animation-delay: -2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 50%;
  right: 10%;
  animation-delay: -4s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.line {
  position: absolute;
  background: rgba(26, 26, 26, 0.08);
}

.line-1 {
  width: 2px;
  height: 150px;
  top: 20%;
  left: 5%;
  transform: rotate(-15deg);
}

.line-2 {
  width: 100px;
  height: 2px;
  bottom: 30%;
  right: 15%;
  transform: rotate(45deg);
}

.square {
  position: absolute;
  background: rgba(26, 26, 26, 0.05);
}

.square-1 {
  width: 60px;
  height: 60px;
  top: 30%;
  left: 85%;
  transform: rotate(15deg);
}

.square-2 {
  width: 40px;
  height: 40px;
  bottom: 20%;
  left: 10%;
  transform: rotate(-30deg);
}
</style>