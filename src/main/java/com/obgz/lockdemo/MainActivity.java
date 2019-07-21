package com.obgz.lockdemo;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.obgz.lockdemo.databinding.MainActBinding;
import com.onbright.oblink.cloud.ObInit;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private MainActBinding mainActBinding;
    private ObInit obInit;
    /**
     * 是否初始化成功,只有初始化成功才能进行其他任何操作，否则可能出现不可预知错误
     */
    private boolean iSInitSuc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActBinding = DataBindingUtil.setContentView(this, R.layout.main_act);
        obInit = new ObInit("Tencent", "Tencent", "uniqueKey", this) {
            @Override
            public void onInitSuc() {
                iSInitSuc = true;
            }

            @Override
            public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                switch (errorCode) {
                    case exceptionError:
                        showToast("网络异常，请检查网络");
                        break;
                    case responseNotOk:
                        showToast("请求失败，请稍后再试或联系工程师");
                        break;
                    case operationFailed:
                        showToast("业务未得到正确处理，请检查逻辑" + s1);
                        break;
                }
            }
        };
        mainActBinding.obinitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSInitSuc) {
                    return;
                }
                obInit.init();
            }
        });
        disAbleBtn();
    }

    /**
     * 未初始化，按钮不可被点击
     */
    private void disAbleBtn() {
        mainActBinding.addOboxBtn.setEnabled(false);
        mainActBinding.deleteOboxBtn.setEnabled(false);
        mainActBinding.addLockBtn.setEnabled(false);
        mainActBinding.deleteLockBtn.setEnabled(false);
        mainActBinding.sendCoercionValidatecodeUserBtn.setEnabled(false);
        mainActBinding.modifyUserBtn.setEnabled(false);
        mainActBinding.validateAdminPwdBtn.setEnabled(false);
        mainActBinding.forgetAdminPwdBtn.setEnabled(false);
        mainActBinding.resetAdminPwdBycodeBtn.setEnabled(false);
        mainActBinding.modifyAdminPwdBycodeBtn.setEnabled(false);
        mainActBinding.queryTempUserBtn.setEnabled(false);
        mainActBinding.addTempUserBtn.setEnabled(false);
        mainActBinding.deleteTempUserBtn.setEnabled(false);
        mainActBinding.modifyTempUserBtn.setEnabled(false);
        mainActBinding.sendCodeToTempUserBtn.setEnabled(false);
        mainActBinding.queryPushListBtn.setEnabled(false);
        mainActBinding.modifyPushListBtn.setEnabled(false);
        mainActBinding.finishBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (!iSInitSuc) {
            showToast("请先进行一次成功的初始化操作");
            return;
        }
        switch (v.getId()) {
        }
    }

    /**
     * 显示提示信息
     *
     * @param showMsg 信息
     */
    private void showToast(String showMsg) {
        Toast.makeText(MainActivity.this, showMsg, Toast.LENGTH_SHORT).show();
    }


}
