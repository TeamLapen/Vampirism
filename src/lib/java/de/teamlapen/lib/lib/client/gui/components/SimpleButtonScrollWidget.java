package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleButtonScrollWidget extends ScrollWidget<Component> {

    private final List<Component> components;
    private final @Nullable Consumer<Integer> buttonClickConsumer;
    private final @Nullable BiConsumer<Integer, Boolean> buttonHoverConsumer;

    public SimpleButtonScrollWidget(int pX, int pY, int pWidth, int pHeight, List<Component> buttonTexts, @Nullable Consumer<Integer> buttonClickConsumer, @Nullable BiConsumer<Integer, Boolean> buttonHoverConsumer) {
        super(pX, pY, pWidth - 8, pHeight);
        this.components = buttonTexts;
        this.buttonHoverConsumer = buttonHoverConsumer;
        this.buttonClickConsumer = buttonClickConsumer;
        Font font = Minecraft.getInstance().font;
        this.updateContent((s, x, y, width, isXYInside) -> {
            if (buttonHoverConsumer != null) {
                return new HoverButton(x, y, width, 18, s, font, isXYInside, this::onButtonClick, this::onButtonHover);
            } else {
                return new ModifiedButton(x, y, width, 18, s, font, isXYInside, this::onButtonClick);
            }
        }, builder -> {
            for (Component text : buttonTexts) {
                builder.addWidget(text);
            }
        });
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

    @Override
    protected int getMaxScrollAmount() {
        return Math.max(0, this.getInnerHeight() - this.height + this.totalInnerPadding());
    }

    public static Builder builder(int x, int y, int width, int height) {
        return new Builder(x, y, width, height);
    }

    public static class Builder {
        private int x;
        private int y;
        private int width;
        private int height;
        private List<Component> components;
        private Consumer<Integer> buttonClickConsumer;
        private BiConsumer<Integer, Boolean> buttonHoverConsumer;

        protected Builder(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setPosition(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
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

        public SimpleButtonScrollWidget build() {
            return new SimpleButtonScrollWidget(x, y, width, height, components, buttonClickConsumer, buttonHoverConsumer);
        }
    }

    private static class ModifiedButton extends ExtendedButton {

        private final BiFunction<Integer, Integer, Boolean> isXYInside;

        public ModifiedButton(int xPos, int yPos, int width, int height, Component text, Font font, BiFunction<Integer, Integer, Boolean> isXYInside, Consumer<Component> onClick) {
            super(xPos, yPos, width, height, text, (i) -> onClick.accept(text));
            this.isXYInside = isXYInside;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            this.isHovered = this.isHovered && this.isXYInside.apply(mouseX, mouseY);
            super.renderButton(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            return super.clicked(pMouseX, pMouseY) && this.isXYInside.apply((int) pMouseX, (int) pMouseY);
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            super.onClick(pMouseX, pMouseY);
        }
    }

    private static class HoverButton extends ModifiedButton {

        private final BiConsumer<Component, Boolean> onHover;
        private boolean hoverActive;

        public HoverButton(int xPos, int yPos, int width, int height, Component text, Font font, BiFunction<Integer, Integer, Boolean> isXYInside, Consumer<Component> onClick, BiConsumer<Component, Boolean> onHover) {
            super(xPos, yPos, width, height, text, font, isXYInside, onClick);
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
