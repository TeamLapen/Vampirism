/*
 * Note: This code has been modified from David Quintana's solution.
 * Below is the required copyright notice.
 * Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of the contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package de.teamlapen.faction.client.gui.radialmenu;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.faction.client.IMinecraftAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.util.List;

//@EventBusSubscriber
public abstract class GuiRadialMenu<T> extends Screen implements IMinecraftAccessor {
    private static final float PRECISION = 5.0f;
    protected static final int MAX_SLOTS = 30;
    private static final float SECONDARY_RING_GAP = 3;
    private static final float SECONDARY_RING_WIDTH = 30;

    protected boolean closing;
    private final RadialMenu<T> radialMenu;
    private final boolean allowMouseDirection;
    protected final List<IRadialMenuSlot<T>> radialMenuSlots;
    protected final float OPEN_ANIMATION_LENGTH = 0.20f;
    protected float totalTime;
    protected long lastTime;
    /**
     * Zero-Based index
     */
    protected int selectedItem;
    /**
     * Zero-Based index into the secondary items of {@link #selectedItem} or -1 if the primary item is selected
     */
    protected int selectedSecondaryItem;


    public GuiRadialMenu(RadialMenu<T> radialMenu) {
        this(radialMenu, false);
    }

    public GuiRadialMenu(RadialMenu<T> radialMenu, boolean allowMouseDirection) {
        super(Component.literal(""));
        this.radialMenu = radialMenu;
        this.allowMouseDirection = allowMouseDirection;
        this.radialMenuSlots = this.radialMenu.getRadialMenuSlots();
        this.closing = false;
        this.selectedItem = -1;
        this.selectedSecondaryItem = -1;
    }

//    @SubscribeEvent TODO
//    public static void updateInputEvent(MovementInputUpdateEvent event) {
//        if (Minecraft.getInstance().screen instanceof GuiRadialMenu<?> screen) {
//
//            screen.processInputEvent(event);
//        }
//    }

    protected boolean isMouseOverMenuItems(double mouseDistanceToCenterOfScreen, float radiusIn, float radiusOut) {
        return allowMouseDirection ? mouseDistanceToCenterOfScreen >= 10 : mouseDistanceToCenterOfScreen >= radiusIn && mouseDistanceToCenterOfScreen < radiusOut;
    }

    protected boolean isMouseOverSecondaryItems(double mouseDistanceToCenterOfScreen, float radiusIn, float radiusOut) {
        return allowMouseDirection ? mouseDistanceToCenterOfScreen >= radiusIn : mouseDistanceToCenterOfScreen >= radiusIn && mouseDistanceToCenterOfScreen < radiusOut;
    }

    /**
     * @return the start angle in degrees of the given slot. Slot 0 is at the top, continuing clockwise
     */
    protected static float getSliceStartAngle(int slot, int numberOfSlices) {
        float sliceWidth = 360f / numberOfSlices;
        float angle = (slot - 0.5f) * sliceWidth - 90;
        if (numberOfSlices % 2 != 0) {
            angle += sliceWidth / 2;
        }
        return angle;
    }

    private static int getSegmentAt(double angle, float startAngle, float segmentWidth, int segments) {
        int segment = (int) (Mth.positiveModulo((float) angle - startAngle, 360f) / segmentWidth);
        return Mth.clamp(segment, 0, segments - 1);
    }

    private static int secondaryCount(IRadialMenuSlot<?> slot) {
        List<?> secondarySlotIcons = slot.secondaryItems();
        return secondarySlotIcons == null ? 0 : secondarySlotIcons.size();
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        if (lastTime == 0) {
            lastTime = System.nanoTime();
        }

        long currentTime = System.nanoTime();
        totalTime += Math.min((currentTime - lastTime) / 1_000_000_000f, 0.1f);
        lastTime = currentTime;

        float openAnimation = closing ? 1.0f - totalTime / OPEN_ANIMATION_LENGTH : totalTime / OPEN_ANIMATION_LENGTH;


        float animProgress = Mth.clamp(openAnimation, 0, 1);
        animProgress = (float) (1 - Math.pow(1 - animProgress, 3));
        float radiusIn = Math.max(0.1f, 45 * animProgress);
        float radiusOut = radiusIn * 2;
        float itemRadius = (radiusIn + radiusOut) * 0.5f;
        float secondaryRadiusIn = radiusOut + SECONDARY_RING_GAP * animProgress;
        float secondaryRadiusOut = secondaryRadiusIn + SECONDARY_RING_WIDTH * animProgress;
        float secondaryItemRadius = (secondaryRadiusIn + secondaryRadiusOut) * 0.5f;

        int centerOfScreenX = width / 2;
        int centerOfScreenY = height / 2;
        int numberOfSlices = Math.min(MAX_SLOTS, radialMenuSlots.size());
        if (numberOfSlices == 0) {
            return;
        }
        float sliceWidth = 360f / numberOfSlices;

        double mouseAngle = Math.toDegrees(Math.atan2(mouseY - centerOfScreenY, mouseX - centerOfScreenX));
        double mouseDistanceToCenterOfScreen = Math.sqrt(Math.pow(mouseX - centerOfScreenX, 2) + Math.pow(mouseY - centerOfScreenY, 2));

        if (!closing) {
            selectedItem = -1;
            selectedSecondaryItem = -1;
            int hoveredSlot = getSegmentAt(mouseAngle, getSliceStartAngle(0, numberOfSlices), sliceWidth, numberOfSlices);
            int secondaryCount = secondaryCount(radialMenuSlots.get(hoveredSlot));
            if (secondaryCount > 0 && isMouseOverSecondaryItems(mouseDistanceToCenterOfScreen, secondaryRadiusIn, secondaryRadiusOut)) {
                selectedItem = hoveredSlot;
                selectedSecondaryItem = getSegmentAt(mouseAngle, getSliceStartAngle(hoveredSlot, numberOfSlices), sliceWidth / secondaryCount, secondaryCount);
            } else if (isMouseOverMenuItems(mouseDistanceToCenterOfScreen, radiusIn, radiusOut)) {
                selectedItem = hoveredSlot;
            }
        }

        for (int i = 0; i < numberOfSlices; i++) {
            IRadialMenuSlot<T> slot = this.radialMenuSlots.get(i);
            float sliceStart = getSliceStartAngle(i, numberOfSlices);
            if (selectedItem == i && selectedSecondaryItem == -1) {
                drawSlice(slot, true, graphics, centerOfScreenX, centerOfScreenY, 10, radiusIn, radiusOut, sliceStart, sliceStart + sliceWidth, 63, 161, 191, 60);
            } else {
                drawSlice(slot, false, graphics, centerOfScreenX, centerOfScreenY, 10, radiusIn, radiusOut, sliceStart, sliceStart + sliceWidth, 0, 0, 0, 64);
            }

            int secondaryCount = secondaryCount(slot);
            float secondaryWidth = sliceWidth / Math.max(1, secondaryCount);
            for (int j = 0; j < secondaryCount; j++) {
                float secondaryStart = sliceStart + j * secondaryWidth;
                if (selectedItem == i && selectedSecondaryItem == j) {
                    drawSecondarySlice(slot, j, true, graphics, centerOfScreenX, centerOfScreenY, secondaryRadiusIn, secondaryRadiusOut, secondaryStart, secondaryStart + secondaryWidth, 63, 161, 191, 60);
                } else {
                    drawSecondarySlice(slot, j, false, graphics, centerOfScreenX, centerOfScreenY, secondaryRadiusIn, secondaryRadiusOut, secondaryStart, secondaryStart + secondaryWidth, 0, 0, 0, 64);
                }
            }
        }

        if (selectedItem != -1) {
            IRadialMenuSlot<T> slot = radialMenuSlots.get(selectedItem);
            Component component = selectedSecondaryItem == -1 ? slot.slotName() : slot.secondaryItems().get(selectedSecondaryItem).name();
            graphics.centeredText(font, component.copy(), width / 2, (height - font.lineHeight) / 2, -1);
        }

        for (int i = 0; i < numberOfSlices; i++) {
            ItemStack stack = new ItemStack(Blocks.DIRT);
            float sliceStart = getSliceStartAngle(i, numberOfSlices);
            float angle = (float) Math.toRadians(sliceStart + sliceWidth / 2);
            float posX = centerOfScreenX - 8 + itemRadius * (float) Math.cos(angle);
            float posY = centerOfScreenY - 8 + itemRadius * (float) Math.sin(angle);

            IRadialMenuSlot<T> slot = radialMenuSlots.get(i);
            T primarySlotIcon = slot.primarySlotIcon();
            if (primarySlotIcon != null) {
                radialMenu.drawIcon(primarySlotIcon, graphics, (int) posX, (int) posY, 16);
            }
            drawSliceName(graphics, String.valueOf(i + 1), stack, (int) posX, (int) posY);

            int secondaryCount = secondaryCount(slot);
            float secondaryWidth = sliceWidth / Math.max(1, secondaryCount);
            for (int j = 0; j < secondaryCount; j++) {
                T secondarySlotIcon = slot.secondaryItems().get(j).item();
                if (secondarySlotIcon == null) continue;
                float secondaryAngle = (float) Math.toRadians(sliceStart + (j + 0.5f) * secondaryWidth);
                float secondaryPosX = centerOfScreenX - 8 + secondaryItemRadius * (float) Math.cos(secondaryAngle);
                float secondaryPosY = centerOfScreenY - 8 + secondaryItemRadius * (float) Math.sin(secondaryAngle);
                radialMenu.drawIcon(secondarySlotIcon, graphics, (int) secondaryPosX, (int) secondaryPosY, 16);
            }
        }
    }

    public void drawSliceName(GuiGraphicsExtractor graphics, String sliceName, ItemStack stack, int posX, int posY) {
        graphics.itemDecorations(font, stack, posX + 5, posY, sliceName);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        int adjustedKey = keyEvent.key() - 48;
        if (adjustedKey >= 0 && adjustedKey <= radialMenuSlots.size()) {
            selectedItem = adjustedKey == 0 ? radialMenuSlots.size() : adjustedKey;
            selectedItem = selectedItem - 1; // Offset by 1 because 0 based indexing but users see 1 indexed
            selectedSecondaryItem = -1;
            //  mouseClicked(false);
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        if (mouseButtonEvent.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            this.onClose();
        } else if (mouseButtonEvent.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            if (this.selectedItem != -1) {
                radialMenu.setCurrentSlot(selectedItem, selectedSecondaryItem);
                //noinspection DataFlowIssue
                minecraft.player.closeContainer();
            }
        }
        return true;
    }

    public void drawSlice(IRadialMenuSlot<T> slot, boolean highlighted, GuiGraphicsExtractor GuiGraphicsExtractor, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        submitSlice(GuiGraphicsExtractor, x, y, radiusIn, radiusOut, startAngle, endAngle, r, g, b, a);
    }

    /**
     * Draws the segment of a secondary item in the outer ring
     *
     * @param secondaryIndex index into {@link IRadialMenuSlot#secondaryItems()}
     */
    public void drawSecondarySlice(IRadialMenuSlot<T> slot, int secondaryIndex, boolean highlighted, GuiGraphicsExtractor graphics, float x, float y, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        submitSlice(graphics, x, y, radiusIn, radiusOut, startAngle, endAngle, r, g, b, a);
    }

    protected void submitSlice(GuiGraphicsExtractor GuiGraphicsExtractor, float x, float y, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        // Normalize angles to [0, 360) and ensure sweep is always positive (handles wrap-around at 360)
        float startDeg = Mth.positiveModulo(startAngle, 360.0f);
        float endDeg = Mth.positiveModulo(endAngle, 360.0f);
        float sweepDeg = endDeg - startDeg;
        if (sweepDeg <= 0.0f) {
            sweepDeg += 360.0f;
        }

        int sections = Math.max(1, Mth.ceil(sweepDeg / PRECISION));
        float startRad = (float) Math.toRadians(startDeg);
        float endRad = (float) Math.toRadians(startDeg + sweepDeg);

        GuiGraphicsExtractor.submitGuiElementRenderState(new SliceElement(x, y, 0, radiusIn, radiusOut, startRad, endRad, sections, r, g, b, a, GuiGraphicsExtractor.pose()));

    }

    public record SliceElement(RenderPipeline pipeline, TextureSetup textureSetup, float x, float y, float z,
                               float radiusIn, float radiusOut, float startAngle, float endAngle, int sections, int r,
                               int g, int b, int a,
                               @Nullable ScreenRectangle scissorArea,
                               @Nullable ScreenRectangle bounds) implements GuiElementRenderState {

        public SliceElement(float x, float y, float z,
                            float radiusIn, float radiusOut, float startAngle, float endAngle, int sections, int r,
                            int g, int b, int a, Matrix3x2fStack pose) {
            this(RenderPipelines.GUI, TextureSetup.noTexture(), x, y, z, radiusIn, radiusOut,startAngle, endAngle, sections, r, g, b, a, null, createBounds(x, y, radiusOut, pose));
        }

        @Override
        public void buildVertices(@NotNull VertexConsumer consumer) {

            var angle = endAngle - startAngle;

            for (int i = 0; i < sections; i++) {
                float angle1 = startAngle + (i / (float) sections) * angle;
                float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

                float pos1InX = x + radiusIn * (float) Math.cos(angle1);
                float pos1InY = y + radiusIn * (float) Math.sin(angle1);
                float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
                float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
                float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
                float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
                float pos2InX = x + radiusIn * (float) Math.cos(angle2);
                float pos2InY = y + radiusIn * (float) Math.sin(angle2);

                consumer.addVertex(pos1OutX, pos1OutY, z).setColor(r, g, b, a);
                consumer.addVertex(pos1InX, pos1InY, z).setColor(r, g, b, a);
                consumer.addVertex(pos2InX, pos2InY, z).setColor(r, g, b, a);
                consumer.addVertex(pos2OutX, pos2OutY, z).setColor(r, g, b, a);
            }
        }

        private static ScreenRectangle createBounds(float centerX, float centerY, float radius, Matrix3x2fStack pose) {
            // x, y, width, height
            return new ScreenRectangle(
                    (int) (centerX - radius),
                    (int) (centerY - radius),
                    (int) (radius * 2),
                    (int) (radius * 2)
            ).transformMaxBounds(pose);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void processInputEvent(MovementInputUpdateEvent event) {
        Options settings = Minecraft.getInstance().options;
        Input eInput = event.getInput().keyPresses;
        var up = isKeyDown0(settings.keyUp);
        var down = isKeyDown0(settings.keyDown);
        var left = isKeyDown0(settings.keyLeft);
        var right = isKeyDown0(settings.keyRight);

        var jumping = isKeyDown0(settings.keyJump);
        var shiftKeyDown = isKeyDown0(settings.keyShift);
        var sprint = isKeyDown0(settings.keySprint);
        event.getInput().keyPresses = new Input(up, down, left, right, jumping, shiftKeyDown, sprint);
        event.getInput().tick();
    }

    private static boolean isKeyDown0(KeyMapping keybind) {
        if (keybind.isUnbound()) {
            return false;
        }
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keybind.getKey().getValue());
    }
}