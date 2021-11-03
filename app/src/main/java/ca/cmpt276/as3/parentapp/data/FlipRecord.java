package ca.cmpt276.as3.parentapp.data;

public class FlipRecord {
    private int id;
    private String flipno;
    private String childname;
    private String result;
    private String time;
    private String iswin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlipno() {
        return flipno;
    }

    public void setFlipno(String flipno) {
        this.flipno = flipno;
    }

    public String getChildname() {
        return childname;
    }

    public void setChildname(String childname) {
        this.childname = childname;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIswin() {
        return iswin;
    }

    public void setIswin(String iswin) {
        this.iswin = iswin;
    }
}
