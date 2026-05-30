package de.teamlapen.gui.components.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class ObjectSelectionListExtended<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {

    public ObjectSelectionListExtended(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.repositionEntries();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        this.repositionEntries();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.repositionEntries();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.repositionEntries();
    }
}
