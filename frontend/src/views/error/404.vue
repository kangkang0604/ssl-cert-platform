<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-code">404</div>
      <div class="error-message">页面不存在</div>
      <div class="error-description">抱歉，您访问的页面不存在或已被删除</div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.error-container {
  text-align: center;
  color: #fff;
  
  .error-code {
    font-size: 120px;
    font-weight: 700;
    line-height: 1;
    margin-bottom: 20px;
    text-shadow: 4px 4px 8px rgba(0, 0, 0, 0.2);
  }
  
  .error-message {
    font-size: 32px;
    font-weight: 600;
    margin-bottom: 12px;
  }
  
  .error-description {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 40px;
  }
  
  .error-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    
    .el-button {
      padding: 12px 32px;
      font-size: 16px;
    }
  }
}
</style>
