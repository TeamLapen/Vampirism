package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

public class SimpleButtonScrollWidget extends ScrollWidget<Component, SimpleButtonScrollWidget.ModifiedButton> {

    public SimpleButtonScrollWidget(int pX, int pY, int pWidth, int pHeight, WidgetFactory<Component, SimpleButtonScrollWidget.ModifiedButton> widgetFactory, Consumer<ContentBuilder<Component, SimpleButtonScrollWidget.ModifiedButton>> contentSupplier, Component emptyText) {
        super(pX, pY, pWidth - 8, pHeight, widgetFactory, contentSupplier, emptyText);
    }

    @Override
    protected int getMaxScrollAmount() {
        return Math.max(0, this.getInnerHeight() - this.height + this.totalInnerPadding());
    }

    public static Builder builder(int x, int y, int width, int height) {
        return new Builder(x, y, width, height);
    }

    public static class Builder extends ScrollWidget.Builder<Component, SimpleButtonScrollWidget.ModifiedButton>{
        private List<Component> components;
        private Consumer<Integer> buttonClickConsumer;
        private BiConsumer<Integer, Boolean> buttonHoverConsumer;

        protected Builder(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        public Builder setComponents(List<Component> components) {
            this.components = components;
            return this;
        }

        public Builder setComponents(Component... components) {
            this.components = Arrays.asList(components);
            return this;
        }

        public Builder setComponents(int count, Function<Integer,Component> componentFunction) {
            List<Component> components = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                components.add(componentFunction.apply(i));
            }
            this.components = components;
            return this;
        }

        public Builder setButtonClickConsumer(Consumer<Integer> buttonClickConsumer) {
            this.buttonClickConsumer = buttonClickConsumer;
            return this;
        }

        public Builder setButtonHoverConsumer(BiConsumer<Integer, Boolean> buttonHoverConsumer) {
            this.buttonHoverConsumer = buttonHoverConsumer;
            return this;
        }

        @Override
        public ScrollWidget.Builder<Component, ModifiedButton> widgetFactory(WidgetFactory<Component, ModifiedButton> widgetFactory) {
            throw new UnsupportedOperationException("Cannot set content supplier for this builder");
        }

        @Override
        public ScrollWidget.Builder<Component, ModifiedButton> contentSupplier(Consumer<ContentBuilder<Component, ModifiedButton>> contentSupplier) {
            throw new UnsupportedOperationException("Cannot set content supplier for this builder");
        }

        @Override
        public Builder emptyText(Component emptyText) {
            super.emptyText(emptyText);
            return this;
        }

        private void onButtonClick(Component component) {
            if (this.buttonClickConsumer != null) {
                int index = components.indexOf(component);
                this.buttonClickConsumer.accept(index);
            }

        }

        private void onButtonHover(Component component, boolean isHovered) {
            if (this.buttonHoverConsumer != null) {
                int index = components.indexOf(component);
                this.buttonHoverConsumer.accept(index, isHovered);
            }
        }

        private ModifiedButton simpleWidgetFactory(Component s, int x, int y, int width, Supplier<Vector2dc> c, Consumer<ModifiedButton> v) {
            Font font = Minecraft.getInstance().font;
            if (buttonHoverConsumer != null) {
                return new HoverButton(x, y, width, 18, s, font, Builder.this::onButtonClick, Builder.this::onButtonHover);
            } else {
                return new ModifiedButton(x, y, width, 18, s, font, Builder.this::onButtonClick);
            }
        }

        private void buildContent(ContentBuilder<Component, ModifiedButton> builder) {
            for (Component text : this.components) {
                builder.addWidget(text);
            }
        }

        public SimpleButtonScrollWidget build() {
            return new SimpleButtonScrollWidget(this.x, this.y, this.width, this.height, this::simpleWidgetFactory, this::buildContent, this.emptyText);
        }
    }

    protected static class ModifiedButton extends ExtendedButton implements ItemWidget<Component> {

        private final Component component;

        public ModifiedButton(int xPos, int yPos, int width, int height, Component text, Font font, Consumer<Component> onClick) {
            super(xPos, yPos, width, height, text, (i) -> onClick.accept(text));
            this.component = text;
        }

        @Override
        public Component getItem() {
            return this.component;
        }
    }

    private static class HoverButton extends ModifiedButton {

        private final BiConsumer<Component, Boolean> onHover;
        private boolean hoverActive;

        public HoverButton(int xPos, int yPos, int width, int height, Component text, Font font, Consumer<Component> onClick, BiConsumer<Component, Boolean> onHover) {
            super(xPos, yPos, width, height, text, font, onClick);
            this.onHover = onHover;
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            if (this.visible) {
                if (this.isHovered && !this.hoverActive) {
                    this.onHover.accept(this.getMessage(), this.hoverActive = true);
                } else if (!this.isHovered && this.hoverActive) {
                    this.onHover.accept(this.getMessage(), this.hoverActive = false);
                }

            }
        }
    }
}
