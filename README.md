<a href="https://github.com/jayllfilebyte/GongYun-for-Android/blob/main/README.EN.md">_**Click HERE to view in English**_</a>

<div align="center">

<img src="https://github.com/founchoo/GongYun-for-Android/assets/24630338/3d5c2914-0592-4058-9b54-00e958c62b63" alt="Logo" width="100">

# 工韵
</div>

## 简介

欢迎来到**工韵**的 GitHub 开源页面, 这是一款为**湖工大**学生打造的第三方教务管理系统，运行于 **Android** 平台，支持的最低版本为 **Android 8.0 (API level 26)**

本应用基于 Jetpack Compose 框架开发，使用 Kotlin 语言编写。

本应用运行过程中会在本地**加密**存储用户敏感数据（如：学号、密码、入学年份、当前学年学期）。这些信息仅用于网络请求的发送，不会被用于其他用途。

存储学号和密码的原因如下：教务系统进行登录请求时即使勾选了记住我的选项，接收到的网络返回中的 cookie 到期日期仅为登录时刻之后的两小时，为了避免用户在间隔两小时之后使用本应用时需要重复输入学号和密码进行登录获取登录状态，本应用将它们存储在本地，当网络请求由于登录状态失效而失败时，本应用将会自动为用户进行登录操作。

## 功能一览

目前，该应用提供如下功能：

1. 课程相关
   - 查看各个学年及周数的课表
   - 提供桌面小组件显示当天课表
   - 查看课表备注信息（如网课、课设）
   - 检索教师课表
   - 课前提醒
   - 查看计划课程
   - 查找同期空闲教室及授课教室

2. 成绩相关
   - 排名查询（包括年级、专业、班级排名）
   - 排名柱状图
   - 绩点及算术平均分的自动计算
   - 绩点变化曲线查看
   - 成绩筛选（学年、课程类型）
   - 成绩排序（分数、学分）
   - 成绩分布柱状图
   - 新成绩提醒

## 屏幕截图

注意：考虑到隐私问题，我们已星号处理某些信息，当您实际正常使用该应用时，不会出现该现象。

<img src="https://github.com/founchoo/GongYun-for-Android/assets/24630338/e0385777-f842-42b4-b20b-5bfa177dd3a3">

## 下载

GitHub release: [点此](https://github.com/founchoo/CampusHelper/releases/latest)链接跳转到下载页面，页面下方的 `.apk` 文件即是安装包，下载安装即可。

Google Play Store: [点此前往谷歌应用商店下载](https://play.google.com/store/apps/details?id=com.dart.campushelper)

## 参与开发

发现 bug？想要新功能？创建一个 issue 阐述你当前遇到的问题；已经写好了代码，希望与主分支合并？请创建 pull request。

我们期待您的贡献，期望将此项目继续延续下去。

## 引用及参考

- https://stackoverflow.com/

- https://m3.material.io/

- https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary

- https://google.github.io/accompanist/placeholder/

- https://json2kt.com/

- https://github.com/harmittaa/KoinExample

- https://github.com/patrykandpatrick/vico

- https://plugins.jetbrains.com/plugin/18619-svg-to-compose

- https://github.com/osipxd/encrypted-datastore


## 开源协议

本项目使用 Apache License 2.0
