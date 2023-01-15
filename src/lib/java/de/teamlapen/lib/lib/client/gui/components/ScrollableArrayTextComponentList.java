package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @deprecated use {@link de.teamlapen.lib.lib.client.gui.components.SimpleButtonScrollWidget} instead
 */
@Deprecated
public class ScrollableArrayTextComponentList extends ScrollableListComponent<Pair<Integer, Component>> {

    private static @NotNull Collection<Pair<Integer, Component>> getItems(@NotNull Supplier<Component[]> baseValueSupplier) {
        Collection<Pair<Integer, Component>> items = new ArrayList<>();
        Component[] baseValues = baseValueSupplier.get();
        for (int i = 0; i < baseValues.length; i++) {
            items.add(Pair.of(i, baseValues[i]));
        }
        return items;
    }

    private static @NotNull Supplier<Component[]> createTextArray(int amount, @NotNull MutableComponent baseName) {
        Component[] array = new Component[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = baseName.plainCopy().append(" " + (i + 1));
        }
        return () -> array;
    }

    public ScrollableArrayTextComponentList(int xPos, int yPos, int width, int height, int itemHeight, @NotNull Supplier<Component[]> baseValueSupplier, @NotNull Consumer<Integer> buttonPressed) {
        super(xPos, yPos, width, height, itemHeight, () -> getItems(baseValueSupplier), (item, list) -> new TextComponentItem<>(item, list, buttonPressed));
    }

    public ScrollableArrayTextComponentList(int xPos, int yPos, int width, int height, int itemHeight, int valueAmount, @NotNull MutableComponent baseName, @NotNull Consumer<Integer> buttonPressed) {
        super(xPos, yPos, width, height, itemHeight, () -> getItems(createTextArray(valueAmount, baseName)), (item, list) -> new TextComponentItem<>(item, list, buttonPressed), baseName);
    }

    public ScrollableArrayTextComponentList(int xPos, int yPos, int width, int height, int itemHeight, int valueAmount, @NotNull MutableComponent baseName, @NotNull Consumer<Integer> buttonPressed, @Nullable BiConsumer<Integer, Boolean> onHover) {
        super(xPos, yPos, width, height, itemHeight, () -> getItems(createTextArray(valueAmount, baseName)), (item, list) -> new TextComponentItem<>(item, list, buttonPressed, onHover), baseName);
    }

    @Override
    public ScrollableArrayTextComponentList scrollSpeed(double scrollSpeed) {
        return (ScrollableArrayTextComponentList) super.scrollSpeed(scrollSpeed);
    }

    public static class TextComponentItem<T> extends ListItem<Pair<T, Component>> {

        @NotNull
        private final Consumer<T> onClick;
        @Nullable
        private final BiConsumer<T, Boolean> onHover;
        private boolean hovered;

        public TextComponentItem(@NotNull Pair<T, Component> item, @NotNull ScrollableListComponent<Pair<T, Component>> list, @NotNull Consumer<T> onClick) {
            super(item, list);
            this.onClick = onClick;
            this.onHover = null;
        }

        public TextComponentItem(@NotNull Pair<T, Component> item, @NotNull ScrollableListComponent<Pair<T, Component>> list, @NotNull Consumer<T> onClick, @Nullable BiConsumer<T, Boolean> onHover) {
            super(item, list);
            this.onClick = onClick;
            this.onHover = onHover;
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            this.onClick.accept(this.item.getLeft());
            return true;
        }

        @Override
        public void render(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x - 1, y, listWidth + 1, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            Font font = Minecraft.getInstance().font;
            int width = font.width(this.item.getRight());
            if (width > listWidth) {
                width = listWidth;
            }

            Minecraft.getInstance().font.drawShadow(matrixStack, this.item.getRight(), x + (listWidth / 2F) - (width / 2F), y + 7, -1);

            if (this.onHover != null) {
                boolean newHovered = mouseX >= x && mouseX < x + listWidth && mouseY >= y && mouseY < y + itemHeight;
                if (newHovered != this.hovered) {
                    this.onHover(this.hovered = newHovered);
                }
            }
        }

        protected void onHover(boolean hovered) {
            this.onHover.accept(this.item.getLeft(), hovered);
        }
    }
}
