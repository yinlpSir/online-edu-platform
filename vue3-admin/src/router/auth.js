import { authStoe } from '@/stores'
import router from '@/router'
import { ElMessageBox } from 'element-plus'

// 路由挂载比store挂载早。不能直接初始化
let auth = null
const getToken = () => {
    if (auth === null) {
        auth = authStoe()
    }
    return !!auth.token
}

const getRole = () => {
    if (auth === null) {
        auth = authStoe()
    }
    if(auth.role == 1) return true // 1 is admin
    return false;
}

/**
 * 全局前置守卫（beforeEach）：在路由切换前执行，用于进行全局的权限验证和登录状态检查。
 */
router.beforeEach(async (to, from, next) => {
    
    if (to.meta && to.meta.white === true) { // 放行白名单
        next()
        return
    }
    console.log('router beforeeach')
    // 用户登陆过
    if (getToken()) {
        if (getRole()) { // 验证权限
            /**
             *  ?? 叫空值合并操作符
             *    解释：当左侧的操作数为 null 或者 undefined 时，返回其右侧操作数，否则返回左侧操作数。
             *    例如：console.log(null ?? "xx") // xx
             *          console.log(1 ?? "xx") // 1
             */
            document.title = to.meta.title ?? '在线教育平台后台管理系统'
            next()
        } else { // 提示权限不足
            try {
                await ElMessageBox.confirm(
                    `当前用户暂时无权访问，请联系管理员或尝试重新登录获取权限`,
                    '访问失败',
                    {
                        confirmButtonText: '返回',
                        cancelButtonText: '重新登录',
                        type: 'error'
                    }
                )
                next(Error('没有访问权限'))
            } catch (error) {
                next({
                    name: 'login',
                    query: {
                        redirect: to.fullPath
                    }
                })
            }
        }
        return
    }
    // 第一次登录的用户
    next({
        name: 'login'
    })
})

export default router
