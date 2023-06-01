package cn.authing.guard.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceDetail implements Parcelable {

    private String name;
    private String type;
    private String status;
    private String os;
    private String mod;
    private String version;
    private String deviceId;

    public DeviceDetail() {
    }

    protected DeviceDetail(Parcel in) {
        name = in.readString();
        type = in.readString();
        status = in.readString();
        os = in.readString();
        mod = in.readString();
        version = in.readString();
        deviceId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeString(os);
        dest.writeString(mod);
        dest.writeString(version);
        dest.writeString(deviceId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceDetail> CREATOR = new Creator<DeviceDetail>() {
        @Override
        public DeviceDetail createFromParcel(Parcel in) {
            return new DeviceDetail(in);
        }

        @Override
        public DeviceDetail[] newArray(int size) {
            return new DeviceDetail[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
