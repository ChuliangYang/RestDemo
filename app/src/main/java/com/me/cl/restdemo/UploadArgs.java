package com.me.cl.restdemo;

/**
 * Created by CL on 7/31/18.
 */
public class UploadArgs {
    /**
     * path : /Homework/math/Matrices.txt
     * mode : add
     * autorename : true
     * mute : false
     * strict_conflict : false
     */

    private String path="/Homework/math/test.txt";
    private String mode="add";
    private boolean autorename=true;
    private boolean mute=false;
    private boolean strict_conflict=false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isAutorename() {
        return autorename;
    }

    public void setAutorename(boolean autorename) {
        this.autorename = autorename;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isStrict_conflict() {
        return strict_conflict;
    }

    public void setStrict_conflict(boolean strict_conflict) {
        this.strict_conflict = strict_conflict;
    }
}
