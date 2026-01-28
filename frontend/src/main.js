import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import '@/styles/index.scss'

// 创建Vue应用
const app = createApp(App)

// 创建Pinia实例
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 挂载应用
app.mount('#app')

console.log('╔═══════════════════════════════════════════════════════════════╗')
console.log('║     SSL证书自动续期平台前端启动成功                              ║')
console.log('║     访问地址: http://localhost:3000                             ║')
console.log('╚═══════════════════════════════════════════════════════════════╝')
