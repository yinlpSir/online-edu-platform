import ajax from './ajax';

export const getAllBoughtCourse = (params) => {
    return ajax({
        method:'GET',
        url:'/course/info/allBoughtCourse',
        params:params
    })
}

export const getAllCourse = (params) => {
    return ajax({
        method:'GET',
        url:'/course/info/all',
        params:params,
    })
}

/**
 *  获取轮播图
 * @returns 
 */
export const getCarousels = () => {
    return ajax({
        method:'GET',
        url:'/course/info/banner'
    })
}

/**
 * 添加课程
 * @param {} params 
 * @returns 
 */
export const addCourse = async (params) => {
    return await ajax({
        method:'POST',
        url:'/course/info',
        data:params,
        timeout:30000,
        headers:{
            "Content-Type":'multipart/form-data',
        }
    })
}
/**
 * 添加 课程章节
 * @param {} params 
 * @returns 
 */
export const addClass = async (params) => {
    return await ajax({
        method:'POST',
        url:'/course/class',
        data:params,
        timeout:60000,
        headers:{
            "Content-Type":'multipart/form-data',
        }
    })
}

/**
 *  添加课程资料
 * @param {*} data 
 * @returns 
 */
export const addPlastic = async (data)=>{
    return await ajax({
        method:'POST',
        url:`/course/plastic`,
        data:data,
        timeout:60000,
        headers:{
            "Content-Type":'multipart/form-data',
        }
    })
}

export const delPlastic = async (id)=>{
    return await ajax({
        method:'DELETE',
        timeout:60000,
        url:`/course/plastic/${id}`,
    })
}

export const getCourseDetail = (courseId) => {
    return ajax({
        method:'GET',
        url:`/course/info/detail/${courseId}`,
    })
}

export const buyCourse = (courseId) => {
    return ajax({
        method:'POST',
        url:`/course/info/buyCourse/${courseId}`,
    })
}

/**
 * 设置课的视频的观看进度
 * @param {*} classId 
 * @param {*} classProcess 
 * @returns 
 */
export const setVideoProcess = (classId,classProcess) => {
    return ajax({
        method:'PUT',
        url:`/course/class/saveClassProcess/${classId}/${classProcess}`,
    })
}

export const delCourse = (courseId) => {
    return ajax({
        method:'DELETE',
        url:`/course/info/${courseId}`,
    })
}

/**
 *  获取所有的课程
 * @param {*} data 
 * @returns 
 */
export const getTeacherAllCourse = (data) => {
    return ajax({
        method:'GET',
        url:'/course/info/courseManagement',
        params:data,
    })
}

/**
 * 删除课程章节
 * @param {*} classId 
 * @returns 
 */
export const delClass = (classId) => {
    return ajax({
        method:'DELETE',
        url:`/course/class/${classId}`,
    })
}