package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.ILordPlayer;
import de.teamlapen.factions.api.items.IRefinementItem;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.core.FactionAppearanceScreens;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.client.gui.screens.skills.SkillsScreen;
import de.teamlapen.factions.client.gui.screens.taskboard.TaskListWidget;
import de.teamlapen.factions.common.core.FactionKeys;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.inventory.ITaskMenu;
import de.teamlapen.factions.common.inventory.FactionMenu;
import de.teamlapen.factions.common.network.packets.server.ServerboundDeleteRefinementPacket;
import de.teamlapen.factions.misc.mixin.client.accessor.AbstractContainerScreenAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class FactionMenuScreen extends AbstractContainerScreen<FactionMenu> implements ExtendedScreen {

    private static final ResourceLocation BACKGROUND = FResourceLocation.mod("textures/gui/container/faction_menu.png");
    private static final ResourceLocation BACKGROUND_REFINEMENTS = FResourceLocation.mod("textures/gui/container/faction_menu_refinements.png");
    private static final WidgetSprites SKILLS = new WidgetSprites(FResourceLocation.mod("widget/skills"), FResourceLocation.mod("widget/skills_highlighted"));
    private static final WidgetSprites SETTINGS = new WidgetSprites(FResourceLocation.mod("widget/settings"), FResourceLocation.mod("widget/settings_highlighted"));
    private static final WidgetSprites REMOVE_ACCESSORY = new WidgetSprites(FResourceLocation.mod("widget/remove_accessory"), FResourceLocation.mod("widget/remove_accessory_highlighted"));
    private static final WidgetSprites LOCATE_TASK_MASTER = new WidgetSprites(FResourceLocation.mod("widget/locate_task_master"), FResourceLocation.mod("widget/locate_task_master_highlighted"));

    private static final int WIDTH = 234;
    private static final int HEIGHT = 205;

    private static final int TASK_LIST_WIDTH = 137;
    private static final int TASK_LIST_HEIGHT = 94;

    private final IFactionPlayer<?> factionPlayer;
    private TaskListWidget taskList;
    private final Map<Integer, Button> refinementRemoveButtons = new Int2ObjectOpenHashMap<>(3);
    private Component level;

    public FactionMenuScreen(@NotNull FactionMenu container, @NotNull Inventory playerInventory, @NotNull Component titleIn) {
        super(container, playerInventory, titleIn);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        if (this.menu.areRefinementsAvailable()) {
            for (int i = 0; i < this.menu.getRefinementStacks().size(); i++) {
                ItemStack stack = this.menu.getRefinementStacks().get(i);
                Slot slot = this.menu.getSlot(i);
                int x = slot.x + this.leftPos;
                int y = slot.y + this.topPos;
                graphics.renderItem(stack, x, y);
                graphics.renderItemDecorations(this.font, stack, x, y, null);
            }
        }

        this.renderAccessorySlots(graphics, mouseX, mouseY, partialTicks);

        this.renderTooltip(graphics, mouseX, mouseY);
        if (this.menu.areRefinementsAvailable()) {
            this.renderHoveredRefinementTooltip(graphics, mouseX, mouseY);
        }
    }

    protected void renderAccessorySlots(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        for (Slot slot : this.menu.slots) {
            if (((AbstractContainerScreenAccessor) this).invokeIsHovering(slot, mouseX, mouseY) && slot instanceof FactionMenu.RemovingSelectorSlot && refinementRemoveButtons.containsKey(slot.getSlotIndex()) && !this.menu.getRefinementStacks().get(slot.getSlotIndex()).isEmpty()) {
                this.refinementRemoveButtons.get(slot.getSlotIndex()).render(graphics, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        refreshTaskList();
    }

    @Override
    protected void init() {
        super.init();
        if (factionPlayer.getLevel() > 0) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(factionPlayer.asEntity());
            MutableComponent component = handler.getLordPlayer().filter(x -> x.getLordLevel() > 0).map(ILordPlayer::getLordTitle).map(x -> x.plainCopy().append(" (" + handler.getLordLevel() + ")")).orElseGet(() -> Component.translatable("text.factions.level").append(" " + factionPlayer.getLevel()));
            this.level = component.withStyle(style -> style.withColor(factionPlayer.getFaction().value().getChatColor()));
        } else {
            this.level = Component.empty();
        }

        this.taskList = this.addRenderableWidget(new TaskListWidget(
                this.menu, this.factionPlayer,
                TASK_LIST_WIDTH+20, TASK_LIST_HEIGHT+10
        ));
        this.taskList.setPosition(this.leftPos + 83-10, this.topPos + 6);
        this.taskList.setEmptyMessage(Component.translatable("gui.factions.faction_menu.no_tasks"));
       // this.list = this.addRenderableWidget(new TaskList(Minecraft.getInstance(), this.menu, factionPlayer, this.leftPos + 83, this.topPos + 7, 137, 104, () -> new ArrayList<>(this.menu.getTaskInfos())));

        var button1 = this.addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 90, 20, 20, SKILLS, context -> {
            if (this.minecraft.player.isAlive()) {
                FactionPlayerHandler.get(this.minecraft.player).getCurrentSkillPlayer().ifPresent(f -> Minecraft.getInstance().setScreen(new SkillsScreen(f, this)));
            }
        }, Component.empty()));
        button1.setTooltip(Tooltip.create(Component.translatable("gui.factions.faction_menu.skill_screen")));

        var button2 = this.addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 126, 20, 20, SETTINGS, (context) -> {
            EditSelectActionScreen.show();
        }, Component.empty()));
        button2.setTooltip(Tooltip.create(Component.translatable("gui.factions.faction_menu.edit_actions")));
        var button3 = this.addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 147, 20, 20, SETTINGS, (context) -> {
            EditSelectMinionTaskScreen.show();
        }, Component.empty()));
        button3.setTooltip(Tooltip.create(Component.translatable("gui.factions.faction_menu.edit_tasks")));
        button3.visible = FactionPlayerHandler.get(factionPlayer.asEntity()).getLordLevel() > 0;

        var definition = FactionAppearanceScreens.getProvider(factionPlayer.getFaction().value());

        if (definition != null) {
            Button appearanceButton = this.addRenderableWidget(new ImageButton(this.leftPos + 29, this.topPos + 90, 20, 20, definition.widgetSpritesSupplier(), (context) -> {
                Minecraft.getInstance().setScreen(definition.provider().create(this));
            }, Component.empty()));
            appearanceButton.setTooltip(Tooltip.create(Component.translatable("gui.factions.faction_menu.appearance_menu")));
        }

        if (this.menu.areRefinementsAvailable()) {
            NonNullList<ItemStack> refinementList = this.menu.getRefinementStacks();
            for (Slot slot : this.menu.slots) {
                if (slot instanceof FactionMenu.RemovingSelectorSlot) {
                    Button xButton = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + slot.x + 16 - 5, this.getGuiTop() + slot.y + 16 - 5, 5, 5, REMOVE_ACCESSORY, (button) -> {
                        FactionsMod.proxy.sendToServer(new ServerboundDeleteRefinementPacket(IRefinementItem.AccessorySlotType.values()[slot.index]));
                        refinementList.set(slot.index, ItemStack.EMPTY);
                    }, Component.empty()) {
                        @Override
                        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                            if (!refinementList.get(slot.index).isEmpty() && ((AbstractContainerScreenAccessor) FactionMenuScreen.this).getDraggingItem().isEmpty() && overSlot(slot, pMouseX, pMouseY)) {
                                super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                            }
                        }

                        private boolean overSlot(@NotNull Slot slot, int mouseX, int mouseY) {
                            mouseX -= FactionMenuScreen.this.leftPos;
                            mouseY -= FactionMenuScreen.this.topPos;
                            return slot.x <= mouseX && slot.x + 16 > mouseX && slot.y <= mouseY && slot.y + 16 > mouseY;
                        }
                    });
                    xButton.setTooltip(Tooltip.create(Component.translatable("gui.factions.faction_menu.destroy_item").withStyle(ChatFormatting.RED)));
                    refinementRemoveButtons.put(slot.getSlotIndex(), xButton);
                }
            }
        }

    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int width = this.font.width(this.level);
        graphics.drawString(this.font, this.level, (int) Math.max(5, 31 - (float) width / 2), 81, -1, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float pPartialTick, int mouseX, int mouseY) {
        var texture = this.menu.areRefinementsAvailable() ? BACKGROUND_REFINEMENTS : BACKGROUND;
        GuiRenderer.blit(graphics, texture, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.leftPos + 7, this.topPos + 8, this.leftPos + 56, this.topPos + 78, 30, 0.0625f, mouseX, mouseY, this.minecraft.player);
    }

    protected void renderHoveredRefinementTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
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
                        graphics.setTooltipForNextFrame(this.font, Component.translatable("gui.factions.faction_menu.destroy_item").withStyle(ChatFormatting.RED), mouseX, mouseY);
                    }
                }
            }
        }
    }

//    private static class TaskList extends de.teamlapen.factions.client.gui.screens.taskboard.TaskList {
//
//        public TaskList(Minecraft minecraft, ITaskMenu menu, IFactionPlayer<?> factionPlayer, int x, int y, int width, int height, Supplier<List<ITaskInstance>> itemSupplier) {
//            super(menu, factionPlayer, x, y, width, height, itemSupplier);
//        }
//
//        @Override
//        protected TaskEntry createItem(ITaskInstance item) {
//            return new TaskEntry(item);
//        }
//
//        @Override
//        public void renderWidget(GuiGraphics graphics, int p_283242_, int p_282891_, float p_283683_) {
//            super.renderWidget(graphics, p_283242_, p_282891_, p_283683_);
//            if (children().isEmpty()) {
//                graphics.drawCenteredString(minecraft.font, Component.translatable("gui.vampirism.vampirism_menu.no_tasks"), this.getX() + width / 2, this.getY() + height / 2, 0x404040);
//            }
//
//        }
//
//        private class TaskEntry extends de.teamlapen.factions.client.gui.screens.taskboard.TaskList.TaskEntry {
//
//            private @Nullable ImageButton button;
//
//            public TaskEntry(ITaskInstance taskInstance) {
//                super(taskInstance);
//
//                if (!taskInstance.isUnique(menu.getRegistry())) {
//                    this.button = new ImageButton(0, 0, 8, 11, LOCATE_TASK_MASTER, this::clickLocator, Component.empty());
//                    this.button.setTooltip(Tooltip.create(createTooltip()));
//                }
//            }
//
//            @Override
//            public boolean mouseClicked(MouseButtonEvent p_445873_, boolean p_433971_) {
//                if (this.button != null && button.mouseClicked(p_445873_, p_433971_)) {
//                    return true;
//                }
//                return super.mouseClicked(p_445873_, p_433971_);
//            }
//
//            @Override
//            protected void renderToolTips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//                if (this.button != null && !button.isMouseOver(mouseX, mouseY)) {
//                    super.renderToolTips(guiGraphics, mouseX, mouseY);
//                }
//            }
//
//            private void clickLocator(Button button) {
//                Player player = factionPlayer.asEntity();
//                Component position = ((VampirismMenu) menu).taskWrapper.get(getItem().getTaskBoard()).getLastSeenPos().map(pos -> {
//                    int i = Mth.floor(Util.horizontalDistance(player.blockPosition(), pos));
//                    MutableComponent itextcomponent = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", pos.getX(), "~", pos.getZ())).withStyle((p_241055_1_) -> {
//                        return p_241055_1_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent.SuggestCommand("/tp @s " + pos.getX() + " ~ " + pos.getZ())).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip")));
//                    });
//                    return itextcomponent.append(Component.translatable("gui.vampirism.vampirism_menu.distance", i));
//                }).orElseGet(() -> Component.translatable("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD));
//                player.displayClientMessage(Component.translatable("gui.vampirism.vampirism_menu.last_known_pos").append(position), false);
//            }
//
//            private Component createTooltip() {
//                Component position = ((VampirismMenu) menu).taskWrapper.get(this.getItem().getTaskBoard()).getLastSeenPos().map(pos -> Component.literal("[" + pos.toShortString() + "]").withStyle(ChatFormatting.GREEN)).orElseGet(() -> Component.translatable("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD));
//                return Component.translatable("gui.vampirism.vampirism_menu.last_known_pos").append(position);
//            }
//
//            @Override
//            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
//                super.renderContent(guiGraphics, mouseX, mouseY, isHovering, partialTick);
//                if (this.button != null) {
//                    this.button.setPosition(getRowLeft() + getRowWidth() - this.button.getWidth() - 1, getY() + 1);
//                    this.button.render(guiGraphics, mouseX, mouseY, partialTick);
//                }
//            }
//        }
//    }
}
