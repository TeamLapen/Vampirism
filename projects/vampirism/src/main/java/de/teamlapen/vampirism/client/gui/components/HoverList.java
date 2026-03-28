package de.teamlapen.vampirism.client.gui.components;

import de.teamlapen.faction.client.gui.components.SimpleList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HoverList<T extends HoverList.Entry<T>> extends SimpleList<T> {

    public HoverList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pItemHeight);
    }

    public static Builder<?> builder(int x, int y, int pWidth, int pHeight) {
        return new Builder<>(x, y, pWidth, pHeight);
    }

    public static class Builder<T extends Entry<T>> extends SimpleList.Builder<T> {

        protected List<Triple<Component, Runnable, Consumer<Boolean>>> components;

        public Builder(int x, int y, int pWidth, int pHeight) {
            super(x, y, pWidth, pHeight);
        }

        public Builder<T> componentsWithClickAndHover(List<Triple<Component, Runnable, Consumer<Boolean>>> components) {
            this.components = components;
            return this;
        }

        public Builder<T> componentsWithClickAndHover(List<? extends Component> components, Consumer<Integer> clickConsumer, BiConsumer<Integer, Boolean> hoverConsumer) {
            this.components = components.stream().map(x -> Triple.<Component, Runnable, Consumer<Boolean>>of(x, () -> clickConsumer.accept(components.indexOf(x)), hovered -> hoverConsumer.accept(components.indexOf(x), hovered))).toList();
            return this;
        }

        @Override
        public HoverList<T> build() {
            HoverList<T> simpleList = new HoverList<>(Minecraft.getInstance(), this.pWidth, this.pHeight, this.y, this.itemHeight);
            simpleList.setX(this.x);
            //noinspection unchecked
            simpleList.replaceEntries(((Collection<T>) components.stream().map(x -> new Entry<T>(x.getLeft(), x.getMiddle(), x.getRight())).toList()));
            return simpleList;
        }
    }

    public static class Entry<T extends Entry<T>> extends SimpleList.Entry<T> {

        private final Consumer<Boolean> onHover;
        private boolean hovered;

        public Entry(Component component, Runnable onClick, Consumer<Boolean> onHover) {
            super(component, onClick);
            this.onHover = onHover;
        }

        @Override
        public void renderContent(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            super.renderContent(GuiGraphicsExtractor, mouseX, mouseY, isHovering, partialTick);
            boolean newHovered = this.isMouseOver(mouseX, mouseY);
            if (newHovered != this.hovered) {
                this.hovered = newHovered;
                this.onHover.accept(this.hovered);
            }
        }
    }
}
