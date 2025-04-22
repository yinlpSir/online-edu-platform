create table user(
    id bigint primary key,-- 雪花算法生成
    username varchar(30) not null,
    password varchar(255) not null,
    phone_number char(11) unique not null CHECK(phone_number REGEXP '^1[3456789]\\d{9}$'),
    gender unsigned tinyint(1) not null, -- 0 表示 女，1 表示 男
    head_portrait varchar(255) comment '头像',
    introduction text comment '个人简介',
    role unsigned tinyint(1) not null,# 1 是管理员，2 是老师，3 是学生
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0 表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table student(
    id bigint primary key,
    grade enum('1','2','3','4','5','6','7','8','9','10','11','12') comment '年级',
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table teacher(
    id bigint primary key,
    real_name varchar(30) not null,
    teacher_certificate_img varchar(255) not null comment '教师资格证路径',
    is_authenticated unsigned tinyint default 0, -- 0 为没有认证，1 为认证
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table course (
    id bigint primary key,
    course_name varchar(50) not null,
    description varchar(255) not null comment '课程介绍',
    course_cover varchar(255) not null comment '课程封面',
    course_type int not null default 3 comment '课程类别', # 1 轮播图 2 平台推荐 3 其他课程
    price decimal(10,2) default 0.00,
    grade set('1','2','3','4','5','6','7','8','9','10','11','12'),
    number int not null comment '课时数量',
	subject enum('语文','数学','英语','物理','化学','政治','地理','生物','历史') not null comment '科目',
    purchase_quantity int default 0 comment '购买人数',
    is_passed unsigned tinyint(1) default 0 comment '课程是否审核', -- 0为 待审核，1为已审核，-1未通过审核
    teacher_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table student_buy_course (
    id bigint primary key,
    student_id bigint not null,
    course_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table class(
    id bigint primary key,
    class_name varchar(50) not null,
    number int not null comment '课程序号',
    video_path varchar(255),
    start_time datetime comment '课程开始时间', 
    end_time datetime,
    last_time timestamp comment '课程的实际持续时间',
    class_process timestamp comment '课程的进度',
    course_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

-- 课程资料
create table course_plastic(
    id bigint primary key,
    plastic_name varchar(50) not null,
    plastic_path varchar(255) not null,
    plastic_size bigint not null,
    course_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

-- 一个课程可被多个用户购买 ，一个用户可购买多个课程
create table order(
    id bigint primary key,
    order_no varchar(50) not null comment '订单编号',
    order_price decimal(10,2) default 0.00,
    order_subject varchar(50) not null,
    order_status unsigned tinyint default 0, -- 1 表 已支付，0 表 未支付
    user_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table comment(
    id bigint primary key,
    content text not null,
    grade tinyint not null comment '评价等级',-- 对课程的评价等级(1~5级);
    user_id bigint not null,
    course_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

-- 作业
create table assignment(
    id bigint primary key,
    name varchar(50) not null,
    question_count unsigned smallint not null comment '题量',
    deadline datetime not null comment '截止时间',
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table question(
    id bigint primary key,
    content varchar(255) not null,
    answer varchar(255),
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

create table option(
    id bigint primary key,
    letter enum('A','B','C','D') not null, 
    content varchar(255) not null,
    question_id bigint not null,
    is_deleted unsigned tinyint(1) default 0, -- 1 表示 删除，0表示 未删除
    create_time datetime default now(),
    update_time datetime
);

