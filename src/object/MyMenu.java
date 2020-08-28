package object;

public class MyMenu {
    private int id;
    private String name;
    private double price;
    private int count;
    public MyMenu(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount() {
        this.count++;
    }

    public void minusCount() {
        if (this.count > 0) {
            this.count--;
        }
    }
}
