package cn.authing.guard.device;

import cn.authing.guard.data.DeviceEvent;

public interface IDeviceReceiver {

    void onOpen();

    void onReceiverEvent(DeviceEvent deviceEvent);

    void onError(String errorMsg);

}
