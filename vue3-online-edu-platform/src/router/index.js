import { createRouter, createWebHistory } from "vue-router";
import useCourseStore from "@/store/store.course"
import path from 'path-browserify';

/**
 * 格式化 路由 的 path
 * @param {*} children 
 * @param {*} basePath 
 */
const formatPathOfRouteItem = (children, basePath) => {
    children.path = path.resolve(basePath, children.path)
    if (children.children) {
        for (const item of children.children) {
            formatPathOfRouteItem(item, children.path)
        }
    }
}

const formatRouteList = (routes) => {
    const routelist = []
    for (const item of routes) {
        formatPathOfRouteItem(item, '/')
        routelist.push(item)
    }
    return routelist
}

const routes=formatRouteList([
    {
        path:'/',
        component:()=>import('../components/HomePage.vue'),
        name:'HomePage',
        alias:'/index',
        children:[
            {
                path:'', // 这将确保导航到 '/' 时始终显示嵌套路由
                components:{
                    default:()=>import('../components/OepCarousel.vue'),
                    main:()=>import('../components/OepMain.vue'),
                },
            },
            {
                path:'course/video',
                name:'CourseVideo',
                component:()=>import('@/components/courses/OepWatchVideo.vue'),
            },
            {
                path:'course/detail',
                name:'CourseDetail',
                component:()=>import('../components/courses/OepCourseDetail.vue'),
            },
            {
                path:'course/buy',
                name:'CourseBuy',
                component:()=>import('../components/courses/OepBuyCourse.vue'),
            },
            {
                path:'course/purchased',
                name:'CoursePurchased',
                component:()=>import('../components/courses/OepCoursePurchased.vue'),
            },
            {
                path:'tips',
                name:'Tip',
                component:()=>import('@/components/OepTip.vue'),
            },
            {
                path:'user/center',
                name:'UserCenter',
                component:()=>import('../components/user/OepUserCenter.vue'),
                children:[
                    {
                        path:'',
                        alias:'course',
                        component:()=>import('@/components/user/OepUserCourse.vue')
                    },
                    {
                        path:'info',
                        name:'UserInfo',
                        component:()=>import('@/components/user/OepUserInfo.vue')
                    },
                    {
                        path:'message',
                        name:'UserMessage',
                        component:()=>import('@/components/user/OepUserMessage.vue')
                    },
                    {
                        path:'account',
                        name:'Account',
                        component:()=>import('@/components/user/OepUserAccount.vue')
                    },
                ]
            },
            {
                path:'user/register',
                name:'Register',
                component:()=>import('../components/user/OepRegister.vue'),
                children:[
                    {
                        path:'',
                        component:()=>import('../components/user/OepStudentRegister.vue'),
                    },
                    {
                        path:'teacher',
                        name:'TeacherRegister',
                        component:()=>import('../components/user/OepTeacherRegister.vue'),
                    },
                ],
            },
            {
                path:'course/search',
                name:'CourseSearch',
                component:()=>import('../components/user/OepCourseSearchDisplay.vue'),
            },
            {
                path:'teacher',
                name:'TeacherHomePage',
                component:()=>import('../components/teacher/OepTeacherHomePage.vue'),
                children:[
                    {
                        path:'',
                        name:'AllCourse',
                        alias:'allCourse',
                        component:()=>import('../components/teacher/OepTeacherAllCourse.vue')
                    },
                    {
                        path:'draftBox',
                        name:'CourseDraftBox',
                        component:()=>import('../components/teacher/OepTeacherCourseDraftBox.vue')
                    },
                    {
                        path:'publishCourse',
                        name:'PublishCourse',
                        component:()=>import('../components/teacher/OepPublishCourse.vue'),
                        children:[
                            {
                                path:'',
                                name:'CourseForm',
                                component:()=>import('../components/teacher/publish.course/OepCourseForm.vue')
                            },
                            {
                                path:'addCourse',
                                name:'AddCourse',
                                component:()=>import('../components/teacher/publish.course/OepAddCourse.vue'),
                                children:[
                                    {
                                        path:'',
                                        alias:'basicInfo',
                                        component:()=>import('../components/teacher/publish.course/OepAddCourseBasicInfo.vue'),
                                    },
                                    {
                                        path:'',
                                        alias:'outline',
                                        component:()=>import('../components/teacher/publish.course/OepAddCourseOutline.vue'),
                                    },
                                    {
                                        path:'',
                                        alias:'description',
                                        component:()=>import('../components/teacher/publish.course/OepAddCourseDescription.vue'),
                                    },
                                ]
                            },
                        ]
                    },
                    {
                        path:'account',
                        name:'Account',
                        component:()=>import('../components/teacher/OepTeacherAccount.vue')
                    },
                    {
                        path:'info',
                        name:'Info',
                        component:()=>import('../components/teacher/OepTeacherInfo.vue')
                    },
                ]
            },
        ]
    },
])

// router forward guard

const router = createRouter({
    history:createWebHistory(),
    routes,
})

const notLoginUrls = [
    '/',
    '/index',
    '/course/detail',
    '/user/register',
    '/course/search',
    '/user/register/teacher',
    '/tips',
]

import useUserStore from "@/store/store.user"

// 全局路由前置守卫
router.beforeEach((to,from,next)=>{

    const courseStore = useCourseStore()
    const userStore = useUserStore()

    let notLogin=notLoginUrls.some(e => {
        return e.includes(to.path)
    })
    if(!notLogin && (userStore.token == null || userStore.token == undefined || userStore.token == "")) {
        ElMessage.warning('您还未登录!')
        router.push("/")
        return false;
    }

    /**
     * to 即将跳转的目标路由对象
     * from 当前导航正要离开的路由对象
     * next 用于控制导航行为的回调函数。用于控制导航的继续或中断
     *      中断 next(false)
     *      重定向到其他路由 next('/other-route')
     *      继续导航 next(true)
     */

    if(from.path === '/teacher/publishCourse/addCourse' && to.path === '/teacher/publishCourse/description') {
        router.push('/teacher/publishCourse/addCourse/description')
    }

    if(to.path === '/teacher/publishCourse' && courseStore.courseForm != undefined) router.push('/teacher/publishCourse/addCourse')

    /**
     *  /teacher/publishCourse/addCourse 跳转到这个路径得先选择 课程形式。担心手动跳转到这个路径,所以判断一下
     */
    const addCoursePaths = [
        '/teacher/publishCourse/addCourse',
        '/teacher/publishCourse/addCourse/basicInfo',
        '/teacher/publishCourse/addCourse/description',
        '/teacher/publishCourse/addCourse/outline',
    ]
    if(addCoursePaths.includes(to.path)){
        if(courseStore.courseForm != undefined) next(true)
        else router.push('/teacher/publishCourse')
    }


    next(true)

    
    // to is object
    // if(to.path != '/login' && store.username == null) return '/login';
    
    //返回 false 以取消导航
    // return false;
})

export default router