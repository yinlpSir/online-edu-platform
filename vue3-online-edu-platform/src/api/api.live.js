import ajax from './ajax';

/**
 * 开启直播
 * @param {*} lessonId 
 * @returns 
 */
export const startLive = (lessonId) => {
    return ajax({
        method:'GET',
        url:`/live/startLive/${lessonId}`,
    })
}