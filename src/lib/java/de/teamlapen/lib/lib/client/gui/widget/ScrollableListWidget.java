package de.teamlapen.lib.lib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * This Widget does everything by itself except:
 * - {@link #mouseDragged(double, double, int, double, double)} must be called in {@link net.minecraft.client.gui.screens.Screen#mouseDragged(double, double, int, double, double)}
 * - {@link #renderToolTip(PoseStack, int, int)} must be called in {@link net.minecraft.client.gui.screens.Screen#render(PoseStack, int, int, float)}
 */
public class ScrollableListWidget<T> extends ExtendedButton {

    private static final ResourceLocation MISC = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/misc.png");
    protected final List<ListItem<T>> listItems = new ArrayList<>();
    private final @NotNull ItemCreator<T> itemSupplier;
    private final int itemHeight;
    private final int scrollerWidth = 9;
    private final @NotNull Supplier<Collection<T>> baseValueSupplier;
    private int scrolled;
    private double scrolledD;
    private boolean scrollerClicked;
    private boolean canScroll = true;
    private double scrollSpeed = 1D;

    public ScrollableListWidget(int xPos, int yPos, int width, int height, int itemHeight, @NotNull Supplier<Collection<T>> baseValueSupplier, @NotNull ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, Component.literal(""), (button) -> {
        });
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
        this.baseValueSupplier = baseValueSupplier;
        this.refresh();
    }

    public ScrollableListWidget(int xPos, int yPos, int width, int height, int itemHeight, @NotNull Supplier<Collection<T>> baseValueSupplier, @NotNull ItemCreator<T> itemSupplier, Component name) {
        super(xPos, yPos, width, height, name, (button) -> {
        });
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
        this.baseValueSupplier = baseValueSupplier;
        this.refresh();
    }

    /**
     * adds a new element at the end of the list
     */
    public void addItem(@NotNull T element) {
        this.listItems.add(this.itemSupplier.apply(element, this));
        this.setCanScroll();
    }

    public boolean isEmpty() {
        return this.listItems.isEmpty();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) return false;
        this.scrolledD = this.scrolled;
        if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height) {
            if (mouseX > this.x + this.width - this.scrollerWidth) {
                this.scrollerClicked = true;
            } else {
                for (int i = 0; i < this.listItems.size(); i++) {

                    int y = i * itemHeight - scrolled;

                    if (y < -itemHeight) {
                        continue;
                    }


                    ListItem<T> item = this.listItems.get(i);
                    if (mouseX > this.x + 1 && mouseX < this.x + this.width - this.scrollerWidth && mouseY > this.y + 1 + y && mouseY < this.y + 1 + y + this.itemHeight) {
                        if (item.onClick(mouseX, mouseY)) {
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.visible && this.canScroll && this.scrollerClicked) {
            double perc = (dragY / (this.height - 27));
            double s = (this.listItems.size() * this.itemHeight - this.height) * perc;
            this.scrolledD += s;
            this.scrolled = ((int) scrolledD);
            this.scrolled = Mth.clamp(this.scrolled, 0, this.listItems.size() * this.itemHeight - this.height + 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrollerClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!this.visible) return false;
        if (this.canScroll) {
            this.scrolled = Mth.clamp(this.scrolled + 4 * ((int) -(delta * this.scrollSpeed)), 0, this.listItems.size() * this.itemHeight - this.height + 2);
            this.scrolledD = scrolled;
            return true;
        }
        return false;
    }

    public void refresh() {
        this.setItems(this.baseValueSupplier.get());
    }

    public void removeItem(T element) {
        this.listItems.removeIf(item -> item.item == element);
        this.setCanScroll();
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        matrixStack.pushPose();
        RenderSystem.enableDepthTest();
        matrixStack.translate(0, 0, 950);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.translate(0.0F, 0.0F, -950.0F);

        RenderSystem.depthFunc(518);
        matrixStack.translate(this.x, this.y, 0);
        fill(matrixStack, this.width, this.height, 0, 0, -0xff0000);
        matrixStack.translate(-x, -y, 0);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();

        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);


        this.renderItems(matrixStack, mouseX, mouseY, partialTicks);

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(518);
        matrixStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.translate(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        matrixStack.popPose();

        this.renderToolTip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height + 1) {

            int itemHeight = this.itemHeight; // only 1 pixel between items
            for (int i = 0; i < this.listItems.size(); i++) {

                int y = i * itemHeight - scrolled;

                if (y < -itemHeight) {
                    continue;
                }

                ListItem<T> item = this.listItems.get(i);
                item.preRenderToolTip(matrixStack, this.x, this.y + y + 1, this.width - scrollerWidth, this.height, this.itemHeight, mouseX, mouseY, this.getBlitOffset());

            }
        }
    }

    public ScrollableListWidget<T> scrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        return this;
    }

    public void setItems(@NotNull Collection<T> elements) {
        this.listItems.clear();
        elements.forEach(item -> this.listItems.add(this.itemSupplier.apply(item, this)));
        this.setScrolled(0);
        this.setCanScroll();
    }

    /**
     * adds the newElement after the afterElement in this list
     */
    protected void addItem(@NotNull ListItem<T> newElement, @NotNull ListItem<T> afterElement) {
        this.listItems.add(this.listItems.indexOf(afterElement) + 1, newElement);
        this.setCanScroll();
    }

    protected void removeItem(ListItem<T> item) {
        this.listItems.remove(item);
        this.setCanScroll();
    }

    private void renderBackground(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ScreenUtils.blitWithBorder(matrixStack, new ResourceLocation("textures/gui/widgets.png"), x, y, 0, 46, this.width - this.scrollerWidth + 1, this.height, 200, 20, 3, 3, 3, 3, this.getBlitOffset());
    }

    private void renderItems(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int itemHeight = this.itemHeight; // only 1 pixel between items
        for (int i = 0; i < this.listItems.size(); i++) {

            int y = i * itemHeight - scrolled;

            ListItem<T> item = this.listItems.get(i);
            item.render(matrixStack, this.x + 1, this.y + 1 + y, this.width - scrollerWidth - 1, this.height, this.itemHeight, mouseX, mouseY, partialTicks, this.getBlitOffset());

        }
        this.renderScrollBar(matrixStack, mouseX, mouseY, partialTicks);
        this.hLine(matrixStack, this.x, this.x + width - 1, this.y, 0xff000000);
        this.hLine(matrixStack, this.x, this.x + width - 1, this.y + height - 1, 0xff000000);
    }

    private void renderScrollBar(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ScreenUtils.blitWithBorder(matrixStack, MISC, this.x + this.width - this.scrollerWidth, this.y, 0, 0, 9, this.height, 9, 200, 2, getBlitOffset());
        this.renderScroller(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderScroller(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int scrollerHeight = 27;
        int scrollHeight = this.height - 2 - scrollerHeight;
        float perc = (float) this.scrolled / (float) (this.listItems.size() * this.itemHeight - this.height + 2);
        int yOffset = (int) (scrollHeight * perc);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);
        RenderSystem.setShaderTexture(0, MISC);
        int yMaxSize = Mth.clamp(this.itemHeight * this.listItems.size()-2,0,27);
        blit(matrixStack, this.x + this.width - this.scrollerWidth + 1, this.y + yOffset + 1, this.canScroll ? 9 : 16, 0, 7, yMaxSize);
    }

    private void setCanScroll() {
        this.canScroll = this.listItems.size() * this.itemHeight > this.height;
    }

    private void setScrolled(int scrolled) {
        this.scrolledD = this.scrolled = scrolled;
    }

    @FunctionalInterface
    public interface ItemCreator<T> {
        ListItem<T> apply(@NotNull T item, @NotNull ScrollableListWidget<T> list);
    }

    public abstract static class ListItem<T> {

        private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

        @NotNull
        protected final T item;
        @NotNull
        protected final ScrollableListWidget<T> list;

        /**
         * @param item the presenting item
         * @param list the list this item is owned by
         */
        public ListItem(@NotNull T item, @NotNull ScrollableListWidget<T> list) {
            this.item = item;
            this.list = list;
        }

        /**
         * called when the element is clicked
         */
        public boolean onClick(double mouseX, double mouseY) {
            return false;
        }

        /**
         * checks whether the mouse is over the element or not and calls {@link #renderToolTip(PoseStack, int, int, int, int, int, int, int, float)} appropriately
         *
         * @param x          x start position of the list item
         * @param y          y start position of the list item
         * @param listWidth  width of the list/list item
         * @param listHeight height of the list
         * @param itemHeight height of the list item
         */
        public void preRenderToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
            int ySize = Mth.clamp(listHeight, 0, itemHeight);

            if (mouseX > x && mouseX < x + listWidth && mouseY > y && mouseY < y + ySize) {
                this.renderToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, zLevel);
            }
        }

        /**
         * renders the element itself
         *
         * @param x          x start position of the list item
         * @param y          y start position of the list item
         * @param listWidth  width of the list/list item
         * @param listHeight height of the list
         * @param itemHeight height of the list item
         */
        public void render(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            int v = 66;
            if (mouseX >= x && mouseX < x + listWidth && mouseY >= y && mouseY < y + itemHeight) {
                v = 86;
            }
            RenderSystem.enableDepthTest();
            ScreenUtils.blitWithBorder(matrixStack, WIDGETS, x, y, 0, v, listWidth + 1, itemHeight, 200, 20, 3, 3, 3, 3, zLevel);
            RenderSystem.disableDepthTest();
        }

        /**
         * renders the tooltip when the mouse is over the element
         *
         * @param x          x start position of the list item
         * @param y          y start position of the list item
         * @param listWidth  width of the list/list item
         * @param listHeight height of the list
         * @param itemHeight height of the list item
         */
        public void renderToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
        }
    }
}
