import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

//element-plus 按需导入
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // server:{//配置跨域代理服务器，为解决跨域问题
  //   proxy:{//配置 代理 转发的规则
  //     '/api':{// api 表示拦截以 /api开头的请求路径
  //       target:'http://192.168.124.40:30000/',//配置 此规则 转发的地址。即跨域的域名，不需要写路径
  //       changeOrigin:true,//是否开启跨域
  //       rewrite:(path)=>path.replace(/^\/api/,''),
  //     }
  //   },
  // }
})
