package edu.hawaii.its.mis.service;

public class Result {

    private String value;

    // Constructor.
    public Result(String value) {
        this.value = nv(value);
    }

    public String getValue() {
        return value;
    }

    // Helper method.
    protected String nv(String s) {
        return s != null ? s : "";
    }
}
