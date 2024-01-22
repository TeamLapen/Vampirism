package de.teamlapen.vampirism.client.gui.screens.diffuser;

import de.teamlapen.lib.lib.client.gui.ProgressBar;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.inventory.diffuser.DiffuserMenu;
import de.teamlapen.vampirism.network.PlayerOwnedBlockEntityLockPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.NotNull;

public abstract class DiffuserScreen<T extends DiffuserMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/diffuser.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE = new ResourceLocation("container/furnace/lit_progress");

    protected final int xSize = 176;
    protected final int ySize = 166;

    protected ProgressBar startupBar;
    private LockIconButton lock;

    public DiffuserScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.startupBar = this.addRenderableOnly(new ProgressBar(this.getGuiLeft() + (xSize - 150) / 2, this.getGuiTop() + 23, 150, getBootMessage(0)));
        this.startupBar.setColor(getProgressBarColor());
        this.startupBar.setFGColor(getProgressBarFGColor());
        if (this.menu.hasOwner()) {
            lock = this.addRenderableWidget(new LockIconButton(this.getGuiLeft() + xSize - 30, this.getGuiTop() + 50, new Button.OnPress() {
                @Override
                public void onPress(@NotNull Button pButton) {
                    setLock(!((LockIconButton) pButton).isLocked());
                    lock.setTooltip(Tooltip.create(getLockText()));
                }
            }));
            lock.setLocked(this.menu.getLockStatus() == PlayerOwnedBlockEntity.Lock.PRIVATE);
            lock.active = this.menu.isOwner(minecraft.player);
            lock.setTooltip(Tooltip.create(getLockText()));
        }
    }

    private Component getLockText() {
        if (this.menu.isOwner(minecraft.player)) {
            return switch (this.menu.getLockStatus()) {
                case PUBLIC -> Component.translatable("text.vampirism.screen.unlocked_for_other_player");
                case PRIVATE -> Component.translatable("text.vampirism.screen.locked_for_other_player");
            };
        } else {
            return Component.translatable("text.vampirism.screen.lock_not_owner");
        }
    }

    private void setLock(boolean locked) {
        lock.setLocked(locked);
        PlayerOwnedBlockEntity.Lock lock = locked ? PlayerOwnedBlockEntity.Lock.PRIVATE : PlayerOwnedBlockEntity.Lock.PUBLIC;
        menu.setLockStatus(lock);
        this.minecraft.player.connection.send(new PlayerOwnedBlockEntityLockPacket(this.menu.containerId, new PlayerOwnedBlockEntity.LockDataHolder(lock)));
    }

    protected abstract int getProgressBarColor();

    protected int getProgressBarFGColor() {
        return 0xFFFFFF;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void containerTick() {
        this.updateProgress();
    }

    protected void updateProgress() {
        float bootProgress = this.menu.getBootProgress();
        this.startupBar.setProgress(bootProgress);
        this.startupBar.setMessage(getBootMessage(bootProgress));
    }

    protected Component getBootMessage(float progress) {
        if (progress == 1f) {
            return Component.translatable("text.vampirism.fog_diffuser.active");
        } else if (progress == 0) {
            return Component.translatable("text.vampirism.fog_diffuser.idle");
        } else {
            return Component.translatable("text.vampirism.fog_diffuser.booting");
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BACKGROUND, this.getGuiLeft(), this.getGuiTop(), 0, 0, 0, this.xSize, this.ySize, 256, 256);
        if (this.menu.isLit()) {
            int l = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            pGuiGraphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14,0,14-l, getGuiLeft()+26 + 19, getGuiTop() + 53 +2+(14-l), 14, l);
        }
    }
}
