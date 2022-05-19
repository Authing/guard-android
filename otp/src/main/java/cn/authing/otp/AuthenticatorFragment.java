package cn.authing.otp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AuthenticatorFragment extends Fragment implements CountDownListener {

    private ListView listView;
    private List<TOTPEntity> totpList;
    private TOTPAdapter adapter;
    private TextView textView;
    private float degree = 360;
    private boolean countingDown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.authing_authenticator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = getView().findViewById(R.id.lv_otps);
        adapter = new TOTPAdapter();
        listView.setAdapter(adapter);
        textView = getView().findViewById(R.id.no_data);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        totpList = db.getOTPs();
        if (null == totpList || totpList.size() == 0){
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        countingDown = true;
        countDown();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (null == totpList || position >= totpList.size()){
                return;
            }
            TOTPEntity totpEntity = totpList.get(position);
            if (null == dialog){
                dialog = new AuthenticatorDetailDialog(getActivity(), R.style.BaseDialog);
                dialog.setOnDismissListener(dialog -> onResume());
            }
            dialog.setData(totpEntity);
            dialog.show();
        });
    }

    private AuthenticatorDetailDialog dialog;

    @Override
    public void onPause() {
        super.onPause();
        countingDown = false;
    }

    private void countDown() {
        if (countingDown) {
            getView().postDelayed(this::countDown, 1000);
            // TODO each item might have different period
            int delta = TOTPUtils.getRemainingMilliSeconds();
            degree = 360f / TOTPUtils.TIME_STEP * delta / 1000;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public float getDegree() {
        return degree;
    }

    private class TOTPAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return totpList == null ? 0 :totpList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.authing_authenticator_item, parent, false);
            }

            TOTPEntity data = totpList.get(position);
            TextView tvAccount = view.findViewById(R.id.tv_account);
            tvAccount.setText(data.getAccount());
            TextView tvCode = view.findViewById(R.id.tv_totp_code);
            tvCode.setText(data.getTotpCode());

            CountDownPie countDownPie = view.findViewById(R.id.countdown_pie);
            countDownPie.setListener(AuthenticatorFragment.this);
            countDownPie.invalidate();
            return view;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != dialog){
            dialog.dismiss();
            dialog = null;
        }
    }
}
