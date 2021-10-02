package cn.fkj233.hook.miuistatusbarlrcy;

import org.json.JSONException;
import org.json.JSONObject;

public class Config2 {
    JSONObject config;
    JSONObject temp;

    public Config2() {
        try {
            if (ConfigTools2.getConfig().equals("")) {
                this.temp = new JSONObject();
                this.config = new JSONObject();
                return;
            }
            this.temp = new JSONObject(ConfigTools2.getConfig() + "");
            this.config = new JSONObject(this.temp.getString("图标配置"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setIcon(String str) {
        try {
            this.config.put("图标模式", str);
            this.temp.put("图标配置", this.config);
            ConfigTools2.setConfig(ConfigTools2.formateJson(this.temp.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIcon() {
        if (this.config == null) {
            return new Config2().getIcon();
        }
        try {
            if (this.config.get("图标模式") == null) {
                return "关闭";
            }
            return (String) this.config.get("图标模式");
        } catch (JSONException e) {
            return "关闭";
        }
    }
}