package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ScrollWidget<T> extends AbstractScrollWidget {

    private GridWidget content = new GridWidget();

    public ScrollWidget(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.empty());
    }

    @Override
    protected int getInnerHeight() {
        return this.content.getHeight();
    }

    @Override
    protected boolean scrollbarVisible() {
        return this.getInnerHeight() > this.height;
    }

    @Override
    protected double scrollRate() {
        return 9.0;
    }

    @Override
    protected int innerPadding() {
        return 1;
    }

    protected int containerWidth() {
        return this.width - totalInnerPadding();
    }

    protected int containerHeight() {
        return this.height - totalInnerPadding();
    }

    public double getScrollAmount() {
        return this.scrollAmount();
    }

    @Override
    protected void renderContents(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        pMouseY += this.scrollAmount();
        int i = this.innerPadding();
        int j = this.innerPadding();
        pPoseStack.pushPose();
        pPoseStack.translate(i,j,0);
        this.content.setPosition(this.getX(), this.getY());
        this.content.render(pPoseStack,pMouseX,pMouseY,pPartialTick);
        pPoseStack.popPose();
    }

    public void updateContent(WidgetFactory<T> func, Consumer<ContentBuilder<T>> consumer) {
        ContentBuilder<T> builder = new ContentBuilder<>(func, this.containerWidth(), this::withinScrolledContentAreaPoint);
        consumer.accept(builder);
        this.content = builder.build();
        this.setScrollAmount(Math.min(this.getScrollAmount(), this.getMaxScrollAmount()));
    }

    protected boolean withinScrolledContentAreaPoint(double pX, double pY) {
        return pX >= (double)this.getX() && pX < (double)(this.getX() + this.width) && pY >= (double)this.getY() + this.scrollAmount() && pY < (double)(this.getY() + this.height + scrollAmount());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean result = super.mouseClicked(pMouseX, pMouseY, pButton);
        if (!result && (result = this.withinContentAreaPoint(pMouseX, pMouseY))) {
            this.content.mouseClicked(pMouseX, pMouseY, pButton);
        }
        return result;
    }

    public static class ContentBuilder<T> {
        private final GridWidget grid;
        private final int width;
        private final GridWidget.RowHelper helper;
        private final LayoutSettings alignHeader;
        private final WidgetFactory<T> widgetFactory;
        private final BiFunction<Integer, Integer, Boolean> isXYInside;

        public ContentBuilder(WidgetFactory<T> widgetFactory, int innerWidth, BiFunction<Integer, Integer, Boolean> isXYInside) {
            this.width = innerWidth;
            this.widgetFactory = widgetFactory;
            this.isXYInside = isXYInside;
            this.grid = new GridWidget();
            this.grid.defaultCellSetting().alignHorizontallyLeft();
            this.helper = this.grid.createRowHelper(1);
            this.alignHeader = this.helper.newCellSettings().alignHorizontallyCenter().paddingHorizontal(0);
        }

        public void addWidget(AbstractWidget widget) {
            this.helper.addChild(widget, this.alignHeader);
        }

        public void addWidget(T widget) {
            this.helper.addChild(widgetFactory.create(widget, 0,0, this.width, this.isXYInside), this.alignHeader);
        }

        public void addSpacer(int height) {
            this.helper.addChild(SpacerWidget.height(height));
        }

        GridWidget build() {
            this.grid.pack();
            return this.grid;
        }
    }

    @FunctionalInterface
    public interface WidgetFactory<T> {
        AbstractWidget create(T t, int x, int y, int width, BiFunction<Integer, Integer, Boolean> isXYInside);
    }
}
