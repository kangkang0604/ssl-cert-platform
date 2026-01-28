<template>
  <div class="domains-page">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="域名">
          <el-input v-model="filterForm.keyword" placeholder="搜索域名" clearable />
        </el-form-item>
        <el-form-item label="验证状态">
          <el-select v-model="filterForm.verificationStatus" placeholder="全部" clearable>
            <el-option label="未验证" :value="0" />
            <el-option label="验证中" :value="1" />
            <el-option label="验证成功" :value="2" />
            <el-option label="验证失败" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>域名列表</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>添加域名
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" :loading="loading" style="width: 100%">
        <el-table-column prop="domain" label="域名" min-width="200" />
        <el-table-column prop="dnsProvider" label="DNS服务商" width="150" align="center">
          <template #default="{ row }">
            {{ getDnsProviderName(row.dnsProvider) }}
          </template>
        </el-table-column>
        <el-table-column prop="verificationStatus" label="验证状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getVerificationType(row.verificationStatus)" size="small">
              {{ getVerificationText(row.verificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resolveStatus" label="解析状态" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.resolveStatus === 1" color="#67c23a"><CircleCheck /></el-icon>
            <el-icon v-else color="#f56c6c"><CircleClose /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="resolveIp" label="解析IP" width="140" />
        <el-table-column prop="createTime" label="添加时间" width="160" align="center">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleVerify(row)">验证</el-button>
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

    <!-- 添加域名对话框 -->
    <el-dialog v-model="showAddDialog" title="添加域名" width="500px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="域名" prop="domain">
          <el-input v-model="addForm.domain" placeholder="example.com 或 *.example.com" />
        </el-form-item>
        <el-form-item label="DNS服务商" prop="dnsProvider">
          <el-select v-model="addForm.dnsProvider" placeholder="选择DNS服务商" style="width: 100%;">
            <el-option label="阿里云DNS" value="aliyun" />
            <el-option label="腾讯云DNSPod" value="tencent" />
            <el-option label="Cloudflare" value="cloudflare" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddDomain">确定</el-button>
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

const filterForm = reactive({ keyword: '', verificationStatus: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const addForm = reactive({ domain: '', dnsProvider: '' })
const addFormRef = ref(null)
const addRules = {
  domain: [{ required: true, message: '请输入域名', trigger: 'blur' }],
  dnsProvider: [{ required: true, message: '请选择DNS服务商', trigger: 'change' }]
}

// 模拟数据
const mockDomains = [
  { id: 1, domain: 'example.com', dnsProvider: 'aliyun', verificationStatus: 2, resolveStatus: 1, resolveIp: '192.168.1.1', createTime: new Date() },
  { id: 2, domain: '*.test.com', dnsProvider: 'tencent', verificationStatus: 0, resolveStatus: 1, resolveIp: '192.168.1.2', createTime: new Date() },
]

const loadData = async () => {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    tableData.value = mockDomains
    pagination.total = mockDomains.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(filterForm, { keyword: '', verificationStatus: '' }); handleSearch() }
const handlePageChange = (page) => { pagination.pageNum = page; loadData() }

const handleAddDomain = async () => {
  if (!addFormRef.value) return
  await addFormRef.value.validate()
  ElMessage.success('域名添加成功')
  showAddDialog.value = false
  loadData()
}

const handleVerify = (row) => { ElMessage.success('验证功能开发中') }
const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除域名 ${row.domain} 吗？`, '提示', { type: 'warning' })
  ElMessage.success('删除成功')
  loadData()
}

const getDnsProviderName = (value) => ({ aliyun: '阿里云DNS', tencent: '腾讯云DNSPod', cloudflare: 'Cloudflare' }[value] || '-')
const getVerificationType = (status) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[status] || 'info')
const getVerificationText = (status) => ({ 0: '未验证', 1: '验证中', 2: '验证成功', 3: '验证失败' }[status] || '未知')

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.domains-page {
  .filter-card { margin-bottom: 20px; }
  .table-card {
    .card-header { display: flex; justify-content: space-between; align-items: center; }
  }
  .pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
}
</style>
