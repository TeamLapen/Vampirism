package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.lib.storage.UpdateParams;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.ITaskPlayer;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
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
    protected boolean isDirty;

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
        this.actionHandler.resetTimers();
    }

    @MustBeInvokedByOverriders
    @Override
    public void onRespawn() {
        this.player.addEffect(new MobEffectInstance(ModEffects.RESURRECTION_FATIGUE, 300));
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

    @MustBeInvokedByOverriders
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
                this.sync(UpdateParams.all());
            }

        } else {
            if (newLevel == 0) {
                this.onLevelReset(true);
            }
        }
    }

    @MustBeInvokedByOverriders
    protected void onLevelReset(boolean client) {
        this.getActionHandler().resetTimers();

        if (!client) {
            this.getSkillHandler().reset();
        }
    }

    @Override
    public boolean needsUpdate() {
        return this.isDirty || this.actionHandler.needsUpdate() || this.skillHandler.needsUpdate();
    }

    @Override
    public void updateSend() {
        this.isDirty = false;
    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);

        assert this.taskManager != null;
        this.taskManager.saveToCompound(provider, tag);
        this.actionHandler.saveToCompound(provider, tag);
        this.skillHandler.saveToCompound(provider, tag);
        return tag;
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);

        assert this.taskManager != null;
        this.taskManager.loadFromCompound(provider, nbt);
        this.actionHandler.loadFromCompound(provider, nbt);
        this.skillHandler.loadFromCompound(provider, nbt);
    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeUpdateNBTInternal(@NotNull HolderLookup.Provider provider, UpdateParams params) {
        var nbt = super.serializeUpdateNBTInternal(provider, params);
        this.actionHandler.updateToCompound(provider, nbt, params);
        this.skillHandler.updateToCompound(provider, nbt, params);
        return nbt;
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeUpdateNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag nbt) {
        super.deserializeUpdateNBT(provider, nbt);
        this.actionHandler.updateFromCompound(provider, nbt);
        this.skillHandler.updateFromCompound(provider, nbt);
    }
}
