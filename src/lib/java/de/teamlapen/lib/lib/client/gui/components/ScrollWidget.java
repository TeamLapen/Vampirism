package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScrollWidget<T, Z extends AbstractWidget & ScrollWidget.ItemWidget<T>> extends AbstractScrollWidget {

    protected ListWidget content = new ListWidget();
    protected Consumer<ContentBuilder<T, Z>> contentSupplier;
    protected ContentBuilder<T, Z> builder;
    protected Component emptyText;

    public ScrollWidget(int pX, int pY, int pWidth, int pHeight, WidgetFactory<T, Z> widgetFactory, Consumer<ContentBuilder<T, Z>> contentSupplier, Component emptyText) {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.contentSupplier = contentSupplier;
        this.builder = createBuilder(widgetFactory, this.containerWidth(), this::getOffsetAmount, this::onClick);
        this.emptyText = emptyText;
        this.updateContent();
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

    public Vector2dc getOffsetAmount() {
        return new Vector2d(this.getX(), this.getY() - this.scrollAmount());
    }

    @Override
    protected void renderContents(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        pMouseY += this.scrollAmount();
        int i = this.getX() + this.innerPadding();
        int j = this.getY() + this.innerPadding();
        pPoseStack.pushPose();
        pPoseStack.translate(i,j,0);
        this.content.pack();
        this.content.render(pPoseStack, pMouseX - this.getX(), pMouseY - this.getY(), pPartialTick);
        pPoseStack.popPose();

        if (this.content.isEmpty()) {
            Font font = Minecraft.getInstance().font;
            int width = font.width(this.emptyText);
            if (width > 0) {
                font.drawShadow(pPoseStack, this.emptyText, this.getX() + this.width/2 - (width / 2F), this.getY() + this.height / 2, 0);
            }
        }
    }

    public void updateContent() {
        this.builder.reset();
        this.contentSupplier.accept(this.builder);
        this.content = this.builder.build();
        this.setScrollAmount(Math.min(this.scrollAmount(), this.getMaxScrollAmount()));
    }

    protected void onClick(Z widget) {

    }

    protected ContentBuilder<T, Z> createBuilder(WidgetFactory<T, Z> func, int width, Supplier<Vector2dc> scrollAmount, Consumer<Z> onClick) {
        return new ContentBuilder<>(func, width, scrollAmount, onClick);
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
        if (this.visible && !result && (result = this.withinContentAreaPoint(pMouseX, pMouseY))) {
            pMouseY += this.scrollAmount();
            this.content.mouseClicked(pMouseX - this.getX(), pMouseY - this.getY(), pButton);
        }
        return result;
    }

    public static <T,Z extends AbstractWidget & ScrollWidget.ItemWidget<T>> Builder<T,Z> builder(int x, int y, int width, int height) {
        return new Builder<>(x, y, width, height);
    }

    public boolean isEmpty() {
        return this.content.isEmpty();
    }

    public static class Builder<T, Z extends AbstractWidget & ScrollWidget.ItemWidget<T>> {
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected Consumer<T> onClick;
        protected WidgetFactory<T, Z> widgetFactory;
        protected Consumer<ContentBuilder<T, Z>> contentSupplier;
        protected Component emptyText = Component.empty();


        protected Builder(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Builder<T, Z> widgetFactory(WidgetFactory<T, Z> widgetFactory) {
            this.widgetFactory = widgetFactory;
            return this;
        }

        public Builder<T, Z> contentSupplier(Consumer<ContentBuilder<T, Z>> contentSupplier) {
            this.contentSupplier = contentSupplier;
            return this;
        }

        public Builder<T, Z> emptyText(Component emptyText) {
            this.emptyText = emptyText;
            return this;
        }

        public ScrollWidget<T,Z> build() {
            return new ScrollWidget<>(this.x, this.y, this.width, this.height, this.widgetFactory, this.contentSupplier, this.emptyText);
        }
    }
    public static class ContentBuilder<T,Z extends AbstractWidget & ItemWidget<T>>  {
        private final ListWidget list;
        private final int width;
        private final WidgetFactory<T,Z> widgetFactory;
        private final Consumer<Z> onClick;
        private final Supplier<Vector2dc> scrollAmount;

        public ContentBuilder(WidgetFactory<T,Z> widgetFactory, int innerWidth, Supplier<Vector2dc> scrollAmount, Consumer<Z> onClick) {
            this.width = innerWidth;
            this.widgetFactory = widgetFactory;
            this.onClick = onClick;
            this.list = new ListWidget(0,0,innerWidth);
            this.scrollAmount = scrollAmount;
        }

        public void addWidget(AbstractWidget widget) {
            this.list.addWidget(widget);
        }

        public void addSpacer(int height) {
            this.list.addWidget(SpacerWidget.height(height));
        }

        public ListWidget build() {
            this.list().pack();
            return list();
        }

        public void addWidget(T widget) {
            this.list.addWidget(create(widget, 0,0, this.width(), this.scrollAmount(), this.onClick()));
        }

        public int width() {
            return width;
        }

        public Z create(T widget, int x, int y, int width, Supplier<Vector2dc> scrollAmount, Consumer<Z> onClick) {
            return widgetFactory.create(widget, x, y, width, scrollAmount, onClick);
        }

        public Supplier<Vector2dc> scrollAmount() {
            return this.scrollAmount;
        }

        public Consumer<Z> onClick() {
            return this.onClick;
        }

        public void reset() {
            this.list.reset();
        }

        public ListWidget list() {
            return this.list;
        }
    }

    @FunctionalInterface
    public interface WidgetFactory<T, Z extends AbstractWidget & ItemWidget<T>> {
        Z create(T t, int x, int y, int width, Supplier<Vector2dc> scrollAmountSupplier, Consumer<Z> onClick);
    }

    public interface ItemWidget<T> {
        T getItem();
    }
}
