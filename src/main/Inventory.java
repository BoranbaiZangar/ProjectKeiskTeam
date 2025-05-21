package main;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items = new ArrayList<>();
    private int maxSlots = 10;

    public boolean addItem(Item item) {
        if (items.size() < maxSlots) {
            items.add(item);
            System.out.println("Добавлен предмет: " + item.getName());
            return true;
        }
        System.out.println("Инвентарь полон! Нельзя добавить: " + item.getName());
        return false;
    }

    public boolean useItem(String name, Player player) {
        for (Item item : items) {
            if (item.getName().equals(name)) {
                item.use(player);
                items.remove(item);
                return true;
            }
        }
        return false;
    }

    public List<Item> getItems() {
        return items;
    }
}