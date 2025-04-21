'use strict';

import ajax from './ajax.js';

export const getAllCourse = (data) => {
    return ajax({
        method:'GET',
        url:'/course/info/courseManagement',
        params:data,
    })
}

export const courseAuthentication = (id,isPassed) => {
    return ajax({
        method:'PUT',
        url:`/course/info/authenticate/${id}/${isPassed}`,
    })
}