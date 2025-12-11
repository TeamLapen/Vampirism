package de.teamlapen.vampirism.common.entity.player;

import de.teamlapen.factions.api.factions.ILordPlayer;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.LevelingChange;
import de.teamlapen.factions.api.factions.lord.ILordTitleProvider;
import de.teamlapen.factions.common.config.ModConfig;
import de.teamlapen.factions.common.tasks.TaskManager;
import de.teamlapen.factions.common.factions.FactionBasePlayer;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.tasks.ITaskPlayer;
import de.teamlapen.factions.common.actions.ActionHandler;
import de.teamlapen.factions.common.skills.SkillHandler;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class CommonFactionPlayer<T extends IFactionPlayer<T> & ISkillPlayer<T> & ITaskPlayer<T> & ILordPlayer<T>> extends FactionBasePlayer<T> implements ISkillPlayer<T>, ITaskPlayer<T>, ILordPlayer<T> {

    /**
     * {@code @NotNull} on server, otherwise {@code null}
     */
    private final @Nullable TaskManager<T> taskManager;
    private final ActionHandler<T> actionHandler;
    private final SkillHandler<T> skillHandler;

    public CommonFactionPlayer(Player player) {
        super(player);
        if (player instanceof ServerPlayer serverPlayer) {
            //noinspection unchecked,rawtypes
            this.taskManager = new TaskManager(serverPlayer, this, this.getFaction());
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
    public void levelChanged(LevelingChange changes) {
        onLevelChanged(changes.getNewLevel());
    }

    protected void onLevelChanged(int newLevel) {

    }

    @MustBeInvokedByOverriders
    @Override
    public void leaveFaction() {
        onLevelChanged(0);
        this.onLevelReset();
        this.sync();
    }

    @MustBeInvokedByOverriders
    protected void onLevelReset() {
        this.getActionHandler().resetTimers();
        this.getSkillHandler().reset();
    }

    public int getMaxMinions() {
        return getLordLevel() * ModConfig.SERVER.miMinionPerLordLevel.get();
    }

    @Override
    public int getLordLevel() {
        return factionHandler().getLordLevel();
    }

    @SuppressWarnings("NullableProblems")
    public Optional<ILordTitleProvider> lordTitles() {
        return Optional.ofNullable(getFaction().value().lordTitles());
    }

    @Override
    public IPlayableFaction.TitleGender titleGender() {
        return factionHandler().titleGender();
    }

    @Override
    public @Nullable Component getLordTitle() {
        return lordTitles().map(titles -> titles.getLordTitle(getLordLevel(), factionHandler().titleGender())).orElse(null);
    }

    @Override
    public @Nullable Component getLordTitleShort() {
        return lordTitles().map(titles -> titles.getShort(getLordLevel(), factionHandler().titleGender())).orElse(null);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void registerProperties() {
        this.registerProperty(VResourceLocation.mod("action_handler")).subProperty(() -> this.actionHandler).register();
        this.registerProperty(VResourceLocation.mod("skill_handler")).subProperty(() -> this.skillHandler).register();
        //noinspection DataFlowIssue
        this.registerProperty(VResourceLocation.mod("task_manager")).subProperty(() -> this.taskManager).disableClientSync().register();
    }
}
