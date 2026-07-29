package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.client.core.FactionAppearanceScreens;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.screens.skills.SkillsScreen;
import de.teamlapen.faction.client.gui.screens.taskboard.TaskListWidget;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionKeys;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.network.packets.server.ServerboundDeleteRefinementPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.faction.common.world.inventory.FactionMenu;
import de.teamlapen.faction.common.world.inventory.ITaskMenu;
import de.teamlapen.faction.misc.mixin.client.accessor.AbstractContainerScreenAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FactionMenuScreen extends AbstractContainerScreen<FactionMenu> implements ExtendedScreen {

    private static final Identifier BACKGROUND = FIdentifier.mod("textures/gui/container/faction_menu.png");
    private static final Identifier BACKGROUND_REFINEMENTS = FIdentifier.mod("textures/gui/container/faction_menu_refinements.png");
    private static final WidgetSprites SKILLS = new WidgetSprites(FIdentifier.mod("widget/skills"), FIdentifier.mod("widget/skills_highlighted"));
    private static final WidgetSprites SETTINGS = new WidgetSprites(FIdentifier.mod("widget/settings"), FIdentifier.mod("widget/settings_highlighted"));
    private static final WidgetSprites REMOVE_ACCESSORY = new WidgetSprites(FIdentifier.mod("widget/remove_accessory"), FIdentifier.mod("widget/remove_accessory_highlighted"));
    private static final WidgetSprites LOCATE_TASK_MASTER = new WidgetSprites(FIdentifier.mod("widget/locate_task_master"), FIdentifier.mod("widget/locate_task_master_highlighted"));

    private static final int WIDTH = 234;
    private static final int HEIGHT = 205;

    private static final int TASK_LIST_WIDTH = 137;
    private static final int TASK_LIST_HEIGHT = 94;

    private final IFactionPlayer<?> factionPlayer;
    private TaskListWidget taskList;
    private final Map<Integer, Button> refinementRemoveButtons = new Int2ObjectOpenHashMap<>(3);
    private Component level;

    public FactionMenuScreen(@NotNull FactionMenu container, @NotNull Inventory playerInventory, @NotNull Component titleIn) {
        super(container, playerInventory, titleIn, WIDTH, HEIGHT);
        this.inventoryLabelX = 36;
        this.inventoryLabelY = this.imageHeight - 93;
        this.menu.setReloadListener(this::refreshTaskList);
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Cannot open faction container without faction player"));
    }

    private void refreshTaskList() {
        if (this.taskList != null) {
            this.taskList.refreshEntries(new ArrayList<>(this.menu.getTaskInfos()));
        }
    }

    @Override
    public @NotNull ITaskMenu getTaskContainer() {
        return this.menu;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        if (FactionKeys.FACTION_MENU.matches(keyEvent)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseDragged(@NotNull MouseButtonEvent event, double dragX, double dragY) {
        super.mouseDragged(event, dragX, dragY);
        if (!this.isQuickCrafting) {
            this.taskList.mouseDragged(event, dragX, dragY);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.taskList != null && this.taskList.isMouseOver(mouseX, mouseY)) {
            return this.taskList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        if (this.menu.areRefinementsAvailable()) {
            for (int i = 0; i < this.menu.getRefinementStacks().size(); i++) {
                ItemStack stack = this.menu.getRefinementStacks().get(i);
                Slot slot = this.menu.getSlot(i);
                int x = slot.x + this.leftPos;
                int y = slot.y + this.topPos;
                graphics.item(stack, x, y);
                graphics.itemDecorations(this.font, stack, x, y, null);
            }
        }

        this.renderAccessorySlots(graphics, mouseX, mouseY, partialTicks);

        this.extractTooltip(graphics, mouseX, mouseY);
        if (this.menu.areRefinementsAvailable()) {
            this.renderHoveredRefinementTooltip(graphics, mouseX, mouseY);
        }
    }

    protected void renderAccessorySlots(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        for (Slot slot : this.menu.slots) {
            if (((AbstractContainerScreenAccessor) this).invokeIsHovering(slot, mouseX, mouseY) && slot instanceof FactionMenu.RemovingSelectorSlot && refinementRemoveButtons.containsKey(slot.getSlotIndex()) && !this.menu.getRefinementStacks().get(slot.getSlotIndex()).isEmpty()) {
                this.refinementRemoveButtons.get(slot.getSlotIndex()).extractRenderState(graphics, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        refreshTaskList();
    }

    @Override
    protected void init() {
        super.init();
        this.level = Objects.requireNonNullElseGet(factionPlayer.getLevelDisplay(), Component::empty).copy().withStyle(style -> style.withColor(factionPlayer.getFaction().value().getChatColor()));

        this.taskList = this.addRenderableWidget(new TaskListWidget(
                this.menu, this.factionPlayer,
                TASK_LIST_WIDTH+20, TASK_LIST_HEIGHT+10
        ));
        this.taskList.setPosition(this.leftPos + 83-10, this.topPos + 6);
        this.taskList.setEmptyMessage(Component.translatable("gui.factionapi.faction_menu.no_tasks"));
       // this.list = this.addRenderableWidget(new TaskList(Minecraft.getInstance(), this.menu, factionPlayer, this.leftPos + 83, this.topPos + 7, 137, 104, () -> new ArrayList<>(this.menu.getTaskInfos())));

        var button1 = this.addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 90, 20, 20, SKILLS, context -> {
            if (this.minecraft.player.isAlive()) {
                this.minecraft.player.closeContainer();
                FactionPlayerHandler.get(this.minecraft.player).getCurrentSkillPlayer().ifPresent(f -> Minecraft.getInstance().setScreen(new SkillsScreen(f, () -> FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.FACTION_MENU)))));
            }
        }, Component.empty()));
        button1.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.faction_menu.skill_screen")));

        var button2 = this.addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 126, 20, 20, SETTINGS, (context) -> {
            EditSelectActionScreen.show();
        }, Component.empty()));
        button2.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.faction_menu.edit_actions")));
        var button3 = this.addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 147, 20, 20, SETTINGS, (context) -> {
            EditSelectMinionTaskScreen.show();
        }, Component.empty()));
        button3.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.faction_menu.edit_tasks")));
        button3.visible = FactionPlayerHandler.get(factionPlayer.asEntity()).getLordLevel() > 0;

        var definition = FactionAppearanceScreens.getProvider(factionPlayer.getFaction().value());

        if (definition != null) {
            Button appearanceButton = this.addRenderableWidget(new ImageButton(this.leftPos + 29, this.topPos + 90, 20, 20, definition.widgetSpritesSupplier(), (context) -> {
                Minecraft.getInstance().setScreen(definition.provider().create(() -> FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.FACTION_MENU))));
            }, Component.empty()));
            appearanceButton.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.faction_menu.appearance_menu")));
        }

        if (this.menu.areRefinementsAvailable()) {
            NonNullList<ItemStack> refinementList = this.menu.getRefinementStacks();
            for (Slot slot : this.menu.slots) {
                if (slot instanceof FactionMenu.RemovingSelectorSlot) {
                    Button xButton = this.addRenderableWidget(new ImageButton(this.getLeftPos() + slot.x + 16 - 5, this.getTopPos() + slot.y + 16 - 5, 5, 5, REMOVE_ACCESSORY, (button) -> {
                        FactionsMod.proxy.sendToServer(new ServerboundDeleteRefinementPacket(IRefinementItem.AccessorySlotType.values()[slot.index]));
                        refinementList.set(slot.index, ItemStack.EMPTY);
                    }, Component.empty()) {

                        @Override
                        public void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
                            if (!refinementList.get(slot.index).isEmpty() && ((AbstractContainerScreenAccessor) FactionMenuScreen.this).getDraggingItem().isEmpty() && overSlot(slot, mouseX, mouseY)) {
                                super.extractContents(guiGraphicsExtractor, mouseX, mouseY, partialTick);
                            }
                        }

                        private boolean overSlot(@NotNull Slot slot, int mouseX, int mouseY) {
                            mouseX -= FactionMenuScreen.this.leftPos;
                            mouseY -= FactionMenuScreen.this.topPos;
                            return slot.x <= mouseX && slot.x + 16 > mouseX && slot.y <= mouseY && slot.y + 16 > mouseY;
                        }
                    });
                    xButton.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.faction_menu.destroy_item").withStyle(ChatFormatting.RED)));
                    refinementRemoveButtons.put(slot.getSlotIndex(), xButton);
                }
            }
        }

    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        int width = this.font.width(this.level);
        graphics.text(this.font, this.level, (int) Math.max(5, 31 - (float) width / 2), 81, -1, false);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        var texture = this.menu.areRefinementsAvailable() ? BACKGROUND_REFINEMENTS : BACKGROUND;
        GuiRenderer.blit(graphics, texture, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, this.leftPos + 7, this.topPos + 8, this.leftPos + 56, this.topPos + 78, 30, 0.0625f, mouseX, mouseY, this.minecraft.player);
    }

    protected void renderHoveredRefinementTooltip(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null) {
            int index = this.hoveredSlot.index;
            NonNullList<ItemStack> list = this.menu.getRefinementStacks();
            if (index < list.size() && index >= 0) {
                if (this.getMenu().getCarried().isEmpty() && !list.get(index).isEmpty()) {
                    if (!this.refinementRemoveButtons.get(this.hoveredSlot.getSlotIndex()).isHoveredOrFocused()) {
                        graphics.setTooltipForNextFrame(this.font, list.get(index), mouseX, mouseY);

                    }
                } else {
                    if (!list.get(index).isEmpty() && this.menu.getSlot(index).mayPlace(this.getMenu().getCarried())) {
                        graphics.setTooltipForNextFrame(this.font, Component.translatable("gui.factionapi.faction_menu.destroy_item").withStyle(ChatFormatting.RED), mouseX, mouseY);
                    }
                }
            }
        }
    }
}
