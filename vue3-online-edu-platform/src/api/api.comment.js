import ajax from './ajax';

export const publishComment = (data) => {
    return ajax({
        method:'POST',
        url:'/course/info/comment',
        data:data
    })
}

export const delComment = (commentId) => {
    return ajax({
        method:'DELETE',
        url:`/course/info/comment/${commentId}`,
    })
}