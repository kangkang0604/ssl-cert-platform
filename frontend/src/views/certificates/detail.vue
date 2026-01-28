<template>
  <div class="certificate-detail-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-page-header @back="goBack">
            <template #content>
              <span class="page-title">证书详情</span>
            </template>
            <template #extra>
              <el-button type="primary" @click="handleDeploy">
                <el-icon><Upload /></el-icon>
                部署证书
              </el-button>
              <el-button @click="handleRenew" :disabled="certificate?.status !== 1">
                <el-icon><Refresh /></el-icon>
                手动续期
              </el-button>
            </template>
          </el-page-header>
        </div>
      </template>

      <div v-if="certificate" class="detail-content">
        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="2" border class="info-section">
          <el-descriptions-item label="域名">
            {{ certificate.domainName }}
          </el-descriptions-item>
          <el-descriptions-item label="证书类型">
            <el-tag>{{ certificate.certTypeText }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="证书品牌">
            {{ certificate.brand }}
          </el-descriptions-item>
          <el-descriptions-item label="证书状态">
            <el-tag :type="getStatusType(certificate.status)">
              {{ certificate.statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="剩余天数">
            <span :class="{ 'text-danger': certificate.daysRemaining <= 7 }">
              {{ certificate.daysRemaining || '-' }} 天
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="自动续期">
            {{ certificate.autoRenew === 1 ? '是' : '否' }}
          </el-descriptions-item>
          <el-descriptions-item label="续期次数">
            {{ certificate.renewCount }} 次
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateFn(certificate.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="生效时间">
            {{ formatDateFn(certificate.notBefore) }}
          </el-descriptions-item>
          <el-descriptions-item label="过期时间">
            {{ formatDateFn(certificate.notAfter) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 证书信息 -->
        <el-descriptions title="证书信息" :column="1" border class="info-section">
          <el-descriptions-item label="证书主题">
            {{ certificate.subject || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="颁发者">
            {{ certificate.issuer || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="序列号">
            {{ certificate.serialNumber || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 证书内容 -->
        <div class="cert-content-section">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="证书内容" name="cert">
              <div class="content-box">
                <div class="content-header">
                  <span>证书内容 (PEM格式)</span>
                  <el-button type="primary" link @click="downloadCert('cert')">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                  <el-button type="primary" link @click="copyContent(certificate.certPem)">
                    <el-icon><CopyDocument /></el-icon>
                    复制
                  </el-button>
                </div>
                <el-input
                  type="textarea"
                  :rows="10"
                  :model-value="certificate.certPem"
                  readonly
                  class="content-textarea"
                />
              </div>
            </el-tab-pane>

            <el-tab-pane label="私钥内容" name="key">
              <div class="content-box warning-box">
                <div class="content-header">
                  <span>私钥内容 (PEM格式)</span>
                  <el-tag type="danger" size="small">请妥善保管私钥</el-tag>
                  <el-button type="primary" link @click="downloadCert('key')">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                  <el-button type="primary" link @click="copyContent(certificate.privateKey)">
                    <el-icon><CopyDocument /></el-icon>
                    复制
                  </el-button>
                </div>
                <el-input
                  type="textarea"
                  :rows="10"
                  :model-value="certificate.privateKey"
                  readonly
                  class="content-textarea"
                />
              </div>
            </el-tab-pane>

            <el-tab-pane label="完整证书链" name="fullchain">
              <div class="content-box">
                <div class="content-header">
                  <span>完整证书链</span>
                  <el-button type="primary" link @click="downloadCert('full')">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                </div>
                <el-input
                  type="textarea"
                  :rows="15"
                  :model-value="`-----BEGIN CERTIFICATE-----\n${certificate.certPem}\n-----END CERTIFICATE-----\n\n-----BEGIN PRIVATE KEY-----\n${certificate.privateKey}\n-----END PRIVATE KEY-----`"
                  readonly
                  class="content-textarea"
                />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCertificateDetail } from '@/api/certificate'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const certificate = ref(null)
const activeTab = ref('cert')

onMounted(() => {
  const certId = route.params.id
  if (certId) {
    loadCertificateDetail(certId)
  }
})

const loadCertificateDetail = async (certId) => {
  loading.value = true
  try {
    const response = await getCertificateDetail(certId)
    certificate.value = response.data
  } catch (error) {
    ElMessage.error('获取证书详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/certificates')
}

const handleDeploy = () => {
  router.push(`/deployments?certId=${certificate.value.id}`)
}

const handleRenew = async () => {
  try {
    await ElMessageBox.confirm('确定要手动续期此证书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('续期功能开发中')
  } catch {}
}

const downloadCert = (type) => {
  if (!certificate.value) return
  
  let content = ''
  let filename = ''
  
  switch (type) {
    case 'cert':
      content = certificate.value.certPem
      filename = 'certificate.pem'
      break
    case 'key':
      content = certificate.value.privateKey
      filename = 'private.key'
      break
    case 'full':
      content = `-----BEGIN CERTIFICATE-----\n${certificate.value.certPem}\n-----END CERTIFICATE-----\n\n-----BEGIN PRIVATE KEY-----\n${certificate.value.privateKey}\n-----END PRIVATE KEY-----`
      filename = 'fullchain.pem'
      break
  }
  
  if (!content) {
    ElMessage.warning('证书内容不存在')
    return
  }
  
  const blob = new Blob([content], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  
  ElMessage.success('下载成功')
}

const copyContent = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'danger',
    5: 'danger'
  }
  return map[status] || 'info'
}
</script>

<style lang="scss" scoped>
.certificate-detail-page {
  .page-title {
    font-size: 18px;
    font-weight: 600;
  }

  .detail-content {
    .info-section {
      margin-bottom: 20px;
    }

    .cert-content-section {
      .content-box {
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        overflow: hidden;

        &.warning-box {
          border-color: #e6a23c;
          background: #fdf6ec;
        }

        .content-header {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 16px;
          background: #f5f7fa;
          border-bottom: 1px solid #e4e7ed;
          font-weight: 500;
        }

        .content-textarea {
          :deep(textarea) {
            border: none;
            resize: none;
            font-family: monospace;
            font-size: 12px;
          }
        }
      }
    }
  }

  .text-danger {
    color: #F56C6C;
    font-weight: 600;
  }
}
</style>
