<script setup>

    import { ref,toRaw, watch } from "vue";
    import {delCourse,getTeacherAllCourse,delClass,addClass,addPlastic,delPlastic} from "@/api/api.course"
    import {delComment} from "@/api/api.comment"
    import {startLive} from "@/api/api.live"
    import { ElMessage } from "element-plus";

    const table = ref({
        total: 0, // 总共多少条数据
        query: { // 查询参数
            pageSize: 10, // 每页显示多少条数据
            currentPage: 1, // 当前页
            courseName:"",
        },
        data: []
    })

    const loading = ref(false)

    // #region 分页相关函数
    const handleChangePage = (value) => {
        table.value.query.currentPage = value
        handleTableData()
    }
    const handleChangePageSize = (value) => {
        table.value.query.pageSize = value
        handleTableData()
    }
    // #endregion
    
    // 获得数据
    const handleTableData = async () => {
        loading.value = true
        await getTeacherAllCourse(toRaw(table.value.query)).then((response) => {
            const {data} = response
            table.value.data = data.data.records
            table.value.total = data.data.total
            loading.value = false
        },error => ElMessage.error(error.response.data.msg))
    }

    handleTableData();

    // #region 课程介绍 相关dialog 操作
    const courseDescriptionDialogVisible = ref(false)

    const courseDescription = ref(null)

    const handleShowCourseDescription = (description)=>{
        courseDescriptionDialogVisible.value = true
        courseDescription.value = description
    }
    // #endregion

    // #region 观看课程视频 相关dialog操作

    const videoDialogVisible = ref(false)

    const currentVideoPath = ref(null)

    const handleShowVideo = (videoPath)=>{
        videoDialogVisible.value = true
        
        if(videoPath == null) return ;

        currentVideoPath.value = videoPath
    }

    const handleVideoClose = ()=> { 
        currentVideoPath.value = null
    }

    // #endregion

    /**
     * 查看资料
     */
    const handleShowPlastic = (plasticPath) => {
        window.open(plasticPath)
    }

    /**
     * 删除课程
     */
    const delCourseHandle = async (courseId) => {
        await ElMessageBox.confirm('请仔细确认是否删除该课程?(这将是不可逆的)', '警告', {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            loading.value=true
            delCourse(courseId).then((response) => {
                const {data} = response
                setTimeout(()=>{
                    ElMessage.success(data.message)
                    handleTableData()
                },1000)
            },error => {
                ElMessage.error(error.response.data.message)
            })
        })
    }

    /**
     * 删除课程章节
     */
    const delLesson = async (classId) => {
        await ElMessageBox.confirm('您确认是否删除该章节?(这将是不可逆的)', '警告', {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            loading.value = true
            delClass(classId).then((response) => {
                const {data} = response
                ElMessage.success(data.message)
                setTimeout(()=>{
                    handleTableData()
                },1000)
            },error => {
                ElMessage.error(error.response.data.message)
            })
        })
    }

    // #region 添加课程章节相关

    const formRef = ref()

    const addLessonDialog = ref({
        visible: false,
        isLiveCourse:false, // 是否是直播课
        title:'添加课程',
        form: {},
        formRule: {
            className: [
                { required: true, message: '请输入章节名', trigger: 'blur' },
            ],
            startTime:[
                { required: true, type:'date',message: '请输入章节名', trigger: 'change' },
            ],
            endTime:[
                { required: true, type:'date', message: '请输入章节名', trigger: 'change' },
            ]
        },
    })

    /**
     *  添加课程章节
     * @param {*} courseId 
     */
    const addOneClass = async (courseInfo)=>{

        if(courseInfo.classList[0].startTime == null) {
            // 非直播课程章节
            addLessonDialog.value.isLiveCourse = false
            addLessonDialog.value.form = {
                courseId:courseInfo.id,
                className:'',
                number:1,
                video:null,
                classProcess:0,
            }
            addLessonDialog.value.title = '添加录播课程章节'
        }else {
            // 直播课
            addLessonDialog.value.isLiveCourse = true
            addLessonDialog.value.form = {
                courseId:courseInfo.id,
                className:'',
                number:1,
                startTime:'',
                endTime:'',
                classProcess:0,
            }
            addLessonDialog.value.title = '添加直播课程章节'
        }
        
        addLessonDialog.value.visible = true
    }

    const submitAddClass =async (formEl)=>{
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                if(addLessonDialog.value.form.video == null){
                    ElMessage.error('请上传课程视频!')
                    return ;
                }
                loading.value = true
                addClass(toRaw(addLessonDialog.value.form)).then(res => {
                    const {data} = res
                    ElMessage.success(data.message)
                    addLessonDialog.value.visible = false
                    handleTableData()
                },err => ElMessage.error(err.response.data.message))
            }
        })
        
    }

    const uploadRef = ref()

    //当超出限制时，执行的钩子
    const handleExceed = (files,uploadFiles,ref)=>{
        ElMessage.error('至多上传一个视频！')
    }

    // 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用。
    const handleUploadChange = (uploadFile,uploadFiles,ref) => {
        if(uploadFile.raw.type == 'video/mp4'){
            addLessonDialog.value.form.video = uploadFile.raw
            return 
        }
        
        ElMessage.error('仅支持mp4格式!')
        ref.handleRemove(uploadFile.raw)
        ref.clearFiles()
    }

    /**
     * 删除文件之前的钩子，参数为上传的文件和文件列表， 若返回 false 或者返回 Promise 且被 reject，则停止删除。
     * @param {*} uploadFile 一个File对象，它是被删除的文件
     */
    const handleFileRemove =(uploadFile)=>{
        addLessonDialog.value.form.video = null
    }

    //#endregion

    // #region 添加课程资料相关

    const plasticFormRef = ref()

    const uploadPlasticRef = ref()

    const addPlasticDialog = ref({
        visible:false,
        title:'添加课程资料',
        form:{},
        formRule:{
            plasticName: [
                { required: true, message: '请输入章节名', trigger: 'blur' },
            ],
        }
    })
    
    const addPlasticFun = (courseId)=>{
        addPlasticDialog.value.form = {
            plasticName:'',
            plastic:null,
            courseId:courseId,
        }
        addPlasticDialog.value.visible = true
    }

    const submitAddPlastic =async (formEl)=>{
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                if(addPlasticDialog.value.form.plastic == null){
                    ElMessage.error('请上传课程资料!')
                    return ;
                }
                loading.value = true
                addPlastic(toRaw(addPlasticDialog.value.form)).then(res => {
                    const {data} = res
                    console.log(data)
                    ElMessage.success(data.message)
                    addPlasticDialog.value.visible = false
                    handleTableData()
                },err => ElMessage.error(err.response.data.message))
            }
        })
    }

    const delPlasticFun = async (plasticId) => {
        await ElMessageBox.confirm('您确认是否删除该资料?(这将是不可逆的)', '警告', {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            loading.value = true
            delPlastic(plasticId).then((response) => {
                const {data} = response
                ElMessage.success(data.message)
                setTimeout(()=>{
                    handleTableData()
                },1000)
            },error => {
                ElMessage.error(error.response.data.message)
            })
        })
    }

    // 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用。
    const handlePlasticUploadChange = (uploadFile,uploadFiles,ref) => {
        addPlasticDialog.value.form.plastic = uploadFile.raw
    }

    //当超出限制时，执行的钩子
    const handlePlasticExceed = ()=>{
        ElMessage.error('至多上传一个资料！')
    }

    const handlePlasticRemove = ()=>{
        addPlasticDialog.value.form.plastic = null
    }

    // #endregion

    //#region 抽屉相关

    const showCommentContainer = ref(false)

    const comments = ref([])

    const showCommentContainerHandle = (coms)=>{
        showCommentContainer.value = true
        comments.value = coms
    }

    const delCommentHandle =(commentId) => {
        ElMessageBox.confirm('确定是否删除此条评论?(这将是不可逆的)', '警告', {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            loading.value = false
            delComment(commentId).then((response) => {
                const {data} = response
                ElMessage.success(data.message)
                showCommentContainer.value = false
                handleTableData()
            },error => {
                ElMessage.error(error.response.data.message)
            })
        })
    }

    //#endregion

    //#region 直播相关
    /**
     * 开始直播的处理函数
     * @param {*} lessonId 
     */
    const startLiveHandle = (lessonId)=>{
        startLive(lessonId).then(res => {
            const {data} = res
            window.open(data.data)
        },err=> ElMessage.error(err.response.data.message))
    }

    /**
     * 正在直播的处理函数
     * @param {*} liveUrl 
     */
    const livingHandle = (liveUrl)=>{
        window.open(liveUrl)
    }

    //#endregion

</script>

<template>
    <el-main class="ea-table" v-loading.fullscreen.lock="loading">
        <el-table :data="table.data" style=" width: 100%" empty-text="暂无课程">
            <el-table-column type="expand">
                <template #default="props">
                    <div m="4" style="margin-left: 20px; padding-right: 40px;">
                        <h3>课时列表:</h3>
                        <el-table :data="props.row.classList" :border="false" style="margin-bottom: 20px; margin-left: 10px;" empty-text="暂无课程">
                            <el-table-column label="课时名" prop="className" />
                            <el-table-column label="课时数" prop="number" />
                            <el-table-column label="开始时间" prop="startTime">
                                <template #default="scope">
                                        <span>{{scope.row.startTime?scope.row.startTime.replace('T',' '):' '}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column label="结束时间" prop="endTime">
                                <template #default="scope">
                                        <span>{{scope.row.endTime?scope.row.endTime.replace('T',' '):' '}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column label="视频时长" prop="lastTime" />
                            <el-table-column fixed="right" label="操作" width="100">
                                <template #default="scope">
                                    <el-button v-if="scope.row.videoPath != null" link type="primary" @click="handleShowVideo(scope.row.videoPath)">
                                        查看内容
                                    </el-button>
                                    <div v-else>
                                        <el-button v-if="scope.row.liveInfo?scope.row.liveInfo.liveStatus == 1?true:false:false" link type="primary" @click="startLiveHandle(scope.row.id)">
                                            开始直播
                                        </el-button>
                                        <el-button v-else-if="scope.row.liveInfo?scope.row.liveInfo.liveStatus == 2?true:false:false" link type="primary" @click="livingHandle(scope.row.liveInfo.liveUrl)">
                                            正在直播
                                        </el-button>
                                        <el-button v-else link disabled>直播已结束</el-button>
                                    </div>
                                    <el-button link type="warning" @click="delLesson(scope.row.id)">
                                        删除
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>

                        <h3>课程资料列表:</h3>
                        <el-table :data="props.row.classPlastic" :border="false" style="margin-bottom: 20px; margin-left: 10px;" empty-text="暂无资料">
                            <el-table-column label="资料名" prop="plasticName" />
                            <el-table-column label="大小" prop="plasticSize">
                                <template #default="scope">
                                        <span>{{((scope.row.plasticSize)/(1024*1024)).toFixed(2)}}MB</span>
                                </template>
                            </el-table-column>
                            <el-table-column fixed="right" label="操作" width="100">
                                <template #default="scope">
                                    <el-button link type="primary" @click="handleShowPlastic(scope.row.plasticPath)">
                                        查看资料
                                    </el-button>
                                    <el-button link type="warning" @click="delPlasticFun(scope.row.id)">
                                        删除
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="封面" width="220">
                <template #default="scope">
                    <div style="display: flex; align-items: center">
                        <el-image style="width: 220px; height: 120px" :src="scope.row.courseCover" :fit="fit" />
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名" />
            <el-table-column prop="subject" label="科目" />
            <el-table-column prop="number" label="课时数" />
            <el-table-column label="价格">
                <template #default="scope">
                        <span>{{scope.row.price.toFixed(2)}}</span>
                </template>
            </el-table-column>
            <el-table-column label="审核状态">
                <template #default="scope">
                    <el-tag v-if="scope.row.isPassed == '1'" type="success">已审核</el-tag>
                    <el-tag v-else-if="scope.row.isPassed == '0'" type="warning">待审核</el-tag>
                    <el-tag v-else type="danger">未通过审核</el-tag>
                </template>
            </el-table-column>
            
            <el-table-column fixed="right" label="操作" width="120">
                <template #default="scope">
                    
                    <el-button link type="default" @click="handleShowCourseDescription(scope.row.description)">
                        查看课程介绍
                    </el-button>
                    <el-button link type="default" @click="showCommentContainerHandle(scope.row.courseComment)">
                        查看评论
                    </el-button>
                    <el-button link type="primary" @click="addOneClass(scope.row)">
                        添加一节课
                    </el-button>
                    <el-button link type="primary" @click="addPlasticFun(scope.row.id)">
                        添加课程资料
                    </el-button>
                    <el-button link type="warning" @click="delCourseHandle(scope.row.id)">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <br>
        <el-pagination v-show="table.total" background layout="total, sizes, prev, pager, next" :total="table.total" :page-sizes="[10, 15, 20]"
            :default-page-size="table.query.pageSize" @current-change="handleChangePage" @size-change="handleChangePageSize" />
    </el-main>

    <!--课程介绍 dialog-->
    <el-dialog v-model="courseDescriptionDialogVisible" width="75%" title="课程介绍" center >
        <div style=" text-align: center;">
            <span v-html="courseDescription"></span>
        </div>
    </el-dialog>

    <!--观看课程内容的 dialog-->
    <el-dialog v-model="videoDialogVisible" title="课程内容" width="50%" center @close="handleVideoClose">
        <video 
            v-if="currentVideoPath != null" 
            :src="currentVideoPath" 
            controls
            height="400"
            style="width: 100%;"
        ></video>
        <el-empty v-else description="暂无内容" />
    </el-dialog>

    <!--评论(抽屉)-->
    <el-drawer
        v-model="showCommentContainer"
        direction="ltr"
        size="36%"
    >
        <template #header>
            <h3 style="color: black;">评论</h3>
        </template>
        
        <div>
            <el-empty :image-size="200" description="空空如也" v-if="comments.length == 0"/>
            <div class="comment" v-else v-for="com in comments" :key="com.username">
                <div class="headPortrait">
                    <el-image 
                        style="width: 45px; height: 45px; border-radius: 50%;" 
                        :src="com.headPortrait"
                        :fit="cover" 
                    />
                </div>
                <div class="comment-body">
                    <div style="font-size: 16px;">{{com.username}}</div>
                    <div style="display: flex; align-items: center;">
                        <div class="time">
                            {{com.createTime.replace('T',' ')}} <span style="margin-left: 6px;">|</span>
                        </div>
                        <div style="margin-left: 6px;">
                            <el-rate v-model="com.grade" disabled size="small" />
                        </div>
                    </div>
                    <div style="font-size: 16px;margin-top: 4px;">
                        {{com.content}}
                    </div>
                </div>
                <div>
                    <el-button link type="warning" size="small" @click="delCommentHandle(com.id)">删除</el-button>
                </div>
            </div>
        </div>
    </el-drawer>

    <!--添加课程章节相关-->
    <el-dialog v-model="addLessonDialog.visible" :title="addLessonDialog.title" :close-on-click-modal="false" destroy-on-close
        close-on-press-escape center width="600px">
        <el-form ref="formRef" :model="addLessonDialog.form" :rules="addLessonDialog.formRule" label-position="left" hide-required-asterisk>
            <el-form-item label="章节名称:" prop="className">
                <el-input v-model="addLessonDialog.form.className" placeholder="请输入章节名称" />
            </el-form-item>
            <el-form-item label="课程序号:">
                <el-input-number v-model="addLessonDialog.form.number" step-strictly :min="1" :max="100" />
            </el-form-item>
            <div class="date" v-if="addLessonDialog.isLiveCourse">
                    <div style=" margin-bottom: 6px; display: flex; flex-wrap: wrap;">
                        <p style="width: 100%; margin-bottom: 8px;">直播开始时间：</p>
                        <el-date-picker
                            style="min-width: 260px; margin: 0 auto;"
                            v-model="addLessonDialog.form.startTime"
                            type="datetime"
                            placeholder="请选择开始时间"
                            @change="handleStartTimeChange"
                        />
                    </div>
                    <div style="display: flex; flex-wrap: wrap;">
                        <p style="width: 100%; margin-bottom: 8px;">直播结束时间：</p>
                        <el-date-picker
                            style="min-width: 260px; margin: 0 auto;"
                            v-model="addLessonDialog.form.endTime"
                            type="datetime"
                            placeholder="请选择结束时间"
                            :disabled="addLessonDialog.form.startTime === '' || addLessonDialog.form.startTime == null ? true:false"
                            @change="handleEndTimeChange"
                        />
                    </div>
                </div>
                
                <div v-else>
                    <p style="margin-bottom: 8px;">上传课程视频：</p>
                    <!-- 
                        注意：
                            1、auto-upload 为 false 时，before-upload 不生效 
                    -->
                    <el-upload
                        class="upload"
                        ref="uploadRef"
                        :limit="1"
                        :on-exceed="(files,uploadFiles)=>handleExceed(files,uploadFiles,uploadRef)"
                        :auto-upload="false"
                        :before-remove="handleFileRemove"
                        :on-change="(uploadFile,uploadFiles)=>handleUploadChange(uploadFile,uploadFiles,uploadRef)"
                    >
                        <template #trigger>
                            <el-button type="primary" style="min-width: 260px;">点击上传视频</el-button>
                        </template>
                        <template #tip>
                            <div class="el-upload__tip">
                                注意:视频格式仅限 mp4 格式.
                            </div>
                        </template>
                    </el-upload>
                </div>
        </el-form>

        <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="addLessonDialog.visible = false">取消</el-button>
                        <el-button type="primary" @click="submitAddClass(formRef)">
                            添加
                        </el-button>
                    </span>
                </template>
    </el-dialog>

    <!--添加课程资料dialog-->
    <el-dialog v-model="addPlasticDialog.visible" :title="addPlasticDialog.title" :close-on-click-modal="false" destroy-on-close
        close-on-press-escape center width="600px" >
        <el-form ref="plasticFormRef" :model="addPlasticDialog.form" :rules="addPlasticDialog.formRule" label-position="left" hide-required-asterisk>
            <el-form-item label="资料名称:" prop="plasticName" >
                <el-input v-model="addPlasticDialog.form.plasticName" placeholder="请输入资料名称" />
            </el-form-item>
                
                <el-form-item label="上传课程资料：">
                    <el-upload
                        ref="uploadPlasticRef"
                        :limit="1"
                        :on-exceed="handlePlasticExceed"
                        :auto-upload="false"
                        :before-remove="handlePlasticRemove"
                        :on-change="(uploadFile,uploadFiles)=>handlePlasticUploadChange(uploadFile,uploadFiles,uploadPlasticRef)"
                    >
                        <template #trigger>
                            <el-button type="primary" style="min-width: 260px;">点击上传课程资料</el-button>
                        </template>
                    </el-upload>
                </el-form-item>
        </el-form>

        <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="addPlasticDialog.visible = false">取消</el-button>
                        <el-button type="primary" @click="submitAddPlastic(plasticFormRef)">
                            添加
                        </el-button>
                    </span>
                </template>
    </el-dialog>
</template>

<style scoped>
    .comment{
        display: flex;
        margin-bottom: 20px;
    }
    .comment .headPortrait{
        width: 45px;
        height: 45px;
        /* border: 1px solid skyblue; */
        margin-right: 20px;
    }
    .comment .comment-body{
        width: 100%;
        border-bottom: 1px solid #edeff2;
        padding-bottom: 15px;
        /* border: 1px solid skyblue; */
    }
    .comment .comment-body .time{
        font-size: 12px;
        color: #586470;
        
    }

    /** 上传视频相关 */
    .upload{
        min-width: 260px;
        margin: 0 auto;
        text-align: center;
    }
    :deep(.upload .el-button>span){
        width: auto;
    }
</style>