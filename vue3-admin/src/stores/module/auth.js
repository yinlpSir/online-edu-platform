// @ts-check
import { defineStore } from 'pinia'

export default defineStore('auth', {
    state: () => {
        return {
            token: null, // 用户token数据
            role: null // 用户的role
        }
    },
    persist: true // 配置持久化
})
