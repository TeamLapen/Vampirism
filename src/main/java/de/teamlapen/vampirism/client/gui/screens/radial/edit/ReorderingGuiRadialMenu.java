package de.teamlapen.vampirism.client.gui.screens.radial.edit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.teamlapen.lib.lib.client.gui.components.ScrollWidget;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.DrawCallback;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.GuiRadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.vampirism.api.util.ItemOrdering;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReorderingGuiRadialMenu<T> extends GuiRadialMenu<ItemWrapper<T>> {

    private T movingItem;
    private final ItemOrdering<T> ordering;
    private final Function<T, MutableComponent> nameFunction;
    private final DrawCallback<T> drawCallback;
    private final Consumer<ItemOrdering<T>> saveAction;
    private final Function<T, Boolean> isEnabled;
    private ItemScrollWidget excludedList;
    private Boolean wasGuiHidden;

    public ReorderingGuiRadialMenu(ItemOrdering<T> ordering, Function<T, MutableComponent> nameFunction, DrawCallback<T> drawCallback, @NotNull Consumer<ItemOrdering<T>> saveAction, Function<T, Boolean> isEnabled) {
        super(createMenu(ordering, nameFunction, drawCallback, isEnabled));
        this.ordering = ordering;
        this.nameFunction = nameFunction;
        this.drawCallback = drawCallback;
        this.saveAction = saveAction;
        this.isEnabled = isEnabled;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new ResetButton(0, this.height - 40, 140, 20, (context) -> this.reset()));
        this.addRenderableWidget(new ExtendedButton(0, this.height - 20, 140, 20, Component.translatable("gui.done"), (context) -> this.onClose()));
        this.excludedList = this.addRenderableWidget(new ItemScrollWidget(0, 20, 140, this.height - 70, builder -> ordering.getExcluded().forEach(item -> builder.addWidget(new NoItemRadialMenuSlot<>(nameFunction, new ItemWrapper<>(item), isEnabled))), Component.empty()));

        if (this.wasGuiHidden == null) {
            this.wasGuiHidden = Minecraft.getInstance().options.hideGui;
        }
        Minecraft.getInstance().options.hideGui = true;
    }

    /**
     * from {@link #renderDirtBackground(int)}
     */
    @Override
    public void renderBackground(@NotNull PoseStack pPoseStack) {
        double width = 140;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double) this.height, 0.0D).uv(0.0F, (float) this.height / f + (float) 0).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double) width, (double) this.height, 0.0D).uv((float) width / f, (float) this.height / f + (float) 0).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double) width, 0.0D, 0.0D).uv((float) width / 32.0F, (float) 0).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float) 0).color(64, 64, 64, 255).endVertex();
        tesselator.end();
        //noinspection UnstableApiUsage
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, pPoseStack));
    }

    @Override
    public void onClose() {
        super.onClose();
        this.saveOrdering();
        if (this.wasGuiHidden != null) {
            Minecraft.getInstance().options.hideGui = this.wasGuiHidden;
        }
    }

    public void reset() {
        this.ordering.reset();
        this.excludedList.updateContent();
        this.radialMenuSlots.clear();
        this.checkEmpty();
    }

    private void addDummyMenuItems() {
        if (!(this.radialMenuSlots.size() == 1 && this.radialMenuSlots.get(0).primarySlotIcon().getOptional().isEmpty())) {
            for (int i = this.radialMenuSlots.size() - 1; i >= 0; i--) {
                this.radialMenuSlots.add(i, new NoItemRadialMenuSlot<>(this.nameFunction, new ItemWrapper<>(), this.isEnabled));
            }
        }
    }

    public void excludeItem() {
        this.ordering.exclude(this.movingItem);
        this.movingItem = null;
        this.excludedList.updateContent();
        this.removeDummyItems();
        this.checkEmpty();
    }

    private void pickExcludedItem(ReorderingItemWidget<T> widget) {
        this.movingItem = widget.getItem().primarySlotIcon().get();
        addDummyMenuItems();
    }

    private void removeDummyItems() {
        this.radialMenuSlots.removeIf(slot -> slot.primarySlotIcon().getOptional().isEmpty());
        this.checkEmpty();
    }

    private void saveOrdering() {
        this.syncOrdering();
        this.saveAction.accept(this.ordering);
    }

    private void syncOrdering() {
        this.ordering.applyOrdering(this.radialMenuSlots.stream().map(IRadialMenuSlot::primarySlotIcon).flatMap(a -> a.getOptional().stream()).collect(Collectors.toList()));
        this.excludedList.updateContent();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for(GuiEventListener guieventlistener1 : this.children()) {
            if (guieventlistener1.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }

        if (this.movingItem != null) {
            if (this.selectedItem != -1) {
                IRadialMenuSlot<ItemWrapper<T>> selected = this.radialMenuSlots.get(this.selectedItem);
                this.movingItem = selected.primarySlotIcon().swapItem(this.movingItem);
                if (this.movingItem == null) {
                    this.removeDummyItems();
                }
                if(this.movingItem != null) {
                    this.ordering.exclude(this.movingItem);
                }
                syncOrdering();
            }
        } else {
            if (this.selectedItem != -1) {
                if (this.radialMenuSlots.get(this.selectedItem).primarySlotIcon().getOptional().isEmpty()) return true;
                IRadialMenuSlot<ItemWrapper<T>> selected = this.radialMenuSlots.get(this.selectedItem);
                addDummyMenuItems();
                this.movingItem = selected.primarySlotIcon().swapItem(null);
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        this.excludedList.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private void checkEmpty() {
        if (this.radialMenuSlots.isEmpty()) {
            this.radialMenuSlots.add(new NoItemRadialMenuSlot<>(this.nameFunction, new ItemWrapper<>(), this.isEnabled));
        }
    }

    @Override
    public void drawSlice(IRadialMenuSlot<ItemWrapper<T>> slot, boolean highlighted, BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        if (this.movingItem == null && !slot.primarySlotIcon().getOptional().map(this.isEnabled).orElse(true)) {
            r = 80;
        }
        super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, r, g, b, Math.min(255, (int)(a*2f)));
    }

    @Override
    public void drawSliceName(String sliceName, ItemStack stack, int posX, int posY) {
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, Component.translatable("Excluded:"), 70, 5, -1);
        super.render(ms, mouseX, mouseY, partialTicks);
        if (this.movingItem != null) {
            this.drawCallback.accept(this.movingItem, ms, mouseX-8, mouseY-8, 16, false);
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
        }
        return true;
    }

    @Override
    protected void processInputEvent(MovementInputUpdateEvent event) {
    }

    private static <T> RadialMenu<ItemWrapper<T>> createMenu(ItemOrdering<T> ordering, Function<T, MutableComponent> nameFunction, DrawCallback<T> drawCallback, Function<T, Boolean> isEnabled) {
        List<IRadialMenuSlot<ItemWrapper<T>>> collect = ordering.getOrdering().stream().map(a -> (IRadialMenuSlot<ItemWrapper<T>>) new NoItemRadialMenuSlot<>(nameFunction, new ItemWrapper<>(a), isEnabled)).collect(Collectors.toList());
        if (collect.isEmpty()) {
            collect.add(new NoItemRadialMenuSlot<>(nameFunction, new ItemWrapper<>(), isEnabled));
        }
        return new RadialMenu<>((i) -> {
        }, collect, (objectToBeDrawn, poseStack, positionX, positionY, size, renderTransparent) -> {
            objectToBeDrawn.run(item -> drawCallback.accept(item, poseStack, positionX, positionY, size, renderTransparent));
        }, 0);
    }

    public class ItemScrollWidget extends ScrollWidget<IRadialMenuSlot<ItemWrapper<T>>, ReorderingItemWidget<T>> {

        public ItemScrollWidget(int pX, int pY, int pWidth, int pHeight, Consumer<ContentBuilder<IRadialMenuSlot<ItemWrapper<T>>, ReorderingItemWidget<T>>> contentSupplier, Component emptyText) {
            super(pX, pY, pWidth, pHeight, (tiRadialMenuSlot, x, y, width, scrollAmountSupplier, onClick) -> new ReorderingItemWidget<>(tiRadialMenuSlot, x, y, width, 20, onClick), contentSupplier, emptyText);
        }

        @Override
        protected void onClick(ReorderingItemWidget<T> widget) {
            if (ReorderingGuiRadialMenu.this.movingItem != null) {
                ReorderingGuiRadialMenu.this.movingItem = widget.getItem().primarySlotIcon().swapItem(ReorderingGuiRadialMenu.this.movingItem);
            } else {
                ReorderingGuiRadialMenu.this.pickExcludedItem(widget);
            }
        }

        @Override
        public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
            if (this.visible && ReorderingGuiRadialMenu.this.movingItem != null) {
                int i = this.getX() + this.innerPadding();
                int j = this.getY() + this.innerPadding();
                pPoseStack.pushPose();
                pPoseStack.translate(i, j, 0.04);
                ScreenUtils.drawGradientRect(pPoseStack.last().pose(), this.getBlitOffset(), 0, 0, this.containerWidth(), this.containerHeight(), 0xd0000000, 0xd0000000);
                drawCenteredString(pPoseStack, Minecraft.getInstance().font, Component.translatable("Place here to exclude"), this.width / 2, this.height / 2, 0xFFFFFF);
                pPoseStack.popPose();
            }
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (ReorderingGuiRadialMenu.this.movingItem != null && withinContentAreaPoint(pMouseX, pMouseY)) {
                ReorderingGuiRadialMenu.this.excludeItem();
                return true;
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }
    }

    private static class ResetButton extends ExtendedButton {

        private static final Component DESCRIPTION = Component.translatable("text.vampirism.gui.reset");
        private static final Component DESCRIPTION_CONFIRM = Component.translatable("text.vampirism.gui.reset_question").withStyle(ChatFormatting.DARK_RED);

        private boolean isClicked = false;

        public ResetButton(int xPos, int yPos, int width, int height, OnPress handler) {
            super(xPos, yPos, width, height, DESCRIPTION, handler);
        }

        @Override
        public void onPress() {
            if (this.isClicked) {
                super.onPress();
                this.isClicked = false;
            } else {
                this.isClicked = true;
            }
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            var result = super.clicked(pMouseX, pMouseY);
            if (!result) {
                this.isClicked = false;
            }
            return result;
        }

        @Override
        public @NotNull Component getMessage() {
            return this.isClicked ? DESCRIPTION_CONFIRM : DESCRIPTION;
        }

        @Override
        public int getFGColor() {
            //noinspection DataFlowIssue
            return this.isClicked ? ChatFormatting.DARK_RED.getColor() : super.getFGColor();
        }
    }
}
