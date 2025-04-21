<template>

    <!-- 优先处理单菜单，不存在children 则属于单菜单 -->
    <template v-if="!item.children">
      <el-menu-item
        :index="item.path"
      >
        <el-icon v-if="item.meta.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <template #title>
          <span>{{ item.meta.title }}</span>
        </template>
      </el-menu-item>
    </template>

    <!-- 菜单组，需要嵌套循环 -->
    <template v-if="item.children">
      <el-sub-menu
        :index="item.path"
      >
        <template #title>
          <el-icon v-if="item.meta.icon">
            <component :is="item.meta.icon" />
          </el-icon>
          <span>{{ item.meta.title }}</span>
        </template>
        <MenuItem
          v-for="(itemChild, index) in item.children"
          :key="index"
          :item="itemChild"
          :allow="allow"
        />
      </el-sub-menu>
    </template>

</template>
<script setup>
defineProps({
  item: { type: Object, default: () => ({}) },
  allow: { type: String, default: '' }
})

const handleOpen = (url) => {
  window.open(url, '_bank')
}
</script>
