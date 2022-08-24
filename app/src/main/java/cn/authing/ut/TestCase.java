package cn.authing.ut;

import android.os.Parcel;
import android.os.Parcelable;

public class TestCase implements Parcelable {

    public static final Creator<TestCase> CREATOR = new Creator<TestCase>() {
        @Override
        public TestCase createFromParcel(Parcel in) {
            return new TestCase(in);
        }

        @Override
        public TestCase[] newArray(int size) {
            return new TestCase[size];
        }
    };
    private String moduleName;
    private String caseName;
    private String caseSubName;
    private String apiName;
    private String method;
    private String params;
    private String url;
    private boolean isOidc;
    private String result;

    public TestCase() {
    }

    protected TestCase(Parcel in) {
        moduleName = in.readString();
        caseName = in.readString();
        caseSubName = in.readString();
        apiName = in.readString();
        method = in.readString();
        params = in.readString();
        url = in.readString();
        isOidc = in.readByte() != 0;
        result = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(moduleName);
        dest.writeString(caseName);
        dest.writeString(caseSubName);
        dest.writeString(apiName);
        dest.writeString(method);
        dest.writeString(params);
        dest.writeString(url);
        dest.writeByte((byte) (isOidc ? 1 : 0));
        dest.writeString(result);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCaseSubName() {
        return caseSubName;
    }

    public void setCaseSubName(String caseSubName) {
        this.caseSubName = caseSubName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOidc() {
        return isOidc;
    }

    public void setOidc(boolean oidc) {
        isOidc = oidc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
