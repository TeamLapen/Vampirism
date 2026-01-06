package de.teamlapen.faction.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CollectionList<T extends CollectionList.Entry<T>> extends ContainerObjectSelectionList<T> {

    public CollectionList(int width, int height, int defaultItemHeight) {
        super(Minecraft.getInstance(), width, height, 0, defaultItemHeight);
    }

    public void add(T entry) {
        addEntry(entry);
    }

    public void add(T entry, int height) {
        addEntry(entry, height);
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

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public static abstract class Entry<T extends Entry<T>> extends ContainerObjectSelectionList.Entry<T> {

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return widgets();
        }

        @Override
        public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            widgets().forEach(w -> w.render(guiGraphics, mouseX, mouseY, partialTick));
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return widgets();
        }

        public abstract List<? extends AbstractWidget> widgets();

        protected abstract void repositionEntries(int width, int height, int x, int y);

        protected void updateWidget(AbstractWidget widget, int width, int height, int x, int y) {
            widget.setWidth(width);
            widget.setHeight(height);
            widget.setPosition(x, y);
        }

        @Override
        public void setWidth(int width) {
            super.setWidth(width);
            repositionEntries(getWidth(), getHeight(), getX(), getY());
        }

        @Override
        public void setHeight(int height) {
            super.setHeight(height);
            repositionEntries(getWidth(), getHeight(), getX(), getY());
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            repositionEntries(getWidth(), getHeight(), getX(), getY());
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            repositionEntries(getWidth(), getHeight(), getX(), getY());
        }
    }
}
