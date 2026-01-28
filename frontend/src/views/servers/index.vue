<template>
  <div class="servers-page">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="服务器名称">
          <el-input v-model="filterForm.keyword" placeholder="搜索服务器" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filterForm.type" placeholder="全部类型" clearable>
            <el-option label="Nginx" :value="1" />
            <el-option label="Apache" :value="2" />
            <el-option label="IIS" :value="3" />
            <el-option label="群晖" :value="4" />
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
          <span>服务器列表</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>添加服务器
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" :loading="loading" style="width: 100%">
        <el-table-column prop="name" label="服务器名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ getServerTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="host" label="主机地址" width="180" />
        <el-table-column prop="port" label="端口" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastHeartbeat" label="最后心跳" width="160" align="center">
          <template #default="{ row }">{{ row.lastHeartbeat ? formatDate(row.lastHeartbeat) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="添加时间" width="160" align="center">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleTest(row)">测试连接</el-button>
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

    <!-- 添加/编辑服务器对话框 -->
    <el-dialog v-model="showAddDialog" :title="isEdit ? '编辑服务器' : '添加服务器'" width="500px">
      <el-form :model="serverForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="服务器名称" prop="name">
          <el-input v-model="serverForm.name" placeholder="请输入服务器名称" />
        </el-form-item>
        <el-form-item label="服务器类型" prop="type">
          <el-select v-model="serverForm.type" placeholder="选择服务器类型" style="width: 100%;">
            <el-option label="Nginx" :value="1" />
            <el-option label="Apache" :value="2" />
            <el-option label="IIS" :value="3" />
            <el-option label="群晖" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机地址" prop="host">
          <el-input v-model="serverForm.host" placeholder="如：192.168.1.1 或 domain.com" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="serverForm.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="serverForm.username" placeholder="SSH用户名" />
        </el-form-item>
        <el-form-item label="认证方式" prop="authType">
          <el-radio-group v-model="serverForm.authType">
            <el-radio :label="1">密码</el-radio>
            <el-radio :label="2">SSH密钥</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="serverForm.authType === 1" label="密码" prop="password">
          <el-input v-model="serverForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item v-if="serverForm.authType === 2" label="私钥" prop="privateKey">
          <el-input v-model="serverForm.privateKey" type="textarea" :rows="4" placeholder="粘贴SSH私钥内容" />
        </el-form-item>
        <el-form-item label="证书路径" prop="certPath">
          <el-input v-model="serverForm.certPath" placeholder="/etc/ssl/certs/" />
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

const filterForm = reactive({ keyword: '', type: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const serverForm = reactive({ id: null, name: '', type: 1, host: '', port: 22, username: '', authType: 1, password: '', privateKey: '', certPath: '' })
const formRef = ref(null)
const formRules = { name: [{ required: true, message: '请输入服务器名称', trigger: 'blur' }], type: [{ required: true, message: '请选择服务器类型', trigger: 'change' }], host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }], username: [{ required: true, message: '请输入用户名', trigger: 'blur' }] }

const mockServers = [
  { id: 1, name: '生产服务器', type: 1, host: '192.168.1.100', port: 22, username: 'root', status: 1, lastHeartbeat: new Date(), createTime: new Date() },
  { id: 2, name: '测试服务器', type: 1, host: '192.168.1.101', port: 22, username: 'ubuntu', status: 0, createTime: new Date() },
]

const loadData = async () => {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    tableData.value = mockServers
    pagination.total = mockServers.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { Object.assign(filterForm, { keyword: '', type: '' }); handleSearch() }
const handlePageChange = (page) => { pagination.pageNum = page; loadData() }
const getServerTypeName = (type) => ({ 1: 'Nginx', 2: 'Apache', 3: 'IIS', 4: '群晖' }[type] || '其他')

const handleTest = async (row) => { ElMessage.success('连接测试成功') }
const handleEdit = (row) => { Object.assign(serverForm, row); isEdit.value = true; showAddDialog.value = true }
const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除服务器 ${row.name} 吗？`, '提示', { type: 'warning' })
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
.servers-page {
  .filter-card { margin-bottom: 20px; }
  .table-card {
    .card-header { display: flex; justify-content: space-between; align-items: center; }
  }
  .pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
}
</style>
