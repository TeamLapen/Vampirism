package de.teamlapen.lib.lib.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * This Widget does everything by itself except:
 * - {@link #mouseDragged(double, double, int, double, double)} must be called in {@link net.minecraft.client.gui.screen.Screen#mouseDragged(double, double, int, double, double)}
 * - {@link #renderToolTip(MatrixStack, int, int)} must be called in {@link net.minecraft.client.gui.screen.Screen#render(MatrixStack, int, int, float)}
 */
public class ScrollableListWidget<T> extends ExtendedButton {

    private static final ResourceLocation MISC = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/misc.png");

    private final ItemCreator<T> itemSupplier;
    private final int itemHeight;
    private final int scrollerWidth = 9;

    private final List<ListItem<T>> listItems = new ArrayList<>();
    private int scrolled;
    private double scrolledD;
    private boolean scrollerClicked;
    private final boolean canScroll = true;
    private final Supplier<Collection<T>> baseValueSupplier;

    public ScrollableListWidget(int xPos, int yPos, int width, int height, int itemHeight, @Nonnull Supplier<Collection<T>> baseValueSupplier, @Nonnull ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, new StringTextComponent(""), (button) -> {
        });
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
        this.baseValueSupplier = baseValueSupplier;
        this.refresh();
    }

    public void refresh() {
        this.setItems(this.baseValueSupplier.get());
    }

    public void setItems(@Nonnull Collection<T> elements) {
        this.listItems.clear();
        elements.forEach(item -> this.listItems.add(this.itemSupplier.apply(item, this)));
        this.setScrolled(0);
        this.setCanScroll();
    }

    /**
     * adds a new element at the end of the list
     */
    public void addItem(@Nonnull T element) {
        this.listItems.add(this.itemSupplier.apply(element, this));
        this.setCanScroll();
    }

    /**
     * adds the newElement after the afterElement in this list
     */
    public void addItem(@Nonnull ListItem<T> newElement, @Nonnull ListItem<T> afterElement) {
        this.listItems.add(this.listItems.indexOf(afterElement) + 1, newElement);
        this.setCanScroll();
    }

    public void removeItem(T element) {
        this.listItems.removeIf(item -> item.item == element);
//        if (this.scrolled > this.listItems.size() * this.itemHeight - this.height) {
//            this.setScrolled(this.listItems.size() * this.itemHeight - this.height);
//        }
        this.setCanScroll();
    }

    public void removeItem(ListItem<T> item) {
        this.listItems.remove(item);
//        int sc = this.listItems.size() * this.itemHeight - this.height;
//        if (this.scrolled > sc && sc >0) {
//            this.setScrolled(sc);
//        }
        this.setCanScroll();
    }

    private void setCanScroll() {
//        this.canScroll = this.listItems.size() * this.itemHeight > this.height;;
    }

    private void setScrolled(int scrolled) {
        this.scrolledD = this.scrolled = scrolled;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0,0,950);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);

        RenderSystem.depthFunc(518);
        matrixStack.translate(this.x,this.y, 0);
        fill(matrixStack, this.width, this.height, 0, 0, -0xff0000);
        matrixStack.translate(-x,-y,0);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();

        this.renderBackground(matrixStack, mouseX,  mouseY, partialTicks);


        this.renderItems(matrixStack, mouseX, mouseY, partialTicks);

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.popMatrix();

        this.renderToolTip(matrixStack, mouseX, mouseY);
    }

    private void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        GuiUtils.drawContinuousTexturedBox(matrixStack, new ResourceLocation("textures/gui/widgets.png"), x, y, 0, 46, this.width - this.scrollerWidth+1, this.height, 200, 20, 3, 3, 3, 3, this.getBlitOffset());
    }

    private void renderItems(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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

    private void renderScrollBar(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        GuiUtils.drawContinuousTexturedBox(matrixStack, MISC, this.x + this.width - this.scrollerWidth, this.y,0,0,9, this.height, 9, 200,2, getBlitOffset());
        this.renderScroller(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderScroller(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int scrollerHeight = 27;
        int scrollHeight = this.height - 2 - scrollerHeight;
        float perc = (float) this.scrolled / (float) (this.listItems.size() * this.itemHeight - this.height + 2);
        int yOffset = (int) (scrollHeight * perc);
        Minecraft.getInstance().textureManager.bindTexture(MISC);
        blit(matrixStack, this.x + this.width - this.scrollerWidth + 1, this.y + yOffset + 1, this.canScroll ? 9 : 16, 0, 7, 27);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.canScroll) {
            this.scrolled = MathHelper.clamp(this.scrolled + 4 * ((int) -delta), 0, this.listItems.size() * this.itemHeight - this.height + 2);
            this.scrolledD = scrolled;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(this.canScroll && this.scrollerClicked) {
            double perc = (dragY/(this.height-27));
            double s = (this.listItems.size() * this.itemHeight - this.height) * perc;
            this.scrolledD += s;
            this.scrolled = ((int) scrolledD);
            this.scrolled = MathHelper.clamp(this.scrolled, 0, this.listItems.size() * this.itemHeight - this.height + 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
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

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrollerClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @FunctionalInterface
    public interface ItemCreator<T> {
        ListItem<T> apply(@Nonnull T item, @Nonnull ScrollableListWidget<T> list);
    }

    public abstract static class ListItem<T> {

        private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

        @Nonnull
        protected final T item;
        @Nonnull
        protected final ScrollableListWidget<T> list;

        /**
         * @param item the presenting item
         * @param list the list this item is owned by
         */
        public ListItem(@Nonnull T item, @Nonnull ScrollableListWidget<T> list) {
            this.item = item;
            this.list = list;
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
        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            RenderSystem.enableDepthTest();
            GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS, x, y, 0, 66, listWidth + 1, itemHeight, 200, 20, 3, 3, 3, 3, zLevel);
            RenderSystem.disableDepthTest();
        }

        /**
         * checks whether the mouse is over the element or not and calls {@link #renderToolTip(MatrixStack, int, int, int, int, int, int, int, float)} appropriately
         *
         * @param x          x start position of the list item
         * @param y          y start position of the list item
         * @param listWidth  width of the list/list item
         * @param listHeight height of the list
         * @param itemHeight height of the list item
         */
        public void preRenderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
            int ySize = MathHelper.clamp(listHeight, 0, itemHeight);

            if (mouseX > x && mouseX < x + listWidth && mouseY > y && mouseY < y + ySize) {
                this.renderToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, zLevel);
            }
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
        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
        }

        /**
         * called when the element is clicked
         */
        public boolean onClick(double mouseX, double mouseY) {
            return false;
        }
    }
}
