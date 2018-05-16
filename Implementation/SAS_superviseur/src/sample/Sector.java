package sample;

public class Sector {
    private String name;
    private String category;
    private double time;
    private String date;

    public Sector(String name, String category, double time, String date) {
        this.name = name;
        this.category = category;
        this.time = time;
        this.date = date;
    }

    public Sector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
