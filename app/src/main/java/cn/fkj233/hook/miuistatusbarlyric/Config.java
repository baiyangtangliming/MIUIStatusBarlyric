package cn.fkj233.hook.miuistatusbarlyric;


import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    JSONObject config;
    String fanse = "关闭";
    int lyricMaxWidth = 35;
    Boolean lyricService = true;
    int lyricWidth = 35;
    JSONObject temp;
    int timeWidth = -1;

    public Config() {
        try {
            if (ConfigTools.getConfig().equals("")) {
                this.temp = new JSONObject();
                this.config = new JSONObject();
                return;
            }
            this.temp = new JSONObject(ConfigTools.getConfig() + "");
            this.config = new JSONObject(this.temp.getString("配置文件"));
        } catch (JSONException e) {
        }
    }

    public void setLyricService(Boolean bool) {
        try {
            this.config.put("总开关", bool);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public Boolean getLyricService() {
        try {
            return (Boolean) this.config.get("总开关");
        } catch (JSONException e) {
            return true;
        }
    }

    public void setLyricWidth(int i) {
        try {
            this.config.put("歌词宽度", i);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public int getLyricWidth() {
        try {
            return (Integer) this.config.get("歌词宽度");
        } catch (JSONException e) {
            return 35;
        }
    }

    public void setLyricMaxWidth(int i) {
        try {
            this.config.put("歌词最大宽度", i);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public int getLyricMaxWidth() {
        try {
            return (Integer) this.config.get("歌词最大宽度");
        } catch (JSONException e) {
            return 35;
        }
    }

    public void setTimeWidth(int i) {
        try {
            this.config.put("时间宽度", i);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public int getTimeWidth() {
        try {
            return (Integer) this.config.get("时间宽度");
        } catch (JSONException e) {
            return -1;
        }
    }

    public void setLyricAnim(String str) {
        try {
            this.config.put("歌词动效", str);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public String getLyricAnim() {
        try {
            return (String) this.config.get("歌词动效");
        } catch (JSONException e) {
            return "关闭";
        }
    }

    public void setFanse(String str) {
        try {
            this.config.put("反色模式", str);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public String getFanse() {
        try {
            return (String) this.config.get("反色模式");
        } catch (JSONException e) {
            return "关闭";
        }
    }

    public void setAodLyricService(Boolean bool) {
        try {
            this.config.put("息屏歌词", bool);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public Boolean getAodLyricService() {
        try {
            return (Boolean) this.config.get("息屏歌词");
        } catch (JSONException e) {
            return false;
        }
    }

    public void setSrcService(Boolean bool) {
        try {
            this.config.put("防烧屏", bool);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public Boolean getSrcService() {
        try {
            return (Boolean) this.config.get("防烧屏");
        } catch (JSONException e) {
            return true;
        }
    }

    public void setSign(String str) {
        try {
            this.config.put("自定义签名", str);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public String getSign() {
        try {
            return (String) this.config.get("自定义签名");
        } catch (JSONException e) {
            return "";
        }
    }

    public void setLyricOff(Boolean bool) {
        try {
            this.config.put("歌曲暂停自动关闭歌词", bool);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public Boolean getLyricOff() {
        try {
            if (this.config.get("歌曲暂停自动关闭歌词") == null) {
                return false;
            }
            return (Boolean) this.config.get("歌曲暂停自动关闭歌词");
        } catch (JSONException e) {
            return false;
        }
    }

    public void setHideNoti(Boolean bool) {
        try {
            this.config.put("隐藏通知图标", bool);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public Boolean getHideNoti() {
        try {
            return (Boolean) this.config.get("隐藏通知图标");
        } catch (JSONException e) {
            return false;
        }
    }

    public void setLyricModel(String str) {
        try {
            this.config.put("歌词获取模式", str);
            this.temp.put("配置文件", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
        }
    }

    public String getLyricModel() {
        try {
            return (String) this.config.get("歌词获取模式");
        } catch (JSONException e) {
            return "增强模式";
        }
    }

    public void setIcon(String str) {
        try {
            this.config.put("图标模式", str);
            this.temp.put("图标配置", this.config);
            ConfigTools.setConfig(ConfigTools.formateJson(this.temp.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIcon() {
        if (this.config == null) {
            return new Config().getIcon();
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