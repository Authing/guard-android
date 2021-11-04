package cn.authing.guard.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = -5986447815199326409L;

    public static class Address {
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
    private String birthday;
    private String family_name;
    private String gender;
    private String given_name;
    private String locale;
    private String middle_name;
    private String name;
    private String nickname;
    private String picture;
    private String preferred_username;
    private String profile;
    private String updated_at;
    private String website;
    private String zoneinfo;
    private String email;
    private boolean email_verified;
    private Address address;
    private String phone_number;
    private boolean phone_number_verified;

    private String accessToken;

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
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public boolean isPhone_number_verified() {
        return phone_number_verified;
    }

    public void setPhone_number_verified(boolean phone_number_verified) {
        this.phone_number_verified = phone_number_verified;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static UserInfo createUserInfo(JSONObject data) throws JSONException {
        String id = data.getString("id");
        String username = data.getString("username");
        String phone = data.getString("phone");
        String email = data.getString("email");
        String token = data.getString("token");
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setName(username);
        userInfo.setPhone_number(phone);
        userInfo.setEmail(email);
        userInfo.setAccessToken(token);
        return userInfo;
    }
}
