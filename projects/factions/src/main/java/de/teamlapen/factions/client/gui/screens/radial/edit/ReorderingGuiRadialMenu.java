package de.teamlapen.factions.client.gui.screens.radial.edit;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.components.ColoredImageWidget;
import de.teamlapen.factions.client.gui.components.RepositionCallback;
import de.teamlapen.factions.client.gui.radialmenu.DrawCallback;
import de.teamlapen.factions.client.gui.radialmenu.GuiRadialMenu;
import de.teamlapen.factions.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenu;
import de.teamlapen.factions.common.util.ItemOrdering;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReorderingGuiRadialMenu<T> extends GuiRadialMenu<ItemWrapper<T>> {

    protected static final Identifier BACKGROUND = FResourceLocation.mod("widget/background");

    protected ItemWrapper<T> movingItem;
    private final ItemOrdering<T> ordering;
    private final Function<T, MutableComponent> nameFunction;
    private final DrawCallback<T> drawCallback;
    private final Consumer<ItemOrdering<T>> saveAction;
    private final Function<T, Boolean> isEnabled;
    private ExcludedItemList excludedList;
    private Boolean wasGuiHidden;
    protected final GridLayout layout = new GridLayout();
    protected final List<RepositionCallback> repositionCallback = new ArrayList<>();

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

        setupGrid();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();

        if (this.wasGuiHidden == null) {
            this.wasGuiHidden = Minecraft.getInstance().options.hideGui;
        }
        Minecraft.getInstance().options.hideGui = true;

        updateExcludedList();
    }

    protected void setupGrid() {
        addExcludeList();
    }

    protected void addExcludeList() {
        int excludesWidth = 140;
        var excludesWrapper = new GridLayout();
        var background = excludesWrapper.addChild(ColoredImageWidget.sprite(excludesWidth, this.height, BACKGROUND, ARGB.colorFromFloat(1, 0.5f, 0.5f, 0.5f)), 0,0);
        this.repositionCallback.add(((width1, height1) -> background.setHeight(height1)));
        var excludes = new GridLayout()
                .rowSpacing(2);
        excludesWrapper.addChild(excludes,0,0, excludesWrapper.newCellSettings().padding(4).paddingTop(5));

        GridLayout.RowHelper rowHelper = excludes.createRowHelper(1);
        rowHelper.defaultCellSetting().alignHorizontallyCenter();
        rowHelper.addChild(new StringWidget(Component.translatable("text.factions.excluded_actions"), Minecraft.getInstance().font), rowHelper.newCellSettings().alignHorizontallyCenter().paddingVertical(1));

        this.excludedList = rowHelper.addChild(new ExcludedItemList(excludesWidth - 8, this.height - 55 - 11));
        this.repositionCallback.add((width1, height1) -> excludedList.setHeight(height1 - 55 - 11));
        rowHelper.addChild(new ResetButton(0, 0, excludesWidth - 30, 20, (context) -> this.reset()), rowHelper.newCellSettings().paddingHorizontal(1));
        rowHelper.addChild(new ExtendedButton(0, 0, excludesWidth - 30, 20, Component.translatable("gui.done"), (context) -> this.onClose()), rowHelper.newCellSettings().paddingHorizontal(1));

        this.layout.addChild(excludesWrapper,0,0);
    }

    @Override
    protected void repositionElements() {
        this.repositionCallback.forEach(x -> x.repositionElements(this.width, this.height));
        this.layout.arrangeElements();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int p_296369_, int p_296477_, float p_294317_) {
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
        if (!(this.radialMenuSlots.size() == 1 && this.radialMenuSlots.getFirst().primarySlotIcon().get() == null)) {
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

    protected void removeDummyItems() {
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
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        for (GuiEventListener guieventlistener1 : this.children()) {
            if (guieventlistener1.mouseClicked(mouseButtonEvent, isDoubleClick)) {
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
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
//        this.excludedList.mouseDragged(event, dragX, dragY);
        return super.mouseDragged(event, dragX, dragY);
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
        super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, r, g, b, Math.min(255, (int) (a * 2f)));
    }

    @Override
    public void drawSliceName(GuiGraphics graphics, String sliceName, ItemStack stack, int posX, int posY) {
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, Component.translatable("gui.factions.reordering.excluded"), 70, 5, -1);
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.movingItem != null) {
            this.drawCallback.accept(this.movingItem.get(), graphics, mouseX - 8, mouseY - 8, 16, false);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
        }
        return super.keyPressed(keyEvent);
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

    public class ExcludedItemList extends ContainerObjectSelectionList<ExcludedEntry<T>> {

        public ExcludedItemList(int pWidth, int pHeight) {
            super(Minecraft.getInstance(), pWidth, pHeight, 0, 20);
        }

        public void updateContent(List<T> newItems, Function<T, MutableComponent> nameFunction) {
            this.replaceEntries(newItems.stream().map(s -> new ExcludedEntry<>(s, nameFunction.apply(s), () -> selectItem(s))).toList());
        }

        private void selectItem(T selected) {
            if (ReorderingGuiRadialMenu.this.movingItem != null) {
            } else {
                ReorderingGuiRadialMenu.this.pickExcludedItem(selected);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleCLick) {
            if (isMouseOver(event.x(), event.y())) {
                if (ReorderingGuiRadialMenu.this.movingItem != null) {
                    ReorderingGuiRadialMenu.this.excludeItem();
                    return true;
                }
            }
            return super.mouseClicked(event, doubleCLick);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int p_283242_, int p_282891_, float p_283683_) {
            super.renderWidget(guiGraphics, p_283242_, p_282891_, p_283683_);
            if (this.visible && ReorderingGuiRadialMenu.this.movingItem != null) {
                int i = this.getX();
                int j = this.getY();
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(i, j);
                guiGraphics.fillGradient(0, 0, this.getWidth(), this.getHeight(), -1072689136, -804253680);
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("text.factions.place_exclude"), this.width / 2, this.height / 2, -1);
                guiGraphics.pose().popMatrix();
            }
        }
    }

    public static class ExcludedEntry<T> extends ContainerObjectSelectionList.Entry<ExcludedEntry<T>> {

        private final T item;
        private final Button button;

        public ExcludedEntry(@NotNull T item, Component name, Runnable onClick) {
            this.item = item;
            this.button = Button.builder(name, b -> onClick.run()).size(getWidth(), getHeight()).build();
        }

        public T getItem() {
            return item;
        }

        @Override
        public void setPosition(int x, int y) {
            super.setPosition(x, y);
            this.button.setPosition(x,y);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            this.button.setX(x);
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            this.button.setY(y);
        }

        @Override
        public void setHeight(int height) {
            super.setHeight(height);
            this.button.setHeight(height);
        }

        @Override
        public void setWidth(int width) {
            super.setWidth(width);
            this.button.setWidth(width -2);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(button);
        }

        @Override
        public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(button);
        }
    }

    public static class ResetButton extends ExtendedButton {

        private static final Component DESCRIPTION = Component.translatable("gui.factions.reset");
        private static final Component DESCRIPTION_CONFIRM = Component.translatable("gui.factions.reset_question").withStyle(ChatFormatting.DARK_RED);

        private boolean isClicked = false;

        public ResetButton(int xPos, int yPos, int width, int height, OnPress handler) {
            super(xPos, yPos, width, height, DESCRIPTION, handler);
        }

        @Override
        public void onPress(InputWithModifiers modifier) {
            if (this.isClicked) {
                super.onPress(modifier);
                this.isClicked = false;
            } else {
                this.isClicked = true;
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            var result = super.mouseClicked(event, doubleClick);
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
            return this.isClicked ? 0xffff0000 : super.getFGColor();
        }
    }
}
