import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 静态路由
const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '控制台', icon: 'Odometer' }
      },
      {
        path: 'certificates',
        name: 'Certificates',
        component: () => import('@/views/certificates/index.vue'),
        meta: { title: '证书管理', icon: 'Certificate' }
      },
      {
        path: 'certificates/apply',
        name: 'CertificateApply',
        component: () => import('@/views/certificates/apply.vue'),
        meta: { title: '申请证书', icon: 'Plus' }
      },
      {
        path: 'certificates/:id',
        name: 'CertificateDetail',
        component: () => import('@/views/certificates/detail.vue'),
        meta: { title: '证书详情', hidden: true }
      },
      {
        path: 'domains',
        name: 'Domains',
        component: () => import('@/views/domains/index.vue'),
        meta: { title: '域名管理', icon: 'Grid' }
      },
      {
        path: 'servers',
        name: 'Servers',
        component: () => import('@/views/servers/index.vue'),
        meta: { title: '服务器管理', icon: 'Monitor' }
      },
      {
        path: 'deployments',
        name: 'Deployments',
        component: () => import('@/views/deployments/index.vue'),
        meta: { title: '部署管理', icon: 'Upload' }
      },
      {
        path: 'monitors',
        name: 'Monitors',
        component: () => import('@/views/monitors/index.vue'),
        meta: { title: '监控管理', icon: 'Bell' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/index.vue'),
        meta: { title: '系统设置', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  }
]

// 创建路由器
const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title 
    ? `${to.meta.title} - SSL证书管理平台` 
    : 'SSL证书管理平台'

  // 验证登录状态
  if (to.path !== '/login' && to.path !== '/register') {
    if (!userStore.token) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }

  // 已登录用户访问登录页，跳转到首页
  if ((to.path === '/login' || to.path === '/register') && userStore.token) {
    next({ name: 'Dashboard' })
    return
  }

  next()
})

export default router
