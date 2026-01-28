import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, logout, getUserInfo } from '@/api/user'
import { useRouter } from 'vue-router'

export const useUserStore = defineStore('user', () => {
  const router = useRouter()
  
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const email = ref(localStorage.getItem('email') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  const packageName = ref(localStorage.getItem('packageName') || '')
  const packageExpireTime = ref(localStorage.getItem('packageExpireTime') || '')

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 方法
  async function doLogin(data) {
    try {
      const response = await login(data)
      // 后端返回结构: {code, message, data: {token, userId, ...}, timestamp}
      // response.data 是后端的Result对象
      // response.data.data 才是登录的真正数据
      const loginData = response.data.data
      const { token: newToken, userId: newUserId, username: newUsername, email: newEmail, avatar: newAvatar } = loginData

      token.value = newToken
      userId.value = newUserId
      username.value = newUsername
      email.value = newEmail
      avatar.value = newAvatar

      // 持久化存储
      localStorage.setItem('token', newToken)
      localStorage.setItem('userId', newUserId)
      localStorage.setItem('username', newUsername)
      localStorage.setItem('email', newEmail)
      localStorage.setItem('avatar', newAvatar || '')

      // 获取用户套餐信息
      await getUserInfo()

      return { success: true }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }

  async function doRegister(data) {
    try {
      await register(data)
      return { success: true, message: '注册成功' }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }

  async function doLogout() {
    try {
      await logout()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除状态
      token.value = ''
      userId.value = ''
      username.value = ''
      email.value = ''
      avatar.value = ''
      packageName.value = ''
      packageExpireTime.value = ''

      // 清除持久化存储
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('email')
      localStorage.removeItem('avatar')
      localStorage.removeItem('packageName')
      localStorage.removeItem('packageExpireTime')

      // 跳转到登录页
      router.push({ name: 'Login' })
    }
  }

  async function getUserInfo() {
    try {
      const response = await getUserInfo()
      // 后端返回结构: {code, message, data: {...}, timestamp}
      const data = response.data.data
      packageName.value = data.packageName || '免费版'
      packageExpireTime.value = data.packageExpireTime || ''
      localStorage.setItem('packageName', packageName.value)
      localStorage.setItem('packageExpireTime', packageExpireTime.value || '')
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  return {
    // 状态
    token,
    userId,
    username,
    email,
    avatar,
    packageName,
    packageExpireTime,
    // 计算属性
    isLoggedIn,
    // 方法
    doLogin,
    doRegister,
    doLogout,
    getUserInfo
  }
})
