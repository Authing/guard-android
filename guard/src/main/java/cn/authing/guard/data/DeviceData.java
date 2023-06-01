package cn.authing.guard.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceData implements Parcelable {

    private DeviceDetail device;
    private String lastLoginTime;
    private String lastIp;
    private boolean online;

    public DeviceData() {
    }

    protected DeviceData(Parcel in) {
        device = in.readParcelable(DeviceDetail.class.getClassLoader());
        lastLoginTime = in.readString();
        lastIp = in.readString();
        online = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeString(lastLoginTime);
        dest.writeString(lastIp);
        dest.writeByte((byte) (online ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceData> CREATOR = new Creator<DeviceData>() {
        @Override
        public DeviceData createFromParcel(Parcel in) {
            return new DeviceData(in);
        }

        @Override
        public DeviceData[] newArray(int size) {
            return new DeviceData[size];
        }
    };

    public DeviceDetail getDevice() {
        return device;
    }

    public void setDevice(DeviceDetail device) {
        this.device = device;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
