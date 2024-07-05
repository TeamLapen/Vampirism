package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.ITaskPlayer;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommonFactionPlayer<T extends IFactionPlayer<T> & ISkillPlayer<T> & ITaskPlayer<T>> extends FactionBasePlayer<T> implements ISkillPlayer<T>, ITaskPlayer<T> {

    /**
     * {@code @NotNull} on server, otherwise {@code null}
     */
    private final @Nullable TaskManager<T> taskManager;
    private final @NotNull ActionHandler<T> actionHandler;
    private final @NotNull SkillHandler<T> skillHandler;

    public CommonFactionPlayer(Player player) {
        super(player);
        if (player instanceof ServerPlayer) {
            this.taskManager = new TaskManager((ServerPlayer) player, this, this.getFaction());
        } else {
            this.taskManager = null;
        }
        this.actionHandler = createActionHandler();
        this.skillHandler = createSkillHandler();
    }

    protected abstract ActionHandler<T> createActionHandler();

    protected abstract SkillHandler<T> createSkillHandler();

    @Override
    public @NotNull ActionHandler<T> getActionHandler() {
        return this.actionHandler;
    }

    @Override
    public @NotNull SkillHandler<T> getSkillHandler() {
        return this.skillHandler;
    }

    /**
     * null on client and @NotNull on server
     */
    @NotNull
    @Override
    public TaskManager getTaskManager() {
        assert this.taskManager != null;
        return this.taskManager;
    }

    @MustBeInvokedByOverriders
    @Override
    public void onDeath(DamageSource src) {
        this.actionHandler.deactivateAllActions();
    }

    @MustBeInvokedByOverriders
    @Override
    public void onUpdate() {
        if (!isRemote()) {
            assert this.taskManager != null;
            this.taskManager.tick();
        }
        if (getLevel() > 0) {
            this.actionHandler.updateActions();
        }
    }

    @Override
    public void sync() {
        CompoundTag syncTag = new CompoundTag();
        boolean syncAll = false;
        CompoundTag actionTag = this.actionHandler.serializeUpdateNBT(asEntity().level().registryAccess(), false);
        if (!actionTag.isEmpty()) {
            syncAll = true;
            syncTag.put(this.actionHandler.nbtKey(), actionTag);
        }
        CompoundTag skillTag = this.skillHandler.serializeUpdateNBT(asEntity().level().registryAccess(), false);
        if (!skillTag.isEmpty()) {
            syncTag.put(this.skillHandler.nbtKey(), skillTag);
        }

        if (!syncTag.isEmpty()) {
            sync(syncTag, syncAll);
        }
    }

    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            this.actionHandler.onActionsReactivated();
        }
    }

    @MustBeInvokedByOverriders
    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!isRemote()) {
            if (newLevel <= 0) {
                this.onLevelReset(false);
                this.sync(true);
            }

        } else {
            if (newLevel == 0) {
                this.onLevelReset(true);
            }
        }
    }

    protected void onLevelReset(boolean client) {
        this.getActionHandler().resetTimers();

        if (!client) {
            this.getSkillHandler().reset();
        }
    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);

        assert this.taskManager != null;
        tag.put(this.taskManager.nbtKey(), this.taskManager.serializeNBT(provider));
        tag.put(this.actionHandler.nbtKey(), this.actionHandler.serializeNBT(provider));
        tag.put(this.skillHandler.nbtKey(), this.skillHandler.serializeNBT(provider));
        return tag;
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);

        assert this.taskManager != null;
        this.taskManager.deserializeNBT(provider, nbt.getCompound(this.taskManager.nbtKey()));
        this.actionHandler.deserializeNBT(provider, nbt.getCompound(this.actionHandler.nbtKey()));
        this.skillHandler.deserializeNBT(provider, nbt.getCompound(this.skillHandler.nbtKey()));
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT(@NotNull HolderLookup.Provider provider, boolean all) {
        var nbt = super.serializeUpdateNBT(provider, all);
        nbt.put(this.actionHandler.nbtKey(), this.actionHandler.serializeUpdateNBT(provider, false));
        nbt.put(this.skillHandler.nbtKey(), this.skillHandler.serializeUpdateNBT(provider, false));
        return nbt;
    }

    @Override
    public void deserializeUpdateNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag nbt) {
        super.deserializeUpdateNBT(provider, nbt);
        this.actionHandler.deserializeUpdateNBT(provider, nbt.getCompound(this.actionHandler.nbtKey()));
        this.skillHandler.deserializeUpdateNBT(provider, nbt.getCompound(this.skillHandler.nbtKey()));
    }
}
