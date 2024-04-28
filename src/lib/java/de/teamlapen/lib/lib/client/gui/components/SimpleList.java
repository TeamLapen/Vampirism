package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SimpleList<T extends SimpleList.Entry<T>> extends VisibleObjectSelectionList<T> {

    public SimpleList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pItemHeight);
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderDecorations(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        graphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom() + 4, -16777216, 0);
        graphics.fillGradient(this.getX(), this.getY() - 4, this.getRight() - 6, this.getBottom(), 0, -16777216);
    }

    @Override
    protected void renderItem(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        super.renderItem(graphics, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth - 6, pHeight);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int p_283242_, int p_282891_, float p_283683_) {
        guiGraphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom(), -1072689136, -804253680);
        super.renderWidget(guiGraphics, p_283242_, p_282891_, p_283683_);
    }

    @Override
    protected void renderSelection(GuiGraphics p_283589_, int p_240142_, int p_240143_, int p_240144_, int p_240145_, int p_240146_) {
//        super.renderSelection(p_283589_, p_240142_,p_240143_ +6, p_240144_, p_240145_, p_240146_);
    }

    @Override
    protected int getScrollbarPosition() {
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
    protected int getRowTop(int pIndex) {
        return super.getRowTop(pIndex) - 4;
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, super.getMaxScroll() - 4);
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
        protected List<Pair<Component, Runnable>> components;

        public Builder(int x, int y, int pWidth, int pHeight) {
            this.x = x;
            this.y = y;
            this.pWidth = pWidth;
            this.pHeight = pHeight;
        }

        public Builder<T> itemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
            return this;
        }

        public Builder<T> components(List<Component> components) {
            this.components = components.stream().map(x -> Pair.of(x, (Runnable) () -> {
            })).toList();
            return this;
        }

        public Builder<T> componentsWithClick(List<Pair<Component, Runnable>> components) {
            this.components = components;
            return this;
        }

        public Builder<T> componentsWithClick(List<Component> components, Consumer<Integer> onClick) {
            this.components = components.stream().map(x -> Pair.<Component, Runnable>of(x, () -> onClick.accept(components.indexOf(x)))).toList();
            return this;
        }

        public SimpleList<T> build() {
            SimpleList<T> simpleList = new SimpleList<>(Minecraft.getInstance(), this.pWidth, this.pHeight, this.y, this.itemHeight);
            simpleList.setX(this.x);
            //noinspection unchecked
            simpleList.replaceEntries(((Collection<T>) components.stream().map(x -> new Entry<T>(x.getKey(), x.getValue())).toList()));
            return simpleList;
        }
    }

    public static class Entry<T extends Entry<T>> extends ObjectSelectionList.Entry<T> {
        protected static final WidgetSprites SPRITES = new WidgetSprites(new ResourceLocation("widget/button"), new ResourceLocation("widget/button_disabled"), new ResourceLocation("widget/button_highlighted"));

        private final Component component;
        private final Runnable onClick;

        public Entry(Component component, Runnable onClick) {
            this.component = component;
            this.onClick = onClick;
        }

        @Override
        public @NotNull Component getNarration() {
            return this.component;
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(0, 0, pIsMouseOver ? 2 : 1);
            graphics.blitSprite(SPRITES.get(true, pIsMouseOver), pLeft, pTop, pWidth, pHeight + 5);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.drawCenteredString(minecraft.font, this.component, pLeft + pWidth / 2, pTop + 5, 0xFFFFFF);
            pose.popPose();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            this.onClick.run();
            return true;
        }
    }
}
