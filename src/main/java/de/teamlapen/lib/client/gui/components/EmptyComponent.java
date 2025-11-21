package de.teamlapen.lib.client.gui.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EmptyComponent implements LayoutElement {

    private int x, y;
    private int width, height;

    public EmptyComponent(int width, int height) {
        this.height = height;
        this.width = width;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) {

    }
}
