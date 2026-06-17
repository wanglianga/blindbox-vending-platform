<template>
  <el-container class="main-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon :size="28" color="#fff"><Box /></el-icon>
        <span v-if="!isCollapse" class="logo-text">盲盒管理平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#1f2d3d"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
        class="menu"
      >
        <template v-for="item in menuList" :key="item.path">
          <el-menu-item :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo.avatar">
                {{ userStore.userInfo.name?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo.name }}</span>
              <el-tag size="small" type="info">{{ roleLabel }}</el-tag>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { roleMap } from '@/mock'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const roleLabel = computed(() => roleMap[userStore.userInfo.role] || '')

const menuList = computed(() => {
  const role = userStore.userInfo.role
  const routes = router.options.routes
  const mainRoute = routes.find(r => r.path === '/')
  if (!mainRoute) return []
  
  return mainRoute.children
    .filter(child => child.meta?.roles?.includes(role) && child.path !== 'dashboard')
    .map(child => ({
      path: '/' + child.path,
      title: child.meta.title,
      icon: child.meta.icon
    }))
})

const breadcrumbs = computed(() => {
  const crumbs = [{ path: '/dashboard', title: '首页' }]
  if (route.path !== '/dashboard') {
    crumbs.push({ path: route.path, title: route.meta.title })
  }
  return crumbs
})

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.main-container {
  height: 100%;
}

.sidebar {
  background: #1f2d3d;
  transition: width 0.3s;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: #fff;
    background: rgba(0, 0, 0, 0.2);
    
    .logo-text {
      font-size: 16px;
      font-weight: 600;
    }
  }
  
  .menu {
    border-right: none;
  }
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px !important;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: #606266;
      
      &:hover {
        color: #409eff;
      }
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      
      .user-name {
        font-size: 14px;
        color: #303133;
      }
    }
  }
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
