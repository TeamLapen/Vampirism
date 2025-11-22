package de.teamlapen.vampirism.common.entity.player;

import de.teamlapen.factions.common.tasks.TaskManager;
import de.teamlapen.factions.common.factions.FactionBasePlayer;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.tasks.ITaskPlayer;
import de.teamlapen.factions.common.actions.ActionHandler;
import de.teamlapen.factions.common.skills.SkillHandler;
import de.teamlapen.vampirism.api.util.VResourceLocation;
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
    private final ActionHandler<T> actionHandler;
    private final SkillHandler<T> skillHandler;
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
    public ActionHandler<T> getActionHandler() {
        return this.actionHandler;
    }

    @Override
    public SkillHandler<T> getSkillHandler() {
        return this.skillHandler;
    }

    /**
     * null on client and @NotNull on server
     */
    @Override
    public TaskManager<?> getTaskManager() {
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

    @MustBeInvokedByOverriders
    @Override
    protected void registerProperties() {
        this.registerProperty(VResourceLocation.mod("action_handler"), true, () -> this.actionHandler);
        this.registerProperty(VResourceLocation.mod("skill_handler"), true, () -> this.skillHandler);
        //noinspection DataFlowIssue
        this.registerProperty(VResourceLocation.mod("task_manager"), false, () -> this.taskManager); // task manager is not synced
    }
}
