package com.cagatay.javacpptest.constants;

public enum PathConfig {
    VIDEO_PATH("C:\\xampp\\htdocs\\javacpptest\\");

    private String path;

    PathConfig(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
