package main;

import java.util.ArrayList;
import java.util.List;

public class Container {
    private final List<Item> container = new ArrayList<>();
    private int container_capacity = 15;

    public List<Item> getContainer() {
        return container;
    }

    public int getContainerSize(){
        return container.size();
    }

    public Item getItem(int index)
    {
        return container.get(index);
    }

    public String repr()
    {
        StringBuilder sb = new StringBuilder(container.size() * 15);
        sb.append("========================\n");
        for (int i = 0; i < container.size(); i++)
        {
            sb.append(String.format("Предмет |`%2d`| : %s\n", i + 1, container.get(i)));
        }
        return sb.append("========================").toString();
    }

    public void removeItem(int index)
    {
        Item removed_item = container.remove(index);
        //on_inventory_updated(removed_item);
    }

    public boolean putItem(Item item)
    {
        container.add(item);
       // on_inventory_updated(item);

        return true;
    }
}
