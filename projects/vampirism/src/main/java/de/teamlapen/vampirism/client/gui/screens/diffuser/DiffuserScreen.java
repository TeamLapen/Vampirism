package de.teamlapen.vampirism.client.gui.screens.diffuser;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.gui.components.ProgressBar;
import de.teamlapen.vampirism.common.network.packets.common.PlayerOwnedBlockEntityLockPacket;
import de.teamlapen.vampirism.common.world.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.common.world.inventory.diffuser.DiffuserMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public abstract class DiffuserScreen<T extends DiffuserMenu> extends AbstractContainerScreen<T> {
    private static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/container/diffuser.png");
    private static final Identifier LIT_PROGRESS_SPRITE = VIdentifier.mc("container/furnace/lit_progress");

    protected final int xSize = 176;
    protected final int ySize = 166;

    protected ProgressBar startupBar;
    private LockIconButton lock;

    public DiffuserScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.startupBar = this.addRenderableOnly(new ProgressBar(this.getLeftPos() + (xSize - 150) / 2, this.getTopPos() + 23, 150, getBootMessage(0)));
        this.startupBar.setColor(getProgressBarColor());
        this.startupBar.setFGColor(getProgressBarFGColor());
        if (this.menu.hasOwner()) {
            lock = this.addRenderableWidget(new LockIconButton(this.getLeftPos() + xSize - 30, this.getTopPos() + 50, pButton -> {
                setLock(!((LockIconButton) pButton).isLocked());
                lock.setTooltip(Tooltip.create(getLockText()));
            }));
            lock.setLocked(this.menu.getLockStatus() == PlayerOwnedBlockEntity.Lock.PRIVATE);
            lock.active = this.menu.isOwner(minecraft.player);
            lock.setTooltip(Tooltip.create(getLockText()));
        }
    }

    private Component getLockText() {
        if (this.menu.isOwner(minecraft.player)) {
            return switch (this.menu.getLockStatus()) {
                case PUBLIC -> Component.translatable("gui.vampirism.diffuser.unlocked_for_others");
                case PRIVATE -> Component.translatable("gui.vampirism.diffuser.locked_for_others");
            };
        } else {
            return Component.translatable("gui.vampirism.diffuser.not_the_owner");
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
            return Component.translatable("gui.vampirism.diffuser.active");
        } else if (progress == 0) {
            return Component.translatable("gui.vampirism.diffuser.idle");
        } else {
            return Component.translatable("gui.vampirism.diffuser.booting");
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.xSize, this.ySize, 256, 256);
        if (this.menu.isLit()) {
            int l = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - l, getLeftPos() + 26 + 19, getTopPos() + 53 + 2 + (14 - l), 14, l);
        }
    }
}
