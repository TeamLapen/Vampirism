package de.teamlapen.lib.lib.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScrollableListWithDummyWidget<T> extends ScrollableListWidget<T> {

    @Nullable
    private ListItem<T> dummyItem;
    @Nonnull
    private final ItemCreator<T> itemCreator;

    public ScrollableListWithDummyWidget(int xPos, int yPos, int width, int height, int itemHeight) {
        this(xPos, yPos, width, height, itemHeight, ListItem::new);
    }

    public ScrollableListWithDummyWidget(int xPos, int yPos, int width, int height, int itemHeight, @Nonnull ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, itemHeight, (item, list) -> itemSupplier.apply(item, (ScrollableListWithDummyWidget<T>) list, false));
        this.itemCreator = itemSupplier;
    }

    protected void clickItem(@Nonnull ListItem<T> listItem) {
        boolean flag = false;
        if (this.dummyItem != null) {
            flag = listItem.item == this.dummyItem.item;
            this.removeItem(this.dummyItem);
            this.dummyItem = null;
        }

        if (!flag) {
            this.addItem(this.dummyItem = this.itemCreator.apply(listItem.item, this, true), listItem);
        }
    }

    public static class ListItem<T> extends ScrollableListWidget.ListItem<T> {


        protected final boolean isDummy;

        public ListItem(T item, ScrollableListWithDummyWidget<T> list, boolean isDummy) {
            super(item, list);
            this.isDummy = isDummy;
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            if (this.isDummy) {
                this.renderDummy(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
            } else {
                this.renderItem(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
            }
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (this.isDummy) {
                this.renderDummyToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, zLevel);
            } else {
                this.renderItemToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, zLevel);
            }
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (this.isDummy) {
                return this.onDummyClick(mouseX, mouseY);
            } else {
                ((ScrollableListWithDummyWidget<T>) this.list).clickItem(this);
                return true;
            }
        }

        public void renderItem(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
        }

        public void renderDummy(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
        }

        public void renderItemToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            super.renderToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, zLevel);
        }

        public void renderDummyToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            super.renderToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, zLevel);
        }

        public boolean onDummyClick(double mouseX, double mouseY) {
            return super.onClick(mouseX, mouseY);
        }

    }

    @FunctionalInterface
    public interface ItemCreator<T> {
        ListItem<T> apply(T item, ScrollableListWithDummyWidget<T> list, boolean isDummy);
    }

}

