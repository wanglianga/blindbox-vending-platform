<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="login-logo">
          <el-icon :size="48" color="#fff"><Box /></el-icon>
          <h1>盲盒自动售货机</h1>
          <p>智能管理平台</p>
        </div>
        <div class="login-features">
          <div class="feature-item">
            <el-icon><Monitor /></el-icon>
            <span>设备远程监控</span>
          </div>
          <div class="feature-item">
            <el-icon><DataLine /></el-icon>
            <span>数据实时统计</span>
          </div>
          <div class="feature-item">
            <el-icon><Money /></el-icon>
            <span>智能分账系统</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-subtitle">请选择角色并输入账号信息</p>
        
        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
          <el-form-item prop="role">
            <el-radio-group v-model="loginForm.role" class="role-selector">
              <el-radio-button value="OPERATOR">运营公司</el-radio-button>
              <el-radio-button value="SUPPLIER">供应商</el-radio-button>
              <el-radio-button value="MALL">商场</el-radio-button>
              <el-radio-button value="REPLENISHER">补货员</el-radio-button>
              <el-radio-button value="CUSTOMER_SERVICE">客服</el-radio-button>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入账号" size="large">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password>
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form>
        
        <div class="login-tips">
          <p>测试账号：admin / supplier / mall / replenisher / cs</p>
          <p>密码：123456</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  role: 'OPERATOR'
})

const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function handleLogin() {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await login(loginForm.username, loginForm.password)
        if (result.userInfo.role !== loginForm.role) {
          ElMessage.error('该账号不属于当前选择的角色')
          loading.value = false
          return
        }
        userStore.setToken(result.token)
        userStore.setUserInfo(result.userInfo)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 900px;
  height: 560px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  display: flex;
  overflow: hidden;
}

.login-left {
  width: 400px;
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.login-logo {
  color: #fff;
  
  h1 {
    font-size: 28px;
    margin: 16px 0 8px;
    font-weight: 600;
  }
  
  p {
    font-size: 16px;
    opacity: 0.9;
  }
}

.login-features {
  .feature-item {
    display: flex;
    align-items: center;
    color: #fff;
    font-size: 15px;
    margin-bottom: 20px;
    
    .el-icon {
      font-size: 20px;
      margin-right: 12px;
    }
  }
}

.login-right {
  flex: 1;
  padding: 60px 50px;
}

.login-title {
  font-size: 26px;
  color: #303133;
  margin-bottom: 8px;
}

.login-subtitle {
  color: #909399;
  margin-bottom: 40px;
  font-size: 14px;
}

.login-form {
  .role-selector {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    gap: 0;
  }
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
}

.login-tips {
  margin-top: 30px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  
  p {
    color: #909399;
    font-size: 12px;
    line-height: 1.8;
    margin: 0;
  }
}
</style>
