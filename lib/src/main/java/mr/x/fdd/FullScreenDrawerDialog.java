package mr.x.fdd;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FullScreenDrawerDialog {

    /**
     * 弹窗控制器，供回调中关闭弹窗（带下滑动画）
     */
    public interface Controller {
        /**
         * 带动画关闭弹窗（从上往下滑出，滑完再真正 dismiss）
         */
        void dismiss();

        /**
         * 立即关闭弹窗（无动画）
         */
        void dismissImmediately();

        /**
         * 获取弹窗对象（一般不需要，除非要操作Window等）
         */
        Dialog getDialog();
    }

    /**
     * 回调接口
     */
    public interface Callback {
        /**
         * 布局加载完成，可在此获取子控件实例、设置属性、绑定事件
         *
         * @param content    加载的XML布局根View
         * @param controller 弹窗控制器，调用 dismiss() 可带动画关闭
         */
        void onViewCreated(View content, Controller controller);
    }

    private static final int DURATION_IN = 320;
    private static final int DURATION_OUT = 260;

    /**
     * 显示全屏抽屉弹窗（加载XML布局，带回调）
     *
     * @param activity    上下文Activity
     * @param layoutResId 要加载的布局文件资源ID，如 R.layout.xxx
     * @param callback    布局加载完成回调，可获取子控件实例并控制属性
     */
    public static void show(final Activity activity, final int layoutResId, final Callback callback) {
        try {
            final float d = activity.getResources().getDisplayMetrics().density;
            final int screenH = activity.getResources().getDisplayMetrics().heightPixels;

            final Dialog dlg = new Dialog(activity);
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.setCanceledOnTouchOutside(true);

            // 根容器
            final FrameLayout rootBox = new FrameLayout(activity);

            // 外层容器：横条 + XML内容都放在这里，整体一起平移，避免横条残留
            final FrameLayout panelWrapper = new FrameLayout(activity);
            panelWrapper.setClickable(true);

            // 圆角背景放在外层容器上（白色 + 顶部圆角）
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(0xFFFFFFFF);
            bg.setCornerRadii(new float[]{22 * d, 22 * d, 22 * d, 22 * d, 0f, 0f, 0f, 0f});
            panelWrapper.setBackground(bg);

            // 初始透明 + 完全在屏幕下方，配合入场动画实现"慢慢显示"
            panelWrapper.setAlpha(0f);

            // 加载XML布局，填入外层容器
            final View content = activity.getLayoutInflater().inflate(layoutResId, panelWrapper, false);
            panelWrapper.addView(content, new FrameLayout.LayoutParams(-1, -1));

            // 顶部拖拽横条（覆盖在XML布局顶部区域，用于下拉关闭）
            FrameLayout handleBox = new FrameLayout(activity);
            FrameLayout.LayoutParams lpHandleBox = new FrameLayout.LayoutParams(-1, (int) (26 * d));
            lpHandleBox.gravity = Gravity.TOP;
            panelWrapper.addView(handleBox, lpHandleBox);

            View handle = new View(activity);
            GradientDrawable hb = new GradientDrawable();
            hb.setColor(0xFFD9DDE3);
            hb.setCornerRadius(3 * d);
            handle.setBackground(hb);
            FrameLayout.LayoutParams lpH = new FrameLayout.LayoutParams((int) (40 * d), (int) (5 * d));
            lpH.gravity = Gravity.CENTER;
            handleBox.addView(handle, lpH);

            // 外层容器添加到根容器，底部对齐
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -1);
            lp.gravity = Gravity.BOTTOM;
            rootBox.addView(panelWrapper, lp);

            // 统一的带动画关闭逻辑：先慢慢淡出 + 下滑，动画结束后才真正 dismiss
            final Runnable dismissWithAnimation = new Runnable() {
                @Override
                public void run() {
                    panelWrapper.animate()
                            .translationY(screenH)
                            .alpha(0f)
                            .setDuration(DURATION_OUT)
                            .start();
                    panelWrapper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dlg.isShowing()) {
                                dlg.dismiss();
                            }
                        }
                    }, DURATION_OUT - 10);
                }
            };

            // 下拉拖拽关闭逻辑（操作 panelWrapper，横条随面板一起移动）
            handleBox.setOnTouchListener(new View.OnTouchListener() {
                float downRaw = 0f;
                float startTy = 0f;

                @Override
                public boolean onTouch(View view, MotionEvent ev) {
                    int act = ev.getAction();
                    if (act == MotionEvent.ACTION_DOWN) {
                        downRaw = ev.getRawY();
                        startTy = panelWrapper.getTranslationY();
                        return true;
                    }
                    if (act == MotionEvent.ACTION_MOVE) {
                        float ty = startTy + (ev.getRawY() - downRaw);
                        panelWrapper.setTranslationY(Math.max(0f, ty));
                        // 下拉时同步降低透明度，越往下越透明
                        float progress = Math.min(1f, Math.max(0f, ty) / screenH);
                        panelWrapper.setAlpha(1f - progress);
                        return true;
                    }
                    if (act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL) {
                        float ty = panelWrapper.getTranslationY();
                        if (ty > screenH * 0.2f) {
                            dismissWithAnimation.run();
                        } else {
                            // 回弹：位置和透明度一起恢复
                            panelWrapper.animate()
                                    .translationY(0f)
                                    .alpha(1f)
                                    .setDuration(160)
                                    .start();
                        }
                        return true;
                    }
                    return false;
                }
            });

            dlg.setContentView(rootBox, new ViewGroup.LayoutParams(-1, -1));
            Window win = dlg.getWindow();
            if (win != null) {
                win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                win.setLayout(-1, -1);
                win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                win.setDimAmount(0.5f);
            }

            // 控制器：对外暴露带动画关闭
            final Controller controller = new Controller() {
                @Override
                public void dismiss() {
                    dismissWithAnimation.run();
                }

                @Override
                public void dismissImmediately() {
                    if (dlg.isShowing()) {
                        dlg.dismiss();
                    }
                }

                @Override
                public Dialog getDialog() {
                    return dlg;
                }
            };

            // 在show之前回调，方便调用方在弹窗显示前初始化控件
            if (callback != null) {
                callback.onViewCreated(content, controller);
            }

            dlg.show();

            // 入场动画：从底部升起 + 慢慢显示（透明度 0→1）
            panelWrapper.setTranslationY(screenH);
            panelWrapper.setAlpha(0f);
            panelWrapper.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(DURATION_IN)
                    .start();

        } catch (Throwable t) {
            Toast.makeText(activity, "弹窗显示失败", Toast.LENGTH_SHORT).show();
        }
    }
}