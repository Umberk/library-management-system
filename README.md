# 📚 图书管理系统 (Library Management System)

> 基于SpringBoot + MyBatis的图书管理系统，实现图书管理、借阅管理、用户权限控制等核心功能。

[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.3.1-brightgreen)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-2.1.3-red)](https://mybatis.org/mybatis-3/)
[![MySQL](https://img.shields.io/badge/MySQL-5.7+-blue)](https://www.mysql.com/)

---

## 📝 项目简介

本系统采用SpringBoot + MyBatis架构，实现了完整的图书管理业务流程。系统分为管理员端和普通用户端，支持图书的增删改查、借阅归还、用户权限管理等功能。

## ✨ 功能特性

### 管理员功能
- 📖 图书管理：添加、编辑、删除、查询图书
- 📋 借阅管理：查看所有借阅记录、处理归还
- 👥 用户管理：管理用户信息、设置借阅权限
- 📢 公告管理：发布系统公告

### 普通用户功能
- 🔍 图书浏览：查看图书列表、搜索图书
- 📚 图书借阅：在线借阅图书
- 📤 归还图书：归还已借图书
- 📊 借阅记录：查看个人借阅历史
- ⚙️ 个人中心：修改个人信息

## 🔧 技术栈

**后端**：
- SpringBoot 2.3.1
- MyBatis 2.1.3
- Spring Security
- MySQL 5.7+
- Swagger2

**前端**：
- Thymeleaf
- jQuery
- JavaEx UI

## 🗄️ 数据库设计

| 表名 | 说明 |
|------|------|
| `users` | 用户信息表 |
| `book` | 图书信息表 |
| `borrow` | 借阅记录表 |
| `notice` | 公告信息表 |

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Umberk/library-management-system.git
cd library-management-system
```

### 2. 创建数据库

```sql
CREATE DATABASE book CHARACTER SET utf8;
```

### 3. 导入数据

在项目根目录（如果包含book.sql）或从原项目获取数据库脚本：
```bash
mysql -u root -p book < book.sql
```

### 4. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root      # 你的MySQL用户名
    password: root      # 你的MySQL密码
```

**同时修改** `src/main/java/com/cya/util/JdbcUtil.java`：
```java
public static final String PASS = "root";  // 改成你的MySQL密码
```

### 5. 运行项目

```bash
# 使用Maven
mvn spring-boot:run

# 或使用IDE
# 直接运行 ManagerApplication.java
```

### 6. 访问系统

- 主页：http://127.0.0.1:8080/booksManageBoot
- API文档：http://127.0.0.1:8080/booksManageBoot/doc.html

## 👤 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin |
| 普通用户 | user1 | user1 |
| 普通用户 | user2 | user2 |

## 📁 项目结构

```
src/
├── main/java/com/cya/
│   ├── config/         # 配置类（Security、Swagger）
│   ├── controller/     # 控制层
│   ├── service/        # 业务层
│   ├── dao/            # 数据访问层
│   ├── entity/         # 实体类
│   └── util/           # 工具类
└── main/resources/
    ├── mapper/         # MyBatis映射文件
    ├── static/         # 静态资源
    ├── templates/      # 页面模板
    └── application.yml # 配置文件
```

## 🎯 核心功能实现

- **MVC架构**：Controller → Service → Dao 三层分离
- **权限控制**：Spring Security实现角色权限管理
- **数据持久化**：MyBatis + MySQL
- **API文档**：Swagger2自动生成接口文档
- **分页查询**：PageHelper分页插件

## 🐳 Docker部署

```bash
# 构建镜像
docker build -t books-manage .

# 使用Docker Compose启动
docker-compose up -d
```

## 📄 开源协议

MIT License

## 📞 联系方式

- 作者：Umberk
- GitHub：https://github.com/Umberk

---

⭐ 如果这个项目对你有帮助，欢迎Star支持！