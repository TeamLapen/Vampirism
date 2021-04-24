package de.teamlapen.lib.lib.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ScrollableArrayTextComponentList extends ScrollableListWidget<Pair<Integer, ITextComponent>> {

    public ScrollableArrayTextComponentList(int xPos, int yPos, int width, int height, int itemHeight, @Nonnull Supplier<ITextComponent[]> baseValueSupplier, @Nonnull Consumer<Integer> buttonPressed) {
        super(xPos, yPos, width, height, itemHeight, () -> getItems(baseValueSupplier), (item, list) -> new TextComponentItem<>(item, list, buttonPressed));
    }

    public ScrollableArrayTextComponentList(int xPos, int yPos, int width, int height, int itemHeight, int valueAmount, TextComponent baseName, @Nonnull Consumer<Integer> buttonPressed) {
        super(xPos, yPos, width, height, itemHeight, () -> getItems(createTextArray(valueAmount, baseName)),(item, list) -> new TextComponentItem<>(item, list, buttonPressed), baseName);
    }

    private static Collection<Pair<Integer, ITextComponent>> getItems(Supplier<ITextComponent[]> baseValueSupplier){
        List<ITextComponent> list = Arrays.asList(baseValueSupplier.get());
        return list.stream().map(item -> Pair.of(list.indexOf(item), item)).collect(Collectors.toList());
    }

    private static Supplier<ITextComponent[]> createTextArray(int amount, TextComponent baseName) {
        ITextComponent[] array = new ITextComponent[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = baseName.copyRaw().appendString(" "+(i+1));
        }
        return () -> array;
    }

    public static class TextComponentItem<T> extends ListItem<Pair<T,ITextComponent>> {

        @Nonnull
        private final Consumer<T> onClick;

        public TextComponentItem(@Nonnull Pair<T,ITextComponent> item, @Nonnull ScrollableListWidget<Pair<T,ITextComponent>> list, @Nonnull Consumer<T> onClick) {
            super(item, list);
            this.onClick = onClick;
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x-1, y, listWidth+1, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            int width = font.getStringPropertyWidth(this.item.getRight());
            if (width > listWidth) {
                width = listWidth;
            }

            Minecraft.getInstance().fontRenderer.func_243246_a(matrixStack, this.item.getRight(), x + (listWidth/2) - (width/2), y + 7,-1);

        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            onClick.accept(this.item.getLeft());
            return true;
        }
    }
}
