package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWidget;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.inventory.container.VampirismContainer;
import de.teamlapen.vampirism.player.TaskManager;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class VampirismScreen extends ContainerScreen<VampirismContainer> implements ExtendedScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampirism_menu.png");


    private final int display_width = 234;
    private final int display_height = 205;

    private int oldMouseX;
    private int oldMouseY;
    private ScrollableListWidget<TaskContainer.TaskInfo> list;
    private final IFactionPlayer<?> factionPlayer;

    public VampirismScreen(VampirismContainer container, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(container, playerInventory, titleIn);
        this.xSize = display_width;
        this.ySize = display_height;
        this.playerInventoryTitleX = 36;
        this.playerInventoryTitleY = this.ySize - 93;
        this.container.setReloadListener(() -> this.list.refresh());
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(list = new ScrollableListWithDummyWidget<>(this.guiLeft + 83, this.guiTop + 7, 145, 104, 21, this::refreshTasks, (item, list1, isDummy) -> new TaskItem(item, list1, isDummy, this, this.factionPlayer)));

        this.addButton(new ImageButton(this.guiLeft + 5, this.guiTop + 90, 20, 20, 40, 205, 20, BACKGROUND, 256, 256, context -> {
            if (this.minecraft.player.isAlive() && FactionPlayerHandler.get(this.minecraft.player).getCurrentFaction() != null) {
                Minecraft.getInstance().displayGuiScreen(new SkillsScreen(this));
            }
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.skill_screen"), mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        this.addButton(new ImageButton(this.guiLeft + 26, this.guiTop + 90, 20, 20, 0, 205, 20, BACKGROUND, 256, 256, (context) -> {
            IPlayableFaction<?> factionNew = FactionPlayerHandler.get(this.minecraft.player).getCurrentFaction();
            Minecraft.getInstance().displayGuiScreen(new SelectActionScreen(factionNew.getColor(), true));
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.edit_actions"), mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        Button button = this.addButton(new ImageButton(this.guiLeft + 47, this.guiTop + 90, 20, 20, 20, 205, 20, BACKGROUND, 256, 256, (context) -> {
            Minecraft.getInstance().displayGuiScreen(new VampirePlayerAppearanceScreen(this));
        }, (button1, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, new TranslationTextComponent("gui.vampirism.vampirism_menu.appearance_menu"), mouseX, mouseY);
        }, StringTextComponent.EMPTY));
        if (!Helper.isVampire(minecraft.player)) {
            button.active = false;
            button.visible = false;
        }

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeys.getKeyBinding(ModKeys.KEY.SKILL).matchesKey(keyCode, scanCode)) {
            this.closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Collection<TaskContainer.TaskInfo> refreshTasks() {
        return this.container.tasks.entrySet().stream().flatMap(a -> a.getValue().stream().map(b -> new TaskContainer.TaskInfo(b, a.getKey()))).collect(Collectors.toList());
    }

    @Override
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    @Override
    public TaskContainer getTaskContainer() {
        return this.container;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        for (int i = 0; i < this.container.getRefinementStacks().size(); i++) {
            ItemStack stack = this.container.getRefinementStacks().get(i);
            Slot slot = this.container.getSlot(i);
            int x = slot.xPos + this.guiLeft;
            int y = slot.yPos + this.guiTop;
            this.itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, stack, x, y);
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y, null);
        }

        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
        this.list.renderToolTip(matrixStack, mouseX, mouseY);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (!this.dragSplitting) {
            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float v, int i, int i1) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        InventoryScreen.drawEntityOnScreen(this.guiLeft + 31, this.guiTop + 72, 30, (float) (this.guiLeft + 10) - this.oldMouseX, (float) (this.guiTop + 75 - 50) - this.oldMouseY, this.minecraft.player);
    }

    private class TaskItem extends de.teamlapen.vampirism.client.gui.widget.TaskItem<VampirismScreen> {

        private ImageButton button;

        public TaskItem(TaskContainer.TaskInfo item, ScrollableListWithDummyWidget<TaskContainer.TaskInfo> list, boolean isDummy, VampirismScreen screen, IFactionPlayer<?> factionPlayer) {
            super(item, list, isDummy, screen, factionPlayer);
            if (!item.task.isUnique()) {
                this.button = new ImageButton(0, 0, 8, 11, 0, 229, 11, TASKMASTER_GUI_TEXTURE, 256, 256, this::onClick, this::onTooltip, StringTextComponent.EMPTY);
            }
        }

        @Override
        public void renderItem(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.renderItem(matrixStack, x, y, listWidth, listHeight, itemHeight, mouseX, mouseY, partialTicks, zLevel);
            if (this.button != null) {
                this.button.x = x + listWidth - 13;
                this.button.y = y + 5;
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

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (this.button != null && !this.isDummy && mouseX > this.button.x && mouseX < this.button.x + this.button.getWidth() && mouseY > this.button.y && mouseY < this.button.y + this.button.getHeightRealms()) {
                this.button.onClick(mouseX, mouseY);
                return true;
            } else {
                return super.onClick(mouseX, mouseY);
            }
        }

        private void onTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
            TaskManager.TaskBoardInfo info = container.taskBoardInfos.get(this.item.taskBoard);
            if (info != null) {
                VampirismScreen.this.renderWrappedToolTip(matrixStack, Collections.singletonList(new TranslationTextComponent("gui.vampirism.vampirism_menu.last_known_pos").append(new StringTextComponent("[" + info.getLastSeenPos().getCoordinatesAsString() + "]").mergeStyle(TextFormatting.GREEN))), mouseX, mouseY, font);
            }
        }

        private void onClick(Button button) {
            TaskManager.TaskBoardInfo info = container.taskBoardInfos.get(this.item.taskBoard);
            if (info != null) {
                BlockPos pos = info.getLastSeenPos();
                PlayerEntity player = this.factionPlayer.getRepresentingPlayer();
                int i = MathHelper.floor(getDistance(player.getPosition().getX(), player.getPosition().getZ(), pos.getX(), pos.getZ()));
                ITextComponent itextcomponent = TextComponentUtils.wrapWithSquareBrackets(new TranslationTextComponent("chat.coordinates", pos.getX(), "~", pos.getZ())).modifyStyle((p_241055_1_) -> {
                    return p_241055_1_.setFormatting(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " ~ " + pos.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
                });
                player.sendStatusMessage(new TranslationTextComponent("gui.vampirism.vampirism_menu.last_known_pos").append(itextcomponent).append(new TranslationTextComponent("gui.vampirism.vampirism_menu.distance", i)), false);
            }
        }

        private float getDistance(int x1, int z1, int x2, int z2) {
            int i = x2 - x1;
            int j = z2 - z1;
            return MathHelper.sqrt((float) (i * i + j * j));
        }
    }

}
