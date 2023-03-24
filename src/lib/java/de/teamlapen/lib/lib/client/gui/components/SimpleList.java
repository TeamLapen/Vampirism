package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SimpleList<T extends SimpleList.Entry<T>> extends VisibleObjectSelectionList<T> {

    public SimpleList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void renderDecorations(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        fillGradient(pPoseStack, this.x0, this.y0, this.x1 - 6, this.y0 + 4, -16777216, 0);
        fillGradient(pPoseStack, this.x0, this.y1 - 4, this.x1 - 6, this.y1, 0, -16777216);
    }

    @Override
    protected void renderItem(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        super.renderItem(pPoseStack, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth - 6, pHeight);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void renderBackground(@NotNull PoseStack pPoseStack) {
        fillGradient(pPoseStack, this.x0, this.y0, this.x1 - 6, this.y1, -1072689136, -804253680);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
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
            SimpleList<T> simpleList = new SimpleList<T>(Minecraft.getInstance(), this.pWidth, this.pHeight, this.y, this.y + this.pHeight, this.itemHeight);
            simpleList.setLeftPos(this.x);
            //noinspection unchecked
            simpleList.replaceEntries(((Collection<T>) components.stream().map(x -> new Entry<T>(x.getKey(), x.getValue())).toList()));
            return simpleList;
        }
    }

    public static class Entry<T extends Entry<T>> extends ObjectSelectionList.Entry<T> {
        protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

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
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            pPoseStack.pushPose();
            if (pIsMouseOver) {
                pPoseStack.translate(0, 0, 1);
            }
            blitNineSliced(pPoseStack, pLeft, pTop, pWidth, pHeight + 5, 20, 4, 200, 20, 0, this.getTextureY(pIsMouseOver));
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.drawCenteredString(pPoseStack, minecraft.font, this.component, pLeft + pWidth / 2, pTop + 5, 0xFFFFFF);
            pPoseStack.popPose();
        }

        public int getTextureY(boolean isMouseOver) {
            int i = 1;
            if (isMouseOver) {
                i = 2;
            }

            return 46 + i * 20;
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            this.onClick.run();
            return true;
        }
    }
}
