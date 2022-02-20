package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWidget;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.skills.SkillsScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.inventory.container.VampirismContainer;
import de.teamlapen.vampirism.network.CDeleteRefinementPacket;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.Helper;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * TODO 1.19 refactor VampirismContainerScreen
 */
public class VampirismScreen extends ContainerScreen<VampirismContainer> implements ExtendedScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu.png");
    private static final ResourceLocation BACKGROUND_REFINEMENTS = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu_refinements.png");


    private final int display_width = 234;
    private final int display_height = 205;
    private final IFactionPlayer<?> factionPlayer;
    private int oldMouseX;
    private int oldMouseY;
    private ScrollableListWidget<ITaskInstance> list;
    private final Map<Integer, Button> refinementRemoveButtons = new Int2ObjectOpenHashMap<>(3);
    private ITextComponent level;

    public VampirismScreen(VampirismContainer container, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(container, playerInventory, titleIn);
        this.imageWidth = display_width;
        this.imageHeight = display_height;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = this.imageHeight - 93;
        this.menu.setReloadListener(() -> this.list.refresh());
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Cannot open Vampirism container without faction player"));
    }

    @Override
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    @Override
    public TaskContainer getTaskContainer() {
        return this.menu;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeys.getKeyBinding(ModKeys.KEY.SKILL).matches(keyCode, scanCode)) {
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.menu.areRefinementsAvailable()) {
            for (int i = 0; i < this.menu.getRefinementStacks().size(); i++) {
                ItemStack stack = this.menu.getRefinementStacks().get(i);
                Slot slot = this.menu.getSlot(i);
                int x = slot.x + this.leftPos;
                int y = slot.y + this.topPos;
                this.itemRenderer.renderAndDecorateItem(this.minecraft.player, stack, x, y);
                this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y, null);
            }
        }
        if (this.list.isEmpty()) {
            ITextComponent text = new TranslationTextComponent("gui.vampirism.vampirism_menu.no_tasks").withStyle(TextFormatting.WHITE);
            int width = this.font.width(text);
            this.font.drawShadow(matrixStack, text, this.leftPos + 152 - (width / 2), this.topPos + 52, 0);
        }

        this.renderAccessorySlots(matrixStack, mouseX, mouseY, partialTicks);

        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
        this.list.renderToolTip(matrixStack, mouseX, mouseY);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if (this.menu.areRefinementsAvailable()) {
            this.renderHoveredRefinementTooltip(matrixStack, mouseX, mouseY);
        }
    }

    protected void renderAccessorySlots(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (Slot slot : this.menu.slots) {
            if (this.isHovering(slot, mouseX, mouseY) && slot instanceof VampirismContainer.RemovingSelectorSlot && !this.menu.getRefinementStacks().get(slot.getSlotIndex()).isEmpty()){
                this.refinementRemoveButtons.get(slot.getSlotIndex()).render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        if (factionPlayer.getLevel() > 0) {
            this.level = FactionPlayerHandler.getOpt(factionPlayer.getRepresentingPlayer()).filter(f -> f.getLordLevel() > 0).map(f -> f.getLordTitle().copy().append(" (" + f.getLordLevel() + ")")).orElseGet(() -> new TranslationTextComponent("text.vampirism.level").append(" " + factionPlayer.getLevel())).withStyle(factionPlayer.getFaction().getChatColor());
        } else {
            this.level = StringTextComponent.EMPTY;
        }
        this.addButton(list = new ScrollableListWithDummyWidget<>(this.leftPos + 83, this.topPos + 7, 145, 104, 21, this::refreshTasks, (item, list1, isDummy) -> new TaskItem(item, list1, isDummy, this, this.factionPlayer)));

        this.addButton(new ImageButton(this.leftPos + 5, this.topPos + 90, 20, 20, 40, 205, 20, BACKGROUND, 256, 256, context -> {
            if (this.minecraft.player.isAlive() && VampirismPlayerAttributes.get(this.minecraft.player).faction != null) {
                Minecraft.getInstance().setScreen(new SkillsScreen(FactionPlayerHandler.getCurrentFactionPlayer(this.minecraft.player).orElse(null),this));
            }
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.skill_screen"), mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        this.addButton(new ImageButton(this.leftPos + 26, this.topPos + 90, 20, 20, 0, 205, 20, BACKGROUND, 256, 256, (context) -> {
            IPlayableFaction<?> factionNew = VampirismPlayerAttributes.get(this.minecraft.player).faction;
            Minecraft.getInstance().setScreen(new SelectActionScreen(factionNew.getColor(), true));
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.edit_actions"), mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        Button appearanceButton = this.addButton(new ImageButton(this.leftPos + 47, this.topPos + 90, 20, 20, 20, 205, 20, BACKGROUND, 256, 256, (context) -> {
            Minecraft.getInstance().setScreen(new VampirePlayerAppearanceScreen(this));
        }, (button1, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.appearance_menu"), mouseX, mouseY);
        }, StringTextComponent.EMPTY));
        if (!Helper.isVampire(minecraft.player)) {
            appearanceButton.active = false;
            appearanceButton.visible = false;
        }

        NonNullList<ItemStack> refinementList = this.menu.getRefinementStacks();
        for (Slot slot : this.menu.slots) {
            if (slot instanceof VampirismContainer.RemovingSelectorSlot){
                Button xButton = this.addButton(new ImageButton(this.getGuiLeft() + slot.x + 16 - 5, this.getGuiTop() + slot.y + 16 - 5, 5, 5, 60, 205, 0, BACKGROUND_REFINEMENTS, 256, 256, (button) -> {
                    VampirismMod.dispatcher.sendToServer(new CDeleteRefinementPacket(IRefinementItem.AccessorySlotType.values()[slot.index]));
                    refinementList.set(slot.index, ItemStack.EMPTY);
                }, (button12, matrixStack, xPos, yPos) -> {
                    VampirismScreen.this.renderTooltip(matrixStack,new TranslationTextComponent("gui.vampirism.vampirism_menu.destroy_item").withStyle(TextFormatting.RED), xPos, yPos);
                }, StringTextComponent.EMPTY) {
                    @Override
                    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                        this.visible = !refinementList.get(slot.index).isEmpty() && VampirismScreen.this.draggingItem.isEmpty() && overSlot(slot, mouseX, mouseY);
                        super.render(matrixStack, mouseX, mouseY, partialTicks);
                    }

                    private boolean overSlot(Slot slot, int mouseX, int mouseY) {
                        mouseX -= VampirismScreen.this.leftPos;
                        mouseY -= VampirismScreen.this.topPos;
                        return slot.x <= mouseX && slot.x + 16 > mouseX && slot.y <= mouseY && slot.y +16 > mouseY;
                    }
                });
                refinementRemoveButtons.put(slot.getSlotIndex(),xButton );
            }
        }

    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        int width = this.font.width(this.level);
        this.font.draw(stack, this.level,Math.max(5, 31 - (float)width /2), 81, -1);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float v, int i, int i1) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(this.menu.areRefinementsAvailable() ? BACKGROUND_REFINEMENTS : BACKGROUND);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventory(this.leftPos + 31, this.topPos + 72, 30, (float) (this.leftPos + 10) - this.oldMouseX, (float) (this.topPos + 75 - 50) - this.oldMouseY, this.minecraft.player);
    }

    protected void renderHoveredRefinementTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (this.hoveredSlot != null) {
            int index = this.hoveredSlot.index;
            NonNullList<ItemStack> list = this.menu.getRefinementStacks();
            if (index < list.size() && index >= 0) {
                if (this.minecraft.player.inventory.getCarried().isEmpty() && !list.get(index).isEmpty()) {
                    if (!this.refinementRemoveButtons.get(this.hoveredSlot.getSlotIndex()).isHovered()){
                        this.renderTooltip(matrixStack, list.get(index), mouseX, mouseY);

                    }
                } else {
                    if (!list.get(index).isEmpty() && this.menu.getSlot(index).mayPlace(this.minecraft.player.inventory.getCarried())) {
                        this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.destroy_item").withStyle(TextFormatting.RED), mouseX, mouseY);
                    }
                }
            }
        }
    }

    private class TaskItem extends de.teamlapen.vampirism.client.gui.widget.TaskItem<VampirismScreen> {

        private ImageButton button;

        public TaskItem(ITaskInstance item, ScrollableListWithDummyWidget<ITaskInstance> list, boolean isDummy, VampirismScreen screen, IFactionPlayer<?> factionPlayer) {
            super(item, list, isDummy, screen, factionPlayer);
            if (!item.isUnique()) {
                this.button = new ImageButton(0, 0, 8, 11, 0, 229, 11, TASKMASTER_GUI_TEXTURE, 256, 256, this::onClick, this::onTooltip, StringTextComponent.EMPTY);
            }
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (this.button != null && !this.isDummy && mouseX > this.button.x && mouseX < this.button.x + this.button.getWidth() && mouseY > this.button.y && mouseY < this.button.y + this.button.getHeight()) {
                this.button.onClick(mouseX, mouseY);
                return true;
            } else {
                return super.onClick(mouseX, mouseY);
            }
        }

        @Override
        public void renderItem(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.renderItem(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            if (this.button != null) {
                this.button.x = x + listWidth - 13;
                this.button.y = y + 1;
                this.button.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void renderItemToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
            if (this.button != null && this.button.isHovered()) {
                this.button.renderToolTip(matrixStack, mouseX, mouseY);
            } else {
                super.renderItemToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, zLevel);
            }
        }

        private float getDistance(int x1, int z1, int x2, int z2) {
            int i = x2 - x1;
            int j = z2 - z1;
            return MathHelper.sqrt((float) (i * i + j * j));
        }

        private void onClick(Button button) {
            PlayerEntity player = this.factionPlayer.getRepresentingPlayer();
            ITextComponent position = menu.taskWrapper.get(this.item.getTaskBoard()).getLastSeenPos().map(pos -> {
                int i = MathHelper.floor(getDistance(player.blockPosition().getX(), player.blockPosition().getZ(), pos.getX(), pos.getZ()));
                IFormattableTextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", pos.getX(), "~", pos.getZ())).withStyle((p_241055_1_) -> {
                    return p_241055_1_.withColor(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " ~ " + pos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
                });
                return itextcomponent.append(new TranslationTextComponent("gui.vampirism.vampirism_menu.distance", i));
            }).orElseGet(() -> new TranslationTextComponent("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(TextFormatting.GOLD));
            player.displayClientMessage(new TranslationTextComponent("gui.vampirism.vampirism_menu.last_known_pos").append(position), false);

        }

        private void onTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
            ITextComponent position = menu.taskWrapper.get(this.item.getTaskBoard()).getLastSeenPos().map(pos -> new StringTextComponent("[" + pos.toShortString() + "]").withStyle(TextFormatting.GREEN)).orElseGet(() -> new TranslationTextComponent("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(TextFormatting.GOLD));

            VampirismScreen.this.renderWrappedToolTip(matrixStack, Collections.singletonList(new TranslationTextComponent("gui.vampirism.vampirism_menu.last_known_pos").append(position)), mouseX, mouseY, font);

        }
    }

}
