package com.driverskr.lib.dialog;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;
import androidx.viewbinding.ViewBinding;

import com.driverskr.lib.R;

/**
 * @Author: driverSkr
 * @Time: 2023/11/15 10:36
 * @Description: dialog$
 */
public abstract class BaseDialog<T extends ViewBinding> extends AppCompatDialog implements DialogInit<T> {

    private int mGravity = Gravity.CENTER;

    private float widthWeight = 0f;
    private float heightWeight = 0f;

    protected T mBinding;

    public BaseDialog(Context context) {
        this(context, 0, 0);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BaseDialog(Context context, float widthWeight, float heightWeight) {
        this(context, Gravity.CENTER, widthWeight, heightWeight);
    }

    public BaseDialog(Context context, int gravity, float widthWeight, float heightWeight) {
        super(context, R.style.BaseDialogTheme);
        init(gravity, widthWeight, heightWeight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDialog();
        initView();
        // 必须放在这里,不然通过构造方法传过去的之在该方法之后接收到
        //initData();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 初始化
     */
    private void init(int gravity, float widthWeight, float heightWeight) {
        mBinding = bindView();

        //setContentView(LayoutInflater.from(getContext()).inflate(getLayout(), null));
        setContentView(mBinding.getRoot());

        mGravity = gravity;

        if (widthWeight <= 1f) {
            this.widthWeight = widthWeight;
        }

        if (heightWeight <= 1f) {
            this.heightWeight = heightWeight;
        }
    }

    /**
     * dialog初始化
     */
    private void initDialog() {
        // 设置宽度为屏宽、位置靠近屏幕底部
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager windowManager = window.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        //window.setBackgroundDrawableResource(R.color.transparent)
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = mGravity;
        Point displaySize = new Point();
        display.getSize(displaySize);

        if (widthWeight > 0f) {
            //设置dialog宽度
            wlp.width = (int) (displaySize.x * widthWeight);
        } else {
            wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        if (heightWeight > 0f) {
            //设置dialog宽度
            wlp.height = (int) (displaySize.y * heightWeight);
        } else {
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        window.setAttributes(wlp);
        mBinding.getRoot().postInvalidate();
    }
}
