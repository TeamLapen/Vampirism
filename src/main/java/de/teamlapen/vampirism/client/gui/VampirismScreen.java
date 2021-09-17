package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWidget;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.inventory.container.VampirismContainer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.Helper;
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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public class VampirismScreen extends AbstractContainerScreen<VampirismContainer> implements ExtendedScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu.png");
    private static final ResourceLocation BACKGROUND_REFINEMENTS = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu_refinements.png");


    private final int display_width = 234;
    private final int display_height = 205;
    private final IFactionPlayer<?> factionPlayer;
    private int oldMouseX;
    private int oldMouseY;
    private ScrollableListWidget<ITaskInstance> list;

    public VampirismScreen(VampirismContainer container, Inventory playerInventory, Component titleIn) {
        super(container, playerInventory, titleIn);
        this.imageWidth = display_width;
        this.imageHeight = display_height;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = this.imageHeight - 93;
        this.menu.setReloadListener(() -> this.list.refresh());
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
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
        if (ModKeys.getKeyBinding(ModKeys.KEY.VAMPIRISM_MENU).matches(keyCode, scanCode)) {
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
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.menu.areRefinementsAvailable()) {
            for (int i = 0; i < this.menu.getRefinementStacks().size(); i++) {
                ItemStack stack = this.menu.getRefinementStacks().get(i);
                Slot slot = this.menu.getSlot(i);
                int x = slot.x + this.leftPos;
                int y = slot.y + this.topPos;
                this.itemRenderer.renderAndDecorateItem( stack, x, y);
                this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y, null);
            }
        }
        if (this.list.isEmpty()) {
            Component text = new TranslatableComponent("gui.vampirism.vampirism_menu.no_tasks").withStyle(ChatFormatting.WHITE);
            int width = this.font.width(text);
            this.font.drawShadow(matrixStack, text, this.leftPos + 152 - (width / 2), this.topPos + 52, 0);
        }

        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
        this.list.renderToolTip(matrixStack, mouseX, mouseY);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if (this.menu.areRefinementsAvailable()) this.renderHoveredRefinementTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(list = new ScrollableListWithDummyWidget<>(this.leftPos + 83, this.topPos + 7, 145, 104, 21, this::refreshTasks, (item, list1, isDummy) -> new TaskItem(item, list1, isDummy, this, this.factionPlayer)));

        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.topPos + 90, 20, 20, 40, 205, 20, BACKGROUND, 256, 256, context -> {
            if (this.minecraft.player.isAlive() && VampirismPlayerAttributes.get(this.minecraft.player).faction != null) {
                Minecraft.getInstance().setScreen(new SkillsScreen(this));
            }
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslatableComponent("gui.vampirism.vampirism_menu.skill_screen"), mouseX, mouseY);
        }, TextComponent.EMPTY));

        this.addRenderableWidget(new ImageButton(this.leftPos + 26, this.topPos + 90, 20, 20, 0, 205, 20, BACKGROUND, 256, 256, (context) -> {
            IPlayableFaction<?> factionNew = VampirismPlayerAttributes.get(this.minecraft.player).faction;
            Minecraft.getInstance().setScreen(new ActionSelectScreen(new Color(factionNew.getColor()), true));
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslatableComponent("gui.vampirism.vampirism_menu.edit_actions"), mouseX, mouseY);
        }, TextComponent.EMPTY));

        Button button = this.addRenderableWidget(new ImageButton(this.leftPos + 47, this.topPos + 90, 20, 20, 20, 205, 20, BACKGROUND, 256, 256, (context) -> {
            Minecraft.getInstance().setScreen(new VampirePlayerAppearanceScreen(this));
        }, (button1, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslatableComponent("gui.vampirism.vampirism_menu.appearance_menu"), mouseX, mouseY);
        }, TextComponent.EMPTY));
        if (!Helper.isVampire(minecraft.player)) {
            button.active = false;
            button.visible = false;
        }

    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float v, int i, int i1) {
        this.renderBackground(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0,this.menu.areRefinementsAvailable() ? BACKGROUND_REFINEMENTS : BACKGROUND);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventory(this.leftPos + 31, this.topPos + 72, 30, (float) (this.leftPos + 10) - this.oldMouseX, (float) (this.topPos + 75 - 50) - this.oldMouseY, this.minecraft.player);
    }

    protected void renderHoveredRefinementTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (this.hoveredSlot != null) {
            int index = this.hoveredSlot.index;
            NonNullList<ItemStack> list = this.menu.getRefinementStacks();
            if (index < list.size() && index >= 0) {
                if (this.getMenu().getCarried().isEmpty() && !list.get(index).isEmpty()) {
                    this.renderTooltip(matrixStack, list.get(index), mouseX, mouseY);
                } else {
                    if (!list.get(index).isEmpty() && this.menu.getSlot(index).mayPlace(this.getMenu().getCarried())) {
                        this.renderTooltip(matrixStack, new TranslatableComponent("gui.vampirism.vampirism_menu.destroy_item").withStyle(ChatFormatting.RED), mouseX, mouseY);
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
                this.button = new ImageButton(0, 0, 8, 11, 0, 229, 11, TASKMASTER_GUI_TEXTURE, 256, 256, this::onClick, this::onTooltip, TextComponent.EMPTY);
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
        public void renderItem(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.renderItem(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            if (this.button != null) {
                this.button.x = x + listWidth - 13;
                this.button.y = y + 1;
                this.button.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void renderItemToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
            if (this.button != null && this.button.isHovered()) {
                this.button.renderToolTip(matrixStack, mouseX, mouseY);
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
                MutableComponent itextcomponent = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", pos.getX(), "~", pos.getZ())).withStyle((p_241055_1_) -> {
                    return p_241055_1_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " ~ " + pos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip")));
                });
                return itextcomponent.append(new TranslatableComponent("gui.vampirism.vampirism_menu.distance", i));
            }).orElseGet(() -> new TranslatableComponent("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD));
            player.displayClientMessage(new TranslatableComponent("gui.vampirism.vampirism_menu.last_known_pos").append(position), false);

        }

        private void onTooltip(Button button, PoseStack matrixStack, int mouseX, int mouseY) {
            Component position = menu.taskWrapper.get(this.item.getTaskBoard()).getLastSeenPos().map(pos -> new TextComponent("[" + pos.toShortString() + "]").withStyle(ChatFormatting.GREEN)).orElseGet(() -> new TranslatableComponent("gui.vampirism.vampirism_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD));
            renderComponentToolTip(matrixStack, Collections.singletonList(new TranslatableComponent("gui.vampirism.vampirism_menu.last_known_pos").append(position)), mouseX, mouseY, font);

        }
    }

}
