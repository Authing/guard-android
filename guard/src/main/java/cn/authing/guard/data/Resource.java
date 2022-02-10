package cn.authing.guard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.util.Util;

public class Resource implements Serializable {
    private String code;
    private String type;
    private List<String> actions = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public static List<Resource> parse(JSONArray array) throws JSONException {
        List<Resource> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            Resource o = new Resource();
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("code")) {
                o.setCode(obj.getString("code"));
            }
            if (obj.has("type")) {
                o.setType(obj.getString("type"));
            }
            if (obj.has("actions")) {
                JSONArray actions = obj.getJSONArray("actions");
                o.setActions(Util.toStringList(actions));
            }
            list.add(o);
        }
        return list;
    }
}
