# 业主管理系统 - 项目进度与交接文档

> 用途：开新会话时让 AI 先读这份文件，就不用重新解释项目背景。
> 维护方式：每完成一个能跑起来的功能就更新一次，保证「已完成」「待办」「已知问题」三节反映真实状态。

最后更新：2026-10-21

---

## 0. 新会话开场白（直接复制发给 AI）

```
我是一名大三学生，正在做「业主管理系统」的全栈练习项目，后端在 E:\Owner_management_system。

请先读同目录下的 AGENTS.md 和 PROJECT_PROGRESS.md，了解项目约定和当前进度。

项目约定摘要：
1. 未经我明确同意，禁止修改、新增、删除任何项目文件（包括 .java / pom.xml / .yaml / .md）。
   发现问题只能指出并说明原因，改之前要按「文件+行号+改前改后+依据」的形式申请。
2. 这是学习项目，默认只讲解原理和步骤，给代码示例让我自己敲，不要替我做掉。
3. 回答用中文，要讲「为什么」，给结论时带依据（文件路径 + 行号 / 实测输出）。
4. 可以直接做的只读操作：看文件、搜索、只读 SQL（SELECT/SHOW/DESC）、编译校验。
   其他写操作（改文件、INSERT/UPDATE/DELETE/ALTER/DROP）都要先问。

我今天想做的事：<在这里写>
```

---

## 1. 项目概况

一个物业/业主管理系统，用来学习全栈开发。计划顺序：先做后端 → 用 Apifox 调试 → 再写前端。

- 仓库根目录：`E:\Owner_management_system`（注意：之前叫 `E:\Owner management system`，已改名）
- 后端模块：`backend`
- 前端模块：`frontend`
- 远程仓库：`git@github.com:shankong/Owner-management-system.git`
- 当前分支：`master`

---

## 2. 技术栈与环境

| 项目 | 版本 / 说明 |
|---|---|
| JDK | Java 21 |
| 框架 | Spring Boot 3.5.14 |
| 构建 | Maven，本地仓库 `D:\WorkSoftware\apache-maven-3.9.4\mvn_repo` |
| 持久层 | MyBatis-Plus 3.5.17（必须是 `mybatis-plus-spring-boot3-starter`） |
| 多数据源 | dynamic-datasource 4.6.0 + Druid 1.2.28 |
| 参数校验 | spring-boot-starter-validation |
| 安全 | spring-boot-starter-security（Spring Security 6.5.x） |
| JWT | java-jwt 4.5.0 |
| 邮件 | spring-boot-starter-mail（QQ 邮箱 SMTP） |
| 缓存 | spring-boot-starter-data-redis |
| 数据库 | MySQL 8.0.34，库名 `owner_system`（账号密码见 `application.yaml`） |
| 接口前缀 | `/api`（`server.servlet.context-path` 配置，controller 里不要重复写） |

**编译前必须设置 JAVA_HOME**（系统默认 JDK 是 17，直接编译会报「不支持发行版本 21」）：

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.12.1'
Set-Location 'E:\Owner_management_system\backend'
& 'D:\WorkSoftware\apache-maven-3.9.4\bin\mvn.cmd' -DskipTests compile
```

**Redis**：已安装但默认没启动。

```powershell
D:\WorkSoftware\Redis-8.6.3-Windows-x64-cygwin-with-Service\redis-server.exe
```

默认连 `localhost:6379`，`application.yaml` 里目前没有显式 Redis 配置。

---

## 3. 目录结构

**后端** `backend/src/main/java/com/shankong/admin/`

```
├── AdminApplication.java                启动类
├── common/
│   ├── Result.java                      统一返回体 Result<T>
│   └── exception/
│       ├── BusinessException.java       业务异常（code + message）
│       └── GlobalExceptionHandler.java  全局异常处理器
├── config/
│   ├── JwtConfig.java                   读取 jwt.* 配置
│   ├── PasswordConfig.java              PasswordEncoder(BCrypt) Bean
│   └── SecurityConfig.java              路径放行规则 + 过滤器链
├── controller/UserController.java       用户接口（登录、管理员注册）
├── dto/
│   ├── UserLoginDTO.java                email + password + emailCode
│   ├── UserRegisterDTO.java             username + password + email + realName + emailCode
│   └── UserVO.java                      token + userTypeName + userInfo
├── entity/User.java                     用户实体
├── mapper/UserMapper.java               extends BaseMapper<User>
├── service/
│   ├── EmailCodeService.java            验证码生成、Redis 存取、限流、校验
│   ├── UserService.java                 登录、注册业务
│   └── convert/UserConvert.java         实体 → VO 转换
└── utils/
    ├── EmailUtil.java                   发送 HTML 邮件
    └── JwtTokenUtil.java                生成 JWT
```

**前端** `frontend/src/`

```
├── App.vue
├── main.ts
├── router/index.ts        路由（目前 routes 是空数组）
├── stores/counter.ts      Pinia 示例
└── views/LoginView.vue    登录页（目前只有空模板）
```

前端技术栈：Vue 3 + TypeScript + Vite，集成 vue-router、Pinia、axios、Element Plus。

---

## 4. 数据库

5 张表，主键都是 `bigint AUTO_INCREMENT`：

`building`（楼栋）、`house`（房屋）、`owner`（业主）、`owner_house`（业主-房屋关系）、`user`（系统用户）

外键：

- `house.building_id` → `building.id`
- `owner_house.house_id` → `house.id`
- `owner_house.owner_id` → `owner.id`

**唯一索引**

- `user.username`
- `user.phone`
- `user.email`（`uk_user_email`，2026-10-20 添加；MySQL 的 UNIQUE 允许多个 NULL）
- `owner.phone`

**当前数据（2026-10-21）**：`user` 表 2 条，其余 4 张表为空。

| id | username | email | phone | user_type | status |
|---|---|---|---|---|---|
| 1 | admin | NULL | 13800000000 | 1 管理员 | 1 正常 |
| 2 | shankong | 2197902558@qq.com | NULL | 1 管理员 | 1 正常 |

密码均为 BCrypt 密文（60 字符，`$2a$10$` 开头）。

---

## 5. 已完成

1. **统一返回体** `Result<T>`：code / message / data + `success()` / `error()`，带 `@Data`。
2. **全局异常处理** `GlobalExceptionHandler`：
   - `BusinessException` → 按异常自带 code 返回
   - `MethodArgumentNotValidException` → 400 + 具体校验提示
   - `HttpMessageNotReadableException` → 400「请求体格式不正确」
   - `Exception` → 记 error 日志，返回 500
3. **密码加密**：`PasswordConfig` 注册 `BCryptPasswordEncoder`。
4. **JWT**：`JwtTokenUtil.generateToken()` 生成 token，`UserVO` 返回 `token + userInfo`。
5. **登录接口** `POST /api/user/login`：邮箱 + 密码 + 邮箱验证码，校验通过后签发 token。
6. **管理员注册** `POST /api/user/adminRegister`：用户名、密码、邮箱、姓名、邮箱验证码；查重 → 加密 → 入库，`userType` 固定为 1。
7. **接口安全**：`SecurityConfig` 放行 `/`、`/user/login`、`/user/adminRegister`；关闭 CSRF；会话 STATELESS。
8. **邮件发送**：`EmailUtil` 用 JavaMailSender 发送 HTML 验证码邮件。
9. **验证码服务**：`EmailCodeService`
   - `seedCode(email)`：60 秒限流 + 生成 6 位码 + Redis 存 5 分钟 + 发邮件
   - `verify(email, code)`：比对成功后删除 key
   - 使用 `StringRedisTemplate`、`SecureRandom`
10. **数据库连接修复**：JDBC URL 加 `allowPublicKeyRetrieval=true`，解决 MySQL 8 重启后的 `Public Key Retrieval is not allowed`。
11. **.gitignore**：忽略 `backend/target/`、`backend/logs/`、`backend/.idea/`。
12. **前端骨架**：Vue 3 + Vite + TS + Element Plus + Pinia + axios 已初始化。

---

## 6. 进行中 / 待办

按建议顺序：

- [x] **补「发送验证码」接口**：`UserController` 目前只有 `/login` 和 `/adminRegister`，没有任何接口调用 `emailCodeService.seedCode(...)`，所以登录/注册链路目前发不出码
- [ ] **启动 Redis** 并验证 `StringRedisTemplate` 能正常读写
- [x] **验证码场景隔离**：现在注册和登录共用 `register:code:` / `register:limit:`，注册码可以拿去登录，反之亦然；应改成 `register:` / `login:` 两个命名空间
- [x] **验证码消耗顺序**：现在先验码（验完即删）再验密码，密码输错一次验证码就作废；应把「校验并删除验证码」放到最后一步
- [x] **明确登录模式**：现在是「邮箱 + 密码 + 验证码」（双因素）；如果只想做验证码登录，应去掉密码字段
- [ ] **前端登录页**：`LoginView.vue` 还是空模板，`router/index.ts` 的 routes 是空数组
- [ ] 其余 4 个模块 CRUD：楼栋、房屋、业主、业主-房屋关系
- [ ] 补测试：`AdminApplicationTests` 目前是空的，可用 MockMvc 写接口测试
- [ ] 把注册/登录发 token 的逻辑抽成私有方法复用

---

## 7. 已知问题（按优先级）

1. **没有发送验证码的接口**，当前功能无法端到端跑通。
2. **Redis 未启动**，`EmailCodeService` 一调用就会连接失败。
3. **验证码命名空间未区分注册/登录**（见待办）。
4. **验证码先删后验密码**，失败一次就要重新收邮件。
5. **敏感信息硬编码并已提交到 Git**：
   - `application.yaml` 里的邮箱授权码、数据库密码、JWT secret
   - 正确做法：本地用 `application-local.yaml`（加入 .gitignore），或改成 `${ENV_VAR:默认值}` 形式
6. **QQ 邮箱端口与加密方式需核对**：当前是 `port: 587` + `mail.smtp.ssl.enable: true`。QQ 邮箱常见组合是 465 + SSL，或 587 + STARTTLS；发信报错时先查这里。
7. **`@Transactional` 加在 `UserService` 类上**：登录这种只读方法也会开事务。建议只标注写操作方法（注册/更新）。
8. **方法命名拼写**：`EmailUtil.seedEmail`、`EmailCodeService.seedCode` 应为 `sendEmail` / `sendCode`。
9. **`EmailUtil` 的 catch 吞掉原始异常**：`catch(Exception e)` 后直接抛「发送邮件失败」，没有 `log.error`，真正原因（认证失败、连接超时）看不到。
10. **`.gitignore` 路径已过期**：项目目录改名、IDEA 打开根目录后，根目录的 `.idea/`、`.vscode/`、`logs/` 不再被规则匹配，目前是未跟踪状态。
11. **前端路由为空**，登录页只是空模板。

---

## 8. 踩过的坑（避免重复踩）

**Spring / MyBatis-Plus**

1. MyBatis-Plus 必须用 `mybatis-plus-spring-boot3-starter`；Boot 2 时代的 starter 在 Boot 3 下会找不到 SqlSessionFactory。
2. `spring.datasource.type` 写了不生效：dynamic-datasource 会抢先创建 DataSource，类型要写在每个数据源下面。
3. Jackson 序列化依赖 getter，只有私有字段会返回 `{}`；`Result` 必须加 `@Data`。
4. 下划线字段 → 驼峰属性靠 `map-underscore-to-camel-case: true`。
5. `selectOne` 要求「最多一条」，查到多条会抛 `TooManyResultsException`；只有唯一索引字段才安全。
6. `BaseMapper.exists()` 在 3.5.17 中是基于 `selectCount` 实现的（生成 COUNT 查询），但语义比 `selectOne` 更贴切，也不会把整行（含密码）查出来。

**参数校验**

7. DTO 上的 `@NotBlank` 等注解必须配合 Controller 参数的 `@Valid` 才生效。
8. 请求体完全为空时先抛 `HttpMessageNotReadableException`，轮不到 `@Valid`；已单独处理成 400。
9. 同一个字段上多个约束是「且」不是「或」，写两个 `@Pattern` 无法表达「手机号或邮箱」。

**Spring Security**

10. 官方 starter 不要手写 `<version>`；`spring-boot-starter-security:4.1.1` 与 Boot 3.5.14 混用会导致两个 `SecurityFilterAutoConfiguration` 抢注同一个 Bean。
11. `@Bean` 方法名默认就是 Bean 名，不能叫 `springSecurityFilterChain`（框架占用），应叫 `filterChain`。
12. Security 的路径匹配**不含 context-path**：配置了 `/api`，规则里要写 `/user/login` 而不是 `/api/user/login`。
13. 启动日志的 `Using generated security password` 是 Boot 的临时内存用户，不是数据库用户。
14. 不要用 `spring.main.allow-bean-definition-overriding=true` 掩盖 Bean 冲突。

**MySQL**

15. MySQL 8 默认 `caching_sha2_password`，`useSSL=false` 时需要 `allowPublicKeyRetrieval=true`，否则重启后报 `Public Key Retrieval is not allowed`；该参数仅限本地开发。
16. UNIQUE 索引允许多个 NULL，所以「邮箱可选但填了必须唯一」可以直接用唯一索引实现。

**Redis / 验证码**

17. Redis 没有目录结构，`register:code:` 这类前缀只是命名约定；不同业务必须用不同前缀，否则会互相覆盖。
18. 限流要用 `setIfAbsent`（SET NX EX）原子操作，不能用「先 get 再 set」，否则并发下会失效。
19. 验证码 key 必须设 TTL，否则永久堆积。
20. 验证码用 `SecureRandom`，不要用 `Random`（后者可预测）。

**Git**

21. GitHub 开了邮箱隐私保护时，推送真实邮箱的提交会被拒（`GH007`）；本仓库已改用 noreply 邮箱。
22. 第一次提交误把 `backend/target/` 纳入版本库，后来用 `.gitignore` + `git rm -r --cached` 摘除；提交前先 `git status`。
23. 项目目录改名后，所有工具/脚本/文档里的旧路径都要同步更新。

---

## 9. 项目约定（同 AGENTS.md，摘要）

1. 未经本人明确同意，禁止修改、新增、删除任何项目文件；AI 只能指出问题并说明原因。
2. 需要改代码时先书面申请：**文件完整路径 + 行号、改前改后对比、依据（文档/实测/报错原文）**，本人回复「可以 / 改吧 / 同意」后才动手。一次同意只对当次那一项有效。
3. 不擅自重命名包名/类名/文件名，不顺手格式化、重构。
4. 不删除自认为没用的文件，先问。
5. 临时验证代码也要先申请，用完删除并说明。
6. 可以直接做：查看/搜索文件、只读 SQL、编译校验。
7. 任何写操作都要先经同意：写文件、改文件、删文件，以及 INSERT/UPDATE/DELETE/ALTER/DROP。
8. 回答用中文，讲「为什么」，给结论时带依据。

---

## 10. Git 状态

**已推送的提交**

```
0b4ecae 实现管理员注册接口
ec20c6f 修复本地数据库连接失败：允许获取服务器公钥
a208069 初始化 Vue3 + Vite 前端项目并添加登录页
b6c8ab7 添加项目进度文档，供新会话接手
f06bb9f 实现登录 JWT 令牌签发
2b629f2 实现登录密码密文校验与接口路径安全校验
270c8b5 添加 .gitignore，停止跟踪编译产物与日志
d8c2a02 实现用户登录接口，搭建统一返回与异常处理
d41a8e1 创建项目，搭了一个框架，还没开始写
```

**当前未提交的改动（2026-10-21）**

- 修改：`pom.xml`（mail + redis）、`Result.java`（补注释）、`UserController.java`、`UserLoginDTO.java`、`UserRegisterDTO.java`、`UserService.java`、`application.yaml`
- 新增：`EmailCodeService.java`、`EmailUtil.java`
- 未跟踪：`.idea/`、`.vscode/`、`logs/`（不应提交）

提交邮箱：`shankong <286970053+shankong@users.noreply.github.com>`（本仓库局部配置）

---

## 11. 更新日志

- 2026-10-14：搭建 Maven / MyBatis-Plus / 多数据源骨架；实现统一返回体、业务异常与全局异常处理、密码登录；确认数据库表结构。
- 2026-10-15：登录密码改为 BCrypt；引入 Spring Security 路径控制；添加 `.gitignore`；建立本进度文档；登录成功签发 JWT。
- 2026-10-16：实现管理员注册接口；修复 MySQL 8 公钥获取导致的连接失败；初始化 Vue 3 + Vite 前端项目。
- 2026-10-20：`user.email` 增加唯一索引；引入邮件与 Redis 依赖；新增 `EmailUtil`、`EmailCodeService`；登录与注册改为「邮箱 + 验证码」校验。
- 2026-10-21：更新本文档，记录当前进度、待办与已知问题。
