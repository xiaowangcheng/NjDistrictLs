package net.htwater.njdistrictfx.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.adapter.FunctionAdapter;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("ValidFragment")
public class ChildFunctionFragment extends Fragment {
    private String index;

    public ChildFunctionFragment() {

    }

    public static Fragment getInstance(String index) {
        Fragment fragment = new ChildFunctionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_child_function, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);

        Bundle bundle = getArguments();
        if (null != bundle) {
            index = bundle.getString("index");
        }
        String key = "class_id#" + index;
        String menu = SharedPreferencesUtil.getMenu();
        List<String> newFunctionList = new ArrayList<>();
        try {
            JSONObject menuObject = new JSONObject(menu);
            JSONArray jsonArray = menuObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String menuValue = jsonObject.getString("menu");
                newFunctionList.add(menuValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // List<String> functionList = DataUtil.getFunctionList(index);
        FunctionAdapter adapter = new FunctionAdapter(getActivity(), handle(newFunctionList));
        gridView.setAdapter(adapter);
        return view;
    }

    private List<String> handle(List<String> list) {
        String[] functions = SharedPreferencesUtil.getFunctionSubitemList().split(",");
        if (functions.length == 1) {
            return list;
        }
        List<String> visibleList = Arrays.asList(functions);
        List<String> resultList = new ArrayList<>();
        for (String x : list) {
            if (visibleList.contains(x)) {
                resultList.add(x);
            }
        }
        return resultList;
    }
}