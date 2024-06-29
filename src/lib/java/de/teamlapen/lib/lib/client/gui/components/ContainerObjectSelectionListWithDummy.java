package de.teamlapen.lib.lib.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.util.Mth;
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
    protected void renderListBackground(GuiGraphics p_331297_) {
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int p_283242_, int p_282891_, float p_283683_) {
        int color = 0xFFFFFFFF;
        graphics.hLine(this.getX() - 1, this.getRight() - 6, this.getY() - 1, color);
        graphics.hLine(this.getX() - 1, this.getRight() - 6, this.getBottom(), color);
        graphics.vLine(this.getX() - 1, this.getY() - 1, this.getBottom() + 1, color);
        graphics.vLine(this.getRight() - 6, this.getY() - 1, this.getBottom() + 1, color);
        graphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom(), 0xFF000000, 0xFF000000);
        super.renderWidget(graphics, p_283242_, p_282891_, p_283683_);
    }

    @Override
    protected void renderDecorations(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        graphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getY() + 4, -16777216, 0);
        graphics.fillGradient(this.getX(), this.getBottom() - 4, this.getRight() - 6, this.getBottom(), 0, -16777216);
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
    protected void renderItem(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        super.renderItem(graphics, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth - 6, pHeight);
    }

    @Nullable
    @Override
    protected Entry<Z> getEntryAtPosition(double pMouseX, double pMouseY) {
        int i = this.getRowWidth() / 2;
        int j = this.getX() + this.width / 2;
        int k = j - i;
        int l = j + i;
        int i1 = Mth.floor(pMouseY - (double) this.getY()) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int j1 = i1 / this.itemHeight;
        return pMouseX >= (double) k && pMouseX <= (double) l - 6 && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - 5;
    }

    @Override
    protected int getRowTop(int pIndex) {
        return super.getRowTop(pIndex) - 4;
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, super.getMaxScroll() - 4);
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
        double scrollAmount = this.getScrollAmount();
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
        double scrollAmount = this.getScrollAmount();
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
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
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