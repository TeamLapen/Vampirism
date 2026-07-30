package de.teamlapen.vampirism.common.world.entity.player;

import de.teamlapen.faction.api.factions.FactionExtensionType;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.factions.FactionBasePlayer;
import de.teamlapen.faction.common.factions.actions.ActionHandler;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class CommonFactionPlayer<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends FactionBasePlayer<T> implements ISkillPlayer<T> {

    /**
     * {@code @NotNull} on server, otherwise {@code null}
     */
    private final ActionHandler<T> actionHandler;
    private final SkillHandler<T> skillHandler;

    public CommonFactionPlayer(Player player) {
        super(player);
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

    @MustBeInvokedByOverriders
    @Override
    public void onDeath(DamageSource src) {
        this.actionHandler.deactivateAllActions();
    }

    @MustBeInvokedByOverriders
    @Override
    public void onRespawn() {
        onLevelChanged(getLevel());
    }

    @MustBeInvokedByOverriders
    @Override
    public void onUpdate() {
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

    @Override
    public Component getShortLevelDisplay() {
        return Component.literal(String.valueOf(getLevel()));
    }

    @Override
    public Component getLevelDisplay() {
        return Component.literal(String.valueOf(getLevel()));
    }

    @MustBeInvokedByOverriders
    @Override
    protected void registerProperties() {
        super.registerProperties();
        this.registerProperty(VIdentifier.mod("action_handler")).subProperty(() -> this.actionHandler).register();
        this.registerProperty(VIdentifier.mod("skill_handler")).subProperty(() -> this.skillHandler).register();
    }
}
