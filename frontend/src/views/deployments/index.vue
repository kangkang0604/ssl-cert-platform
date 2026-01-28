<template>
  <div class="deployments-page">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="域名">
          <el-input v-model="filterForm.keyword" placeholder="搜索域名" clearable />
        </el-form-item>
        <el-form-item label="部署状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="待部署" :value="0" />
            <el-option label="部署中" :value="1" />
            <el-option label="部署成功" :value="2" />
            <el-option label="部署失败" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>部署记录</span>
          <el-button type="primary" @click="showDeployDialog = true">
            <el-icon><Plus /></el-icon>新建部署
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" :loading="loading" style="width: 100%">
        <el-table-column prop="domainName" label="域名" min-width="180" />
        <el-table-column prop="deployType" label="部署类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ getDeployTypeName(row.deployType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="部署目标" min-width="150" />
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deployTime" label="部署时间" width="160" align="center">
          <template #default="{ row }">{{ row.deployTime ? formatDate(row.deployTime) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleRedeploy(row)" :disabled="row.status === 1">
              重新部署
            </el-button>
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新建部署对话框 -->
    <el-dialog v-model="showDeployDialog" title="新建部署" width="600px">
      <el-form :model="deployForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="选择证书" prop="certId">
          <el-select v-model="deployForm.certId" placeholder="选择要部署的证书" style="width: 100%;">
            <el-option v-for="cert in certificateList" :key="cert.id" :label="cert.domainName" :value="cert.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部署类型" prop="deployType">
          <el-radio-group v-model="deployForm.deployType">
            <el-radio :label="1">服务器</el-radio>
            <el-radio :label="2">CDN</el-radio>
            <el-radio :label="3">COS</el-radio>
            <el-radio :label="4">LB</el-radio>
            <el-radio :label="5">WebHook</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <template v-if="deployForm.deployType === 1">
          <el-form-item label="选择服务器" prop="serverId">
            <el-select v-model="deployForm.serverId" placeholder="选择目标服务器" style="width: 100%;">
              <el-option v-for="server in serverList" :key="server.id" :label="server.name" :value="server.id" />
            </el-select>
          </el-form-item>
        </template>
        
        <template v-else-if="deployForm.deployType === 2">
          <el-form-item label="CDN域名" prop="targetName">
            <el-input v-model="deployForm.targetName" placeholder="请输入CDN加速域名" />
          </el-form-item>
        </template>
        
        <template v-else-if="deployForm.deployType === 3">
          <el-form-item label="COS Bucket" prop="targetName">
            <el-input v-model="deployForm.targetName" placeholder="请输入COS Bucket名称" />
          </el-form-item>
        </template>
        
        <template v-else-if="deployForm.deployType === 4">
          <el-form-item label="负载均衡" prop="targetName">
            <el-input v-model="deployForm.targetName" placeholder="请输入负载均衡实例ID" />
          </el-form-item>
        </template>
        
        <template v-else-if="deployForm.deployType === 5">
          <el-form-item label="Webhook URL" prop="targetName">
            <el-input v-model="deployForm.targetName" placeholder="请输入Webhook回调URL" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="showDeployDialog = false">取消</el-button>
        <el-button type="primary" @click="handleDeploy">开始部署</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const tableData = ref([])
const showDeployDialog = ref(false)

const filterForm = reactive({ keyword: '', status: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const deployForm = reactive({ certId: '', deployType: 1, serverId: '', targetName: '' })
const formRef = ref(null)
const formRules = { certId: [{ required: true, message: '请选择证书', trigger: 'change' }], deployType: [{ required: true, message: '请选择部署类型', trigger: 'change' }] }

const certificateList = ref([{ id: 1, domainName: 'example.com' }, { id: 2, domainName: '*.test.com' }])
const serverList = ref([{ id: 1, name: '生产服务器' }, { id: 2, name: '测试服务器' }])

const mockDeployments = [
  { id: 1, domainName: 'example.com', deployType: 1, targetName: '生产服务器', status: 2, deployTime: new Date(), createTime: new Date() },
  { id: 2, domainName: '*.test.com', deployType: 2, targetName: 'cdn.test.com', status: 2, deployTime: new Date(), createTime: new Date() },
]

const loadData = async () => {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    tableData.value = mockDeployments
    pagination.total = mockDeployments.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(filterForm, { keyword: '', status: '' }); handleSearch() }
const handlePageChange = (page) => { pagination.pageNum = page; loadData() }
const getDeployTypeName = (type) => ({ 1: '服务器', 2: 'CDN', 3: 'COS', 4: 'LB', 5: 'WebHook' }[type] || '其他')
const getStatusType = (status) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[status] || 'info')
const getStatusText = (status) => ({ 0: '待部署', 1: '部署中', 2: '部署成功', 3: '部署失败' }[status] || '未知')

const handleRedeploy = (row) => { ElMessage.success('重新部署中...') }
const handleView = (row) => { ElMessage.info('查看详情') }
const handleDeploy = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  ElMessage.success('部署任务已创建')
  showDeployDialog.value = false
  loadData()
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.deployments-page {
  .filter-card { margin-bottom: 20px; }
  .table-card {
    .card-header { display: flex; justify-content: space-between; align-items: center; }
  }
  .pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
}
</style>
