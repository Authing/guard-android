package cn.authing.guard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Organization implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Organization[]> parse(JSONArray array) throws JSONException {
        List<Organization[]> list = new ArrayList<>();
        for (int i = 0, n = array.length(); i < n; i++) {
            JSONArray tree = array.getJSONArray(i);
            int length = tree.length();
            Organization[] organizations = new Organization[length];
            for (int j = 0;j < length;++j) {
                Organization o = new Organization();
                JSONObject obj = tree.getJSONObject(j);
                if (obj.has("id")) {
                    o.setId(obj.getString("id"));
                }
                if (obj.has("name")) {
                    o.setName(obj.getString("name"));
                }
                organizations[j] = o;
            }
            list.add(organizations);
        }
        return list;
    }
}
