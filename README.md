✨ FullScreenDrawerDialog

<p align="center">
  <b>一个零依赖、纯 Java 实现的 Android 全屏底部抽屉弹窗</b>
</p><p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android" />
  <img src="https://img.shields.io/badge/Language-Java-orange?style=flat-square&logo=java" />
  <img src="https://img.shields.io/badge/Min%20SDK-21-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/License-MIT-purple?style=flat-square" />
  <img src="https://jitpack.io/v/mouren-h/FullScreenDrawerDialog.svg" />
</p><p align="center">
  支持任意 XML 布局、下拉拖拽关闭、流畅动画以及灵活的控件回调。
</p>---

📖 项目简介

FullScreenDrawerDialog 是一个轻量级 Android 全屏底部抽屉弹窗组件。

采用纯 Java + Android 原生 API 实现，不依赖 Material、AppCompat 或 BottomSheetDialog。

只需一行代码即可加载任意 XML 布局，并通过回调获取布局内的任意控件实例。

无论是设置文字、绑定点击事件，还是动态修改控件属性，都可以轻松完成。

✨ 功能特性

功能| 描述
📄 XML 布局加载| 支持加载任意自定义 XML 布局
🔌 控件回调| 通过 "onViewCreated" 获取任意子控件
🎬 流畅动画| 打开淡入上滑，关闭淡出下滑
👆 手势拖拽| 支持向下拖动关闭弹窗
🔄 拖动反馈| 拖动过程中面板跟随手指并同步改变透明度
↩️ 自动回弹| 未达到关闭阈值时自动回到原位
🎯 统一关闭| 点击关闭、拖拽关闭、代码关闭统一使用关闭动画
🛡 防重复关闭| 避免重复执行 dismiss 导致异常
🧩 零依赖| 仅使用 Android 原生 API

📱 效果展示

打开弹窗| 下拉拖拽| 关闭弹窗
从底部滑入| 跟随手指移动| 淡出并下滑

«可以在 "screenshots/" 目录放置实际运行效果 GIF，展示更加直观。»

---

🚀 快速开始

1. 添加 JitPack 仓库

在项目根目录的 "settings.gradle" 中添加：

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

2. 添加项目依赖

在模块级 "build.gradle" 中添加：

dependencies {
    implementation 'com.github.mouren-h:FullScreenDrawerDialog:1.0'
}

3. 开始使用

在 Java 文件中导入：

import com.ic0868cc7f7e397aa.FullScreenDrawerDialog;

调用示例：

FullScreenDrawerDialog.show(this, R.layout.home,
    new FullScreenDrawerDialog.Callback() {

        @Override
        public void onViewCreated(
                View content,
                FullScreenDrawerDialog.Controller controller) {

            Button btn = content.findViewById(R.id.an1);

            btn.setText("关闭弹窗");

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 执行关闭动画
                    controller.dismiss();

                }
            });
        }
    });

这样就可以完成弹窗的创建、布局加载和控件操作。

---

📚 API 文档

show()

创建并显示底部抽屉弹窗。

FullScreenDrawerDialog.show(
    Activity activity,
    int layoutResId,
    Callback callback
);

参数| 类型| 说明
activity| Activity| 当前 Activity
layoutResId| int| XML 布局资源 ID
callback| Callback| 布局加载完成回调

Callback

public interface Callback {
    void onViewCreated(
        View content,
        Controller controller
    );
}

参数| 类型| 说明
content| View| XML 布局根 View，可获取任意子控件
controller| Controller| 弹窗控制器

注意： 回调在 "dialog.show()" 之前执行，因此可以提前完成控件初始化。

Controller

弹窗控制器提供以下方法：

方法| 说明
"dismiss()"| 带动画关闭弹窗
"dismissImmediately()"| 立即关闭，不播放动画
"getDialog()"| 获取底层 Dialog 对象

---

🎨 自定义配置

1. 修改面板圆角和背景颜色

找到源码中的 "panelWrapper" 背景设置位置：

GradientDrawable bg = new GradientDrawable();

bg.setColor(0xFFFFFFFF);

bg.setCornerRadii(new float[]{
    22 * d, 22 * d,
    22 * d, 22 * d,
    0f, 0f,
    0f, 0f
});

panelWrapper.setBackground(bg);

2. 修改动画时长

private static final int DURATION_IN = 320;
private static final int DURATION_OUT = 260;

参数| 默认值| 说明
DURATION_IN| 320ms| 打开动画
DURATION_OUT| 260ms| 关闭动画

3. 修改背景遮罩透明度

win.setDimAmount(0.5f);

取值范围：

- "0.0f"：完全透明
- "0.5f"：半透明
- "1.0f"：完全不透明

4. 修改下拉关闭阈值

默认下拉超过屏幕高度的 20% 即触发关闭：

if (ty > screenH * 0.2f) {
    // 执行关闭
}

可以根据实际需求修改比例。

---

⚠️ 注意事项

1. 包名配置： 如果直接复制源码，请将 Java 文件顶部的 package 修改为自己的项目包名，并同步修改导入路径。
2. 布局高度： XML 布局会被放入 MATCH_PARENT 的外层容器，建议根据实际需求使用 ScrollView 或 RecyclerView。
3. 顶部拖拽区域： 顶部横条覆盖布局顶部约 26dp 区域，建议为 XML 内容预留对应空间。
4. Activity 生命周期： 请在 Activity 正常存活期间调用，避免 Activity 销毁后出现窗口泄漏。
5. 依赖版本： 请使用 JitPack 上实际发布的版本。

---

📋 环境要求

项目| 要求
开发平台| Android
开发语言| Java
最低 SDK| API 21（Android 5.0）
实现方式| Android 原生 Dialog + FrameLayout
第三方依赖| 无

---

📁 项目结构

FullScreenDrawerDialog/
│
├── README.md
├── LICENSE
│
├── FullScreenDrawerDialog.java
│
└── screenshots/
    ├── open.gif
    └── close.gif

---

📄 License

本项目采用 MIT License 开源协议。

MIT License

Copyright (c) 2025 mouren-h

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.

---

⭐ Star

如果这个项目对你有帮助，欢迎点个 Star ⭐ 支持一下！

<p align="center">
  Made with ❤️ by mouren-h
</p>