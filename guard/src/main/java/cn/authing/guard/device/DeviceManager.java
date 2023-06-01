package cn.authing.guard.device;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.DeviceData;
import cn.authing.guard.data.DeviceDetail;
import cn.authing.guard.data.DeviceEvent;
import cn.authing.guard.data.DeviceInfo;
import cn.authing.guard.data.DeviceStatus;
import cn.authing.guard.data.Safe;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.Receiver;
import cn.authing.guard.network.WebSocketClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.device.DeviceUtils;
import cn.authing.guard.util.device.PhoneUtils;

public class DeviceManager {

    public static final String DEVICE_LOGOUT = "authing.device.force-logout";

    //用户侧
    public static final int LOGOUT_ANOTHER = 0; //个人中心 - 退出登录
    public static final int PROFILE_UNBIND = 1; //个人中心 - 设备解绑
    //管理员侧
    public static final int SUSPEND_DEVICE_BY_USER = 2; //用户列表 - 个人详情 - 挂起设备
    public static final int SUSPEND_DEVICE = 3; //设备管理 - 挂起设备
    public static final int DISABLE_DEVICE_BY_USER = 4; //用户列表 - 个人详情 - 禁用设备
    public static final int DISABLE_DEVICE = 5; //设备管理 - 禁用设备
    public static final int DELETE_DEVICE_BY_USER = 6; //用户列表 - 个人详情 - 移除设备（解绑）
    public static final int DELETE_DEVICE = 7; //设备管理 - 删除设备

    private String currentMessage;

    private DeviceManager() {
    }

    private static final class DeviceInstanceHolder {
        static final DeviceManager mInstance = new DeviceManager();
    }

    public static DeviceManager getInstance() {
        return DeviceInstanceHolder.mInstance;
    }

    /**
     * 上报设备信息
     */
    public void createDevice(Context context, AuthCallback<JSONObject> callback) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            for (String str : permissions) {
                if (context.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    ((Activity) context).requestPermissions(permissions, Const.REQUEST_PERMISSION_PHONE);
                    if (callback != null) {
                        callback.call(Const.ERROR_CODE_10029, "请先申请 READ_PHONE_STATE 权限", null);
                    }
                    return;
                }
            }
        }

        String deviceName = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            deviceName = PhoneUtils.getDeviceName(context);
        }
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = PhoneUtils.getDeviceName();
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceUniqueId(DeviceUtils.getUniqueDeviceId(context));
        deviceInfo.setName(deviceName);
        deviceInfo.setVersion("Android " + PhoneUtils.getOsVersion());
        deviceInfo.setHks("");
        deviceInfo.setFde("");
        deviceInfo.setHor(DeviceUtils.isDeviceRooted());
        deviceInfo.setType("Mobile");
        deviceInfo.setProducer(PhoneUtils.getDeviceManufacturer());
        deviceInfo.setMod(PhoneUtils.getDeviceModel());
        deviceInfo.setOs("Android");
        deviceInfo.setSn(PhoneUtils.getSerial());
        deviceInfo.setImei(PhoneUtils.getIMEI(context));
        deviceInfo.setMeid(PhoneUtils.getMEID(context));
        deviceInfo.setDescription("");
        createDevice(deviceInfo, callback);
    }

    /**
     * 上报设备
     */
    public static void createDevice(DeviceInfo deviceInfo, AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig(config -> {
            if (config == null) {
                if (callback != null) {
                    callback.call(Const.ERROR_CODE_10002, "Config not found", null);
                }
                return;
            }
            if (!config.isDeviceFuncEnabled()) {
                if (callback != null) {
                    callback.call(Const.ERROR_CODE_10002, "未启用设备管理", null);
                }
                return;
            }
            AuthClient.createDevice(deviceInfo, (AuthCallback<JSONObject>) (code, message, data) -> {
                Log.d("DeviceManager", "createDevice : code = " + code + " message = " + message);
                if (callback != null) {
                    callback.call(code, message, data);
                }
                if (code == 200 && data != null) {
                    if (data.has("deviceUniqueId")) {
                        try {
                            String deviceUniqueId = data.getString("deviceUniqueId");
                            Safe.saveDeviceUniqueId(deviceUniqueId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    /**
     * 获取设备列表信息
     */
    public void deviceList(int page, int limit, DeviceStatus deviceStatus, String os, String keyword, @NotNull AuthCallback<ArrayList<DeviceData>> callback) {
        AuthClient.deviceList(page, limit, deviceStatus, os, keyword, (AuthCallback<JSONObject>) (code, message, jsonData) -> {
            if (code != 200 || jsonData == null) {
                callback.call(code, message, null);
                return;
            }
            try {
                if (jsonData.has("data")) {
                    JSONArray data = jsonData.getJSONArray("data");
                    ArrayList<DeviceData> deviceList = new ArrayList<>();
                    int size = data.length();
                    for (int i = 0; i < size; i++) {
                        DeviceData deviceData = new DeviceData();
                        JSONObject obj = data.getJSONObject(i);
                        if (obj.has("lastLoginTime")) {
                            deviceData.setLastLoginTime(obj.getString("lastLoginTime"));
                        }
                        if (obj.has("lastIp")) {
                            deviceData.setLastIp(obj.getString("lastIp"));
                        }
                        if (obj.has("online")) {
                            deviceData.setOnline(obj.getBoolean("online"));
                        }

                        if (obj.has("device")) {
                            JSONObject device = obj.getJSONObject("device");
                            DeviceDetail deviceDetail = new DeviceDetail();
                            if (device.has("deviceId")) {
                                deviceDetail.setDeviceId(device.getString("deviceId"));
                            }
                            if (device.has("name")) {
                                deviceDetail.setName(device.getString("name"));
                            }
                            if (device.has("type")) {
                                deviceDetail.setType(device.getString("type"));
                            }
                            if (device.has("status")) {
                                deviceDetail.setStatus(device.getString("status"));
                            }
                            if (device.has("os")) {
                                deviceDetail.setOs(device.getString("os"));
                            }
                            if (device.has("version")) {
                                deviceDetail.setVersion(device.getString("version"));
                            }
                            if (device.has("mod")) {
                                deviceDetail.setMod(device.getString("mod"));
                            }
                            deviceData.setDevice(deviceDetail);
                        }
                        deviceList.add(deviceData);
                    }
                    callback.call(code, message, deviceList);
                } else {
                    callback.call(code, message, null);
                }
            } catch (Exception e) {
                callback.call(code, e.getMessage(), null);
            }
        });
    }

    /**
     * 移除设备
     */
    public void removeDevice(String deviceId, @NotNull AuthCallback<JSONObject> callback) {
        AuthClient.removeDevice(deviceId, callback);
    }

    /**
     * 退出登录-下线
     */
    public void logoutByDeviceId(String deviceId, @NotNull AuthCallback<JSONObject> callback) {
        AuthClient.logoutByDeviceId(deviceId, callback);
    }

    /**
     * 订阅设备管理事件
     */
    public void subDeviceEvent(IDeviceReceiver receiver) {
        AuthClient.subEvent(DEVICE_LOGOUT, new Receiver() {
            @Override
            public void onOpen() {
                if (receiver != null) {
                    receiver.onOpen();
                }
                Log.i("DeviceManager", "onOpen");
            }

            @Override
            public void onReceiverMessage(String s) {
                if (currentMessage != null && currentMessage.equals(s)) {
                    return;
                }
                currentMessage = s;
                DeviceEvent deviceEvent;
                try {
                    JSONObject result = new JSONObject(s);
                    deviceEvent = new DeviceEvent();
                    if (result.has("appId")) {
                        deviceEvent.setAppId(result.getString("appId"));
                    }
                    if (result.has("userId")) {
                        deviceEvent.setUserId(result.getString("userId"));
                    }
                    if (result.has("idTokenId")) {
                        deviceEvent.setIdTokenId(result.getString("idTokenId"));
                    }
                    if (result.has("accessTokenId")) {
                        deviceEvent.setAccessTokenId(result.getString("accessTokenId"));
                    }
                    if (result.has("logoutType")) {
                        deviceEvent.setLogoutType(result.getInt("logoutType"));
                    }
                    if (result.has("suspendEndTime")) {
                        deviceEvent.setSuspendEndTime(result.getLong("suspendEndTime"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (receiver != null) {
                    receiver.onReceiverEvent(deviceEvent);
                }
            }

            @Override
            public void onError(String s) {
                if (receiver != null) {
                    receiver.onError(s);
                }
                Log.e("DeviceManager", "onError : s = " + s);
            }
        });
    }

    /**
     * 关闭单个事件订阅
     */
    public void closeDeviceEvent(String token) {
        WebSocketClient.getInstance().close("code=" + DEVICE_LOGOUT + "&token=" + token);
    }

    /**
     * 关闭所有事件订阅
     */
    public void closeAllDeviceEvent() {
        currentMessage = null;
        WebSocketClient.getInstance().closeAll();
    }

}
