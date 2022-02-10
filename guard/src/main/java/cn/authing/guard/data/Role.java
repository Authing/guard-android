package cn.authing.guard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Role implements Serializable {
    private String id;
    private String code;
    private String description;
    private String namespace;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public static List<Role> parse(JSONArray array) throws JSONException {
        List<Role> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            Role role = new Role();
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("id")) {
                role.setId(obj.getString("id"));
            }
            if (obj.has("code")) {
                role.setCode(obj.getString("code"));
            }
            if (obj.has("description")) {
                role.setDescription(obj.getString("description"));
            }
            if (obj.has("namespace")) {
                role.setNamespace(obj.getString("namespace"));
            }
            list.add(role);
        }
        return list;
    }
}
