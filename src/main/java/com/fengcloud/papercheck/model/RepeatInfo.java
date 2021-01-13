package com.fengcloud.papercheck.model;

/**
 * 检测结果信息
 */
public class RepeatInfo {
    private int total;//总字数
    private int repeat;//重复字数
    private double repeatRadio;//重复占比

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public double getRepeatRadio() {
        return repeatRadio;
    }

    public void setRepeatRadio(double repeatRadio) {
        this.repeatRadio = repeatRadio;
    }
}
