package cn.authing.guard.data;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.util.Util;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = -5986447815199326409L;

    public static class Address implements Serializable {
        private String country;
        private String postal_code;
        private String region;
        private String formatted;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostal_code() {
            return postal_code;
        }

        public void setPostal_code(String postal_code) {
            this.postal_code = postal_code;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getFormatted() {
            return formatted;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }
    }

    private String id;
    private String sub;
    private String status;
    private String company;
    private String browser;
    private String device;
    private int loginsCount;
    private String lastIp;
    private boolean blocked;
    private boolean isDeleted;
    private String birthday;
    private String family_name;
    private String gender;
    private String given_name;
    private String locale;
    private String middle_name;
    private String username;
    private String name;
    private String nickname;
    private String picture;
    private String photo;
    private String preferred_username;
    private String profile;
    private String updated_at;
    private String website;
    private String zoneinfo;
    private String country;
    private String city;
    private String province;
    private String streetAddress;
    private String region;
    private String postalCode;
    private String email;
    private boolean email_verified;
    private Address address;
    private String phone_number;
    private String phoneCountryCode;
    private boolean phone_number_verified;
    private List<CustomData> customData = new ArrayList<>();
    private List<Role> roles;
    private List<Application> applications;
    private List<Resource> resources;
    private List<Organization[]> organizations;

    private String accessToken;
    private String idToken;
    private String refreshToken;
    private String thirdPartySource;
    private MFAData mfaData;
    private String firstTimeLoginToken;
    private String recoveryCode;
    private SocialBindData socialBindData;

    public String getId() {
        return id == null ? getSub() : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getLoginsCount() {
        return loginsCount;
    }

    public void setLoginsCount(int loginsCount) {
        this.loginsCount = loginsCount;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPicture() {
        return picture != null? picture : photo;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPhoto() {
        return photo != null? photo : picture;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getZoneinfo() {
        return zoneinfo;
    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public boolean isPhone_number_verified() {
        return phone_number_verified;
    }

    public void setPhone_number_verified(boolean phone_number_verified) {
        this.phone_number_verified = phone_number_verified;
    }

    public List<CustomData> getCustomData() {
        return customData;
    }

    public void setCustomData(List<CustomData> data) {
        this.customData = data;
    }

    public void setCustomData(String key, String value) {
        for (CustomData d : customData) {
            if (d.getKey().equals(key)) {
                d.setValue(value);
                break;
            }
        }
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Organization[]> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization[]> organizations) {
        this.organizations = organizations;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getThirdPartySource() {
        return thirdPartySource;
    }

    public void setThirdPartySource(String thirdPartySource) {
        this.thirdPartySource = thirdPartySource;
    }

    public MFAData getMfaData() {
        return mfaData;
    }

    public void setMfaData(MFAData mfaData) {
        this.mfaData = mfaData;
    }

    public String getFirstTimeLoginToken() {
        return firstTimeLoginToken;
    }

    public void setFirstTimeLoginToken(String firstTimeLoginToken) {
        this.firstTimeLoginToken = firstTimeLoginToken;
    }

    public String getRecoveryCode() {
        return recoveryCode;
    }

    public void setRecoveryCode(String recoveryCode) {
        this.recoveryCode = recoveryCode;
    }

    public SocialBindData getSocialBindData() {
        return socialBindData;
    }

    public void setSocialBindData(SocialBindData socialBindData) {
        this.socialBindData = socialBindData;
    }

    public static UserInfo createUserInfo(JSONObject data) throws JSONException {
        UserInfo userInfo = new UserInfo();
        return createUserInfo(userInfo, data);
    }

    public static UserInfo createUserInfo(UserInfo userInfo, JSONObject data) throws JSONException {
        if (userInfo == null || data == null) {
            return null;
        }

        if (data.has("id")) {
            String id = data.getString("id");
            userInfo.setId(id);
        }
        if (data.has("sub")) {
            String id = data.getString("sub");
            userInfo.setId(id);
        }
        if (data.has("birthdate")) {
            String s = data.getString("birthdate");
            userInfo.setBirthday(s);
        }
        if (data.has("locale")) {
            String s = data.getString("locale");
            userInfo.setLocale(s);
        }
        if (data.has("website")) {
            String s = data.getString("website");
            userInfo.setWebsite(s);
        }
        if (data.has("zoneinfo")) {
            String s = data.getString("zoneinfo");
            userInfo.setZoneinfo(s);
        }
        if (data.has("status")) {
            String s = data.getString("status");
            userInfo.setStatus(s);
        }
        if (data.has("company")) {
            String s = data.getString("company");
            userInfo.setCompany(s);
        }
        if (data.has("browser")) {
            String s = data.getString("browser");
            userInfo.setBrowser(s);
        }
        if (data.has("device")) {
            String s = data.getString("device");
            userInfo.setDevice(s);
        }
        if (data.has("loginsCount")) {
            int i = data.getInt("loginsCount");
            userInfo.setLoginsCount(i);
        }
        if (data.has("lastIp")) {
            String s = data.getString("lastIp");
            userInfo.setLastIp(s);
        }
        if (data.has("blocked")) {
            boolean b = data.getBoolean("blocked");
            userInfo.setBlocked(b);
        }
        if (data.has("isDeleted")) {
            boolean b = data.getBoolean("isDeleted");
            userInfo.setDeleted(b);
        }
        if (data.has("username")) {
            String username = data.getString("username");
            userInfo.setName(username);
        }
        if (data.has("phone")) {
            String phone = data.getString("phone");
            userInfo.setPhone_number(phone);
        }
        if (data.has("phone_number")) {
            String phone = data.getString("phone_number");
            userInfo.setPhone_number(phone);
        }
        if (data.has("phoneCountryCode")) {
            String phoneCountryCode = data.getString("phoneCountryCode");
            userInfo.setPhoneCountryCode(phoneCountryCode);
        }
        if (data.has("email")) {
            String email = data.getString("email");
            userInfo.setEmail(email);
        }
        if (data.has("token")) {
            String token = data.getString("token");
            userInfo.setIdToken(token);
        }
        if (data.has("photo")) {
            String s = data.getString("photo");
            userInfo.setPhoto(s);
        }
        if (data.has("picture")) {
            String s = data.getString("picture");
            userInfo.setPicture(s);
        }
        if (data.has("updatedAt")) {
            String s = data.getString("updatedAt");
            userInfo.setUpdated_at(s);
        }
        if (data.has("preferredUsername")) {
            String s = data.getString("preferredUsername");
            userInfo.setPreferred_username(s);
        }
        if (data.has("name")) {
            String s = data.getString("name");
            userInfo.setName(s);
        }
        if (data.has("username")) {
            String s = data.getString("username");
            userInfo.setUsername(s);
        }
        if (data.has("gender")) {
            String s = data.getString("gender");
            userInfo.setGender(s);
        }
        if (data.has("givenName")) {
            String s = data.getString("givenName");
            userInfo.setGiven_name(s);
        }
        if (data.has("middleName")) {
            String s = data.getString("middleName");
            userInfo.setMiddle_name(s);
        }
        if (data.has("familyName")) {
            String s = data.getString("familyName");
            userInfo.setFamily_name(s);
        }
        if (data.has("nickname")) {
            String s = data.getString("nickname");
            userInfo.setNickname(s);
        }
        if (data.has("country")) {
            String s = data.getString("country");
            userInfo.setCountry(s);
        }
        if (data.has("city")) {
            String s = data.getString("city");
            userInfo.setCity(s);
        }
        if (data.has("province")) {
            String s = data.getString("province");
            userInfo.setProvince(s);
        }
        if (data.has("region")) {
            String s = data.getString("region");
            userInfo.setRegion(s);
        }
        if (data.has("postalCode")) {
            String s = data.getString("postalCode");
            userInfo.setPostalCode(s);
        }
        if (data.has("streetAddress")) {
            String s = data.getString("streetAddress");
            userInfo.setStreetAddress(s);
        }
        if (data.has("email_verified")) {
            boolean b = data.getBoolean("email_verified");
            userInfo.setEmail_verified(b);
        }
        if (data.has("emailVerified")) {
            boolean b = data.getBoolean("emailVerified");
            userInfo.setEmail_verified(b);
        }
        if (data.has("phone_number_verified")) {
            boolean b = data.getBoolean("phone_number_verified");
            userInfo.setPhone_number_verified(b);
        }
        if (data.has("phoneVerified")) {
            boolean b = data.getBoolean("phoneVerified");
            userInfo.setPhone_number_verified(b);
        }
        if (data.has("recoveryCode")) {
            String s = data.getString("recoveryCode");
            userInfo.setRecoveryCode(s);
        }
        if (data.has("role")) {
            List<String> list = Util.toStringList(data.getJSONArray("role"));
            List<Role> roles = new ArrayList<>();
            for (String s : list) {
                Role role = new Role();
                role.setCode(s);
                roles.add(role);
            }
            userInfo.setRoles(roles);
        }
        userInfo.parseTokens(data);
        return userInfo;
    }

    public String getMappedData(String key) {
        if (TextUtils.isEmpty(key)) {
            return "";
        }

        if ("name".equals(key)) {
            return getName();
        }
        if ("username".equals(key)) {
            return getUsername();
        }
        if ("nickname".equals(key)) {
            return getNickname();
        }
        if ("email".equals(key)) {
            return getEmail();
        }
        if ("phone".equals(key)) {
            return getPhone_number();
        }
        if ("gender".equals(key)) {
            return getGender();
        }
        if ("country".equals(key)) {
            return getCountry();
        }
        if ("city".equals(key)) {
            return getCity();
        }
        if ("province".equals(key)) {
            return getProvince();
        }
        if ("region".equals(key)) {
            return getRegion();
        }
        if ("company".equals(key)) {
            return getCompany();
        }
        if ("streetAddress".equals(key)) {
            return getStreetAddress();
        }
        if ("postalCode".equals(key)) {
            return getPostalCode();
        }
        if ("birthdate".equals(key)) {
            return getBirthday();
        }
        if ("locale".equals(key)) {
            return getLocale();
        }

        for (CustomData field : customData) {
            if (field.getKey().equals(key)) {
                return field.getValue();
            }
        }
        return "";
    }

    public static class CustomData implements Serializable {
        private String key;
        private String value;
        private String dataType;
        private String label;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public void parseCustomData(JSONArray array) {
        if (array == null) {
            return;
        }

        customData.clear();

        for (int i = 0, n = array.length(); i < n; i++) {
            try {
                CustomData data = new CustomData();
                JSONObject obj = array.getJSONObject(i);
                String s;
                if (obj.has("key")) {
                    s = obj.getString("key");
                    data.setKey(s);
                }
                if (obj.has("value")) {
                    s = obj.getString("value");
                    data.setValue(s);
                }
                if (obj.has("label")) {
                    s = obj.getString("label");
                    data.setLabel(s);
                }
                if (obj.has("dataType")) {
                    s = obj.getString("dataType");
                    data.setDataType(s);
                }
                customData.add(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void parseTokens(JSONObject obj) {
        try {
            if (obj.has("access_token")) {
                String s = obj.getString("access_token");
                setAccessToken(s);
            }
            if (obj.has("id_token")) {
                String s = obj.getString("id_token");
                setIdToken(s);
            }
            if (obj.has("refresh_token")) {
                String s = obj.getString("refresh_token");
                setRefreshToken(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
