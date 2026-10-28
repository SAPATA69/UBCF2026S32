package cs.pattharaphon.atthajak.ubcf2026s3.model;

public class Temple {
    private final int id;
    private final String name;
    private final String description;

    public Temple(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
