package com.obgz.lockdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obgz.lockdemo.databinding.MainActBinding;
import com.onbright.oblink.Obox;
import com.onbright.oblink.cloud.ObInit;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.LockAlarm;
import com.onbright.oblink.cloud.bean.LockHistory;
import com.onbright.oblink.cloud.bean.LockPush;
import com.onbright.oblink.cloud.bean.LockTempUser;
import com.onbright.oblink.cloud.bean.LockUser;
import com.onbright.oblink.cloud.handler.ConnectHandler;
import com.onbright.oblink.cloud.handler.OboxHandler;
import com.onbright.oblink.cloud.handler.SmartLockHotelHandler;
import com.onbright.oblink.cloud.net.HttpRespond;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private MainActBinding mainActBinding;
    /**
     * 初始化
     */
    private ObInit obInit;
    /**
     * 是否初始化成功,只有初始化成功才能进行其他任何操作，否则可能出现不可预知错误
     */
    private boolean iSInitSuc;

    /**
     * 门锁工具类，通过此类执行门锁功能操作
     */
    private SmartLockHotelHandler smartLockHotelHandler;
    /**
     * 用户列表
     */
    private List<LockUser> lockUsers;

    /**
     * 门锁列表
     */
    private List<LockTempUser> lockTempUsers;

    /**
     * 推送选项列表
     */
    private List<LockPush> lockPushes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActBinding = DataBindingUtil.setContentView(this, R.layout.main_act);
        changeBtnAble(false);
        obInit = new ObInit("Tencent", "Tencent", "uniqueKey", this) {
            @Override
            public void onInitSuc() {
                iSInitSuc = true;
                changeBtnAble(true);
            }

            @Override
            public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                onFaildHandle(errorCode, s1);
            }
        };
        mainActBinding.obinitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSInitSuc) {
                    showMsg("请不要重复初始化");
                    return;
                }
                obInit.init();
            }
        });
        smartLockHotelHandler = new SmartLockHotelHandler(getSpString("lockSerId")) {
            @Override
            public void noSerialId() {

            }

            @Override
            protected void lockStatusChange(LockStatusEnum lockStatusEnum) {
                showMsg("锁状态发生变化，请查看参数枚举");
            }

            @Override
            protected void batteryValue(int i) {
                showMsg("当前电量值为：" + i);
            }

            @Override
            public void deleteDeviceSuc() {
                showMsg("成功删除设备");
            }

            @Override
            public void searchNewDeviceSuc() {
                showMsg("成功开启扫描，请等待扫描时传入的时间，单位为秒");
            }

            @Override
            protected void onNewDevice(Device device) {
                showMsg("扫描到新设备,设备名称为：" + device.getName()
                        + "设备序列号为：" + device.getSerialId());
                putSpString("lockSerId", device.getSerialId());
            }

            @Override
            public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                onFaildHandle(errorCode, s1);
            }
        };
    }

    /**
     * 未初始化，按钮不可被点击
     */
    private void changeBtnAble(boolean able) {
        mainActBinding.addOboxBtn.setEnabled(able);
        mainActBinding.deleteOboxBtn.setEnabled(able);
        mainActBinding.addLockBtn.setEnabled(able);
        mainActBinding.deleteLockBtn.setEnabled(able);
        mainActBinding.sendCoercionValidatecodeUserBtn.setEnabled(able);
        mainActBinding.modifyUserBtn.setEnabled(able);
        mainActBinding.validateAdminPwdBtn.setEnabled(able);
        mainActBinding.forgetAdminPwdBtn.setEnabled(able);
        mainActBinding.resetAdminPwdBycodeBtn.setEnabled(able);
        mainActBinding.modifyAdminPwdBtn.setEnabled(able);
        mainActBinding.queryTempUserBtn.setEnabled(able);
        mainActBinding.addTempUserBtn.setEnabled(able);
        mainActBinding.deleteTempUserBtn.setEnabled(able);
        mainActBinding.modifyTempUserBtn.setEnabled(able);
        mainActBinding.sendCodeToTempUserBtn.setEnabled(able);
        mainActBinding.queryPushListBtn.setEnabled(able);
        mainActBinding.modifyPushListBtn.setEnabled(able);
        mainActBinding.finishBtn.setEnabled(able);
        mainActBinding.queryLockOpenRecord.setEnabled(able);
        mainActBinding.queryLockWarnRecord.setEnabled(able);
    }

    @Override
    public void onClick(View v) {
        if (!iSInitSuc) {
            showMsg("请先进行一次成功的初始化操作");
            return;
        }
        switch (v.getId()) {
            case R.id.add_obox_btn:
                ConnectHandler connectHandler = new ConnectHandler(this, "mymm1234", new ConnectHandler.ConnectOboxLsn() {
                    @Override
                    public void error(ConnectHandler.ConnectError connectError) {
                        showMsg("添加obox失败，请参考参数枚举查看失败原因");
                    }

                    @Override
                    public void connectOboxSuc(Obox obox) {
                        showMsg("成功添加obox，obox序列号为：" + obox.getSerialId());
                        putSpString("oboxSerId", obox.getSerialId());
                    }

                    @Override
                    public void connectWifiDeviceSuc(String s) {
                        showMsg("成功添加非obox的wifi设备，设备参数为：" + s);
                    }
                }, true);
                connectHandler.start();
                break;
            case R.id.delete_obox_btn:
                String oboxSerId = getSpString("oboxSerId");
                if (oboxSerId == null) {
                    showMsg("请确保有obox");
                    return;
                }
                OboxHandler oboxHandler = new OboxHandler(oboxSerId) {
                    @Override
                    public void noSerialId() {

                    }

                    @Override
                    protected void oboxDeleteSuc(String oboxSerId) {
                        showMsg("成功删除obox，序列号为：" + oboxSerId);
                        putSpString("oboxSerId", null);
                    }

                    @Override
                    public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                        onFaildHandle(errorCode, s1);
                    }
                };
                oboxHandler.deleteObox();
                break;
            case R.id.add_lock_btn:
                oboxSerId = getSpString("oboxSerId");
                smartLockHotelHandler.searchNewDevice(oboxSerId, "30");
                break;
            case R.id.delete_lock_btn:
                String lockSerId = getSpString("lockSerId");
                if (lockSerId == null) {
                    showMsg("要删除的序号为空，不会执行删除操作");
                    return;
                }
                smartLockHotelHandler.deleteDevice();
                break;
            case R.id.query_user_btn:
                smartLockHotelHandler.queryUser(new SmartLockHotelHandler.queryUserLsn() {
                    @Override
                    public void userRecordLoad(List<LockUser> list) {
                        showMsg("获取用户列表成功");
                        lockUsers = list;
                    }
                });
                break;
            case R.id.send_coercion_validatecode_user_btn:
                if (lockUsers.size() == 0) {
                    showMsg("用户列表为空");
                    return;
                }
                LockUser lockUser = lockUsers.get(0);
                smartLockHotelHandler.sendValidateCode(lockUser, "18666860862", new SmartLockHotelHandler.SendCodeLsn() {
                    @Override
                    public void sendCodeOk() {
                        showMsg("sendCodeOk");
                    }
                });
                break;
            case R.id.modify_user_btn:
                if (lockUsers.size() == 0) {
                    showMsg("用户列表为空");
                    return;
                }
                lockUser = lockUsers.get(0);
                lockUser.setNickName("newNickName");
                smartLockHotelHandler.modifyUser(lockUser, "收到的短信验证码", new SmartLockHotelHandler.ModifyUserLsn() {
                    @Override
                    public void modifyUserOk() {
                        showMsg("modifyUserOk");
                    }
                });
                break;
            case R.id.validate_admin_pwd_btn:
                smartLockHotelHandler.validateAdminPwd("123456", new SmartLockHotelHandler.ValidateAdminPwdLsn() {
                    @Override
                    public void validateAdminPwdOk() {
                        showMsg("validateAdminPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                });
                break;
            case R.id.creat_admin_pwd_btn:
                smartLockHotelHandler.createAdminPwd("123456", new SmartLockHotelHandler.CreatAuthPwdLsn() {
                    @Override
                    public void CreatAuthPwdOk() {
                        showMsg("CreatAuthPwdOk");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("areadyHasAdminPwd");
                    }
                });
                break;
            case R.id.forget_admin_pwd_btn:
                smartLockHotelHandler.forgetAdminPwd(new SmartLockHotelHandler.ForgetPwdLsn() {
                    @Override
                    public void forgetPwdOk() {
                        showMsg("forgetPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                });
                break;
            case R.id.reset_admin_pwd_bycode_btn:
                smartLockHotelHandler.resetAdminPwdByCode("123456", new SmartLockHotelHandler.ResetPwdLsn() {
                    @Override
                    public void waitLockReset() {
                        showMsg("waitLockReset");
                    }

                    @Override
                    public void resetPwdOk() {
                        showMsg("resetPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                });
                break;
            case R.id.modify_admin_pwd_btn:
                smartLockHotelHandler.modifyAdminPwd("123456", "123456", new SmartLockHotelHandler.ModifyPwdLsn() {
                    @Override
                    public void modifyPwdOk() {
                        showMsg("modifyPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                });
                break;
            case R.id.query_temp_user_btn:
                smartLockHotelHandler.queryTemporaryUser(new SmartLockHotelHandler.QueryTemporaryUserLsn() {
                    @Override
                    public void queryTemporaryUserOk(List<LockTempUser> list) {
                        lockTempUsers = list;
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
                break;
            case R.id.add_temp_user_btn:
                LockTempUser addLockTempUser = new LockTempUser();
                smartLockHotelHandler.addTemporaryUser(addLockTempUser, new SmartLockHotelHandler.AddTemporaryUserLsn() {
                    @Override
                    public void addTemporaryUserOk(LockTempUser lockTempUser) {
                        showMsg("addTemporaryUserOk");
                        if (lockTempUsers == null) {
                            lockTempUsers = new ArrayList<>();
                        }
                        lockTempUsers.add(lockTempUser);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
                break;
            case R.id.delete_temp_user_btn:
                if (lockTempUsers.size() == 0) {
                    return;
                }
                smartLockHotelHandler.deleteTemporaryUser(lockTempUsers.get(0), new SmartLockHotelHandler.DeleteTemporaryUserLsn() {
                    @Override
                    public void deleteTemporaryUserOk() {
                        showMsg("deleteTemporaryUserOk");
                        lockTempUsers.remove(0);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
                break;
            case R.id.modify_temp_user_btn:
                if (lockTempUsers.size() == 0) {
                    return;
                }
                final LockTempUser modifyLockTempUser = lockTempUsers.get(0);
                modifyLockTempUser.setNickName("newNickName");
                smartLockHotelHandler.modifyTemporaryUser(modifyLockTempUser, new SmartLockHotelHandler.ModifyTemporaryUserLsn() {
                    @Override
                    public void modifyTemporaryUserOk(LockTempUser lockTempUser) {
                        showMsg("modifyTemporaryUserOk");
                        lockTempUsers.remove(modifyLockTempUser);
                        lockTempUsers.add(0, lockTempUser);
                    }

                    @Override
                    public void temporaryUserExpire() {
                        showMsg("temporaryUserExpire");
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
                break;
            case R.id.send_code_to_temp_user_btn:
                if (lockTempUsers.size() == 0) {
                    return;
                }
                LockTempUser sendPwdTempUser = lockTempUsers.get(0);
                smartLockHotelHandler.sendTemporaryUserPwd(sendPwdTempUser, new SmartLockHotelHandler.SendTemporaryUserPwdLsn() {
                    @Override
                    public void sendTemporaryUserPwdOk() {
                        showMsg("sendTemporaryUserPwdOk");
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
                break;
            case R.id.query_push_list_btn:
                smartLockHotelHandler.queryPush(new SmartLockHotelHandler.QueryPushLsn() {
                    @Override
                    public void queryPushOk(String s, List<LockPush> list) {
                        showMsg("queryPushOk,电话号码：" + s);
                        lockPushes = list;
                    }
                });
                break;
            case R.id.modify_push_list_btn:
                if (lockPushes == null) {
                    lockPushes = new ArrayList<>();
                }
                lockPushes.clear();
                LockPush lockPush = new LockPush();
                /*创建一条反锁警报*/
                lockPush.setValue(5);
                lockPush.setEnable(1);
                lockPushes.add(lockPush);
                smartLockHotelHandler.modifyPush("18666860862", lockPushes, new SmartLockHotelHandler.ModifyPushLsn() {
                    @Override
                    public void modifyPushOk() {
                        showMsg("modifyPushOk");
                    }
                });
                break;
            case R.id.query_lock_open_record:
                smartLockHotelHandler.queryLockOpenRecord(new SmartLockHotelHandler.OpenRecordLsn() {
                    @Override
                    public void openRecordLoad(List<LockHistory> list) {

                    }
                });
                break;
            case R.id.query_lock_warn_record:
                smartLockHotelHandler.queryLockWarnRecord(new SmartLockHotelHandler.WarnRecordLsn() {
                    @Override
                    public void warnRecordLoad(List<LockAlarm> list) {

                    }
                });
                break;
            case R.id.finish_btn:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        obInit.destory();
        smartLockHotelHandler.unRegist();
    }

    /**
     * 显示提示信息
     *
     * @param showMsg 信息
     */
    private void showMsg(String showMsg) {
        Toast.makeText(MainActivity.this, showMsg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, showMsg);
    }

    private static final String TAG = "MainActivity";

    private void onFaildHandle(HttpRespond.ErrorCode errorCode, String s1) {
        switch (errorCode) {
            case exceptionError:
                showMsg("网络异常，请检查网络");
                break;
            case responseNotOk:
                showMsg("请求失败，请稍后再试或联系工程师");
                break;
            case operationFailed:
                showMsg("业务未得到正确处理，请检查逻辑" + s1);
                break;
        }
    }

    /**
     * 存储字符串
     *
     * @param key un
     */
    private void putSpString(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("lockdemo", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * 获取字符串
     *
     * @param key un
     * @return un
     */
    private String getSpString(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("lockdemo", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
