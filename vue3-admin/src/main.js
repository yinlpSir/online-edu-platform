
import '@/style/index.scss'

// video.js
import "video.js/dist/video-js.css"
import "video.js/dist/video.min.js"

import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

import App from './App.vue'
import router from './router/auth'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(pinia)
app.use(router)

app.mount('#app')
