package cn.authing.guard.social.linkedin.helpers;

public interface OnBasicProfileListener {
    void onDataRetrievalStart();

    void onDataSuccess(LinkedInUser linkedInUser);

    void onDataFailed(int errCode, String errMessage);

}
