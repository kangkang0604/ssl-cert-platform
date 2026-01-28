<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h2>欢迎回来，{{ userStore.username }}！</h2>
        <p>您当前使用的是 <strong>{{ userStore.packageName }}</strong>，到期时间：{{ userStore.packageExpireTime || '永久' }}</p>
      </div>
      <router-link to="/certificates/apply" class="apply-btn">
        <el-icon><Plus /></el-icon>
        申请新证书
      </router-link>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon total">
            <el-icon><Certificate /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.total || 0 }}</div>
            <div class="stat-label">证书总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon issued">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.issued || 0 }}</div>
            <div class="stat-label">已签发</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon expiring">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.expiring || 0 }}</div>
            <div class="stat-label">即将过期</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon expired">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.expired || 0 }}</div>
            <div class="stat-label">已过期</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 使用情况 -->
    <el-row :gutter="20" class="usage-section">
      <el-col :span="12">
        <el-card class="usage-card">
          <template #header>
            <div class="card-header">
              <span>资源使用情况</span>
            </div>
          </template>
          <div class="usage-item">
            <div class="usage-label">
              <span>证书配额</span>
              <span>{{ usage.cert.used }} / {{ usage.cert.total }}</span>
            </div>
            <el-progress :percentage="usage.cert.percent" :color="getProgressColor(usage.cert.percent)" />
          </div>
          <div class="usage-item">
            <div class="usage-label">
              <span>服务器配额</span>
              <span>{{ usage.server.used }} / {{ usage.server.total }}</span>
            </div>
            <el-progress :percentage="usage.server.percent" :color="getProgressColor(usage.server.percent)" />
          </div>
          <div class="usage-item">
            <div class="usage-label">
              <span>部署次数</span>
              <span>{{ usage.deploy.used }} / {{ usage.deploy.total }}</span>
            </div>
            <el-progress :percentage="usage.deploy.percent" :color="getProgressColor(usage.deploy.percent)" />
          </div>
          <div class="usage-item">
            <div class="usage-label">
              <span>监控数量</span>
              <span>{{ usage.monitor.used }} / {{ usage.monitor.total }}</span>
            </div>
            <el-progress :percentage="usage.monitor.percent" :color="getProgressColor(usage.monitor.percent)" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="usage-card">
          <template #header>
            <div class="card-header">
              <span>最近证书</span>
              <router-link to="/certificates" class="more-link">查看更多</router-link>
            </div>
          </template>
          <el-table :data="recentCertificates" style="width: 100%" v-if="recentCertificates.length > 0">
            <el-table-column prop="domainName" label="域名" min-width="150">
              <template #default="{ row }">
                <el-link type="primary" @click="viewCertDetail(row.id)">
                  {{ row.domainName }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="statusText" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.statusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="daysRemaining" label="剩余天数" width="100">
              <template #default="{ row }">
                {{ row.daysRemaining || '-' }} 天
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无证书" :image-size="100" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-card class="quick-actions">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <div class="action-list">
        <div class="action-item" @click="$router.push('/certificates/apply')">
          <el-icon class="action-icon"><Plus /></el-icon>
          <span>申请证书</span>
        </div>
        <div class="action-item" @click="$router.push('/domains')">
          <el-icon class="action-icon"><Grid /></el-icon>
          <span>域名管理</span>
        </div>
        <div class="action-item" @click="$router.push('/servers')">
          <el-icon class="action-icon"><Monitor /></el-icon>
          <span>服务器管理</span>
        </div>
        <div class="action-item" @click="$router.push('/monitors')">
          <el-icon class="action-icon"><Bell /></el-icon>
          <span>监控设置</span>
        </div>
        <div class="action-item" @click="$router.push('/deployments')">
          <el-icon class="action-icon"><Upload /></el-icon>
          <span>部署记录</span>
        </div>
        <div class="action-item" @click="$router.push('/settings')">
          <el-icon class="action-icon"><Setting /></el-icon>
          <span>系统设置</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCertificateList } from '@/api/certificate'

const router = useRouter()
const userStore = useUserStore()

// 统计数据
const statistics = reactive({
  total: 0,
  issued: 0,
  expiring: 0,
  expired: 0,
  applying: 0
})

// 使用情况
const usage = reactive({
  cert: { used: 0, total: 1, percent: 0 },
  server: { used: 0, total: 1, percent: 0 },
  deploy: { used: 0, total: 10, percent: 0 },
  monitor: { used: 0, total: 5, percent: 0 }
})

// 最近证书
const recentCertificates = ref([])

// 获取进度条颜色
const getProgressColor = (percent) => {
  if (percent >= 90) return '#F56C6C'
  if (percent >= 70) return '#E6A23C'
  return '#67C23A'
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    0: 'info',      // 申请中
    1: 'success',   // 已签发
    2: 'warning',   // 即将过期
    3: 'danger',    // 已过期
    4: 'danger',    // 已吊销
    5: 'danger'     // 申请失败
  }
  return map[status] || 'info'
}

// 查看证书详情
const viewCertDetail = (id) => {
  router.push(`/certificates/${id}`)
}

// 加载数据
const loadData = async () => {
  try {
    // 获取证书列表
    const response = await getCertificateList({ pageNum: 1, pageSize: 5 })
    const data = response.data
    
    // 更新统计数据
    if (data.statistics) {
      Object.assign(statistics, data.statistics)
    }
    
    // 更新最近证书
    recentCertificates.value = data.list || []
    
    // 模拟使用情况数据（实际应该从API获取）
    usage.cert = { used: statistics.total, total: 5, percent: (statistics.total / 5 * 100).toFixed(0) }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .welcome-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    margin-bottom: 20px;
    color: #fff;

    h2 {
      margin-bottom: 8px;
      font-size: 24px;
    }

    p {
      opacity: 0.9;
      font-size: 14px;
    }

    .apply-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      background: #fff;
      color: #667eea;
      border-radius: 6px;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
  }

  .stat-cards {
    margin-bottom: 20px;

    .stat-card {
      :deep(.el-card__body) {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #fff;

        &.total { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        &.issued { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
        &.expiring { background: linear-gradient(135deg, #f2994a 0%, #f2c94c 100%); }
        &.expired { background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%); }
      }

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #333;
      }

      .stat-label {
        font-size: 14px;
        color: #999;
      }
    }
  }

  .usage-section {
    margin-bottom: 20px;

    .usage-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .more-link {
          color: #409eff;
          font-size: 14px;
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }
      }

      .usage-item {
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .usage-label {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          font-size: 14px;
          color: #666;
        }
      }
    }
  }

  .quick-actions {
    .action-list {
      display: grid;
      grid-template-columns: repeat(6, 1fr);
      gap: 20px;

      .action-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
        padding: 20px;
        background: #f5f7fa;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #e4e7ed;
          transform: translateY(-2px);
        }

        .action-icon {
          width: 48px;
          height: 48px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #409eff;
          border-radius: 12px;
          color: #fff;
          font-size: 20px;
        }

        span {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}
</style>
