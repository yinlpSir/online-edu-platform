
export const getCurrentComPath = () => {
    const path = import.meta.url
    return path.substring(0, path.indexOf("components")) + 'assets/'
}

// 获取年级范围
export const getGradeRange = (range) => {
    const list = []
    range.forEach(e => {
        switch (e) {
            case '1': list.push('一年级'); break
            case '2': list.push('二年级'); break
            case '3': list.push('三年级'); break
            case '4': list.push('四年级'); break
            case '5': list.push('五年级'); break
            case '6': list.push('六年级'); break
            case '7': list.push('初一'); break
            case '8': list.push('初二'); break
            case '9': list.push('初三'); break
            case '10': list.push('高一'); break
            case '11': list.push('高二'); break
            case '12': list.push('高三'); break
        }
    });
    return list.toString()
}

// 获取所有年级 (年级筛选)
export const getAllGrade = () => {
    return [
        {
            label: '',
            options: "一年级,二年级,三年级,四年级,五年级,六年级".split(",")
        },
        {
            label: '',
            options: "初一,初二,初三".split(",")
        },
        {
            label: '',
            options: "高一,高二,高三".split(",")
        },
    ]
}

// 所有年级 (添加课程:适用年级)
export const grades = [
    {value:'1',label:'一年级'},
    {value:'2',label:'二年级'},
    {value:'3',label:'三年级'},
    {value:'4',label:'四年级'},
    {value:'5',label:'四年级'},
    {value:'6',label:'六年级'},
    {value:'7',label:'初一'},
    {value:'8',label:'初二'},
    {value:'9',label:'初三'},
    {value:'10',label:'高一'},
    {value:'11',label:'高二'},
    {value:'12',label:'高三'},
]

// 获取所有科目
export const getAllSubject = () => {
    return '语文,数学,英语,物理,化学,政治,地理,生物,历史'.split(",")
}

export const transformGender = (val) => {
    if (val === '男') return 1
    else return 0
}

import {getAllCourse} from '@/api/api.course.js'
import useUserStore from '@/store/store.user.js'

const userStore = useUserStore()

// 搜索课程
export const searchCourse = (condition) => {
    if(condition.searchType == '2') {
        condition.teacherName = userStore.searchContent
        condition.courseName = ''
    }else {
        condition.courseName = userStore.searchContent
        condition.teacherName = ''
    }
    return getAllCourse(condition).then(response => {
        const {data} = response
        userStore.setSearchResult(data.data.records)
        // console.log(data.data.total)
        userStore.total = data.data.total
        userStore.pages = data.data.pages
    },error => {
        ElMessage.error('查询错误!')
        // error.response.data.message
        console.log(error)
    })
}