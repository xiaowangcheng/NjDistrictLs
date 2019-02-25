package net.htwater.njdistrictfx.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by LZY on 2017/4/27.
 */
public class NewComparator implements Comparator<JSONObject> {
    private final int order;
    private final int index;

    /**
     * @param order 0表示降序，1表示升序
     * @param index 0~4依次表示今天、昨天、前天、三天、七天
     */
    public NewComparator(int order, int index) {
        super();
        this.order = order;
        this.index = index;
    }

    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        try {
            float number1, number2;
            String numberString1 = "0", numberString2 = "0";
            switch (index) {
                case 0:
                    numberString1 = lhs.getString("TVALUE");
                    numberString2 = rhs.getString("TVALUE");
                    break;
                case 1:
                    numberString1 = lhs.getString("YVALUE");
                    numberString2 = rhs.getString("YVALUE");
                    break;
                case 2:
                    numberString1 = lhs.getString("BYVALUE");
                    numberString2 = rhs.getString("BYVALUE");
                    break;
                case 3:
                    numberString1 = lhs.getString("TTVALUE");
                    numberString2 = rhs.getString("TTVALUE");
                    break;
                case 4:
                    numberString1 = lhs.getString("STVALUE");
                    numberString2 = rhs.getString("STVALUE");
                    break;
            }
            number1 = Float.valueOf(numberString1);
            number2 = Float.valueOf(numberString2);
            if (order == 0) {
                // 降序排列
                if (number1 > number2) {
                    return -1;
                } else if (number1 == number2) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                // 升序排列
                if (number1 > number2) {
                    return 1;
                } else if (number1 == number2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
