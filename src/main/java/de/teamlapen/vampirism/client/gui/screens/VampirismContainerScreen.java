package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.components.ScrollableListComponent;
import de.teamlapen.lib.lib.client.gui.components.ScrollableListWithDummyWidget;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.screens.skills.SkillsScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.inventory.TaskMenu;
import de.teamlapen.vampirism.inventory.VampirismMenu;
import de.teamlapen.vampirism.network.ServerboundDeleteRefinementPacket;
import de.teamlapen.vampirism.util.Helper;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class VampirismContainerScreen extends AbstractContainerScreen<VampirismMenu> implements ExtendedScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu.png");
    private static final ResourceLocation BACKGROUND_REFINEMENTS = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu_refinements.png");
    private static final int display_width = 234;
    private static final int display_height = 205;

    private final IFactionPlayer<?> factionPlayer;
    private int oldMouseX;
    private int oldMouseY;
    private ScrollableListComponent<ITaskInstance> list;
    private final Map<Integer, Button> refinementRemoveButtons = new Int2ObjectOpenHashMap<>(3);
    private Component level;

    public VampirismContainerScreen(@NotNull VampirismMenu container, @NotNull Inventory playerInventory, @NotNull Component titleIn) {
        super(container, playerInventory, titleIn);
        this.imageWidth = display_width;
        this.imageHeight = display_height;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = this.imageHeight - 93;
        this.menu.setReloadListener(() -> this.list.refresh());
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Cannot open Vampirism container without faction player"));
    }

    @Override
    public @NotNull ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    @Override
    public @NotNull TaskMenu getTaskContainer() {
        return this.menu;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeys.VAMPIRISM_MENU.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (!this.isQuickCrafting) {
            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    public Collection<ITaskInstance> refreshTasks() {
        return this.menu.getTaskInfos();
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.menu.areRefinementsAvailable()) {
            for (int i = 0; i < this.menu.getRefinementStacks().size(); i++) {
                ItemStack stack = this.menu.getRefinementStacks().get(i);
                Slot slot = this.menu.getSlot(i);
                int x = slot.x + this.leftPos;
                int y = slot.y + this.topPos;
                this.itemRenderer.renderAndDecorateItem(stack, x, y);
                this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y, null);
            }
        }
        if (this.list.isEmpty()) {
            Component text = Component.translatable("gui.vampirism.vampirism_menu.no_tasks").withStyle(ChatFormatting.WHITE);
            int width = this.font.width(text);
            this.font.drawShadow(matrixStack, text, this.leftPos + 152 - (width / 2F), this.topPos + 52, 0);
        }

        this.renderAccessorySlots(matrixStack, mouseX, mouseY, partialTicks);

        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
//        this.list.renderToolTip(matrixStack, mouseX, mouseY); //TODO 1.19 readd tooltip
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if (this.menu.areRefinementsAvailable()) {
            this.renderHoveredRefinementTooltip(matrixStack, mouseX, mouseY);
        }
    }

    protected void renderAccessorySlots(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (Slot slot : this.menu.slots) {
            if (this.isHovering(slot, mouseX, mouseY) && slot instanceof VampirismMenu.RemovingSelectorSlot && !this.menu.getRefinementStacks().get(slot.getSlotIndex()).isEmpty()) {
                this.refinementRemoveButtons.get(slot.getSlotIndex()).render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        if (factionPlayer.getLevel() > 0) {
            this.level = FactionPlayerHandler.getOpt(factionPlayer.getRepresentingPlayer()).filter(f -> f.getLordLevel() > 0).map(f -> f.getLordTitle().copy().append(" (" + f.getLordLevel() + ")")).orElseGet(() -> Component.translatable("text.vampirism.level").append(" " + factionPlayer.getLevel())).withStyle(style -> style.withColor(factionPlayer.getFaction().getChatColor()));
        } else {
            this.level = Component.empty();
        }
        this.addRenderableWidget(list = new ScrollableListWithDummyWidget<>(this.leftPos + 83, this.topPos + 7, 145, 104, 21, this::refreshTasks, (item, list1, isDummy) -> new TaskItem(item, list1, isDummy, this, this.factionPlayer)));

        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.topPos + 90, 20, 20, 40, 205, 20, BACKGROUND, 256, 256, context -> {
            if (this.minecraft.player.isAlive() && VampirismPlayerAttributes.get(this.minecraft.player).faction != null) {
                Minecraft.getInstance().setScreen(new SkillsScreen(FactionPlayerHandler.getCurrentFactionPlayer(this.minecraft.player).orElse(null), this));
            }
        }, Component.empty())); //TODO add tooltip

        this.addRenderableWidget(new ImageButton(this.leftPos + 26, this.topPos + 90, 20, 20, 0, 205, 20, BACKGROUND, 256, 256, (context) -> {
            IPlayableFaction<?> factionNew = VampirismPlayerAttributes.get(this.minecraft.player).faction;
            Minecraft.getInstance().setScreen(new ActionSelectScreen<>(new Color(factionNew.getColor()), true));
        }, Component.empty())); //TODO add tooltip

        Button appearanceButton = this.addRenderableWidget(new ImageButton(this.leftPos + 47, this.topPos + 90, 20, 20, 20, 205, 20, BACKGROUND, 256, 256, (context) -> {
            Minecraft.getInstance().setScreen(new VampirePlayerAppearanceScreen(this));
        }, Component.empty())); //TODO add tooltip
        if (!Helper.isVampire(minecraft.player)) {
            appearanceButton.active = false;
            appearanceButton.visible = false;
        }

        NonNullList<ItemStack> refinementList = this.menu.getRefinementStacks();
        for (Slot slot : this.menu.slots) {
            if (slot instanceof VampirismMenu.RemovingSelectorSlot) {
                Button xButton = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + slot.x + 16 - 5, this.getGuiTop() + slot.y + 16 - 5, 5, 5, 60, 205, 0, BACKGROUND_REFINEMENTS, 256, 256, (button) -> {
                    VampirismMod.dispatcher.sendToServer(new ServerboundDeleteRefinementPacket(IRefinementItem.AccessorySlotType.values()[slot.index]));
                    refinementList.set(slot.index, ItemStack.EMPTY);
                }, Component.empty()) { //TODO add tooltip
                    @Override
                    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                        this.visible = !refinementList.get(slot.index).isEmpty() && VampirismContainerScreen.this.draggingItem.isEmpty() && overSlot(slot, mouseX, mouseY);
                        super.render(matrixStack, mouseX, mouseY, partialTicks);
                    }

                    private boolean overSlot(@NotNull Slot slot, int mouseX, int mouseY) {
                        mouseX -= VampirismContainerScreen.this.leftPos;
                        mouseY -= VampirismContainerScreen.this.topPos;
                        return slot.x <= mouseX && slot.x + 16 > mouseX && slot.y <= mouseY && slot.y + 16 > mouseY;
                    }
                });
                refinementRemoveButtons.put(slot.getSlotIndex(), xButton);
            }
        }

    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        int width = this.font.width(this.level);
        this.font.draw(stack, this.level, Math.max(5, 31 - (float) width / 2), 81, -1);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float v, int i, int i1) {
        this.renderBackground(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.menu.areRefinementsAvailable() ? BACKGROUND_REFINEMENTS : BACKGROUND);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventory(this.leftPos + 31, this.topPos + 72, 30, (float) (this.leftPos + 10) - this.oldMouseX, (float) (this.topPos + 75 - 50) - this.oldMouseY, this.minecraft.player);
    }

    protected void renderHoveredRefinementTooltip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        if (this.hoveredSlot != null) {
            int index = this.hoveredSlot.index;
            NonNullList<ItemStack> list = this.menu.getRefinementStacks();
            if (index < list.size() && index >= 0) {
                if (this.getMenu().getCarried().isEmpty() && !list.get(index).isEmpty()) {
                    if (!this.refinementRemoveButtons.get(this.hoveredSlot.getSlotIndex()).isHoveredOrFocused()) {
                        this.renderTooltip(matrixStack, list.get(index), mouseX, mouseY);

                    }
                } else {
                    if (!list.get(index).isEmpty() && this.menu.getSlot(index).mayPlace(this.getMenu().getCarried())) {
                        this.renderTooltip(matrixStack, Component.translatable("gui.vampirism.vampirism_menu.destroy_item").withStyle(ChatFormatting.RED), mouseX, mouseY);
                    }
                }
            }
        }
    }

    private class TaskItem extends de.teamlapen.vampirism.client.gui.components.TaskItem<VampirismContainerScreen> {

        private ImageButton button;

        public TaskItem(@NotNull ITaskInstance item, @NotNull ScrollableListWithDummyWidget<ITaskInstance> list, boolean isDummy, VampirismContainerScreen screen, IFactionPlayer<?> factionPlayer) {
            super(item, list, isDummy, screen, factionPlayer);
            if (!item.isUnique()) {
                this.button = new ImageButton(0, 0, 8, 11, 0, 229, 11, TASKMASTER_GUI_TEXTURE, 256, 256, this::onClick, Component.empty()); //TODO 1.19 tooltip rendering
            }
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (this.button != null && !this.isDummy && mouseX > this.button.getX() && mouseX < this.button.getX() + this.button.getWidth() && mouseY > this.button.getY() && mouseY < this.button.getY() + this.button.getHeight()) {
                this.button.onClick(mouseX, mouseY);
                return true;
            } else {
                return super.onClick(mouseX, mouseY);
            }
        }

        @Override
        public void renderItem(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.renderItem(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            if (this.button != null) {
                this.button.setPosition(x + listWidth - 13, y+1);
                this.button.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void renderItemToolTip(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
            if (this.button != null && this.button.isHoveredOrFocused()) {
                //TODO 1.19 tooltip rendering
            } else {
                super.renderItemToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, zLevel);
            }
        }

        private float getDistance(int x1, int z1, int x2, int z2) {
            int i = x2 - x1;
            int j = z2 - z1;
            return Mth.sqrt((float) (i * i + j * j));
        }

        private void onClick(Button button) {
            Player player = this.factionPlayer.getRepresentingPlayer();
            Component position = menu.taskWrapper.get(this.item.getTaskBoard()).getLastSeenPos().map(pos -> {
                int i = Mth.floor(getDistance(player.blockPosition().getX(), player.blockPosition().getZ(), pos.getX(), pos.getZ()));
                MutableComponent itextcomponent = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", pos.getX(), "~", pos.getZ())).withStyle((p_241055_1_) -> {
                    return p_241055_1_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " ~ " + pos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")));
                });
                return itextcomponent.append(Component.translatable("gui.vampirism.vampirism_menu.distance", i));
            }).orElseGet(() -> Component.translatable("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD));
            player.displayClientMessage(Component.translatable("gui.vampirism.vampirism_menu.last_known_pos").append(position), false);

        }

        private void onTooltip(Button button, @NotNull PoseStack matrixStack, int mouseX, int mouseY) {
            Component position = menu.taskWrapper.get(this.item.getTaskBoard()).getLastSeenPos().map(pos -> Component.literal("[" + pos.toShortString() + "]").withStyle(ChatFormatting.GREEN)).orElseGet(() -> Component.translatable("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD));
            renderComponentTooltip(matrixStack, Collections.singletonList(Component.translatable("gui.vampirism.vampirism_menu.last_known_pos").append(position)), mouseX, mouseY, font);

        }
    }

}
