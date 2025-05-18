package main;

public abstract class Item {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public abstract void use(Player player);

    public String getName() {
        return name;
    }
}