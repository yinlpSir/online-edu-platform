'use strict';

import ajax from './ajax.js';

export const login = (data) => {
    return ajax({
        method:'POST',
        url:'/user/user/login_by_password',
        data:data,
    })
}

export const delUser = (id) => {
    return ajax({
        method:'DELETE',
        url:`/user/user/${id}`,
    })
}

export const logout = () => {
    return ajax({
        method:'GET',
        url:'/user/user/logout',
    })
}

export const teacherAuthentication = (id,isAuthenticated) => {
    return ajax({
        method:'PUT',
        url:`/user/teacher/authenticate/${id}/${isAuthenticated}`,
    })
}

export const getStudent = (data) => {
    return ajax({
        method:'GET',
        url:'/user/student/all',
        params:data,
    })
}

export const getTeacher = (data) => {
    return ajax({
        method:'GET',
        url:'/user/teacher/all',
        params:data,
    })
}