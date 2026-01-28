<template>
  <div class="monitors-page">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="监控域名">
          <el-input v-model="filterForm.keyword" placeholder="搜索域名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="已启用" :value="1" />
            <el-option label="已停用" :value="0" />
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
          <span>监控列表</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>添加监控
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" :loading="loading" style="width: 100%">
        <el-table-column prop="domain" label="监控域名" min-width="180" />
        <el-table-column prop="checkInterval" label="检查间隔" width="120" align="center">
          <template #default="{ row }">{{ formatInterval(row.checkInterval) }}</template>
        </el-table-column>
        <el-table-column prop="warningDays" label="预警天数" width="100" align="center">
          <template #default="{ row }">{{ row.warningDays }}天</template>
        </el-table-column>
        <el-table-column prop="alertChannels" label="告警渠道" width="200">
          <template #default="{ row }">
            <el-tag v-for="channel in parseChannels(row.alertChannels)" :key="channel" size="small" style="margin-right: 4px;">
              {{ getChannelName(channel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastStatus" label="最后状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getLastStatusType(row.lastStatus)" size="small">
              {{ getLastStatusText(row.lastStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastCheckTime" label="最后检查" width="160" align="center">
          <template #default="{ row }">{{ row.lastCheckTime ? formatDate(row.lastCheckTime) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="监控状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleCheckNow(row)">立即检查</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑监控对话框 -->
    <el-dialog v-model="showAddDialog" :title="isEdit ? '编辑监控' : '添加监控'" width="500px">
      <el-form :model="monitorForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="监控域名" prop="domain">
          <el-input v-model="monitorForm.domain" placeholder="example.com" />
        </el-form-item>
        <el-form-item label="检查间隔" prop="checkInterval">
          <el-select v-model="monitorForm.checkInterval" style="width: 100%;">
            <el-option label="每30分钟" :value="1800" />
            <el-option label="每1小时" :value="3600" />
            <el-option label="每2小时" :value="7200" />
            <el-option label="每6小时" :value="21600" />
            <el-option label="每12小时" :value="43200" />
            <el-option label="每天" :value="86400" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警天数" prop="warningDays">
          <el-input-number v-model="monitorForm.warningDays" :min="1" :max="90" />
          <span style="margin-left: 8px; color: #909399;">天</span>
        </el-form-item>
        <el-form-item label="告警渠道" prop="alertChannels">
          <el-checkbox-group v-model="monitorForm.alertChannels">
            <el-checkbox label="email">邮件</el-checkbox>
            <el-checkbox label="wechat">微信</el-checkbox>
            <el-checkbox label="sms">短信</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="monitorForm.alertChannels.includes('email')" label="告警邮箱" prop="alertEmail">
          <el-input v-model="monitorForm.alertEmail" placeholder="请输入告警邮箱" />
        </el-form-item>
        <el-form-item v-if="monitorForm.alertChannels.includes('wechat')" label="微信号" prop="alertWechat">
          <el-input v-model="monitorForm.alertWechat" placeholder="请输入微信号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const tableData = ref([])
const showAddDialog = ref(false)
const isEdit = ref(false)

const filterForm = reactive({ keyword: '', status: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const monitorForm = reactive({ id: null, domain: '', checkInterval: 3600, warningDays: 30, alertChannels: [], alertEmail: '', alertWechat: '' })
const formRef = ref(null)
const formRules = { domain: [{ required: true, message: '请输入监控域名', trigger: 'blur' }], alertChannels: [{ type: 'array', required: true, message: '请选择告警渠道', trigger: 'change' }] }

const mockMonitors = [
  { id: 1, domain: 'example.com', checkInterval: 3600, warningDays: 30, alertChannels: 'email,wechat', lastStatus: 1, lastCheckTime: new Date(), status: 1 },
  { id: 2, domain: '*.test.com', checkInterval: 7200, warningDays: 15, alertChannels: 'email', lastStatus: 2, lastCheckTime: new Date(), status: 1 },
]

const loadData = async () => {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    tableData.value = mockMonitors
    pagination.total = mockMonitors.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(filterForm, { keyword: '', status: '' }); handleSearch() }
const handlePageChange = (page) => { pagination.pageNum = page; loadData() }
const formatInterval = (seconds) => { if (seconds < 3600) return `${seconds / 60}分钟`; if (seconds < 86400) return `${seconds / 3600}小时`; return `${seconds / 86400}天` }
const parseChannels = (channels) => (channels || '').split(',').filter(c => c)
const getChannelName = (channel) => ({ email: '邮件', wechat: '微信', sms: '短信' }[channel] || channel)
const getLastStatusType = (status) => ({ 1: 'success', 2: 'warning', 3: 'danger' }[status] || 'info')
const getLastStatusText = (status) => ({ 1: '正常', 2: '警告', 3: '危险' }[status] || '未知')

const handleStatusChange = (row) => { ElMessage.success(`监控已${row.status === 1 ? '启用' : '停用'}`) }
const handleCheckNow = (row) => { ElMessage.success('检查任务已提交') }
const handleEdit = (row) => { Object.assign(monitorForm, row); monitorForm.alertChannels = parseChannels(row.alertChannels); isEdit.value = true; showAddDialog.value = true }
const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除监控 ${row.domain} 吗？`, '提示', { type: 'warning' })
  ElMessage.success('删除成功')
  loadData()
}
const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
  showAddDialog.value = false
  loadData()
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.monitors-page {
  .filter-card { margin-bottom: 20px; }
  .table-card {
    .card-header { display: flex; justify-content: space-between; align-items: center; }
  }
  .pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
}
</style>
