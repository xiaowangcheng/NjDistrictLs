package net.htwater.njdistrictfx.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.CodeActivity;
import net.htwater.njdistrictfx.activity.UpdateLogActivity;
import net.htwater.njdistrictfx.activity.User.UserActivity;
import net.htwater.njdistrictfx.util.AppUtils;
import net.htwater.njdistrictfx.util.DataUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;



public class SettingFragment extends Fragment {
    private AlertDialog deviceInfo;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        ListView listView = (ListView) view.findViewById(R.id.listView1);
        RelativeLayout login = (RelativeLayout) view.findViewById(R.id.login);
        TextView name = (TextView) view.findViewById(R.id.name);

        TextView textView = new TextView(getActivity());
        textView.setText(getDeviceInfo());
        textView.setTextSize(14);
        textView.setPadding(12, 12, 12, 12);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        deviceInfo = builder.create();
        deviceInfo.setView(textView);
        deviceInfo.setCanceledOnTouchOutside(true);

        final List<String> settingList = DataUtil.getSettingList();
        listView.setAdapter(new MyAdapter(settingList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getActivity(), UpdateLogActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        AppUtils.intit_getClick(getActivity());
                        //EventBus.getDefault().post(new CheckUpdateEvent());
                        break;
                    case 2:
                        Intent intent4 = new Intent(getActivity(), CodeActivity.class);
                        startActivity(intent4);
                        break;
                }
            }
        });
        name.setText(SharedPreferencesUtil.getUserName());

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 获取设备信息
     *
     * @return String
     * @since v 1.0
     */
    @SuppressLint("MissingPermission")
    private String getDeviceInfo() {
        String m_szImei = null; // Requires
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) getActivity()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            m_szImei = TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // READ_PHONE_STATE

        // DEVICE ID
        String m_szDevIDShort = "00" + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
                + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10 + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
                + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
                + Build.TYPE.length() % 10 + Build.USER.length() % 10;
        // 3 android ID - unreliable
        String m_szAndroidID = Secure.getString(getActivity()
                .getContentResolver(), Secure.ANDROID_ID);

        // 4 wifi manager, read MAC address - requires
        // android.permission.ACCESS_WIFI_STATE or comes as null
        WifiManager wm = (WifiManager)getActivity().getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

        // 5 Bluetooth MAC address android.permission.BLUETOOTH required
        // BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // String m_szBTMAC = m_BluetoothAdapter.getAddress();

        // 6 SUM THE IDs
        String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID
                + m_szWLANMAC;// + m_szBTMAC;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        byte p_md5Data[] = m.digest();

        String m_szUniqueID = "";
        for (byte aP_md5Data : p_md5Data) {
            int b = (0xFF & aP_md5Data);
            // if it is a single digit, make sure it have 0 in front (proper
            // padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }
        m_szUniqueID = m_szUniqueID.toUpperCase(Locale.CHINA);

        String brand = android.os.Build.BRAND;
        String model = android.os.Build.MODEL;

        return ("品牌：" + brand + "\n设备型号：" + model + "\nIMEI：" + m_szImei
                + "\n生成的设备号：" + m_szDevIDShort + "\nAndroidID：" + m_szAndroidID
                + "\n无线MAC：" + m_szWLANMAC //+ "\n蓝牙MAC：" + m_szBTMAC
                + "\n\nUNIQUE ID：\n" + m_szUniqueID);
    }

    class MyAdapter extends BaseAdapter {
        private final List<String> list;

        public MyAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getActivity(), R.layout.listitem_setting, null);
            TextView text = (TextView) convertView.findViewById(R.id.text);

            text.setText(list.get(position));

            return convertView;
        }
    }
}
