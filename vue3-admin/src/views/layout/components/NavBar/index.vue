<template>
  <el-main class="ea-nav">
    <div
      class="ea-collapse"
      @click="handleCollapse"
    >
      <el-icon v-if="layout.collapse">
        <Expand />
      </el-icon>
      <el-icon v-else>
        <Fold />
      </el-icon>
    </div>

    <el-breadcrumb
      class="ea-breadcrumb"
      separator-icon="ArrowRight"
    >
      <el-breadcrumb-item :to="{ path: '/' }">
        <strong>首页</strong>
      </el-breadcrumb-item>
      <template
        v-for="(item, index) in breadcrumb"
        :key="index"
      >
        <el-breadcrumb-item v-if="item.meta.title">
          {{ item.meta.title }}
        </el-breadcrumb-item>
      </template>
    </el-breadcrumb>
    <div style="flex:1" />
    <!-- 夜晚模式 -->
    <div
      class="ea-icon"
    >
      <el-switch
        v-model="drakMode"
        inline-prompt
        active-action-icon="Moon"
        inactive-action-icon="Sunny"
      />
    </div>
    <el-dropdown class="ea-dropdown">
      <div style="cursor: pointer;outline: none;">
        <strong class="ea-dropdown-name">管理员 </strong>
        <el-icon><arrow-down /></el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            icon="reading-lamp"
            @click="handleLoginOut"
          >
            登出
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-main>
</template>

<script setup>
import { authStoe, layoutStoe } from '@/stores'
import { useRouter, useRoute } from 'vue-router'
import { watch, ref } from 'vue'
import {logout} from '@/api/api.user.js'
import { ElMessage } from 'element-plus'

const layout = layoutStoe()
const router = useRouter()
const route = useRoute()
const auth = authStoe()

const breadcrumb = ref(route.matched)

watch(route, () => {
  breadcrumb.value = route.matched
})

const handleLoginOut = () => {
  logout().then(response => {
    ElMessage.success('成功登出!')
    auth.token = null
    auth.role = null
    router.push({ name: 'login' })
  },error => ElMessage.error('登出出错,请稍后再试!'))
}

const handleCollapse = () => {
  layout.collapse = !layout.collapse
}

const drakMode = ref(false)
watch(drakMode, () => {
  if (drakMode.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
})

</script>

<style lang="scss" scoped>

@media screen and (max-width: 720px) {
  .ea-icon {
    display: none;
  }

  .ea-dropdown {
    margin-left: auto;
  }

  .ea-dropdown-name {
    display: none;
  }
}

.ea-nav {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0;

  .ea-collapse {
    font-size: 28px;
    line-height: 0;
  }

  .ea-icon {
    margin-left: auto;
    font-size: 20px;
    line-height: 0;
    margin-right: 20px;
  }

  .ea-breadcrumb {
    margin-left: 15px;
  }

}
</style>
