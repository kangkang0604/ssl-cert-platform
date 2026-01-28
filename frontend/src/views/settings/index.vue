<template>
  <div class="settings-page">
    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :span="6">
        <el-card class="menu-card">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人资料</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
            <el-menu-item index="notifications">
              <el-icon><Bell /></el-icon>
              <span>消息通知</span>
            </el-menu-item>
            <el-menu-item index="api">
              <el-icon><Key /></el-icon>
              <span>API密钥</span>
            </el-menu-item>
            <el-menu-item index="about">
              <el-icon><InfoFilled /></el-icon>
              <span>关于系统</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="18">
        <!-- 个人资料 -->
        <el-card v-if="activeMenu === 'profile'" class="content-card">
          <template #header>
            <span>个人资料</span>
          </template>
          <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="100px" style="max-width: 500px;">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>
            <el-form-item label="头像">
              <el-upload class="avatar-uploader" action="#" :show-file-list="false" :auto-upload="false">
                <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 安全设置 -->
        <el-card v-if="activeMenu === 'security'" class="content-card">
          <template #header>
            <span>安全设置</span>
          </template>
          <div class="security-section">
            <div class="security-item">
              <div class="security-info">
                <h4>修改密码</h4>
                <p>定期修改密码可以保护账户安全</p>
              </div>
              <el-button @click="showPasswordDialog = true">修改</el-button>
            </div>
            <el-divider />
            <div class="security-item">
              <div class="security-info">
                <h4>两步验证</h4>
                <p>开启后需要输入验证码才能登录</p>
              </div>
              <el-switch v-model="twoFactorEnabled" />
            </div>
            <el-divider />
            <div class="security-item">
              <div class="security-info">
                <h4>登录设备管理</h4>
                <p>查看和管理当前登录的设备</p>
              </div>
              <el-button>查看</el-button>
            </div>
          </div>
        </el-card>

        <!-- 消息通知 -->
        <el-card v-if="activeMenu === 'notifications'" class="content-card">
          <template #header>
            <span>消息通知</span>
          </template>
          <div class="notification-section">
            <div class="notification-item">
              <div class="notification-info">
                <h4>证书到期提醒</h4>
                <p>证书即将到期时发送提醒通知</p>
              </div>
              <el-switch v-model="notifications.certExpire" />
            </div>
            <el-divider />
            <div class="notification-item">
              <div class="notification-info">
                <h4>部署状态通知</h4>
                <p>证书部署成功或失败时发送通知</p>
              </div>
              <el-switch v-model="notifications.deployStatus" />
            </div>
            <el-divider />
            <div class="notification-item">
              <div class="notification-info">
                <h4>系统公告</h4>
                <p>接收系统更新和维护公告</p>
              </div>
              <el-switch v-model="notifications.systemAnnounce" />
            </div>
          </div>
        </el-card>

        <!-- API密钥 -->
        <el-card v-if="activeMenu === 'api'" class="content-card">
          <template #header>
            <span>API密钥</span>
          </template>
          <div class="api-section">
            <el-alert type="warning" title="API密钥是您访问API的凭证，请妥善保管，不要泄露给他人" :closable="false" style="margin-bottom: 20px;" />
            
            <div class="api-list">
              <div class="api-item" v-for="key in apiKeys" :key="key.id">
                <div class="api-info">
                  <span class="api-name">{{ key.name }}</span>
                  <code class="api-key">{{ key.key }}</code>
                  <span class="api-date">创建于 {{ formatDate(key.createTime) }}</span>
                </div>
                <el-button type="danger" link size="small" @click="deleteApiKey(key)">删除</el-button>
              </div>
            </div>
            
            <el-button type="primary" @click="showApiDialog = true" style="margin-top: 20px;">
              <el-icon><Plus /></el-icon>创建新的API密钥
            </el-button>
          </div>
        </el-card>

        <!-- 关于系统 -->
        <el-card v-if="activeMenu === 'about'" class="content-card">
          <template #header>
            <span>关于系统</span>
          </template>
          <div class="about-section">
            <div class="about-logo">
              <img src="@/assets/logo.svg" alt="Logo" style="width: 80px; height: 80px;" />
              <h3>SSL证书自动续期平台</h3>
              <p>版本 1.0.0</p>
            </div>
            <el-descriptions :column="2" border style="margin-top: 30px;">
              <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
              <el-descriptions-item label="后端框架">Spring Boot 3.2</el-descriptions-item>
              <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
              <el-descriptions-item label="运行环境">Node.js 18+ / Java 17+</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'

const activeMenu = ref('profile')
const showPasswordDialog = ref(false)
const showApiDialog = ref(false)
const twoFactorEnabled = ref(false)

const profileForm = reactive({ username: 'admin', email: 'admin@ssl-platform.com', phone: '', avatar: '' })
const profileFormRef = ref(null)
const profileRules = { email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }] }

const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const passwordFormRef = ref(null)
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }]
}

const notifications = reactive({ certExpire: true, deployStatus: true, systemAnnounce: true })

const apiKeys = ref([
  { id: 1, name: 'API密钥1', key: 'sk-xxxx-xxxx-xxxx', createTime: new Date() }
])

const handleMenuSelect = (index) => { activeMenu.value = index }
const saveProfile = async () => { if (!profileFormRef.value) return; await profileFormRef.value.validate(); ElMessage.success('保存成功') }
const changePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate()
  if (passwordForm.newPassword !== passwordForm.confirmPassword) { ElMessage.error('两次密码不一致'); return }
  ElMessage.success('密码修改成功')
  showPasswordDialog.value = false
}
const deleteApiKey = async (key) => { ElMessage.success('API密钥已删除') }

onMounted(() => { })
</script>

<style lang="scss" scoped>
.settings-page {
  .menu-card { margin-bottom: 20px; }
  .content-card { min-height: 500px; }
  .security-section, .notification-section, .api-section { max-width: 600px; }
  .security-item, .notification-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
  .security-info h4, .notification-info h4 { margin-bottom: 4px; }
  .security-info p, .notification-info p { font-size: 13px; color: #909399; margin: 0; }
  .api-item { display: flex; justify-content: space-between; align-items: center; padding: 16px; background: #f5f7fa; border-radius: 4px; margin-bottom: 12px; }
  .api-name { font-weight: 600; margin-right: 12px; }
  .api-key { background: #e4e7ed; padding: 4px 8px; border-radius: 4px; font-size: 13px; margin-right: 12px; }
  .api-date { font-size: 12px; color: #909399; }
  .about-section { text-align: center; padding: 40px 0; }
  .about-logo h3 { margin: 16px 0 8px; }
  .avatar-uploader { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; }
  .avatar-uploader:hover { border-color: #409eff; }
  .avatar { width: 100px; height: 100px; border-radius: 6px; }
  .avatar-uploader-icon { font-size: 28px; color: #8c939d; }
}
</style>
