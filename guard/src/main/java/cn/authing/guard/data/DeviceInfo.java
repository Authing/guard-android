package cn.authing.guard.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DeviceInfo implements Parcelable {

    private String deviceUniqueId; //设备唯一 ID
    private String name; //设备名称
    private String version; // 设备系统版本
    private String hks; //硬件存储秘钥
    private String fde; //磁盘加密
    private boolean hor; //硬件越狱
    private String sn;  //设备序列号
    private String type;    //设备类型
    private String producer;    //制造商
    private String mod; //设备模组
    private String os;  //设备系统
    private String imei;    //国际识别码
    private String meid;    //设备识别码
    private String description; //设备识别码
    private String language;
    private boolean cookie;
    private String userAgent;
    private HashMap<String, String> customData;

    public DeviceInfo() {
    }


    protected DeviceInfo(Parcel in) {
        deviceUniqueId = in.readString();
        name = in.readString();
        version = in.readString();
        hks = in.readString();
        fde = in.readString();
        hor = in.readByte() != 0;
        sn = in.readString();
        type = in.readString();
        producer = in.readString();
        mod = in.readString();
        os = in.readString();
        imei = in.readString();
        meid = in.readString();
        description = in.readString();
        language = in.readString();
        cookie = in.readByte() != 0;
        userAgent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceUniqueId);
        dest.writeString(name);
        dest.writeString(version);
        dest.writeString(hks);
        dest.writeString(fde);
        dest.writeByte((byte) (hor ? 1 : 0));
        dest.writeString(sn);
        dest.writeString(type);
        dest.writeString(producer);
        dest.writeString(mod);
        dest.writeString(os);
        dest.writeString(imei);
        dest.writeString(meid);
        dest.writeString(description);
        dest.writeString(language);
        dest.writeByte((byte) (cookie ? 1 : 0));
        dest.writeString(userAgent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel in) {
            return new DeviceInfo(in);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHks() {
        return hks;
    }

    public void setHks(String hks) {
        this.hks = hks;
    }

    public String getFde() {
        return fde;
    }

    public void setFde(String fde) {
        this.fde = fde;
    }

    public boolean isHor() {
        return hor;
    }

    public void setHor(boolean hor) {
        this.hor = hor;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMeid() {
        return meid;
    }

    public void setMeid(String meid) {
        this.meid = meid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isCookie() {
        return cookie;
    }

    public void setCookie(boolean cookie) {
        this.cookie = cookie;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public HashMap<String, String> getCustomData() {
        return customData;
    }

    public void setCustomData(HashMap<String, String> customData) {
        this.customData = customData;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (getDeviceUniqueId() != null) {
                jsonObject.put("deviceUniqueId", getDeviceUniqueId());
            }
            if (getName() != null) {
                jsonObject.put("name", getName());
            }
            if (getVersion() != null) {
                jsonObject.put("version", getVersion());
            }
            if (getHks() != null) {
                jsonObject.put("hks", getHks());
            }
            if (getFde() != null) {
                jsonObject.put("fde", getFde());
            }

            jsonObject.put("hor", isHor());

            if (getSn() != null) {
                jsonObject.put("sn", getSn());
            }
            if (getType() != null) {
                jsonObject.put("type", getType());
            }
            if (getProducer() != null) {
                jsonObject.put("producer", getProducer());
            }
            if (getMod() != null) {
                jsonObject.put("mod", getMod());
            }
            if (getOs() != null) {
                jsonObject.put("os", getOs());
            }
            if (getImei() != null) {
                jsonObject.put("imei", getImei());
            }
            if (getMeid() != null) {
                jsonObject.put("meid", getMeid());
            }
            if (getDescription() != null) {
                jsonObject.put("description", getDescription());
            }
            if (getCustomData() != null) {
                jsonObject.put("customData", new JSONObject(getCustomData()));
            }
//            if (getLanguage() != null) {
//                jsonObject.put("language", getLanguage());
//            }
//            jsonObject.put("cookie", isCookie());
//            if (getUserAgent() != null) {
//                jsonObject.put("userAgent", getUserAgent());
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
