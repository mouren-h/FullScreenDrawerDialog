```markdown
# FullScreenDrawerDialog

一个零依赖、纯 Java 实现的 **Android 全屏底部抽屉弹窗**。支持加载任意 XML 布局，下拉拖拽关闭，淡入淡出动画，通过回调即可拿到布局内任意子控件实例并进行控制。

> A zero-dependency Android full-screen bottom sheet dialog written in pure Java. Load any XML layout, drag down to dismiss, with smooth fade in/out animations.

---

## ✨ 特性

- 📄 **加载任意 XML 布局**：`show(activity, R.layout.xxx, callback)` 一行搞定
- 🔌 **回调获取实例**：`onViewCreated` 里直接 `findViewById` 拿到任意子控件，设置文字、绑定点击事件等
- 🎬 **淡入淡出动画**：打开时从底部升起 + 透明度 0→1 慢慢显示；关闭时淡出 + 下滑后再真正 dismiss
- 👆 **下拉拖拽关闭**：顶部横条可拖拽，拖动时面板跟随手指并同步变透明，松手超过阈值自动关闭，否则回弹
- 🎯 **关闭动画统一**：点击按钮关闭、下拉关闭、代码关闭，全部走同一套淡出下滑动画
- 🧩 **零依赖**：不依赖 Material / AppCompat / BottomSheetDialog，纯原生 `Dialog` + `FrameLayout` 实现
- 🛡 **防重复关闭**：内部 `isShowing()` 判断，避免重复 `dismiss()` 崩溃

---

## 📱 效果

| 打开 | 下拉拖拽 | 关闭 |
| :--: | :--: | :--: |
| 从底部升起 + 淡入 | 跟随手指 + 逐渐变透明 | 淡出 + 下滑后关闭 |

> 建议在仓库里放一张/几张 GIF 效果图，替换上面这行说明。

---

## 🚀 快速开始

### 1. 引入代码

把 `FullScreenDrawerDialog.java` 复制到你的项目里（例如 `com.your.package` 下）。

> ⚠️ 注意：源码顶部的 `package` 需要改成你项目实际的包名。

### 2. 调用

```java
FullScreenDrawerDialog.show(this, R.layout.home, new FullScreenDrawerDialog.Callback() {
    @Override
    public void onViewCreated(View content, FullScreenDrawerDialog.Controller controller) {
        Button btn = content.findViewById(R.id.an1);
        btn.setText("关闭");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭弹窗（自动带淡出 + 下滑动画）
                controller.dismiss();
            }
        });
    }
});
```

---

📚 API 说明

FullScreenDrawerDialog.show(Activity activity, int layoutResId, Callback callback)

参数 类型 说明
activity Activity 上下文，用于创建 Dialog 和 inflate 布局
layoutResId int 要加载的 XML 布局资源 ID，如 R.layout.home
callback Callback 布局加载完成回调，可在此获取子控件实例并设置属性、绑定事件

Callback

```java
public interface Callback {
    void onViewCreated(View content, Controller controller);
}
```

参数 类型 说明
content View 加载的 XML 布局根 View，用 findViewById 获取任意子控件
controller Controller 弹窗控制器，可关闭弹窗

回调在 dialog.show() 之前触发，保证弹窗显示前完成控件初始化，不会闪一下。

Controller

方法 说明
dismiss() 带动画关闭弹窗（淡出 + 下滑，动画结束后才真正 dismiss）
dismissImmediately() 立即关闭弹窗，无动画
getDialog() 获取底层 Dialog 对象（一般不需要，除非要操作 Window 等）

---

🎨 自定义

面板圆角 / 背景色

在 panelWrapper 的 GradientDrawable 处修改：

```java
GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFFFFFFFF); // 背景色
bg.setCornerRadii(new float[]{
    22 * d, 22 * d, 22 * d, 22 * d, // 左上、右上圆角
    0f, 0f, 0f, 0f                  // 左下、右下圆角
});
panelWrapper.setBackground(bg);
```

动画时长

```java
private static final int DURATION_IN = 320;   // 打开动画时长（ms）
private static final int DURATION_OUT = 260;  // 关闭动画时长（ms）
```

遮罩透明度

```java
win.setDimAmount(0.5f); // 0f ~ 1f
```

关闭阈值

下拉超过屏幕高度的比例即触发关闭，默认 20%：

```java
if (ty > screenH * 0.2f) { ... }
```

---

⚠️ 注意事项

1. 包名：源码顶部 package com.ic0868cc7f7e397aa; 请替换为你自己的包名，或直接删除 package 行放入同包目录。
2. 布局高度：XML 布局作为抽屉内容被填入 MATCH_PARENT 的外层容器，建议内部使用 ScrollView / RecyclerView 等可滚动控件承载长内容。
3. 顶部横条遮挡：拖拽横条覆盖在布局顶部 26dp 区域，建议 XML 布局顶部留出约 26dp 的内边距，避免内容被横条遮挡。
4. Activity 生命周期：请在 Activity 存活期间调用，避免 Activity 销毁后弹窗泄漏。

---

📋 环境要求

项目 要求
平台 Android
语言 Java
最低 SDK API 21（Android 5.0）
依赖 无（仅使用 Android 原生 API）

---

📄 License

MIT License

---

⭐ Star

如果这个项目对你有帮助，欢迎点个 Star ⭐ 支持一下～

```

---

## 额外建议

**1. 仓库结构建议：**
```

FullScreenDrawerDialog/
├── README.md
├── LICENSE              # MIT
├── FullScreenDrawerDialog.java
└── screenshots/
├── open.gif
└── close.gif

```

**2. 上传前记得处理：**
- 源码里 `package com.ic0868cc7f7e397aa;` 建议改成 `package com.yourname.drawer;` 或删除，README 里也提醒了使用者自行修改
- 把最终版 `FullScreenDrawerDialog.java` 单独放仓库根目录（或 `library/` 目录）方便别人直接复制
- 放 1~2 张 GIF 效果图到 `screenshots/`，README 效果最强

**3. LICENSE 文件（MIT，直接复制）：**

```text
MIT License

Copyright (c) 2025 Your Name

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
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```