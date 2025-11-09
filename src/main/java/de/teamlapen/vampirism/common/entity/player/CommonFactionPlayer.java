package de.teamlapen.vampirism.common.entity.player;

import de.teamlapen.sync.common.storage.UpdateParams;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.ITaskPlayer;
import de.teamlapen.vampirism.common.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.common.entity.player.skills.SkillHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
                this.sync();
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
    public void serialize(@NotNull ValueOutput output) {
        super.serialize(output);

        assert this.taskManager != null;
        this.taskManager.saveToChild(output);
        this.actionHandler.saveToChild(output);
        this.skillHandler.saveToChild(output);
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserialize(@NotNull ValueInput input) {
        super.deserialize(input);

        assert this.taskManager != null;
        this.taskManager.loadFromChild(input);
        this.actionHandler.loadFromChild(input);
        this.skillHandler.loadFromChild(input);
    }

    @MustBeInvokedByOverriders
    @Override
    public void serializeUpdateInternal(ValueOutput output, UpdateParams params) {
        super.serializeUpdateInternal(output, params);
        this.actionHandler.updateToChild(output, params);
        this.skillHandler.updateToChild(output, params);
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeUpdate(@NotNull ValueInput input) {
        super.deserializeUpdate(input);
        this.actionHandler.updateFromChild(input);
        this.skillHandler.updateFromChild(input);
    }
}
