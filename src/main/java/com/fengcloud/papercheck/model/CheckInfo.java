package com.fengcloud.papercheck.model;

public class CheckInfo {
    /**
     * 检测状态：待检测
     */
    public static int CheckStatus_ToCheck = 1;

    /**
     * 检测状态：检测中
     */
    public static int CheckStatus_Checking = 2;
    /**
     * 检测状态：检测成功
     */
    public static int CheckStatus_CheckSuccess = 3;
    /**
     * 检测状态：检测失败
     */
    public static int CheckStatus_CheckFailed = 4;
    /**
     * 总共多少条待检测数据
     */
    private int total;
    /**
     * 当前对象是第几条数据
     */
    private int index;
    /**
     * 检测状态，默认待检测
     */
    private int checkStatus = 1;
    /**
     * 被检索字符串
     */
    private String check;
    /**
     * 是否重复
     */
    private boolean repeat;
    /**
     * 抄袭的文章
     */
    private String title;
    /**
     * 文章地址
     */
    private String url;
    /**
     * 文章作者
     */
    private String author;
    /**
     * 出版的杂志
     */
    private String book;
    /**
     * 出版时间
     */
    private String scTime;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScTime() {
        return scTime;
    }

    public void setScTime(String scTime) {
        this.scTime = scTime;
    }

    @Override
    public String toString() {
        return "CheckInfo{" +
                "total=" + total +
                ", index=" + index +
                ", checkStatus=" + checkStatus +
                ", check='" + check + '\'' +
                ", repeat='" + repeat + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", book='" + book + '\'' +
                ", scTime='" + scTime + '\'' +
                '}';
    }
}
