import { defineStore } from "pinia";
import { reactive, ref } from "vue";

// user store
export default defineStore('user',()=>{

    const showLoginCompo=ref(false)

    const setShowLoginCompo=(value) => showLoginCompo.value=value

    const token=ref(localStorage.getItem('token'))

    const setToken=(value)=>{
        token.value=value
        if(value == null || value == undefined || value == ''){
            console.log('quit login')
            isLogged.value=false;
            localStorage.clear()
            sessionStorage.clear()
            return ;
        }
        console.log('login')
        localStorage.setItem('token', token.value)
    }

    const isLogged=ref(Boolean(sessionStorage.getItem('isLogged')))

    const setIsLogged=(value)=>{
        isLogged.value=value
        sessionStorage.setItem('isLogged',value)
    }
    
    if(token.value) {
        setIsLogged(true)
    }

    const userPartlyInfo=ref(JSON.parse(localStorage.getItem('userPartlyInfo')))
    
    const setUserPartlyInfo=(value)=>{
        userPartlyInfo.value=value
        localStorage.setItem('userPartlyInfo',JSON.stringify(value))
    }

    const userAllInfo={
        id:'11',
        headPortrait:'img src',
        username:'凌沛.',
        introduction:'签名签名签名签名签名',
        phoneNumber:'19873916844',
        gender:1,
        role:1,
        grade:'6',
    }

    // #region 课程搜索相关
    const total = ref(0)
    const pages =ref(0)

    const searchContent = ref()

    const searchCondition = reactive({
            courseName:'',
            searchType:'1',
            grade:'',
            subject:'',
            currentPage:1,
            pageSize:8,
            teacherName:'',
        }
    )
        
    const searchResult = ref([])
    
    const setSearchResult = (data) => {
        searchResult.value = []
        searchResult.value = data
    }
    //#endregion

    return {
        showLoginCompo,
        setShowLoginCompo,
        userPartlyInfo,
        token,
        setToken,
        isLogged,
        setIsLogged,
        userAllInfo,
        setUserPartlyInfo,
        searchCondition,
        searchResult,
        setSearchResult,
        searchContent,
        total,
        pages,
    }
})