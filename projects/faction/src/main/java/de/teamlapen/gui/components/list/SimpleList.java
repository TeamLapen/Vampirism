package de.teamlapen.gui.components.list;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.gui.components.IComponentWithAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleList<T extends SimpleList.Entry<T>> extends VisibleObjectSelectionList<T> {

    public SimpleList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pItemHeight);
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
    }

//    @Override
//    public void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int p_283242_, int p_282891_, float p_283683_) {
////        GuiGraphicsExtractor.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom(), -1072689136, -804253680);
//        super.extractWidgetRenderState(GuiGraphicsExtractor, p_283242_, p_282891_, p_283683_);
//    }

    @Override
    public int scrollBarY() {
        return this.getRight() - 6;
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
    public int getRowTop(int pIndex) {
        return super.getRowTop(pIndex) - 4;
    }

    @Override
    public int maxScrollAmount() {
        return Math.max(0, super.maxScrollAmount() - 4);
    }

    public static Builder<?> builder(int x, int y, int pWidth, int pHeight) {
        return new Builder<>(x, y, pWidth, pHeight);
    }

    public static class Builder<T extends SimpleList.Entry<T>> {

        protected final int x;
        protected final int y;
        protected final int pWidth;
        protected final int pHeight;
        protected int itemHeight = 19;
        @Nullable
        protected Runnable anyClicked;
        protected Collection<IComponentWithAction> components = List.of();

        public Builder(int x, int y, int pWidth, int pHeight) {
            this.x = x;
            this.y = y;
            this.pWidth = pWidth;
            this.pHeight = pHeight;
        }

        public Builder<T> anyClicked(Runnable runnable) {
            this.anyClicked = runnable;
            return this;
        }

        public Builder<T> itemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
            return this;
        }

        public Builder<T> displayOnly(Collection<Component> components) {
            this.components = components.stream().map(IComponentWithAction::of).toList();
            return this;
        }

        public Builder<T> components(Collection<IComponentWithAction> components) {
            this.components = components;
            return this;
        }

        public Builder<T> components(List<Component> components, Consumer<Integer> onClick) {
            this.components = components.stream().map(x -> IComponentWithAction.of(x, () -> onClick.accept(components.indexOf(x)))).toList();
            return this;
        }

        public Builder<T> components(List<Component> components, Consumer<Integer> onClick, BiConsumer<Integer, Boolean> onHover) {
            this.components = components.stream().map(x -> IComponentWithAction.of(x, () -> onClick.accept(components.indexOf(x)), (b) -> onHover.accept(components.indexOf(x), b))).toList();
            return this;
        }

        public SimpleList<T> build() {
            SimpleList<T> simpleList = new SimpleList<>(Minecraft.getInstance(), this.pWidth, this.pHeight, this.y, this.itemHeight);
            //noinspection unchecked
            simpleList.replaceEntries(((Collection<T>) components.stream().map(x -> new Entry<>(x, this.anyClicked)).toList()));
            return simpleList;
        }
    }

    public static class Entry<T extends Entry<T>> extends ObjectSelectionList.Entry<T> {
        protected static final WidgetSprites SPRITES = new WidgetSprites(FIdentifier.mc("widget/button"), FIdentifier.mc("widget/button_disabled"), FIdentifier.mc("widget/button_highlighted"));

        private final IComponentWithAction action;
        @Nullable
        private final Runnable onClick;
        private boolean hovered;

        public Entry(IComponentWithAction action, @Nullable Runnable onClick) {
            this.action = action;
            this.onClick = onClick;
        }

        @Override
        public Component getNarration() {
            return this.action.component();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(true, isHovering), getContentX(), getContentY(), getContentWidth(), getContentHeight());
            GuiGraphicsExtractor.centeredText(Minecraft.getInstance().font, this.action.component(), getContentX() + getContentWidth() / 2, getContentY() + (getContentHeight() - 8) / 2, 0xFFFFFFFF);
            boolean newHovered = this.isMouseOver(mouseX, mouseY);
            if (newHovered != this.hovered) {
                this.hovered = newHovered;
                this.action.onHover().accept(this.hovered);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            this.action.action().run();
            if (this.onClick != null) {
                this.onClick.run();
            }
            return true;
        }
    }
}
