package de.teamlapen.vampirism.client.gui.screens.radial.edit;

import de.teamlapen.lib.lib.client.gui.components.SimpleList;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.DrawCallback;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.GuiRadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.vampirism.api.util.ItemOrdering;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReorderingGuiRadialMenu<T> extends GuiRadialMenu<ItemWrapper<T>> {

    private ItemWrapper<T> movingItem;
    private final ItemOrdering<T> ordering;
    private final Function<T, MutableComponent> nameFunction;
    private final DrawCallback<T> drawCallback;
    private final Consumer<ItemOrdering<T>> saveAction;
    private final Function<T, Boolean> isEnabled;
    private ExcludedItemList excludedList;
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
        this.excludedList = this.addRenderableWidget(new ExcludedItemList(0, 20, 140, this.height - 70));

        if (this.wasGuiHidden == null) {
            this.wasGuiHidden = Minecraft.getInstance().options.hideGui;
        }
        Minecraft.getInstance().options.hideGui = true;

        updateExcludedList();
    }

    /**
     * from {@link #renderDirtBackground(net.minecraft.client.gui.GuiGraphics)}
     */
    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        graphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        int i = 32;
        graphics.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, 140, this.height, i, i);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection UnstableApiUsage
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, graphics));
    }

    @Override
    public void onClose() {
        super.onClose();
        this.saveOrdering();
        if (this.wasGuiHidden != null) {
            Minecraft.getInstance().options.hideGui = this.wasGuiHidden;
        }
    }

    private void updateExcludedList() {
        this.excludedList.updateContent(ordering.getExcluded(), nameFunction);
    }

    public void reset() {
        this.ordering.reset();
        this.updateExcludedList();
        this.radialMenuSlots.clear();
        this.checkEmpty();
        this.movingItem = null;
    }

    private void addDummyMenuItems() {
        if (!(this.radialMenuSlots.size() == 1 && this.radialMenuSlots.get(0).primarySlotIcon().get() == null)) {
            for (int i = this.radialMenuSlots.size() - 1; i >= 0; i--) {
                this.radialMenuSlots.add(i, new NoItemRadialMenuSlot<>(this.nameFunction, new ItemWrapper<>(), this.isEnabled));
            }
        }
    }

    public void excludeItem() {
        excludeItem(this.movingItem);
        this.movingItem.clear();
        this.movingItem = null;
        this.removeDummyItems();
        this.checkEmpty();
    }

    private void excludeItem(ItemWrapper<T> item) {
        if (item != null) {
            this.ordering.exclude(item.get());
        }
        this.updateExcludedList();
    }

    private void pickExcludedItem(T item) {
        this.movingItem = new ItemWrapper<>(item);
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
        this.updateExcludedList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for(GuiEventListener guieventlistener1 : this.children()) {
            if (guieventlistener1.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }

        pickItem();
        return true;
    }

    private void pickItem() {
        if (this.selectedItem != -1) {
            IRadialMenuSlot<ItemWrapper<T>> selected = this.radialMenuSlots.get(this.selectedItem);
            if (this.movingItem != null) {
                selected.primarySlotIcon().swapItem(this.movingItem);
                if (this.movingItem.get() == null) {
                    this.removeDummyItems();
                    this.movingItem = null;
                }
                if (this.radialMenuSlots.stream().noneMatch(s -> s.primarySlotIcon() == this.movingItem)) {
                    this.excludeItem(this.movingItem);
                }
                syncOrdering();
            } else {
                if (selected.primarySlotIcon().get() == null) {
                    return;
                }
                this.movingItem = selected.primarySlotIcon();
                addDummyMenuItems();
            }
        }
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
    public void drawSlice(IRadialMenuSlot<ItemWrapper<T>> slot, boolean highlighted, GuiGraphics buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        if (this.movingItem == null && !slot.primarySlotIcon().getOptional().map(this.isEnabled).orElse(true)) {
            r = 80;
        }
        super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, r, g, b, Math.min(255, (int)(a*2f)));
    }

    @Override
    public void drawSliceName(GuiGraphics graphics, String sliceName, ItemStack stack, int posX, int posY) {
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, Component.translatable("Excluded:"), 70, 5, -1);
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.movingItem != null) {
            this.drawCallback.accept(this.movingItem.get(), graphics, mouseX - 8, mouseY - 8, 16, false);
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

    public class ExcludedItemList extends SimpleList<ExcludedEntry<T>> {

        public ExcludedItemList(int x, int y, int pWidth, int pHeight) {
            super(Minecraft.getInstance(), pWidth, pHeight, y, y + pHeight, 20);
            this.setLeftPos(x);
        }

        public void updateContent(List<T> newItems, Function<T, MutableComponent> nameFunction) {
            this.replaceEntries(newItems.stream().map(s -> new ExcludedEntry<>(s, nameFunction.apply(s), () -> selectItem(s))).toList());
        }

        private void selectItem(T selected) {
            if (ReorderingGuiRadialMenu.this.movingItem != null) {
//                selected.primarySlotIcon().swapItem(ReorderingGuiRadialMenu.this.movingItem);
            } else {
                ReorderingGuiRadialMenu.this.pickExcludedItem(selected);
            }
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (isMouseOver(pMouseX, pMouseY)) {
                if (ReorderingGuiRadialMenu.this.movingItem != null) {
                    ReorderingGuiRadialMenu.this.excludeItem();
                    return true;
                }
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(graphics, pMouseX, pMouseY, pPartialTick);
            if (this.isVisible && ReorderingGuiRadialMenu.this.movingItem != null) {
                int i = this.getLeft();
                int j = this.getTop();
                graphics.pose().pushPose();
                graphics.pose().translate(i, j, 200);
                graphics.fillGradient(0, 0, this.getWidth(), this.getHeight(), -1072689136, -804253680);
                graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("Place here to exclude"), this.width / 2, this.height / 2, 0xFFFFFF);
                graphics.pose().popPose();
            }
        }


    }

    public static class ExcludedEntry<T> extends SimpleList.Entry<ExcludedEntry<T>> {

        private final T item;

        public ExcludedEntry(@NotNull T item, Component name, Runnable onClick) {
            super(name, onClick);
            this.item = item;
        }

        public T getItem() {
            return item;
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
