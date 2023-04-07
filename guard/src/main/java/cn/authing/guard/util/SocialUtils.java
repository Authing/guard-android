package cn.authing.guard.util;

import android.content.Context;

import java.util.List;

import cn.authing.guard.R;
import cn.authing.guard.social.view.AlipayLoginButton;
import cn.authing.guard.social.view.AmazonLoginButton;
import cn.authing.guard.social.view.BaiduLoginButton;
import cn.authing.guard.social.view.DingTalkLoginButton;
import cn.authing.guard.social.view.DouYinLoginButton;
import cn.authing.guard.social.view.FaceBookLoginButton;
import cn.authing.guard.social.view.FingerLoginButton;
import cn.authing.guard.social.view.GitLabLoginButton;
import cn.authing.guard.social.view.GiteeLoginButton;
import cn.authing.guard.social.view.GithubLoginButton;
import cn.authing.guard.social.view.GoogleLoginButton;
import cn.authing.guard.social.view.HuaWeiLoginButton;
import cn.authing.guard.social.view.KuaiShouLoginButton;
import cn.authing.guard.social.view.LarkLoginButton;
import cn.authing.guard.social.view.LineLoginButton;
import cn.authing.guard.social.view.LinkedinLoginButton;
import cn.authing.guard.social.view.OPPOLoginButton;
import cn.authing.guard.social.view.QQLoginButton;
import cn.authing.guard.social.view.SlackLoginButton;
import cn.authing.guard.social.view.SocialLoginButton;
import cn.authing.guard.social.view.WeComLoginButton;
import cn.authing.guard.social.view.WechatLoginButton;
import cn.authing.guard.social.view.WechatMiniProgramLoginButton;
import cn.authing.guard.social.view.WeiboLoginButton;
import cn.authing.guard.social.view.XiaomiLoginButton;

public class SocialUtils {

    public static String parsSource(List<String> types) {
        StringBuilder sb = new StringBuilder();
        if (types.contains(Const.EC_TYPE_WECHAT)) {
            sb.append(Const.TYPE_WECHAT);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_ALIPAY)) {
            sb.append(Const.TYPE_ALIPAY);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_GOOGLE)) {
            sb.append(Const.TYPE_GOOGLE);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_WECHAT_COM)) {
            sb.append(Const.TYPE_WECHAT_COM);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_WECHAT_COM_AGENCY)) {
            sb.append(Const.TYPE_WECHAT_COM_AGENCY);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_LARK_INTERNAL)
                || types.contains(Const.EC_TYPE_LARK_PUBLIC)) {
            sb.append(Const.TYPE_LARK);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_FACEBOOK)) {
            sb.append(Const.TYPE_FACEBOOK);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_WECHAT_MINI_PROGRAM)) {
            sb.append(Const.TYPE_WECHAT_MINI_PROGRAM);
            sb.append("|");
        }
        if (types.contains(Const.TYPE_FINGER)) {
            sb.append(Const.TYPE_FINGER);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_QQ)) {
            sb.append(Const.TYPE_QQ);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_WEIBO)) {
            sb.append(Const.TYPE_WEIBO);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_BAIDU)) {
            sb.append(Const.TYPE_BAIDU);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_LINKEDIN)) {
            sb.append(Const.TYPE_LINKEDIN);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_DING_TALK)) {
            sb.append(Const.TYPE_DING_TALK);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_DOU_YIN)) {
            sb.append(Const.TYPE_DOU_YIN);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_GITHUB)) {
            sb.append(Const.TYPE_GITHUB);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_GITEE)) {
            sb.append(Const.TYPE_GITEE);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_GITLAB)) {
            sb.append(Const.TYPE_GITLAB);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_XIAOMI)) {
            sb.append(Const.TYPE_XIAOMI);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_KUAI_SHOU)) {
            sb.append(Const.TYPE_KUAI_SHOU);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_LINE)) {
            sb.append(Const.TYPE_LINE);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_SLACK)) {
            sb.append(Const.TYPE_SLACK);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_HUAWEI)) {
            sb.append(Const.TYPE_HUAWEI);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_OPPO)) {
            sb.append(Const.TYPE_OPPO);
            sb.append("|");
        }
        if (types.contains(Const.EC_TYPE_AMAZON)) {
            sb.append(Const.TYPE_AMAZON);
            sb.append("|");
        }
        String socialString = sb.toString();
        if (socialString.endsWith("|")) {
            socialString = socialString.substring(0, socialString.length() - 1);
        }
        return socialString;
    }

    public static SocialLoginButton getSocialButton(String src, Context context) {
        SocialLoginButton button = null;
        switch (src) {
            case Const.TYPE_WECHAT:
                button = new WechatLoginButton(context);
                break;
            case Const.TYPE_ALIPAY:
                button = new AlipayLoginButton(context);
                break;
            case Const.TYPE_WECHAT_COM:
            case Const.TYPE_WECHAT_COM_AGENCY:
                button = new WeComLoginButton(context);
                button.setType(src);
                break;
            case Const.TYPE_LARK:
                button = new LarkLoginButton(context);
                break;
            case Const.TYPE_GOOGLE:
                button = new GoogleLoginButton(context);
                break;
            case Const.TYPE_FACEBOOK:
                button = new FaceBookLoginButton(context);
                break;
            case Const.TYPE_WECHAT_MINI_PROGRAM:
                button = new WechatMiniProgramLoginButton(context);
                break;
            case Const.TYPE_FINGER:
                button = new FingerLoginButton(context);
                break;
            case Const.TYPE_QQ:
                button = new QQLoginButton(context);
                break;
            case Const.TYPE_WEIBO:
                button = new WeiboLoginButton(context);
                break;
            case Const.TYPE_BAIDU:
                button = new BaiduLoginButton(context);
                break;
            case Const.TYPE_LINKEDIN:
                button = new LinkedinLoginButton(context);
                break;
            case Const.TYPE_DING_TALK:
                button = new DingTalkLoginButton(context);
                break;
            case Const.TYPE_DOU_YIN:
                button = new DouYinLoginButton(context);
                break;
            case Const.TYPE_GITHUB:
                button = new GithubLoginButton(context);
                break;
            case Const.TYPE_GITEE:
                button = new GiteeLoginButton(context);
                break;
            case Const.TYPE_GITLAB:
                button = new GitLabLoginButton(context);
                break;
            case Const.TYPE_XIAOMI:
                button = new XiaomiLoginButton(context);
                break;
            case Const.TYPE_KUAI_SHOU:
                button = new KuaiShouLoginButton(context);
                break;
            case Const.TYPE_LINE:
                button = new LineLoginButton(context);
                break;
            case Const.TYPE_SLACK:
                button = new SlackLoginButton(context);
                break;
            case Const.TYPE_HUAWEI:
                button = new HuaWeiLoginButton(context);
                break;
            case Const.TYPE_OPPO:
                button = new OPPOLoginButton(context);
                break;
            case Const.TYPE_AMAZON:
                button = new AmazonLoginButton(context);
                break;
        }
        return button;
    }

    public static String getSocialButtonTitle(Context context, String src) {
        String title = "";
        switch (src) {
            case Const.TYPE_WECHAT:
                title = context.getString(R.string.authing_social_wechat);
                break;
            case Const.TYPE_ALIPAY:
                title = context.getString(R.string.authing_social_alipay);
                break;
            case Const.TYPE_WECHAT_COM:
            case Const.TYPE_WECHAT_COM_AGENCY:
                title = context.getString(R.string.authing_social_we_com);
                break;
            case Const.TYPE_LARK:
                title = context.getString(R.string.authing_social_lark);
                break;
            case Const.TYPE_GOOGLE:
                title = context.getString(R.string.authing_social_google);
                break;
            case Const.TYPE_FACEBOOK:
                title = context.getString(R.string.authing_social_facebook);
                break;
            case Const.TYPE_WECHAT_MINI_PROGRAM:
                title = context.getString(R.string.authing_social_wechat_miniprogram);
                break;
            case Const.TYPE_QQ:
                title = context.getString(R.string.authing_social_qq);
                break;
            case Const.TYPE_WEIBO:
                title = context.getString(R.string.authing_social_weibo);
                break;
            case Const.TYPE_BAIDU:
                title = context.getString(R.string.authing_social_baidu);
                break;
            case Const.TYPE_LINKEDIN:
                title = context.getString(R.string.authing_social_linkedin);
                break;
            case Const.TYPE_DING_TALK:
                title = context.getString(R.string.authing_social_ding_talk);
                break;
            case Const.TYPE_DOU_YIN:
                title = context.getString(R.string.authing_social_dou_yin);
                break;
            case Const.TYPE_GITHUB:
                title = context.getString(R.string.authing_social_github);
                break;
            case Const.TYPE_GITEE:
                title = context.getString(R.string.authing_social_gitee);
                break;
            case Const.TYPE_GITLAB:
                title = context.getString(R.string.authing_social_gitlab);
                break;
            case Const.TYPE_XIAOMI:
                title = context.getString(R.string.authing_social_xiaomi);
                break;
            case Const.TYPE_KUAI_SHOU:
                title = context.getString(R.string.authing_social_kuai_shou);
                break;
            case Const.TYPE_LINE:
                title = context.getString(R.string.authing_social_line);
                break;
            case Const.TYPE_SLACK:
                title = context.getString(R.string.authing_social_slack);
                break;
            case Const.TYPE_HUAWEI:
                title = context.getString(R.string.authing_social_huawei);
                break;
            case Const.TYPE_OPPO:
                title = context.getString(R.string.authing_social_oppo);
                break;
            case Const.TYPE_AMAZON:
                title = context.getString(R.string.authing_social_amazon);
                break;
            case Const.TYPE_FINGER:
                title = context.getString(R.string.authing_finger);
                break;
        }
        return title;
    }

    public static String getSocialText(Context context, String src) {
        String str = "";
        switch (src) {
            case Const.TYPE_WECHAT:
                str = context.getString(R.string.authing_login_by_wechat);
                break;
            case Const.TYPE_ALIPAY:
                str = context.getString(R.string.authing_login_by_alipay);
                break;
            case Const.TYPE_WECHAT_COM:
            case Const.TYPE_WECHAT_COM_AGENCY:
                str = context.getString(R.string.authing_login_by_we_com);
                break;
            case Const.TYPE_LARK:
                str = context.getString(R.string.authing_login_by_lark);
                break;
            case Const.TYPE_GOOGLE:
                str = context.getString(R.string.authing_login_by_google);
                break;
            case Const.TYPE_FACEBOOK:
                str = context.getString(R.string.authing_login_by_facebook);
                break;
            case Const.TYPE_WECHAT_MINI_PROGRAM:
                str = context.getString(R.string.authing_login_by_wechat_miniprogram);
                break;
            case Const.TYPE_FINGER:
                str = context.getString(R.string.authing_login_by_finger);
                break;
            case Const.TYPE_QQ:
                str = context.getString(R.string.authing_login_by_qq);
                break;
            case Const.TYPE_WEIBO:
                str = context.getString(R.string.authing_login_by_weibo);
                break;
            case Const.TYPE_BAIDU:
                str = context.getString(R.string.authing_login_by_baidu);
                break;
            case Const.TYPE_LINKEDIN:
                str = context.getString(R.string.authing_login_by_linkedin);
                break;
            case Const.TYPE_DING_TALK:
                str = context.getString(R.string.authing_login_by_ding_talk);
                break;
            case Const.TYPE_DOU_YIN:
                str = context.getString(R.string.authing_login_by_dou_yin);
                break;
            case Const.TYPE_GITHUB:
                str = context.getString(R.string.authing_login_by_github);
                break;
            case Const.TYPE_GITEE:
                str = context.getString(R.string.authing_login_by_gitee);
                break;
            case Const.TYPE_GITLAB:
                str = context.getString(R.string.authing_login_by_gitlab);
                break;
            case Const.TYPE_XIAOMI:
                str = context.getString(R.string.authing_login_by_xiaomi);
                break;
            case Const.TYPE_KUAI_SHOU:
                str = context.getString(R.string.authing_login_by_kuai_shou);
                break;
            case Const.TYPE_LINE:
                str = context.getString(R.string.authing_login_by_line);
                break;
            case Const.TYPE_SLACK:
                str = context.getString(R.string.authing_login_by_slack);
                break;
            case Const.TYPE_HUAWEI:
                str = context.getString(R.string.authing_login_by_huawei);
                break;
            case Const.TYPE_OPPO:
                str = context.getString(R.string.authing_login_by_oppo);
                break;
            case Const.TYPE_AMAZON:
                str = context.getString(R.string.authing_login_by_amazon);
                break;
        }
        return str;
    }

}
