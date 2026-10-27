package cs.pattharaphon.atthajak.ubcf2026s3.model;

public class Candle {
    public Candle(int id, String name, String owner, String templeName) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.templeName = templeName;
    }

    private int id;

    private String name;

    private String owner;

    private String templeName;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getTempleName() {
        return templeName;
    }

}
