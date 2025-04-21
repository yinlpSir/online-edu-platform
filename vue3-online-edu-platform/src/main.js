
import { createApp, toRaw } from 'vue'

// import "video.js/dist/video-js.min.css";
// import "video.js/dist/video-min.js";

import App from './App.vue'

import {createPinia} from 'pinia'

import router from './router'

import useUserStore from "./store/store.user"

const app=createApp(App)

app.use(createPinia())// use pinia
app.use(router) // use router

app.config.productionTip = false // close production tip

app.mount('#app')
