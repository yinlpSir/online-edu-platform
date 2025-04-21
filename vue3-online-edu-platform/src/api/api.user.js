import ajax from './ajax';

export const loginByPassword = (params) => {
    return ajax({
        method:'POST',
        url:'/user/user/login_by_password',
        data:params,
    })
}

export const loginByVerificationCode = (params)=>{
    return ajax({
        method:'POST',
        url:'/user/user/login_by_verification_code',
        data:params,
    })
}

export const studentRegister = (params) => {
    return ajax({
        method:'POST',
        url:'/user/student/register',
        params:params,
    })
}

export const teacherRegister = (params) => {
    return ajax({
        method:'POST',
        url:'/user/teacher/register',
        data:params,
        timeout:30000,
        headers:{
            "Content-Type":'multipart/form-data'
        }
    })
}

export const getVerificationCode = (params) => {
    return ajax({
        method:'GET',
        url:'/user/user/verification_code',
        params:params,
    })
}

export const updateStudent = (params) => {
    return ajax({
        method:'PUT',
        url:'/user/student',
        data:params,
        timeout:300000,
        headers:{
            "Content-Type":'multipart/form-data'
        }
    })
}

export const updateTeacher = (params) => {
    return ajax({
        method:'PUT',
        url:'/user/teacher',
        data:params,
    })
}

export const getCurrentStudent = (params) => {
    return ajax({
        method:'GET',
        url:'/user/student/currentStudent',
        data:params,
    })
}

export const getTeacherDetail = (params) => {
    return ajax({
        method:'GET',
        url:'/user/teacher/{id}',
        data:params,
    })
}

export const logout = () => {
    return ajax({
        method:'GET',
        url:'/user/user/logout',
    })
}