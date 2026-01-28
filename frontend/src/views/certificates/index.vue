<template>
  <div class="certificates-page">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="关键词">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索域名、主题"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="已签发" :value="1" />
            <el-option label="即将过期" :value="2" />
            <el-option label="已过期" :value="3" />
            <el-option label="申请中" :value="0" />
            <el-option label="申请失败" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>证书列表</span>
          <el-button type="primary" @click="$router.push('/certificates/apply')">
            <el-icon><Plus /></el-icon>
            申请证书
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        :loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        
        <el-table-column prop="domainName" label="域名" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="viewDetail(row.id)">
              {{ row.domainName }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column prop="certTypeText" label="类型" width="100" align="center" />

        <el-table-column prop="brand" label="品牌" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.brand }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="daysRemaining" label="剩余天数" width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.daysRemaining <= 7 }">
              {{ row.daysRemaining || '-' }} 天
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="notAfter" label="过期时间" width="160" align="center">
          <template #default="{ row }">
            {{ row.notAfter ? formatDate(row.notAfter) : '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="statusText" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="autoRenew" label="自动续期" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.autoRenew"
              :active-value="1"
              :inactive-value="0"
              @change="handleAutoRenewChange(row)"
            />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row.id)">
              详情
            </el-button>
            <el-button 
              type="success" 
              link 
              size="small" 
              @click="handleRenew(row)"
              :disabled="row.status !== 1"
            >
              续期
            </el-button>
            <el-button type="primary" link size="small" @click="handleDeploy(row)">
              部署
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCertificateList, renewCertificate, deleteCertificate } from '@/api/certificate'
import { formatDate } from '@/utils/date'

const router = useRouter()

// 筛选表单
const filterForm = reactive({
  keyword: '',
  status: ''
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  filterForm.keyword = ''
  filterForm.status = ''
  handleSearch()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await getCertificateList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: filterForm.keyword,
      status: filterForm.status || undefined
    })
    
    const data = response.data
    tableData.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    ElMessage.error('加载证书列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/certificates/${id}`)
}

// 续期
const handleRenew = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要续期证书 "${row.domainName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await renewCertificate(row.id)
    ElMessage.success('续期成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('续期失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除证书 "${row.domainName}" 吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    
    await deleteCertificate(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 部署
const handleDeploy = (row) => {
  router.push(`/deployments?certId=${row.id}`)
}

// 自动续期切换
const handleAutoRenewChange = (row) => {
  // TODO: 调用API更新自动续期设置
  ElMessage.success('设置已更新')
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadData()
}

// 获取状态类型
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

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.certificates-page {
  .filter-card {
    margin-bottom: 20px;
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  .text-danger {
    color: #F56C6C;
    font-weight: 600;
  }
}
</style>
