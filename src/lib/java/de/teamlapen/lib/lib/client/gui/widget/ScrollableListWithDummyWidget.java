package de.teamlapen.lib.lib.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * This is a {@link ScrollableListWidget} that supports a dummy element.
 * This dummy element has the same item as the element which created it.
 * This dummy element is capable to be rendered differently.
 *
 * @param <T> item that should be presented by a list entry
 */
public class ScrollableListWithDummyWidget<T> extends ScrollableListWidget<T> {

    @NotNull
    private final ItemCreator<T> itemCreator;
    @Nullable
    private ListItem<T> dummyItem;

    public ScrollableListWithDummyWidget(int xPos, int yPos, int width, int height, int itemHeight, @NotNull Supplier<Collection<T>> baseValueSupplier, @NotNull ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, itemHeight, baseValueSupplier, (item, list) -> itemSupplier.apply(item, (ScrollableListWithDummyWidget<T>) list, false));
        this.itemCreator = itemSupplier;
    }

    @Override
    public void refresh() {
        super.refresh();
        if (this.dummyItem != null) {
            this.listItems.stream().filter(l -> l.item.equals(this.dummyItem.item)).findAny().ifPresent(a -> this.addItem(this.dummyItem, a));
        }
    }

    protected void clickItem(@NotNull ListItem<T> listItem) {
        boolean flag = false;
        if (this.dummyItem != null) {
            flag = listItem.item.equals(this.dummyItem.item);
            this.removeItem(this.dummyItem);
            this.dummyItem = null;
        }

        if (!flag) {
            this.addItem(this.dummyItem = this.itemCreator.apply(listItem.item, this, true), listItem);
        }
    }

    @FunctionalInterface
    public interface ItemCreator<T> {
        ListItem<T> apply(T item, ScrollableListWithDummyWidget<T> list, boolean isDummy);
    }

    public abstract static class ListItem<T> extends ScrollableListWidget.ListItem<T> {


        protected final boolean isDummy;

        public ListItem(@NotNull T item, @NotNull ScrollableListWithDummyWidget<T> list, boolean isDummy) {
            super(item, list);
            this.isDummy = isDummy;
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

        public boolean onDummyClick(double mouseX, double mouseY) {
            return super.onClick(mouseX, mouseY);
        }

        @Override
        public void render(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            if (this.isDummy) {
                this.renderDummy(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            } else {
                this.renderItem(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            }
        }

        public abstract void renderDummy(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel);

        public abstract void renderDummyToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel);

        /**
         *
         */
        public abstract void renderItem(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel);

        public abstract void renderItemToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel);

        @Override
        public void renderToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
            if (this.isDummy) {
                this.renderDummyToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, zLevel);
            } else {
                this.renderItemToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, zLevel);
            }
        }

    }

}

