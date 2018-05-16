package Worker;

public class Bar {
    private String category;
    private double time;
    private double cumulateTime;
    private String date;
    private int hour;

    public Bar(String category, double time, double cumulateTime, String date, int hour) {
        this.category = category;
        this.time = time;
        this.cumulateTime = cumulateTime;
        this.date = date;
        this.hour = hour;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getCumulateTime() {
        return cumulateTime;
    }

    public void setCumulateTime(double cumulateTime) {
        this.cumulateTime = cumulateTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
