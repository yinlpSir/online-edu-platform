<template>
  <el-scrollbar style="height: calc(100vh - 60px)">
    <!--
      default-active 页面加载时默认激活菜单的 index
    -->
    <el-menu
      unique-opened
      :collapse="layout.collapse"
      :collapse-transition="false"
      :router="true"
      :default-active="active"
    >
      <MenuItem
        v-for="(item, index) in subMenu"
        :key="index"
        :item="item"
      />
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import MenuItem from './MenuItem.vue'
import { watch, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { layoutStoe, authStoe } from '@/stores'
import {default as subMenu} from '@/router/dynamic'

const router = useRouter()
const route = useRoute()

// 通用组件布局
const layout = layoutStoe()

// 菜单权限
// const auth = authStoe()

// 当前活动的菜单
const active = ref(route.path)
watch(route, () => {
  active.value = route.path
  // 如果小于720px则菜单变动关闭侧边栏
  if (document.body.scrollWidth <= 720) {
    layout.collapse = true
  }
})
</script>

<style scoped>
.el-menu {
  border: 0;
}
</style>
