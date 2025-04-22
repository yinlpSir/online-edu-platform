![15](https://github.com/user-attachments/assets/c1846615-abab-4234-94ed-570298b86e5a)# online-edu-platform
基于Spring Boot 3 + Spring Cloud + Spring Security 6 + Vue3 + Element Plus的在线教育平台。

### 项目介绍
在线教育平台是一个基于Spring Boot + Spring Cloud + Vue3的前后端分离的微服务架构项目。平台上提供了一站式的学习资源和服务。通过平台，学生可以随时随地在线学习，获取优质的学习资源；教师可以充分利用平台的资源和工具，提高教学质量和效率。本项目分为学生端、教师端和管理员端。学生端的主要功能是：手机号登录注册、按条件查询课程、购买课程、查看课程相关资源（比如：课程资料、课程视频等）、观看直播及回放等。老师端主要功能是：手机号登录注册、管理自己发布的课程（包括直播课和录播课、课程资料CURD等）、直播授课。管理员端主要功能是：用户管理、教师管理（包括教师身份审核等）、课程管理（包括课程审核等）。

### 主要技术栈
- **后端**：Spring Boot、Spring Security、Spring Cloud、MyBatis Plus、Redis、RabbitMQ
- **前端**：Vue3、Element Plus、Axios、Pinia、Vite、Tinymce、Video.js
- **第三方服务**：腾讯云短信服务、阿里云ECS、阿里云OSS、支付宝沙箱支付、腾讯视频点播服务、欢拓云直播服务、内网穿透(https://natapp.cn/)

### 功能特性
- **短信登录注册**：使用了腾讯云短信服务可对真实的手机号发送短信进行身份验证。
- **沙箱支付**：调用支付宝的支付接口模拟真实的支付功能。
- **直播服务**：对接欢拓云直播接口对平台课程进行直播授课。
- **对象存储服务**：使用阿里云OSS服务存储和管理平台上的非结构化数据。
- **视频点播服务**：使用视频点播服务管理录播课及视频资料，并提高视频播放体验。
- **视频断点续播**：通过记录用户播放进度至数据库并同步至前端本地存储（LocalStorage），实现中途退出后精准续播，提升学习连贯性与用户体验。
- **用户认证和授权**：基于 Spring Security 框架，结合 JWT 与 Redis 实现无状态身份认证、细粒度权限控制及会话状态管理。
- **异步消息队列**：基于 RabbitMQ 实现关键业务异步处理。

### 演示图
<table>
    <tr>
        <td><img alt="1" src="https://github.com/user-attachments/assets/1e2571a1-33ed-4ad4-b326-21bf904b72fa" /></td>
        <td><img alt="2" src="https://github.com/user-attachments/assets/ca3176da-a4ac-41b8-ae8e-887268972e0f" /></td>
    </tr>
    <tr>
        <td><img width="982" alt="3" src="https://github.com/user-attachments/assets/66fb40c9-fbb0-4e3c-8ffd-368d2e02b840" /></td>
        <td><img alt="4" src="https://github.com/user-attachments/assets/cccca5db-554f-46aa-9d68-c84992e1c2de"/></td>
    </tr>
    <tr>
        <td><img width="982" alt="5" src="https://github.com/user-attachments/assets/d0b9c2e5-580b-4f90-9697-e70a88690356" /></td>
        <td><img width="982" alt="6" src="https://github.com/user-attachments/assets/3a5f3cd1-8adc-4ef2-9bd9-550c238e3d37"/></td>
    </tr>
	  <tr>
        <td><img width="970" alt="7" src="https://github.com/user-attachments/assets/8ecbd964-53fd-4dbe-a65e-79ccead83b60" /></td>
    </tr>	 

![8](https://github.com/user-attachments/assets/273adcf4-6298-4e71-a9b7-1bf90ad1fa61)
![9](https://github.com/user-attachments/assets/3bc0d3ab-9757-49b9-9098-bf224e8c3907)
![10](https://github.com/user-attachments/assets/791eb1d3-5142-4253-ab98-3d96b80ff319)
![11](https://github.com/user-attachments/assets/fffaee93-2dcb-4627-a7f7-88f2ce241e71)
![12](https://github.com/user-attachments/assets/feba1994-044f-49ce-9546-57c3cfa3b997)
![13](https://github.com/user-attachments/assets/08bd3a30-533c-49ac-a319-7fe4ded7a996)
![14](https://github.com/user-attachments/assets/d41e5020-5386-4651-9e8d-b47fdce9905d)
![15](https://github.com/user-attachments/assets/8b4bc4fb-466e-4131-95cb-f9f2dd4e897a)
![16](https://github.com/user-attachments/assets/f84db18b-c85f-4866-84f2-5ac925e212ea)
![17](https://github.com/user-attachments/assets/0d73a79a-e8c5-4a0d-ac6e-ee1ef8eee25d)


</table>
