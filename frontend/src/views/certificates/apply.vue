<template>
  <div class="certificate-apply-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-page-header @back="router.back()">
            <template #content>
              <span class="page-title">申请SSL证书</span>
            </template>
          </el-page-header>
        </div>
      </template>

      <el-steps :active="activeStep" finish-status="success" align-center class="apply-steps">
        <el-step title="选择类型" description="选择证书类型" />
        <el-step title="填写信息" description="填写域名信息" />
        <el-step title="DNS验证" description="配置DNS解析" />
        <el-step title="提交申请" description="确认并提交" />
      </el-steps>

      <div class="step-content">
        <!-- 步骤1：选择证书类型 -->
        <div v-if="activeStep === 0" class="step-panel">
          <h3 class="step-title">选择证书类型</h3>
          
          <el-row :gutter="20" class="cert-type-list">
            <el-col :span="8" v-for="type in certTypes" :key="type.value">
              <div 
                class="cert-type-item"
                :class="{ active: applyForm.certType === type.value }"
                @click="applyForm.certType = type.value"
              >
                <div class="type-icon">
                  <el-icon><component :is="type.icon" /></el-icon>
                </div>
                <div class="type-name">{{ type.name }}</div>
                <div class="type-desc">{{ type.description }}</div>
                <el-radio v-model="applyForm.certType" :label="type.value" class="type-radio" />
              </div>
            </el-col>
          </el-row>

          <h3 class="step-title" style="margin-top: 30px;">选择证书品牌</h3>
          
          <el-radio-group v-model="applyForm.brand" class="brand-list">
            <el-radio-button 
              v-for="brand in certBrands" 
              :key="brand.value" 
              :label="brand.value"
            >
              {{ brand.name }}
            </el-radio-button>
          </el-radio-group>

          <div class="step-actions">
            <el-button type="primary" @click="nextStep" :disabled="!applyForm.certType">
              下一步
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 步骤2：填写域名信息 -->
        <div v-if="activeStep === 1" class="step-panel">
          <h3 class="step-title">填写域名信息</h3>
          
          <el-form
            ref="domainFormRef"
            :model="applyForm"
            :rules="domainRules"
            label-width="120px"
            class="domain-form"
          >
            <el-form-item label="域名列表" prop="domains">
              <el-input
                type="textarea"
                v-model="domainInput"
                :rows="4"
                placeholder="每行一个域名，如：example.com"
                @blur="parseDomains"
              />
              <div class="form-tip">
                单域名证书：example.com<br>
                多域名证书：每行一个域名，最多50个<br>
                通配符证书：*.example.com
              </div>
            </el-form-item>

            <el-form-item label="DNS服务商" prop="dnsProvider">
              <el-select v-model="applyForm.dnsProvider" placeholder="选择DNS服务商" style="width: 100%;">
                <el-option label="阿里云DNS" value="aliyun" />
                <el-option label="腾讯云DNSPod" value="tencent" />
                <el-option label="Cloudflare" value="cloudflare" />
              </el-select>
            </el-form-item>

            <el-form-item label="AccessKey ID" v-if="applyForm.dnsProvider">
              <el-input v-model="applyForm.dnsAccessKeyId" placeholder="请输入DNS服务商的AccessKey ID" />
            </el-form-item>

            <el-form-item label="AccessKey Secret" v-if="applyForm.dnsProvider">
              <el-input v-model="applyForm.dnsAccessKeySecret" type="password" placeholder="请输入DNS服务商的AccessKey Secret" show-password />
            </el-form-item>

            <el-form-item label="自动续期">
              <el-switch v-model="applyForm.autoRenew" :active-value="1" :inactive-value="0" />
              <span class="form-tip" style="margin-left: 10px;">证书将在到期前30天自动续期</span>
            </el-form-item>
          </el-form>

          <div class="step-actions">
            <el-button @click="prevStep">
              <el-icon><ArrowLeft /></el-icon>
              上一步
            </el-button>
            <el-button type="primary" @click="nextStep" :disabled="!domainFormRef">
              下一步
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 步骤3：DNS验证 -->
        <div v-if="activeStep === 2" class="step-panel">
          <h3 class="step-title">配置DNS解析</h3>
          
          <el-alert
            type="info"
            title="请添加以下DNS TXT记录"
            description="DNS验证是证明您对该域名所有权的必要步骤"
            show-icon
            :closable="false"
            style="margin-bottom: 20px;"
          />

          <div class="dns-records">
            <el-table :data="dnsRecords" style="width: 100%">
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="host" label="主机记录" />
              <el-table-column prop="value" label="记录值" min-width="300">
                <template #default="{ row }">
                  <el-link type="primary" @click="copyToClipboard(row.value)">
                    {{ row.value }}
                    <el-icon><CopyDocument /></el-icon>
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="ttl" label="TTL" width="100" />
            </el-table>
          </div>

          <div class="verify-section">
            <el-button type="primary" @click="verifyDns" :loading="verifying">
              <el-icon><CircleCheck /></el-icon>
              我已添加DNS记录，验证域名
            </el-button>
          </div>

          <div class="step-actions">
            <el-button @click="prevStep">
              <el-icon><ArrowLeft /></el-icon>
              上一步
            </el-button>
            <el-button type="primary" @click="nextStep" :disabled="!dnsVerified">
              下一步
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 步骤4：提交申请 -->
        <div v-if="activeStep === 3" class="step-panel">
          <h3 class="step-title">确认申请信息</h3>
          
          <el-descriptions :column="2" border class="apply-summary">
            <el-descriptions-item label="证书类型">
              {{ getCertTypeName(applyForm.certType) }}
            </el-descriptions-item>
            <el-descriptions-item label="证书品牌">
              {{ getBrandName(applyForm.brand) }}
            </el-descriptions-item>
            <el-descriptions-item label="域名数量" :span="2">
              {{ applyForm.domains.length }} 个
            </el-descriptions-item>
            <el-descriptions-item label="域名列表" :span="2">
              <div class="domain-list">
                <el-tag v-for="domain in applyForm.domains" :key="domain" size="small" style="margin: 2px;">
                  {{ domain }}
                </el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="DNS服务商">
              {{ getDnsProviderName(applyForm.dnsProvider) }}
            </el-descriptions-item>
            <el-descriptions-item label="自动续期">
              {{ applyForm.autoRenew === 1 ? '是' : '否' }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="step-actions">
            <el-button @click="prevStep">
              <el-icon><ArrowLeft /></el-icon>
              上一步
            </el-button>
            <el-button type="primary" @click="submitApply" :loading="submitting">
              <el-icon><Check /></el-icon>
              确认申请
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { applyCertificate } from '@/api/certificate'

const router = useRouter()

// 当前步骤
const activeStep = ref(0)

// 证书类型
const certTypes = [
  { value: 1, name: '单域名证书', description: '只保护一个域名', icon: 'Link' },
  { value: 2, name: '多域名证书', description: '可保护多个域名', icon: 'Grid' },
  { value: 3, name: '通配符证书', description: '保护所有子域名', icon: 'Connection' }
]

// 证书品牌
const certBrands = [
  { value: 'letsencrypt', name: 'Let\'s Encrypt' },
  { value: 'zerossl', name: 'ZeroSSL' },
  { value: 'google', name: 'Google Trust Services' }
]

// 域名输入
const domainInput = ref('')

// 申请表单
const applyForm = reactive({
  certType: '',
  brand: 'letsencrypt',
  domains: [],
  dnsProvider: '',
  dnsAccessKeyId: '',
  dnsAccessKeySecret: '',
  autoRenew: 1
})

// 表单验证
const domainFormRef = ref(null)
const domainRules = {
  domains: [
    { required: true, message: '请填写域名', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value.length === 0) {
          callback(new Error('请填写域名'))
        } else if (value.length > 50) {
          callback(new Error('最多支持50个域名'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  dnsProvider: [
    { required: true, message: '请选择DNS服务商', trigger: 'change' }
  ]
}

// DNS记录
const dnsRecords = computed(() => {
  return applyForm.domains.map((domain, index) => ({
    type: 'TXT',
    host: `_acme-challenge.${domain.replace('*.', '')}`,
    value: `verify-${Date.now()}-${index}`,
    ttl: '600'
  }))
})

// DNS验证状态
const dnsVerified = ref(false)
const verifying = ref(false)

// 下一步
const nextStep = async () => {
  if (activeStep.value === 0) {
    if (!applyForm.certType) {
      ElMessage.warning('请选择证书类型')
      return
    }
  } else if (activeStep.value === 1) {
    if (!domainFormRef.value) return
    
    await domainFormRef.value.validate((valid) => {
      if (valid) {
        activeStep.value++
      }
    })
    return
  } else if (activeStep.value === 2) {
    if (!dnsVerified.value) {
      ElMessage.warning('请先完成DNS验证')
      return
    }
  }
  
  activeStep.value++
}

// 上一步
const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

// 解析域名
const parseDomains = () => {
  const lines = domainInput.value.split('\n').map(line => line.trim()).filter(line => line)
  applyForm.domains = [...new Set(lines)]
}

// 验证DNS
const verifyDns = async () => {
  verifying.value = true
  
  try {
    // 模拟DNS验证过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    dnsVerified.value = true
    ElMessage.success('DNS验证成功')
  } catch (error) {
    ElMessage.error('DNS验证失败，请检查DNS记录是否正确添加')
  } finally {
    verifying.value = false
  }
}

// 提交申请
const submitting = ref(false)
const submitApply = async () => {
  submitting.value = true
  
  try {
    await applyCertificate({
      certType: applyForm.certType,
      brand: applyForm.brand,
      domains: applyForm.domains,
      dnsProvider: applyForm.dnsProvider,
      dnsAccessKeyId: applyForm.dnsAccessKeyId,
      dnsAccessKeySecret: applyForm.dnsAccessKeySecret,
      autoRenew: applyForm.autoRenew
    })
    
    ElMessage.success('证书申请已提交，请等待签发')
    router.push('/certificates')
  } catch (error) {
    ElMessage.error('证书申请失败')
  } finally {
    submitting.value = false
  }
}

// 复制到剪贴板
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 获取证书类型名称
const getCertTypeName = (value) => {
  const type = certTypes.find(t => t.value === value)
  return type ? type.name : '-'
}

// 获取品牌名称
const getBrandName = (value) => {
  const brand = certBrands.find(b => b.value === value)
  return brand ? brand.name : '-'
}

// 获取DNS服务商名称
const getDnsProviderName = (value) => {
  const providers = {
    aliyun: '阿里云DNS',
    tencent: '腾讯云DNSPod',
    cloudflare: 'Cloudflare'
  }
  return providers[value] || '-'
}
</script>

<style lang="scss" scoped>
.certificate-apply-page {
  .page-title {
    font-size: 18px;
    font-weight: 600;
  }

  .apply-steps {
    margin: 40px 0;
  }

  .step-content {
    max-width: 900px;
    margin: 0 auto;
  }

  .step-panel {
    padding: 20px;
  }

  .step-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #333;
  }

  .cert-type-list {
    .cert-type-item {
      position: relative;
      padding: 30px 20px;
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }

      &.active {
        border-color: #409eff;
        background: #ecf5ff;
      }

      .type-icon {
        width: 60px;
        height: 60px;
        margin: 0 auto 16px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: #fff;
      }

      .type-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
      }

      .type-desc {
        font-size: 13px;
        color: #999;
      }

      .type-radio {
        position: absolute;
        top: 12px;
        right: 12px;
      }
    }
  }

  .brand-list {
    margin-bottom: 20px;
  }

  .domain-form {
    max-width: 600px;

    .form-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #909399;
      line-height: 1.6;
    }
  }

  .dns-records {
    margin-bottom: 30px;
  }

  .verify-section {
    text-align: center;
    margin-bottom: 30px;
  }

  .apply-summary {
    margin-bottom: 30px;

    .domain-list {
      max-height: 150px;
      overflow-y: auto;
    }
  }

  .step-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 40px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
  }
}
</style>
