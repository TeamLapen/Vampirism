package de.teamlapen.lib.lib.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SimpleButtonScrollWidget extends ScrollWidget<Component> {

    private final List<Component> components;
    private final Consumer<Integer> buttonAction;
    public SimpleButtonScrollWidget(int pX, int pY, int pWidth, int pHeight, List<Component> buttonTexts, Consumer<Integer> buttonAction) {
        super(pX, pY, pWidth - 8, pHeight);
        this.components = buttonTexts;
        this.buttonAction = buttonAction;
        Font font = Minecraft.getInstance().font;
        this.updateContent((s, x, y, width, isXYInside) -> new ExtendedButton(x, y, width, 18, s, font, this::onButtonClick, isXYInside), builder -> {
            for (Component text : buttonTexts) {
                builder.addWidget(text);
            }
        });
    }

    private void onButtonClick(Component component) {
        int index = components.indexOf(component);
        this.buttonAction.accept(index);
    }

    @Override
    protected int getMaxScrollAmount() {
        return Math.max(0, this.getInnerHeight() - this.height + this.totalInnerPadding());
    }

    private static class ExtendedButton extends net.minecraftforge.client.gui.widget.ExtendedButton {

        private final BiFunction<Integer, Integer, Boolean> isXYInside;

        public ExtendedButton(int xPos, int yPos, int width, int height, Component text, Font font, Consumer<Component> onClick, BiFunction<Integer, Integer, Boolean> isXYInside) {
            super(xPos, yPos, width, height, text, (i) -> onClick.accept(text));
            this.isXYInside = isXYInside;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            this.isHovered = this.isHovered && this.isXYInside.apply(mouseX, mouseY);
            super.renderButton(poseStack, mouseX, mouseY, partialTick);
        }
    }
}
