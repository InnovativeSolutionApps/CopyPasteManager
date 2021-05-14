package app;

public class SimpleBook {
    private String time;
    private String content;

    public String getsNo() {
        return sNo;
    }

    public void setsNo(String sNo) {
        this.sNo = sNo;
    }

    private String sNo;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public SimpleBook( String sNo, String time, String content) {
        this.sNo = sNo;
        this.time = time;
        this.content = content;
    }


}
