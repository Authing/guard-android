package cn.authing.ut;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.authing.R;

public class UTActivity extends AppCompatActivity implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    private static final String[] mParentMenu = {
            "注册", "登录", "社会化登录", "发送验证码", "获取用户信息", "更新用户信息", "MFA", "账号密码"
    };
    private GroupListAdapter mAdapter;
    private List<List<TestCase>> mChildMenu;
    private ArrayList<TestCase> mAllTestList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("场景测试");
        }
        setContentView(R.layout.activity_ut);
        initData();
        ExpandableListView expandableListView = findViewById(R.id.expand_list);
        expandableListView.setOnGroupClickListener(this);
        expandableListView.setOnChildClickListener(this);

        List<GroupListParent> groupList = new ArrayList<>();
        for (int i = 0; i < mParentMenu.length; i++) {
            List<GroupListChild> childList = new ArrayList<>();
            for (int j = 0; j < mChildMenu.get(i).size(); j++) {
                String caseName = mChildMenu.get(i).get(j).getCaseName();
                String caseSubName = mChildMenu.get(i).get(j).getCaseSubName();
                childList.add(new GroupListChild(caseName + ": " + caseSubName));
            }
            groupList.add(new GroupListParent(mParentMenu[i], childList));
        }
        if (actionBar != null) {
            actionBar.setSubtitle(mAllTestList.size()+"");
        }
        mAdapter = new GroupListAdapter(this, groupList);
        expandableListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_test) {
            Intent intent = new Intent(this, UTTestAllActivity.class);
            intent.putParcelableArrayListExtra("data", mAllTestList);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(this, UTTestActivity.class);
        intent.putExtra("data", mChildMenu.get(groupPosition).get(childPosition));
        startActivity(intent);
        return false;
    }

    private void initData() {
        mChildMenu = new ArrayList<>();
        mAllTestList = new ArrayList<>();
        List<TestCase> registerList = new ArrayList<>();
        registerList.add(TestCaseUtil.createRegisterByEmailCase(0, false));
        registerList.add(TestCaseUtil.createRegisterByEmailCase(1, false));
        registerList.add(TestCaseUtil.createRegisterByEmailCase(2, false));
        registerList.add(TestCaseUtil.createRegisterByPhoneCodeCase(0, false));
        registerList.add(TestCaseUtil.createRegisterByPhoneCodeCase(1, false));
        registerList.add(TestCaseUtil.createRegisterByPhoneCodeCase(2, false));
        registerList.add(TestCaseUtil.createRegisterByEmailCodeCase(0, false));
        registerList.add(TestCaseUtil.createRegisterByEmailCodeCase(1, false));
        registerList.add(TestCaseUtil.createRegisterByEmailCodeCase(2, false));
        mChildMenu.add(registerList);
        mAllTestList.addAll(registerList);

        List<TestCase> loginList = new ArrayList<>();
        loginList.add(TestCaseUtil.createLoginByAccountCase(0, false));
        loginList.add(TestCaseUtil.createLoginByAccountCase(1, false));
        loginList.add(TestCaseUtil.createLoginByAccountCase(2, false));
        loginList.add(TestCaseUtil.createLoginByAccountCase(3, false));
        loginList.add(TestCaseUtil.createLoginByAccountCase(4, false));
        loginList.add(TestCaseUtil.createLoginByPhoneCodeCase(0, false));
        loginList.add(TestCaseUtil.createLoginByPhoneCodeCase(1, false));
        loginList.add(TestCaseUtil.createLoginByPhoneCodeCase(2, false));
        loginList.add(TestCaseUtil.createLoginByEmailCodeCase(0, false));
        loginList.add(TestCaseUtil.createLoginByEmailCodeCase(1, false));
        loginList.add(TestCaseUtil.createLoginByEmailCodeCase(2, false));
        mChildMenu.add(loginList);
        mAllTestList.addAll(loginList);

        List<TestCase> socialList = new ArrayList<>();
        socialList.add(TestCaseUtil.createSocialLoginCase(0, false));
        socialList.add(TestCaseUtil.createSocialLoginCase(1, false));
        socialList.add(TestCaseUtil.createSocialLoginCase(2, false));
        socialList.add(TestCaseUtil.createSocialLoginCase(3, false));
        socialList.add(TestCaseUtil.createSocialLoginCase(4, false));
        socialList.add(TestCaseUtil.createSocialLoginCase(5, false));
        mChildMenu.add(socialList);
        mAllTestList.addAll(socialList);

        List<TestCase> sendVerifyCodeList = new ArrayList<>();
        sendVerifyCodeList.add(TestCaseUtil.createSendSmsCodeCase(0 ));
        sendVerifyCodeList.add(TestCaseUtil.createSendSmsCodeCase(1));
        sendVerifyCodeList.add(TestCaseUtil.createSendSmsCodeCase(2));
        sendVerifyCodeList.add(TestCaseUtil.createSendSmsCodeCase(3));
        sendVerifyCodeList.add(TestCaseUtil.createSendEmailCodeCase(0));
        sendVerifyCodeList.add(TestCaseUtil.createSendEmailCodeCase(1));
        sendVerifyCodeList.add(TestCaseUtil.createSendEmailCodeCase(2));
        sendVerifyCodeList.add(TestCaseUtil.createSendEmailCodeCase(3));
        sendVerifyCodeList.add(TestCaseUtil.createSendEmailCodeCase(4));
        sendVerifyCodeList.add(TestCaseUtil.createSendEmailCodeCase(5));
        mChildMenu.add(sendVerifyCodeList);
        mAllTestList.addAll(sendVerifyCodeList);

        List<TestCase> getUserInfoList = new ArrayList<>();
        getUserInfoList.add(TestCaseUtil.createGetUerInfoCase());
        getUserInfoList.add(TestCaseUtil.createGetCustomUserDataCase());
        getUserInfoList.add(TestCaseUtil.createGetListRolesCase());
        getUserInfoList.add(TestCaseUtil.createGetListApplicationsCase());
        getUserInfoList.add(TestCaseUtil.createGetListAuthorizedResourcesCase());
        getUserInfoList.add(TestCaseUtil.createGetListOrgsCase());
        mChildMenu.add(getUserInfoList);
        mAllTestList.addAll(getUserInfoList);

        List<TestCase> messageList = new ArrayList<>();
        messageList.add(TestCaseUtil.createBindPhoneCase(0));
        messageList.add(TestCaseUtil.createBindPhoneCase(1));
        messageList.add(TestCaseUtil.createBindPhoneCase(2));
        messageList.add(TestCaseUtil.createBindPhoneCase(3));
        messageList.add(TestCaseUtil.createUnbindPhoneCase());
        messageList.add(TestCaseUtil.createBindEmailCase(0));
        messageList.add(TestCaseUtil.createBindEmailCase(1));
        messageList.add(TestCaseUtil.createBindEmailCase(2));
        messageList.add(TestCaseUtil.createBindEmailCase(3));
        messageList.add(TestCaseUtil.createUnbindEmailCase());
        messageList.add(TestCaseUtil.createResetPasswordByPhoneCodeCase(0));
        messageList.add(TestCaseUtil.createResetPasswordByPhoneCodeCase(1));
        messageList.add(TestCaseUtil.createResetPasswordByPhoneCodeCase(2));
        messageList.add(TestCaseUtil.createResetPasswordByPhoneCodeCase(3));
        messageList.add(TestCaseUtil.createResetPasswordByEmailCodeCase(0));
        messageList.add(TestCaseUtil.createResetPasswordByEmailCodeCase(1));
        messageList.add(TestCaseUtil.createResetPasswordByEmailCodeCase(2));
        messageList.add(TestCaseUtil.createResetPasswordByEmailCodeCase(3));
        messageList.add(TestCaseUtil.createUpdatePassword());
        messageList.add(TestCaseUtil.createUpdateProfileCase(0));
        messageList.add(TestCaseUtil.createUpdateProfileCase(1));
        messageList.add(TestCaseUtil.createUpdateProfileCase(2));
        messageList.add(TestCaseUtil.createUpdateProfileCase(3));
        messageList.add(TestCaseUtil.createUpdateProfileCase(4));
        messageList.add(TestCaseUtil.createUpdateProfileCase(5));
        messageList.add(TestCaseUtil.createUpdateProfileCase(6));
        messageList.add(TestCaseUtil.createUpdateProfileCase(7));
        messageList.add(TestCaseUtil.createUpdateProfileCase(8));
        mChildMenu.add(messageList);
        mAllTestList.addAll(messageList);

        List<TestCase> mfaList = new ArrayList<>();
        mfaList.add(TestCaseUtil.createMfaCheckCase(0));
        mfaList.add(TestCaseUtil.createMfaCheckCase(1));
        mfaList.add(TestCaseUtil.createMfaCheckCase(2));
        mfaList.add(TestCaseUtil.createMfaVerifyByPhoneCase(0));
        mfaList.add(TestCaseUtil.createMfaVerifyByPhoneCase(1));
        mfaList.add(TestCaseUtil.createMfaVerifyByPhoneCase(2));
        mfaList.add(TestCaseUtil.createMfaVerifyByPhoneCase(3));
        mfaList.add(TestCaseUtil.createMfaVerifyByEmailCase(0));
        mfaList.add(TestCaseUtil.createMfaVerifyByEmailCase(1));
        mfaList.add(TestCaseUtil.createMfaVerifyByEmailCase(2));
        mfaList.add(TestCaseUtil.createMfaVerifyByOTPCase());
        mfaList.add(TestCaseUtil.createMfaVerifyByRecoveryCodeCase());
        mChildMenu.add(mfaList);
        mAllTestList.addAll(mfaList);

        List<TestCase> accountList = new ArrayList<>();
        accountList.add(TestCaseUtil.createLogoutCase());
        accountList.add(TestCaseUtil.createDeleteAccountCase());
        accountList.add(TestCaseUtil.createCheckAccountCase(0));
        accountList.add(TestCaseUtil.createCheckAccountCase(1));
        accountList.add(TestCaseUtil.createCheckAccountCase(2));
        accountList.add(TestCaseUtil.createCheckPasswordCase());
        accountList.add(TestCaseUtil.createGetSecurityLevelCase());
        mChildMenu.add(accountList);
        mAllTestList.addAll(accountList);

    }


}
