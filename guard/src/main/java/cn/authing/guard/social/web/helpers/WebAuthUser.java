package cn.authing.guard.social.web.helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class WebAuthUser implements Parcelable {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String profileUrl;
    private String code;
    private String accessToken;
    private long accessTokenExpiry;

    public WebAuthUser() {

    }

    protected WebAuthUser(Parcel in) {
        id = in.readString();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        profileUrl = in.readString();
        code = in.readString();
        accessToken = in.readString();
        accessTokenExpiry = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(profileUrl);
        dest.writeString(code);
        dest.writeString(accessToken);
        dest.writeLong(accessTokenExpiry);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WebAuthUser> CREATOR = new Creator<WebAuthUser>() {
        @Override
        public WebAuthUser createFromParcel(Parcel in) {
            return new WebAuthUser(in);
        }

        @Override
        public WebAuthUser[] newArray(int size) {
            return new WebAuthUser[size];
        }
    };


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(long accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }
}
