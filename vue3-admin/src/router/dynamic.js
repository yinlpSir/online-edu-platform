export default [
    {
        path: '',
        name: 'studentManagement',
        component: () => import('@/views/student/index.vue'),
        meta: {
            title: '学生管理',
            icon: 'User'
        }
    },
    {
        path: 'teacher',
        name: 'teacherManagement',
        component: () => import('@/views/teacher/index.vue'),
        meta: {
            title: '教师管理',
            icon: 'User'
        }
    },
    {
        path: 'course',
        name: 'courseManagement',
        component: () => import('@/views/course/index.vue'),
        meta: {
            title: '课程审核',
            icon: 'User'
        }
    },
]