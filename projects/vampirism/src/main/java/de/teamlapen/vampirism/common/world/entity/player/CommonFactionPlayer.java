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

public abstract class CommonFactionPlayer<T extends IFactionPlayer<T> & ISkillPlayer<T> & ILordPlayer<T>> extends FactionBasePlayer<T> implements ISkillPlayer<T>, ILordPlayer<T> {

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

    public int getMaxMinions() {
        return getLordLevel() * FactionConfig.server().minionPerLordLevel.get();
    }

    @Override
    public int getLordLevel() {
        return factionHandler().getLordLevel();
    }

    @Override
    public IPlayableFaction.TitleGender titleGender() {
        return factionHandler().titleGender();
    }

    @Nullable
    @Override
    public Component getLordTitle() {
        return getFaction().components().getOrDefault(FactionDataComponents.LORD_TITLES, LordTitles.EMPTY).get(getLordLevel(), factionHandler().titleGender());
    }

    @Nullable
    @Override
    public Component getLordTitleShort() {
        return getFaction().components().getOrDefault(FactionDataComponents.LORD_TITLES, LordTitles.EMPTY).getShort(getLordLevel(), factionHandler().titleGender());
    }

    @Override
    public Component getShortLevelDisplay() {
        if (getLordLevel() > 0 && getLordTitleShort() instanceof Component shortLord) {
            return shortLord;
        }
        return Component.literal(String.valueOf(getLevel()));
    }

    @Override
    public Component getLevelDisplay() {
        if (getLordLevel() > 0 && getLordTitle() instanceof Component longLord) {
            return longLord;
        }
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
