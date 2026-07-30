package de.teamlapen.vampirism.common.world.entity.player;

import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.factions.FactionBasePlayer;
import de.teamlapen.faction.common.factions.actions.ActionHandler;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

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
    public void levelChanged(FactionUpdate changes) {
        onLevelChanged(changes.getLevel());
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
        return getPlayerLord().filter(l -> l.getLordLevel() > 0).map(ILordPlayer::getLordTitleShort)
                .orElseGet(() -> Component.literal(String.valueOf(getLevel())));
    }

    @Override
    public Component getLevelDisplay() {
        return getPlayerLord().filter(l -> l.getLordLevel() > 0).map(ILordPlayer::getLordTitle)
                .orElseGet(() -> Component.translatable("gui.factionapi.level").append(" " + getLevel()));
    }

    @MustBeInvokedByOverriders
    @Override
    protected void registerProperties() {
        super.registerProperties();
        this.registerProperty(VIdentifier.mod("action_handler")).subProperty(() -> this.actionHandler).register();
        this.registerProperty(VIdentifier.mod("skill_handler")).subProperty(() -> this.skillHandler).register();
    }
}
