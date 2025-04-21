import { createRouter, createWebHistory } from 'vue-router'

// npm install path-browserify
import path from 'path-browserify'; // path.resolve()

import {default as subMenu} from './dynamic'

/**
 * 格式 路由 的 path
 * console.log(path.resolve("/","/system")) // print : /system
 * console.log(path.resolve("/system","administrator")) // print : /system/administrator
 * @param {*} children 
 * @param {*} basePath 
 */
const formatRouteItem = (children, basePath) => {
  /**
   * resolve()
   *  作用：将传入的路径参数解析为绝对路径。它基于当前工作目录和传入的路径参数，生成一个标准化的、绝对的文件路径。
   *  注意：
   *      import {resolve} from 'path' // resolve()是Node.js中path模块提供的一个方法。它用于解析和处理文件路径。
   *      以上导入会报错，大致是因为 为了浏览器的兼容性，该 模块(path) 已外部化。不过你可通过 path-browerify 这个库，它提供了 path模块 相似的方法。
   */
  children.path = path.resolve(basePath, children.path)
  if (children.children) {
    for (const item of children.children) {
      formatRouteItem(item, children.path)
    }
  }
}

const formatRouteList = (routes) => {
  const routelist = []
  for (const item of routes) {
    formatRouteItem(item, '/')
    routelist.push(item)
  }
  return routelist
}

const routes = formatRouteList([
  {
    name: 'notFound',
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/layout/404.vue'),
    meta: { white: true, hidden: true } // white = true 是白名单路由，任何人可直接访问
  }, 
  {
    name: 'login',
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    meta: { white: true, hidden: true }
  },
  {
    path: '/',
    name: 'home',
    component:  () => import('@/views/layout/index.vue'),
    children:subMenu,
  },
])

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

export default router
