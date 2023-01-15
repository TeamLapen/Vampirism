package de.teamlapen.lib.lib.client.gui.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.network.chat.Component;
import org.joml.Vector2dc;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScrollWidgetWithDummyItem<T, Z extends AbstractWidget & ScrollWidget.ItemWidget<T>> extends ScrollWidget<T, Z> {

    private final DummyFactory<T> dummyFactory;
    private final Consumer<LayoutSettings> dummyLayoutSettings;
    private Z dummyItem;

    public ScrollWidgetWithDummyItem(int pX, int pY, int pWidth, int pHeight, WidgetFactory<T, Z> widgetFactory, Consumer<ContentBuilder<T, Z>> contentSupplier, DummyFactory<T> dummyFactory, Consumer<LayoutSettings> dummyLayoutSettings, Component emptyText) {
        super(pX, pY, pWidth, pHeight, widgetFactory, contentSupplier, emptyText);
        this.dummyFactory = dummyFactory;
        this.dummyLayoutSettings = dummyLayoutSettings;
    }

    @Override
    protected void onClick(Z widget) {
        if (this.dummyItem != null) {
            this.content.removeWidget(this.dummyItem);
        }
        if (this.dummyItem != null && this.dummyItem.getItem() == widget.getItem()) {
            this.dummyItem = null;
        } else {
            LayoutSettings layoutSettings = this.content.childLayoutSettings().copy();
            this.dummyLayoutSettings.accept(layoutSettings);
            int width = this.containerWidth() - layoutSettings.getExposed().paddingLeft - layoutSettings.getExposed().paddingRight;
            this.dummyItem = this.dummyFactory.create(widget.getItem(), 0, 0, width, this::getOffsetAmount);
            this.content.addWidgetAfter(widget, this.dummyItem, layoutSettings);
        }
        this.content.pack();
        this.setScrollAmount(Math.min(this.scrollAmount(), this.getMaxScrollAmount()));
    }

    @Override
    public void updateContent() {
        double scrollAmount = this.scrollAmount();
        super.updateContent();
        resetDummyItem();
        this.setScrollAmount(Math.min(scrollAmount, this.getMaxScrollAmount()));
    }

    private void resetDummyItem() {
        if (this.dummyItem != null) {
            T item = this.dummyItem.getItem();
            this.dummyItem = null;
            this.content.getContainedChildren().stream().filter(w -> {
                if (w instanceof ScrollWidget.ItemWidget) {
                    ScrollWidget.ItemWidget<T> itemWidget = (ScrollWidget.ItemWidget<T>) w;
                    return itemWidget.getItem() == item;
                }
                return false;
            }).findAny().ifPresent(s ->  {
                this.onClick((Z) s);
            });
        }
    }

    @FunctionalInterface
    public interface DummyFactory<T> {
        <Z extends AbstractWidget & ItemWidget<T>> Z create(T t, int x, int y, int width, Supplier<Vector2dc> scrollAmountSupplier);
    }

    public static <T,Z extends AbstractWidget & ScrollWidget.ItemWidget<T>> Builder<T,Z> builder(int x, int y, int width, int height) {
        return new Builder<>(x, y, width, height);
    }

    public static class Builder<T, Z extends AbstractWidget & ScrollWidget.ItemWidget<T>> extends ScrollWidget.Builder<T,Z> {

        private DummyFactory<T> dummyFactory;
        private Consumer<LayoutSettings> dummyLayoutSettings;

        protected Builder(int x, int y, int width, int height) {
            super(x, y, width, height);
        }


        public Builder<T,Z> dummyFactory(DummyFactory<T> dummyFactory) {
            this.dummyFactory = dummyFactory;
            return this;
        }

        public Builder<T,Z> dummyLayoutSettings(Consumer<LayoutSettings> dummyLayoutSettings) {
            this.dummyLayoutSettings = dummyLayoutSettings;
            return this;
        }

        @Override
        public Builder<T, Z> widgetFactory(WidgetFactory<T, Z> widgetFactory) {
            super.widgetFactory(widgetFactory);
            return this;
        }

        @Override
        public Builder<T, Z> contentSupplier(Consumer<ContentBuilder<T, Z>> contentSupplier) {
            super.contentSupplier(contentSupplier);
            return this;
        }

        @Override
        public Builder<T, Z> emptyText(Component emptyText) {
            super.emptyText(emptyText);
            return this;
        }

        @Override
        public ScrollWidgetWithDummyItem<T, Z> build() {
            return new ScrollWidgetWithDummyItem<>(this.x, this.y, this.width, this.height, this.widgetFactory, this.contentSupplier, this.dummyFactory, this.dummyLayoutSettings, this.emptyText);
        }
    }
}
