package com.example.zhiwenyan.recoder;



public class Recorder {
    private String path;
    private float time;

    public Recorder() {
    }

    public Recorder(String path, float time) {
        this.path = path;
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Recorder{" +
                "path='" + path + '\'' +
                ", time=" + time +
                '}';
    }
}
