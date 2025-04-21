'use strict';

export const conversionGrade = (grade) => {
    let realGrade;
    switch (grade) {
        case '1': realGrade = '一年级'; break;
        case '2': realGrade = '二年级'; break;
        case '3': realGrade = '三年级'; break;
        case '4': realGrade = '四年级'; break;
        case '5': realGrade = '五年级'; break;
        case '6': realGrade = '六年级'; break;
        case '7': realGrade = '初一'; break;
        case '8': realGrade = '初二'; break;
        case '9': realGrade = '初三'; break;
        case '10': realGrade = '高一'; break;
        case '11': realGrade = '高二'; break;
        case '12': realGrade = '高三'; break;
    }
    return realGrade;
}