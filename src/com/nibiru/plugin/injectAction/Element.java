package com.nibiru.plugin.injectAction;


public class Element {
    public String id;
    public String type;
    public String vartriablename;
    public boolean used = true;

    public String getVartriablename() {
        return vartriablename;
    }

    public void setVartriablename(String vartriablename) {
        this.vartriablename = vartriablename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

}
