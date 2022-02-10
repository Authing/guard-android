package cn.authing.guard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Application implements Serializable {
    private String id;
    private String name;
    private String logo;
    private String domain;
    private String description;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<Application> parse(JSONArray array) throws JSONException {
        List<Application> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            Application o = new Application();
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("id")) {
                o.setId(obj.getString("id"));
            }
            if (obj.has("name")) {
                o.setName(obj.getString("name"));
            }
            if (obj.has("logo")) {
                o.setLogo(obj.getString("logo"));
            }
            if (obj.has("domain")) {
                o.setDomain(obj.getString("domain"));
            }
            if (obj.has("description")) {
                o.setDescription(obj.getString("description"));
            }
            list.add(o);
        }
        return list;
    }
}
