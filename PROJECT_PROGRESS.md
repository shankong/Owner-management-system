# 业主管理系统 - 项目进度与交接文档

> 用途：开新会话时把这份文件交给 AI，就不用重新解释项目背景。
> 维护方式：每完成一个**能跑起来**的功能就更新一次，保证「已完成」和「待办」两节永远反映当前真实状态。

最后更新：2026-10-15

---

## 0. 新会话开场白（直接复制这段发给 AI）

```
我是一名大三学生，正在做「业主管理系统」的全栈练习项目，后端在 E:\Owner management system。

请先读同目录下的 AGENTS.md 和 PROJECT_PROGRESS.md，了解项目约定和当前进度。

项目约定摘要：
1. 未经我明确同意，禁止修改、新增、删除任何项目文件（包括 .java / pom.xml / .yaml / .md）。
   发现问题只能指出并说明原因，改之前要按「文件+行号+改前改后+依据」的形式申请。
2. 这是学习项目，默认只讲解原理和步骤，给代码示例让我自己敲，不要替我做掉。
3. 回答用中文，要讲「为什么」，给结论时带依据（文件路径 + 行号 / 实测输出）。
4. 可以直接做的只读操作：看文件、搜索、只读 SQL、编译校验。其他写操作都要先问。

我今天想做的事：<在这里写>
```

---

## 1. 项目概况

一个物业/业主管理系统，学习全栈开发用。计划先做完后端、用 Apifox 调试，再写前端。

仓库根目录：`E:\Owner management system`
后端模块目录：`backend`
远程仓库：`git@github.com:shankong/Owner-management-system.git`（分支 `master`）

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
| 数据库 | MySQL 8.0.34，库名 `owner_system`（账号密码见 `application.yaml`） |
| 接口前缀 | `/api`（由 `server.servlet.context-path` 配置，controller 里不要重复写） |

**编译前必须做的事**（系统默认 JDK 是 17，直接编译会报「不支持发行版本 21」）：

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.12.1'
& 'D:\WorkSoftware\apache-maven-3.9.4\bin\mvn.cmd' -DskipTests compile
```

---

## 3. 目录结构

```
backend/src/main/java/com/shankong/admin/
├── AdminApplication.java              启动类（@SpringBootApplication）
├── common/
│   ├── Result.java                    统一返回体 Result<T>（code/message/data）
│   └── exception/
│       ├── BusinessException.java     业务异常（code + message）
│       └── GlobalExceptionHandler.java 全局异常处理器
├── config/
│   ├── PasswordConfig.java            注册 PasswordEncoder(BCrypt) Bean
│   └── SecurityConfig.java            Spring Security 路径规则
├── controller/UserController.java     用户接口
├── dto/                               UserLoginDTO / UserRegisterDTO / UserVO
├── entity/User.java                   用户实体（含类型/状态中文名转换方法）
├── mapper/UserMapper.java             extends BaseMapper<User>
└── service/UserService.java           业务逻辑
```

---

## 4. 数据库

5 张表，主键都是 `bigint AUTO_INCREMENT`：

`building`（楼栋）、`house`（房屋）、`owner`（业主）、`owner_house`（业主-房屋关系，多对多）、`user`（系统用户）

外键关系：

- `house.building_id` → `building.id`
- `owner_house.house_id` → `house.id`
- `owner_house.owner_id` → `owner.id`

唯一索引：`user.username`、`user.phone`、`owner.phone`。

**当前数据量（2026-10-15）**：`user` 表 1 条，其余 4 张表为空。

| id | username | real_name | phone | user_type | status | 密码 |
|---|---|---|---|---|---|---|
| 1 | admin | 管理员 | 13800000000 | 1 | 1 | BCrypt 密文（60 字符，`$2a$10$` 开头），明文是 `123456` |

---

## 5. 已完成（可运行、已验证）

1. **统一返回体** `Result<T>`：code / message / data + `success()` / `error()` 静态方法，带 Lombok `@Data`。
2. **全局异常处理** `GlobalExceptionHandler`，四个分支：
   - `BusinessException` → 业务异常，按异常自带的 code 返回
   - `MethodArgumentNotValidException` → `@Valid` 校验失败，返回 400 + 具体提示
   - `HttpMessageNotReadableException` → 请求体缺失或 JSON 格式错，返回 400
   - `Exception` → 兜底，记 error 日志，返回 500「服务器繁忙」
3. **登录接口** `POST /api/user/login`
   - 入参 `UserLoginDTO`（`@NotBlank` 校验），支持**用户名或手机号**登录
   - 校验顺序：查不到用户 → 账号被禁用 → 密码不匹配
   - 密码用 `passwordEncoder.matches(明文, 数据库密文)` 校验（BCrypt）
   - 返回 `UserVO`（不含 password 字段）
4. **密码加密**：`PasswordConfig` 注册 `PasswordEncoder` Bean，实现类是 `BCryptPasswordEncoder`。
5. **接口安全**：`SecurityConfig` 放行 `/` 和 `/user/login`，其余请求需要认证；关闭 CSRF，会话策略 STATELESS。
6. **.gitignore**：忽略 `backend/target/`、`backend/logs/`、`backend/.idea/`。

---

## 6. 待办

按建议的先后顺序：

- [ ] **注册接口** `POST /api/user/register`：DTO 校验 → 用户名/手机号查重 → `passwordEncoder.encode()` 加密 → 入库
- [ ] **登录凭证**：目前登录成功只返回用户信息，没有 token / 登录态。后续再决定用 JWT 还是 Redis 存 session（分布式还没学，可以先做单机版）
- [ ] **前端登录页**（正在进行）：调 `POST /api/user/login`，处理 code != 200 的提示
- [ ] 其余 4 个模块的 CRUD：楼栋、房屋、业主、业主-房屋关系
- [ ] 补测试：`AdminApplicationTests` 目前是空的，可以考虑用 MockMvc 写接口测试
- [ ] （可选）把 `User` 实体里的类型/状态转换方法挪到 Service 或枚举里，实体只放数据、不放业务

---

## 7. 踩过的坑（重要，避免重复踩）

**Spring / MyBatis-Plus**

1. MyBatis-Plus 必须用 `mybatis-plus-spring-boot3-starter`。用 Boot 2 时代的 `mybatis-plus-boot-starter` 会启动失败（找不到 SqlSessionFactory），因为 Boot 3 不再读 `spring.factories`。
2. `spring.datasource.type` 写了不生效：dynamic-datasource 会抢先创建 DataSource。连接池类型要写在各数据源下面（`spring.datasource.dynamic.datasource.master.type`）。
3. Jackson 序列化靠 **getter**，只有私有字段没有 getter 的话，接口会返回 `{}`。所以 `Result` 必须加 `@Data`。
4. 数据库下划线字段 → Java 驼峰属性，靠 `map-underscore-to-camel-case: true`；没开会输出一堆 null。
5. `selectOne` 的语义是「最多一条」，查到 2 条以上直接抛 `TooManyResultsException`。只有字段有唯一约束时才安全。

**参数校验**

6. DTO 上写了 `@NotBlank` 不等于生效，Controller 参数上必须加 `@Valid`。
7. 请求体**完全为空**时，先抛的是 `HttpMessageNotReadableException`（连 DTO 都没造出来，轮不到 `@Valid`），已单独处理成 400。

**Spring Security**

8. 给官方 starter 手写 `<version>` 会踩版本冲突：`spring-boot-starter-security:4.1.1` 和 Boot 3.5.14 同时存在，两个不同包下的 `SecurityFilterAutoConfiguration` 抢注同一个 Bean，启动报 `BeanDefinitionOverrideException`。**官方 starter 一律不写 version**，交给父工程管理。
9. `@Bean` 方法名默认就是 Bean 名。方法取名 `springSecurityFilterChain` 会撞上框架内部的同名 Bean，要改叫 `filterChain`。
10. Spring Security 路径匹配**不包含 context-path**：项目配了 `/api`，安全规则里要写 `/user/login`，不是 `/api/user/login`。
11. 启动日志里那行 `Using generated security password: xxx` 是 Spring Boot 自动生成的临时内存用户（用户名 `user`），不是数据库里的用户。
12. 网上让你加 `spring.main.allow-bean-definition-overriding=true` 来「解决」Bean 冲突的建议不要照做，那是把冲突藏起来，不是解决。

**Git**

13. GitHub 开了邮箱隐私保护时，推送真实邮箱的提交会被拒（`GH007: Your push would publish a private email address`）。本仓库已改用 noreply 邮箱，配置只写在本仓库的 `.git/config` 里。
14. 第一次提交时误把 `backend/target/` 编译产物提交进了仓库，后来用 `.gitignore` + `git rm -r --cached` 摘掉。**提交前先 `git status` 看一遍**。

---

## 8. 本项目约定（同 AGENTS.md，摘要）

1. 未经本人明确同意，禁止修改、新增、删除任何项目文件，AI 只能指出问题并说明原因。
2. 需要改代码时，AI 必须先书面申请：**改哪个文件（完整路径+行号）、改成什么（改前改后对比）、为什么改（官方文档/实测输出/报错原文）**，等本人回复「可以 / 改吧 / 同意」才动手。一次同意只对当次那一项有效。
3. 不擅自重命名包名/类名/文件名，不顺手格式化、重构。
4. 不要删除自认为没用的文件（如 `static/`、`templates/`、`mvnw`、`HELP.md`），先问。
5. 临时验证用的探针代码也要先申请，用完必须删除并说明。
6. 可以直接做：查看/搜索文件、只读 SQL（SELECT/SHOW/DESC）、编译校验。
7. 任何写操作都要先经同意：写文件、改文件、删文件，以及 INSERT/UPDATE/DELETE/ALTER/DROP。
8. 回答用中文，讲「为什么」，给结论时带依据。

---

## 9. Git 提交记录

```
2b629f2 实现登录密码密文校验与接口路径安全校验
270c8b5 添加 .gitignore，停止跟踪编译产物与日志
d8c2a02 实现用户登录接口，搭建统一返回与异常处理
d41a8e1 创建项目，搭了一个框架，还没开始写
```

提交邮箱：`shankong <286970053+shankong@users.noreply.github.com>`（本仓库局部配置）

---

## 10. 更新日志

- 2026-10-14：搭好 Maven/MyBatis-Plus/多数据源骨架；实现统一返回体 `Result`、业务异常与全局异常处理、登录接口；数据库表结构确认。
- 2026-10-15：登录密码改为 BCrypt 密文校验；引入 Spring Security 做接口路径安全控制；添加 `.gitignore`；建立本进度文档。
