package de.teamlapen.faction.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class ContainerObjectSelectionListWithDummy<Z, T extends ContainerObjectSelectionListWithDummy<Z, T, U>.ItemEntry, U extends ContainerObjectSelectionListWithDummy<Z, T, U>.DummyEntry> extends ContainerObjectSelectionList<ContainerObjectSelectionListWithDummy.Entry<Z>> {

    private final Supplier<List<Z>> itemSupplier;
    private @Nullable DummyEntry dummyItem;

    public ContainerObjectSelectionListWithDummy(Minecraft minecraft, int width, int height, int y0, int itemHeight, Supplier<List<Z>> itemSupplier) {
        super(minecraft, width, height, y0, itemHeight);
        this.itemSupplier = itemSupplier;
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int p_283242_, int p_282891_, float p_283683_) {
        int color = 0xFFFFFFFF;
        graphics.horizontalLine(this.getX() - 1, this.getRight() - 6, this.getY() - 1, color);
        graphics.horizontalLine(this.getX() - 1, this.getRight() - 6, this.getBottom(), color);
        graphics.verticalLine(this.getX() - 1, this.getY() - 1, this.getBottom() + 1, color);
        graphics.verticalLine(this.getRight() - 6, this.getY() - 1, this.getBottom() + 1, color);
        graphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom(), 0xFF000000, 0xFF000000);
        super.extractWidgetRenderState(graphics, p_283242_, p_282891_, p_283683_);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft() - 2;
    }

    @Override
    public int scrollBarY() {
        return this.getRight() - 5;
    }

    @Override
    public int getRowTop(int pIndex) {
        return super.getRowTop(pIndex) - 4;
    }

    @Override
    public int maxScrollAmount() {
        return Math.max(0, super.maxScrollAmount() - 4);
    }

    protected abstract T createItem(Z item);

    protected abstract U createDummy(Z item);

    protected void addEntry(int index, Entry<Z> entry) {
        this.children().add(index, entry);
    }

    protected void addEntryAfter(Entry<Z> index, Entry<Z> entry) {
        this.children().add(this.children().indexOf(index) + 1, entry);
    }

    public void updateContent() {
        double scrollAmount = this.scrollAmount();
        //noinspection unchecked
        this.replaceEntries((List<Entry<Z>>) (Object) this.itemSupplier.get().stream().map(this::createItem).toList());
        if (this.dummyItem != null) {
            Z item = this.dummyItem.getItem();
            this.dummyItem = null;
            this.children().stream().filter(e -> isEquivalent(e.getItem(), item)).findFirst().ifPresent(this::selectItem);
        }
        this.setScrollAmount(scrollAmount);
    }

    protected boolean isEquivalent(Z item1, Z item2) {
        return item1 == item2;
    }

    protected void selectItem(Entry<Z> item) {
        double scrollAmount = this.scrollAmount();
        if (this.dummyItem != null) {
            this.removeEntry(this.dummyItem);
        }
        if (this.dummyItem != null && this.dummyItem.getItem() == item.getItem()) {
            this.dummyItem = null;
        } else {
            this.dummyItem = this.createDummy(item.getItem());
            this.addEntryAfter(item, this.dummyItem);
        }
        this.setScrollAmount(scrollAmount);
    }

    public static abstract class Entry<Z> extends ContainerObjectSelectionList.Entry<Entry<Z>> {

        private final Z item;

        public Entry(Z item) {
            this.item = item;
        }

        public Z getItem() {
            return item;
        }
    }

    public abstract class ItemEntry extends Entry<Z> {
        public ItemEntry(Z item) {
            super(item);
        }

        @Override
        public boolean mouseClicked(@NotNull MouseButtonEvent p_445873_, boolean p_433971_) {
            selectItem(this);
            return true;
        }
    }

    public abstract class DummyEntry extends Entry<Z> {
        public DummyEntry(Z item) {
            super(item);
        }
    }
}