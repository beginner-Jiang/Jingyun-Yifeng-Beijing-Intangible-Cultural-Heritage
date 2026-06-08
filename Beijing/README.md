# 京韵遗风 · 北京非物质文化遗产数字博物馆与传承人互动平台
# Jingyun Yifeng · Beijing Intangible Cultural Heritage Digital Museum and Inheritor Interaction Platform

## 1. 项目定位与概述 / Project Positioning and Overview

“京韵遗风”是一个面向公众与非遗传承人的综合性Web平台，旨在解决北京非物质文化遗产传播渠道单一、互动性差、数据陈旧等问题。平台集数字展览、地图寻迹、AI创意生成、传承人互动、数据看板、个人护照等功能于一体，利用现代Web技术构建沉浸式、智能化的非遗数字博物馆，让用户足不出户感受北京非遗魅力，同时为传承人提供展示、交流与招募学徒的窗口。

"Jingyun Yifeng" is a comprehensive web platform for the public and intangible cultural heritage (ICH) inheritors. It aims to solve the problems of single communication channels, poor interaction, and outdated data for Beijing's ICH. The platform integrates digital exhibitions, map tracking, AI creative generation, inheritor interaction, data dashboards, personal passports and other functions. Using modern web technologies, it builds an immersive and intelligent ICH digital museum, allowing users to experience the charm of Beijing's ICH without leaving home, while providing inheritors with a window to display, communicate and recruit apprentices.

## 2. 系统使用的技术栈 / Technology Stack

- **后端 / Backend**：Spring Boot 3.2.5、MyBatis Plus 3.5.7、JWT 0.11.5、MySQL 8.0、Redis 7.x
- **前端 / Frontend**：HTML5/CSS3/JavaScript ES6、Bootstrap 5、Font Awesome 6、ECharts 5、Vue.js (partial)、Baidu Maps WebGL API、lunar.js (lunar calendar and solar terms)
- **AI服务 / AI Services**：Alibaba Cloud DashScope (Tongyi Qianwen, Tongyi Wanxiang)
- **天气服务 / Weather Service**：open-meteo API
- **构建工具 / Build Tools**：Maven、Git

## 3. 系统项目结构 / System Project Structure

项目后端采用标准的Spring Boot分层架构，包结构及主要组成部分如下：
The back-end adopts standard Spring Boot layered architecture. Package structure and main components are as follows:

- **`org.example.beijing`**：根包，包含Spring Boot启动类 `BeijingApplication.java`。Root package, contains Spring Boot starter class `BeijingApplication.java`.
- **`config`**：配置类包，存放跨域配置、MyBatis Plus分页配置、WebMVC配置、阿里云DashScope配置等。Configuration package, containing CORS, MyBatis Plus pagination, WebMVC, and DashScope configurations.
- **`controller`**：控制器层，处理前端HTTP请求，返回统一响应。例如 `AuthController`、`HeritageController`、`AIController` 等。Controller layer, handles front-end HTTP requests and returns unified responses. Examples: `AuthController`, `HeritageController`, `AIController`.
- **`dto`**：数据传输对象，用于封装请求参数和响应数据，如 `LoginDTO`、`RegisterDTO`、`ActivityDTO` 等。Data Transfer Objects, encapsulate request parameters and response data, e.g., `LoginDTO`, `RegisterDTO`, `ActivityDTO`.
- **`entity`**：实体类，与数据库表一一对应，如 `User`、`HeritageItem`、`Activity` 等。Entity classes, map one-to-one with database tables, e.g., `User`, `HeritageItem`, `Activity`.
- **`interceptor`**：拦截器，目前包含 `JwtInterceptor`，用于验证JWT令牌并将用户信息存入请求上下文。Interceptors, currently includes `JwtInterceptor` for validating JWT tokens and storing user information in request context.
- **`mapper`**：MyBatis Plus的Mapper接口，定义数据库操作方法，如 `UserMapper`、`ActivityMapper` 等。MyBatis Plus mapper interfaces, define database operation methods, e.g., `UserMapper`, `ActivityMapper`.
- **`service`**：服务层接口，定义业务逻辑，如 `UserService`、`ActivityService`。Service layer interfaces, define business logic, e.g., `UserService`, `ActivityService`.
- **`service.impl`**：服务层实现类，实现业务逻辑，如 `UserServiceImpl`、`ActivityServiceImpl`。Service implementation classes, implement business logic, e.g., `UserServiceImpl`, `ActivityServiceImpl`.
- **`util`**：工具类包，提供通用工具，如 `JwtUtil`、`FileUploadUtil`、`ResponseResult`、`TextFilterUtil`。Utility package, provides common tools, e.g., `JwtUtil`, `FileUploadUtil`, `ResponseResult`, `TextFilterUtil`.
- **`static`**：存放静态资源（lunar.js、common.js和common.css等）。Static resources (lunar.js, common.js, common.css, etc.).
- **`templates`**：存放模板文件（本项目未使用）。Template files (not used in this project).
- **`webapp`**：存放相关jsp文件，用于构建临时管理后台。Contains JSP files for building a temporary management backend.
- **`application.properties` and `application.yml`**：Spring Boot配置文件，包含数据库连接、Redis配置、JWT密钥、上传路径等。Spring Boot configuration files, contain database connection, Redis configuration, JWT secret, upload paths, etc.
- **`SQL`**：存放测试系统用的相关数据库脚本文件。Database script files for testing.
- **`uploads`**：自定义的上传文件存储目录，根据 `application.yml` 配置，用于存放用户头像、活动封面、作品图片等。Custom upload directory, configured in `application.yml`, stores user avatars, activity covers, artwork images, etc.
- **`pom.xml`**：Maven项目对象模型文件，管理项目依赖和构建配置。Maven project object model file, manages dependencies and build configuration.
- **`README.md`**：项目代码详细解析与运行方式说明文件。This file – detailed explanation of the project code and how to run it.

## 4. 数据库表结构 / Database Table Structure

系统共13张核心表，全部使用InnoDB引擎、utf8mb4字符集，满足第三范式。
The system has 13 core tables, all using InnoDB engine, utf8mb4 character set, and satisfying the third normal form.

| 表名 / Table Name | 说明 / Description | 主要字段 / Main Fields |
|------------------|--------------------|------------------------|
| `user` | 用户表 / User table | id, username, password, email, phone, age, avatar, role, interest_tags, invite_code, bio, created_at, updated_at |
| `inheritor` | 传承人档案 / Inheritor profile | id, name, level, gender, birth_year, biography, image_url, user_id, created_at |
| `heritage_item` | 非遗项目 / ICH item | id, name, category, region, level, number, description, image_url, video_url, inheritor_id, created_at |
| `heritage_site` | 非遗点位 / ICH site | id, name, address, longitude, latitude, heritage_item_id, contact, open_hours, type, description, image_url |
| `activity` | 活动 / Activity | id, title, type, inheritor_id, site_id, start_time, end_time, max_participants, current_participants, description, image_url, status, created_at |
| `activity_signup` | 活动报名 / Activity signup | id, activity_id, user_id, status, apply_time, process_time |
| `collection` | 收藏 / Collection | id, user_id, target_type, target_id, created_at |
| `footprint` | 用户足迹 / User footprint | id, user_id, target_type, target_id, action, created_at |
| `recruit_post` | 招募发布 / Recruit post | id, inheritor_id, title, description, inheritor_name, applicant_count, max_applicants, status, created_at |
| `recruit_application` | 招募申请 / Recruit application | id, recruit_id, user_id, status, apply_time, process_time |
| `login_log` | 登录日志 / Login log | id, user_id, login_time |
| `comment` | 交流评论 / Discussion comment | id, user_id, content, created_at |
| `artwork` | 传承人作品 / Inheritor artwork | id, title, description, image_url, inheritor_id, created_at |

## 5. 系统功能模块与功能梗概 / Functional Modules Overview

### 5.1 用户认证模块 / User Authentication Module
- 密码登录、手机验证码登录、注册（含年龄字段） / Password login, SMS verification code login, registration (with age field)
- JWT token 认证，24小时有效期 / JWT token authentication, 24-hour validity
- 普通用户通过邀请码升级为传承人 / Ordinary users can upgrade to inheritor via invitation code

### 5.2 数字展厅模块 / Digital Exhibition Hall Module
- 非遗项目分类筛选与卡片展示 / ICH item category filtering and card display
- 项目详情模态框（含传承人档案、相关推荐、热度趋势图） / Item detail modal (with inheritor profile, related recommendations, trend chart)
- 非遗节气日历（农历、节气、非遗日活动，可点击查看详情） / ICH solar term calendar (lunar date, solar terms, ICH special days, clickable for details)

### 5.3 非遗地图模块 / ICH Map Module
- 百度地图集成，点位标注、信息窗（含路线规划、天气预报） / Baidu Maps integration, point markers, info windows (with route planning, weather forecast)
- 按类型筛选、关键字搜索、周边搜索、定位到当前位置 / Filter by type, keyword search, nearby search, locate to current position
- 点击信息窗中“天气”按钮，展示未来7天预报，点击单日可查看详细气象与生活指数（体感温度、风力、湿度、紫外线、能见度、气压、日出日落、空气质量、穿衣/防晒/旅游/运动/钓鱼/洗车/过敏/感冒指数） / Click weather button in info window to show 7‑day forecast; click a single day to see detailed weather and life indices (apparent temperature, wind speed, humidity, UV, visibility, pressure, sunrise/sunset, air quality, dressing/sunscreen/travel/sports/fishing/car wash/allergy/cold indices)

### 5.4 京小智AI模块 / Jing Xiaozhi AI Module
- 文生文（通义千问）、文生图（通义万相）、文生视频（异步任务） / Text‑to‑text (Tongyi Qianwen), text‑to‑image (Tongyi Wanxiang), text‑to‑video (asynchronous task)
- 语音输入（Web Speech API）、语音播报开关 / Voice input (Web Speech API), voice broadcast toggle
- 聊天记录按模式分别存储（Redis，每种模式保留最近10条） / Chat history stored separately by mode (Redis, last 10 messages per mode)

### 5.5 数据看板模块 / Data Dashboard Module
- 非遗项目收藏排行榜（柱状图，按时段筛选） / ICH item collection leaderboard (bar chart, time‑period filter)
- 用户年龄结构（玫瑰图，按时段筛选） / User age structure (rose chart, time‑period filter)
- 北京非遗区县热力地图（基于GeoJSON的ECharts地图，颜色深浅表示非遗数量，小面积区县用字母A/B/C标注并配图例） / Beijing ICH district heat map (ECharts map based on GeoJSON, color shade indicates number of ICH items; small districts are marked with letters A/B/C with a legend)
- 用户活跃时段分布（折线图，显示0‑24点登录次数，按时段筛选） / User active hour distribution (line chart, login counts from 0 to 24, time‑period filter)
- 非遗词云（基于AI对话与用户评论） / ICH word cloud (based on AI conversations and user comments)

### 5.6 传承人互动平台模块 / Inheritor Interaction Platform Module
- 活动发布、编辑、结束、报名管理（同意/拒绝） / Activity publishing, editing, ending, enrollment management (approve/reject)
- 作品发布、编辑、删除 / Artwork publishing, editing, deletion
- 非遗交流留言板（Redis存储） / ICH discussion board (Redis storage)
- 学徒招募发布、申请、审核（最多2个招募/用户，招募满员自动截止） / Apprentice recruitment posting, application, review (max 2 recruits per user; automatically closed when full)
- 活动卡片显示进度条（颜色随占用率变化） / Activity card shows progress bar (color changes with occupancy rate)

### 5.7 我的护照模块 / My Passport Module
- 个人信息展示与编辑（头像上传/覆盖、年龄、简介、兴趣标签） / Personal information display and editing (avatar upload/overwrite, age, bio, interest tags)
- 手机号验证修改密码 / Password reset via phone number verification
- 收藏列表（项目/点位/活动） / Collection list (item/site/activity)
- 参与活动记录、自己发布的活动 / Activity participation records, activities published by user
- 招募申请记录与发布记录 / Recruitment application records and posted recruitments
- 申请升级传承人（邀请码 JINGYUN-2026） / Apply for inheritor upgrade (invitation code JINGYUN‑2026)
- 注销账户（物理删除所有相关数据及文件） / Account deletion (physically removes all related data and files)

### 5.8 临时管理后台模块（非核心模块——部分使用JSP+JDBC技术） / Temporary Admin Backend Module (non‑core – partially uses JSP+JDBC)
- 系统状态查看 / System status viewing
- 用户信息管理 / User information management
- 活动信息管理 / Activity information management
- 作品信息管理 / Artwork information management
- 非遗项目信息管理 / ICH item information management
- 招募信息管理 / Recruitment information management
- 评论信息管理 / Comment information management

## 6. 测试与运行方式 / Testing and Running Instructions

### 6.1 环境要求 / Environment Requirements
- JDK 17+
- MySQL 8.0（数据库名 `beijing`，字符集 utf8mb4） / Database name `beijing`, charset utf8mb4
- Redis 7.x（本地或远程） / Local or remote
- Maven 3.9+
- 浏览器（Chrome / Edge 最新版） / Browser (latest Chrome / Edge)

### 6.2 运行步骤 / Running Steps
1. 导入数据库：执行 `Test_Data1.sql`（位于项目根目录），初始化表结构与测试数据（Test_Data1.sql、Test_Data2.redis）。 / Import database: run `Test_Data1.sql` (located in project root) to initialize table structure and test data (Test_Data1.sql, Test_Data2.redis).
2. 修改 `application.yml` 中的数据库连接、Redis配置、阿里云DashScope API Key（如需AI功能）。 / Modify `application.yml` for database connection, Redis configuration, Alibaba Cloud DashScope API key (if AI functions are needed).
3. 在项目根目录执行 `mvn clean package` 打包。 / Run `mvn clean package` in the project root to build.
4. 运行 `java -jar target/beijing-1.0.0.jar`。 / Run `java -jar target/beijing-1.0.0.jar`.
5. 访问 `http://localhost:8080/Index.html`。 / Visit `http://localhost:8080/Index.html`.
6. 测试账号：普通用户（通过测试脚本查询）、传承人（通过测试脚本查询）。亦可直接注册，对于传承人固定邀请码为JINGYUN-2026。 / Test accounts: ordinary users (query via test script), inheritors (query via test script). You can also register directly; the fixed invitation code for inheritor is JINGYUN‑2026.
7. 此外要访问临时管理后台需通过单独的地址：`http://localhost:8080/manage/`进行访问，并使用前端硬编码的用户名（admin）和密码(admin123)。 / To access the temporary admin backend, use the separate address `http://localhost:8080/manage/` and the hard‑coded username `admin` and password `admin123`.

### 6.3 主要测试点 / Main Test Points
- 登录/注册、密码修改、身份升级 / Login/registration, password change, identity upgrade
- 数字展厅分类筛选、收藏、日历节气点击 / Digital exhibition hall category filter, collection, calendar solar term click
- 地图点位加载、搜索、路线规划、天气预报（点击单日查看详情） / Map point loading, search, route planning, weather forecast (click a single day for details)
- AI文生文/文生图/文生视频（需要配置API Key） / AI text‑to‑text/image/video (requires API key)
- 数据看板各图表时段筛选、地图热力图渲染 / Data dashboard charts time‑period filter, map heatmap rendering
- 传承人发布活动/作品/招募、普通用户报名/申请、审核流程 / Inheritor posting activities/artworks/recruitments, ordinary user signup/application, review process
- 个人护照编辑资料、头像上传、注销账户 / Personal passport edit profile, avatar upload, account deletion
- 临时管理后台对前台数据信息的操控以及模拟登录功能效果 / Temporary admin backend manipulation of front‑end data and simulation login function

## 7. 注意事项 / Important Notes

1. **百度地图 AK**：代码中已内置测试AK（`yCLrjypN9iVQFLSctIivlrv6wvPRqvDg`），如需商用请替换为自己的AK。 / A test AK is already embedded (`yCLrjypN9iVQFLSctIivlrv6wvPRqvDg`); replace with your own AK for commercial use.
2. **AI功能**：文生图、文生视频依赖阿里云DashScope，需配置 `aliyun.dashscope.api-key`，免费额度有限。 / Text‑to‑image and text‑to‑video depend on Alibaba Cloud DashScope; configure `aliyun.dashscope.api-key`; free quota is limited.
3. **文件上传路径**：默认 `./uploads/`，请确保运行目录有读写权限。 / Default upload path is `./uploads/`; ensure the running directory has read/write permissions.
4. **Redis**：AI对话历史、论坛留言依赖Redis，请确保服务可用。 / AI conversation history and forum messages rely on Redis; ensure the service is available.
5. **天气接口**：使用open‑meteo免费API，无需密钥，但需能访问外网。 / Uses free open‑meteo API, no key required, but needs internet access.
6. **地图样式**：已自定义暖色调古风样式，若百度地图API升级导致样式失效，可删除 `setMapStyle` 调用。 / A warm‑tone antique style is customised; if a Baidu Maps API upgrade breaks the style, you can remove the `setMapStyle` call.
7. **测试数据**：`Test_Data1.sql` 包含用户、活动、收藏、招募等完整数据，首次运行请务必执行。 / `Test_Data1.sql` contains complete test data (users, activities, collections, recruitments); be sure to run it on first startup.
8. **跨域与拦截器**：后端已配置CORS允许所有来源，JWT拦截器排除 `/api/auth/login` 等路径。 / Backend CORS is configured to allow all origins; JWT interceptor excludes paths such as `/api/auth/login`.
9. **注销账户**：会物理删除用户头像文件、所有关联记录，不可恢复。 / Account deletion physically removes user avatar files and all related records; it is irreversible.
10. **主题切换**：支持日间/夜间/护眼模式，状态保存在 localStorage，多标签页同步。 / Supports light/dark/soft modes; state is saved in localStorage and synchronised across tabs.