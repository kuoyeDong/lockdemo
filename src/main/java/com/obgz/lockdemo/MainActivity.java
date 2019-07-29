package com.obgz.lockdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private MainActBinding binding;
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
    private List<LockUser> lockUsers = new ArrayList<>();

    /**
     * 门锁列表
     */
    private List<LockTempUser> lockTempUsers = new ArrayList<>();

    /**
     * 推送选项列表
     */
    private List<LockPush> lockPushes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_act);
        changeBtnAble(false);

        binding.obinitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSInitSuc) {
                    showMsg("请不要重复初始化");
                    return;
                }
                String uniqueKey = binding.uniquekeyEdt.getText().toString();
                if (TextUtils.isEmpty(uniqueKey)) {
                    showMsg("请输入uniquekey");
                    return;
                }
                obInit = new ObInit("Tencent", "Tencent", uniqueKey,
                        MainActivity.this) {
                    @Override
                    public void onInitSuc() {
                        iSInitSuc = true;
                        changeBtnAble(true);
                        showMsg("初始化成功，已获取token，此token未公开，会在后续的操作中自动使用");
                    }

                    @Override
                    public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                        showMsg("初始化失败，若网络连接正常，请检查参数是否正确，否则请检查网络连接" + i + s + s1);
                    }
                };
                showMsg("初始化开始，请稍后。。。");
                obInit.init();
            }
        });
        addLsn();
        smartLockHotelHandler = new SmartLockHotelHandler(getSpString("lockSerId")) {
            @Override
            public void noSerialId() {
                showMsg("检测到在没序列号的情况下调用了必需序列号的操作，此时目标操作不会被执行，请确认有合法序列号");
            }

            @Override
            protected void lockStatusChange(LockStatusEnum lockStatusEnum) {
                showMsg("锁状态发生变化，请查看参数枚举");
            }

            @Override
            protected void batteryValue(int i) {
                showMsg("当前电量值发生变化，回调参数为电量百分比,当前电量值为：" + i);
            }

            @Override
            public void deleteDeviceSuc() {
                showMsg("成功删除设备");
                putSpString("lockSerId", null);
            }

            @Override
            public void searchNewDeviceSuc() {
                showMsg("成功开启扫描，请等待扫描时传入的时间，单位为秒");
            }

            @Override
            protected void onNewDevice(Device device) {
                showMsg("扫描到新设备回调，请自行保存，回调参数为扫描到的新设备实例,设备名称为：" + device.getName()
                        + "设备序列号为：" + device.getSerialId());
                putSpString("lockSerId", device.getSerialId());
                smartLockHotelHandler.setDeviceSerId(device.getSerialId());
            }

            @Override
            public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                showMsg("操作失败，若网络连接正常，请检查参数是否正确，否则请检查网络连接");
            }
        };
    }

    private void addLsn() {
        binding.addOboxBtn.setOnClickListener(this);
        binding.deleteOboxBtn.setOnClickListener(this);
        binding.addLockBtn.setOnClickListener(this);
        binding.deleteLockBtn.setOnClickListener(this);
        binding.queryLockStatusBtn.setOnClickListener(this);
        binding.queryUserBtn.setOnClickListener(this);
        binding.sendCoercionValidatecodeUserBtn.setOnClickListener(this);
        binding.modifyUserBtn.setOnClickListener(this);
        binding.creatAdminPwdBtn.setOnClickListener(this);
        binding.resetAdminPwdBycodeBtn.setOnClickListener(this);
        binding.modifyAdminPwdBtn.setOnClickListener(this);
        binding.validateAdminPwdBtn.setOnClickListener(this);
        binding.queryTempUserBtn.setOnClickListener(this);
        binding.addTempUserBtn.setOnClickListener(this);
        binding.deleteTempUserBtn.setOnClickListener(this);
        binding.modifyTempUserBtn.setOnClickListener(this);
        binding.sendCodeToTempUserBtn.setOnClickListener(this);
        binding.queryPushListBtn.setOnClickListener(this);
        binding.modifyPushListBtn.setOnClickListener(this);
        binding.queryLockOpenRecord.setOnClickListener(this);
        binding.queryLockWarnRecord.setOnClickListener(this);
        binding.finishBtn.setOnClickListener(this);
    }

    /**
     * 未初始化，按钮不可被点击
     */
    private void changeBtnAble(boolean able) {
        binding.addOboxBtn.setEnabled(able);
        binding.deleteOboxBtn.setEnabled(able);
        binding.addLockBtn.setEnabled(able);
        binding.deleteLockBtn.setEnabled(able);
        binding.queryLockStatusBtn.setEnabled(able);
        binding.queryUserBtn.setEnabled(able);
        binding.sendCoercionValidatecodeUserBtn.setEnabled(able);
        binding.modifyUserBtn.setEnabled(able);
        binding.creatAdminPwdBtn.setEnabled(able);
        binding.resetAdminPwdBycodeBtn.setEnabled(able);
        binding.modifyAdminPwdBtn.setEnabled(able);
        binding.validateAdminPwdBtn.setEnabled(able);
        binding.queryTempUserBtn.setEnabled(able);
        binding.addTempUserBtn.setEnabled(able);
        binding.deleteTempUserBtn.setEnabled(able);
        binding.modifyTempUserBtn.setEnabled(able);
        binding.sendCodeToTempUserBtn.setEnabled(able);
        binding.queryPushListBtn.setEnabled(able);
        binding.modifyPushListBtn.setEnabled(able);
        binding.queryLockOpenRecord.setEnabled(able);
        binding.queryLockWarnRecord.setEnabled(able);
        binding.finishBtn.setEnabled(able);
    }

    @Override
    public void onClick(View v) {
        if (!iSInitSuc) {
            showMsg("请先进行一次成功的初始化操作");
            return;
        }
        switch (v.getId()) {
            case R.id.add_obox_btn:
                String routePwd = binding.routePwdEdt.getText().toString();
                if (TextUtils.isEmpty(routePwd)) {
                    showMsg("输入路由器密码");
                    return;
                }
                ConnectHandler connectHandler = new ConnectHandler(this, routePwd, new ConnectHandler.ConnectOboxLsn() {
                    @Override
                    public void error(ConnectHandler.ConnectError connectError) {
                        showMsg("添加obox失败，请参考参数枚举查看失败原因，或检查获取wifissid相关权限");
                    }

                    @Override
                    public void connectOboxSuc(Obox obox) {
                        showMsg("成功添加obox，成功添加obox，请自行保存回调中的obox实例，添加设备需要用到obox的序列号,obox序列号为：" + obox.getObox_serial_id());
                        putSpString("oboxSerId", obox.getObox_serial_id());
                    }

                    @Override
                    public void connectWifiDeviceSuc(String s) {
                        showMsg("成功添加非obox的wifi设备，此wifi设备并非门锁，而是典型的有wifi热点信号的设备，设备参数在回调中，可自取");
                    }
                }, true);
                connectHandler.start();
                showMsg("正在激活OBOX，不要进行其他操作，耐心等待，最多三分钟。。。");
                break;
            case R.id.delete_obox_btn:
                String oboxSerId = getSpString("oboxSerId");
                OboxHandler oboxHandler = new OboxHandler(oboxSerId) {
                    @Override
                    public void noSerialId() {
                        showMsg("检测到在没序列号的情况下调用了必需序列号的操作，此时目标操作不会被执行，请确认有合法序列号");
                    }

                    @Override
                    protected void oboxDeleteSuc(String oboxSerId) {
                        showMsg("成功删除OBOX，回调参数为该OBOX序列号，序列号为：" + oboxSerId);
                        putSpString("oboxSerId", null);
                    }

                    @Override
                    public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                        showMsg("删除OBOX失败，若网络连接正常，请检查参数是否正确，否则请检查网络连接");
                    }
                };
                oboxHandler.deleteObox();
                break;
            case R.id.add_lock_btn:
                oboxSerId = getSpString("oboxSerId");
                showMsg("OBOX序列号(门锁为OBOX下级设备)，扫描时间(十进制),开启扫描成功回调searchNewDeviceSuc，扫描到门锁回调onNewDevice");
                smartLockHotelHandler.searchNewDevice(oboxSerId, "30");
                break;
            case R.id.delete_lock_btn:
                showMsg("删除门锁，成功回调deleteDeviceSuc");
                smartLockHotelHandler.deleteDevice();
                break;
            case R.id.query_lock_status_btn:
                showMsg("查询门锁状态，成功必然回调lockStatusChange，可能回调batteryValue");
                smartLockHotelHandler.queryLockStatus();
                break;
            case R.id.query_user_btn:
                smartLockHotelHandler.queryUser(new SmartLockHotelHandler.queryUserLsn() {
                    @Override
                    public void userRecordLoad(List<LockUser> list) {
                        showMsg("获取用户列表成功,回调参数为用户列表，请自行保存");
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
                String pushPhone = binding.coercionUserPhoneEdt.getText().toString();
                if (TextUtils.isEmpty(pushPhone)) {
                    showMsg("输入胁迫电话，顺便一提，要有胁迫指纹才会发送成功");
                    return;
                }
                smartLockHotelHandler.sendValidateCode(lockUser, pushPhone, new SmartLockHotelHandler.SendCodeLsn() {
                    @Override
                    public void sendCodeOk() {
                        showMsg("发送验证码成功");
                    }
                });
                break;
            case R.id.modify_user_btn:
                if (lockUsers.size() == 0) {
                    showMsg("用户列表为空");
                    return;
                }
                lockUser = lockUsers.get(0);
                String newNickName = binding.userNameEdt.getText().toString();
                if (TextUtils.isEmpty(newNickName)) {
                    showMsg("输入要修改的名称");
                    return;
                }
                lockUser.setNickName(newNickName);
                String validateCode = binding.validatecodeEdt.getText().toString();
                if (lockUser.hasStressPwd()) {
                    if (TextUtils.isEmpty(validateCode)) {
                        showMsg("输入验证码");
                        return;
                    }
                }
                String coercionPhone = binding.coercionUserPhoneEdt.getText().toString();
                if (TextUtils.isEmpty(coercionPhone)) {
                    showMsg("输入胁迫电话，顺便一提，要有胁迫指纹才会发送成功");
                    return;
                }
                lockUser.setMobile(coercionPhone);
                smartLockHotelHandler.modifyUser(lockUser, validateCode, new SmartLockHotelHandler.ModifyUserLsn() {
                    @Override
                    public void modifyUserOk() {
                        showMsg("修改用户成功");
                    }
                });
                break;
            case R.id.creat_admin_pwd_btn:
                String creatAdminPwd = binding.creatAdminPwdEdt.getText().toString();
                if (TextUtils.isEmpty(creatAdminPwd)) {
                    showMsg("输入要创建的密码");
                    return;
                }
                smartLockHotelHandler.createAdminPwd(creatAdminPwd, new SmartLockHotelHandler.CreatAuthPwdLsn() {

                    @Override
                    public void creatAdminPwdOk() {
                        showMsg("创建权限密码成功，请自行保存此密码");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("已经有权限密码，不能重复创建");
                    }
                });
                break;
            case R.id.reset_admin_pwd_bycode_btn:
                String pushAdminPwd = binding.pushAdminPwdEdt.getText().toString();
                if (TextUtils.isEmpty(pushAdminPwd)) {
                    showMsg("请输入要重置的密码");
                    return;
                }
                String uniqueKey = binding.uniquekeyEdt.getText().toString();
                if (TextUtils.isEmpty(uniqueKey)) {
                    showMsg("请输入uniquekey");
                    return;
                }
                smartLockHotelHandler.resetAdminPwdByCode(pushAdminPwd, uniqueKey, new SmartLockHotelHandler.ResetPwdLsn() {
                    @Override
                    public void waitLockReset() {
                        showMsg("进入等待门锁操作状态，请操作门锁");
                    }

                    @Override
                    public void resetPwdOk() {
                        showMsg("重置密码成功");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg(getString(R.string.no_admin_pwd));
                    }

                });
                break;
            case R.id.modify_admin_pwd_btn:
                String orgAdminPwd = binding.originalAdminPwdEdt.getText().toString();
                if (TextUtils.isEmpty(orgAdminPwd)) {
                    showMsg("输入原密码");
                    return;
                }
                String newAdminPwd = binding.newAdminPwdEdt.getText().toString();
                if (TextUtils.isEmpty(newAdminPwd)) {
                    showMsg("输入新密码");
                    return;
                }
                smartLockHotelHandler.modifyAdminPwd(orgAdminPwd, newAdminPwd, new SmartLockHotelHandler.ModifyPwdLsn() {
                    @Override
                    public void modifyPwdOk() {
                        showMsg("修改权限密码成功");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg(getString(R.string.no_admin_pwd));
                    }

                });
                break;
            case R.id.validate_admin_pwd_btn:
                String adminPwd = binding.adminPwdEdt.getText().toString();
                if (TextUtils.isEmpty(adminPwd)) {
                    showMsg("输入权限密码以验证");
                    return;
                }
                smartLockHotelHandler.validateAdminPwd(adminPwd, new SmartLockHotelHandler.ValidateAdminPwdLsn() {
                    @Override
                    public void validateAdminPwdOk() {
                        showMsg("权限密码验证成功");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg(getString(R.string.no_admin_pwd));
                    }

                });
                break;
            case R.id.query_temp_user_btn:
                smartLockHotelHandler.queryTemporaryUser(new SmartLockHotelHandler.QueryTemporaryUserLsn() {
                    @Override
                    public void queryTemporaryUserOk(List<LockTempUser> list) {
                        lockTempUsers = list;
                        showMsg("查询临时用户成功，请自取回调实例列表处理");
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("尚未验证权限密码，请先验证权限密码");
                    }
                });
                break;
            case R.id.add_temp_user_btn:
                LockTempUser addLockTempUser = new LockTempUser();
                String newTempUserName = binding.newTempUserNameEdt.getText().toString();
                if (TextUtils.isEmpty(newTempUserName)) {
                    showMsg("请输入新创建临时用户名");
                    return;
                }
                addLockTempUser.setNickName(newTempUserName);
                addLockTempUser.setStart("2020-07-22 00:00:00");
                addLockTempUser.setEnd("2020-07-25 00:00:00");
                addLockTempUser.setTimes(6);
                smartLockHotelHandler.addTemporaryUser(addLockTempUser, new SmartLockHotelHandler.AddTemporaryUserLsn() {
                    @Override
                    public void addTemporaryUserOk(LockTempUser lockTempUser) {
                        showMsg("添加临时用户成功，请自取回调实例处理");
                        if (lockTempUsers == null) {
                            lockTempUsers = new ArrayList<>();
                        }
                        lockTempUsers.add(lockTempUser);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("尚未验证权限密码，请先验证权限密码");
                    }
                });
                break;
            case R.id.delete_temp_user_btn:
                if (lockTempUsers.size() == 0) {
                    showMsg("没有临时用户可供操作");
                    return;
                }
                smartLockHotelHandler.deleteTemporaryUser(lockTempUsers.get(0), new SmartLockHotelHandler.DeleteTemporaryUserLsn() {
                    @Override
                    public void deleteTemporaryUserOk() {
                        showMsg("删除临时用户成功");
                        lockTempUsers.remove(0);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("尚未验证权限密码，请先验证权限密码");
                    }
                });
                break;
            case R.id.modify_temp_user_btn:
                if (lockTempUsers.size() == 0) {
                    showMsg("没有临时用户可供操作");
                    return;
                }
                final LockTempUser modifyLockTempUser = lockTempUsers.get(0);
                String tempUserModifyName = binding.modifyTempUserNameEdt.getText().toString();
                if (TextUtils.isEmpty(tempUserModifyName)) {
                    showMsg("输入要修改的临时用户名");
                    return;
                }
                String tempUserMobile = binding.modifyTempUserMobileEdt.getText().toString();
                modifyLockTempUser.setNickName(tempUserModifyName);
                modifyLockTempUser.setMobile(tempUserMobile);
                smartLockHotelHandler.modifyTemporaryUser(modifyLockTempUser, new SmartLockHotelHandler.ModifyTemporaryUserLsn() {
                    @Override
                    public void modifyTemporaryUserOk(LockTempUser lockTempUser) {
                        showMsg("修改临时用户成功，请自取回调实例处理");
                        lockTempUsers.remove(modifyLockTempUser);
                        lockTempUsers.add(0, lockTempUser);
                    }

                    @Override
                    public void temporaryUserExpire() {
                        showMsg("临时用户已过有效时限，不能被修改，请执行删除临时用户操作");
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("尚未验证权限密码，请先验证权限密码");
                    }
                });
                break;
            case R.id.send_code_to_temp_user_btn:
                if (lockTempUsers.size() == 0) {
                    showMsg("没有临时用户可供操作");
                    return;
                }
                LockTempUser sendPwdTempUser = lockTempUsers.get(0);
                String mobile = sendPwdTempUser.getMobile();
                if (TextUtils.isEmpty(mobile)) {
                    showMsg("该临时用户没有设定手机，请先设置手机号");
                    return;
                }
                smartLockHotelHandler.sendTemporaryUserPwd(sendPwdTempUser, new SmartLockHotelHandler.SendTemporaryUserPwdLsn() {
                    @Override
                    public void sendTemporaryUserPwdOk() {
                        showMsg("发送密码成功");
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("尚未验证权限密码，请先验证权限密码");
                    }
                });
                break;
            case R.id.query_push_list_btn:
                smartLockHotelHandler.queryPush(new SmartLockHotelHandler.QueryPushLsn() {
                    @Override
                    public void queryPushOk(String s, List<LockPush> list) {
                        showMsg("查询推送配置成功，请自取回调中的电话号码，以及推送配置列表，详细的配置含义请查看源码文档,电话号码：" + s);
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
                lockPush.setEnable(0);
                lockPushes.add(lockPush);
                String pushMobile = binding.pushMobileEdt.getText().toString();
                smartLockHotelHandler.modifyPush(pushMobile, lockPushes, new SmartLockHotelHandler.ModifyPushLsn() {
                    @Override
                    public void modifyPushOk() {
                        showMsg("修改推送配置成功");
                    }
                });
                break;
            case R.id.query_lock_open_record:
                smartLockHotelHandler.queryLockOpenRecord(new SmartLockHotelHandler.OpenRecordLsn() {
                    @Override
                    public void openRecordLoad(List<LockHistory> list) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            LockHistory lockHistory = list.get(i);
                            sb.append(lockHistory.getDateLine());
                            sb.append("\n");
                        }
                        showMsg("查询锁记录成功，请自取回调列表实例处理,锁记录时间线为：" + sb.toString());
                    }
                });
                break;
            case R.id.query_lock_warn_record:
                smartLockHotelHandler.queryLockWarnRecord(new SmartLockHotelHandler.WarnRecordLsn() {
                    @Override
                    public void warnRecordLoad(List<LockAlarm> list) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            LockAlarm lockAlarm = list.get(i);
                            sb.append(lockAlarm.getDateLine());
                            sb.append("\n");
                        }
                        showMsg("查询警报记录成功，请自取回调列表实例处理,警报记录时间线为：" + sb.toString());
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
        Log.d(TAG, showMsg);
        binding.logTv.setText(showMsg);
    }

    private static final String TAG = "MainActivity";

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
