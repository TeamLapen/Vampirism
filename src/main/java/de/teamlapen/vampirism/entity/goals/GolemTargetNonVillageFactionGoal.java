package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */
public class GolemTargetNonVillageFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final Map<IFaction<?>, Predicate<LivingEntity>> predicates = new HashMap<>();
    private final IronGolemEntity golem;
    private IFaction<?> faction;

    public GolemTargetNonVillageFactionGoal(IronGolemEntity creature) {
        super(creature, LivingEntity.class, 4, false, false, null);
        this.golem = creature;
    }

    @Override
    public boolean shouldExecute() {
        IFaction<?> faction = VReference.HUNTER_FACTION;
        if (VampirismConfig.BALANCE.golemAttackNonVillageFaction.get()) {
            Optional<TotemTileEntity> tile = TotemHelper.getTotemNearPos(((ServerWorld) this.golem.world), this.golem.getPosition(), true);
            faction = tile.map(TotemTileEntity::getControllingFaction).orElse(((IPlayableFaction) VReference.HUNTER_FACTION));
        }

        if (faction != this.faction) {
            this.targetEntitySelector.setCustomPredicate(predicates.computeIfAbsent(this.faction = faction, faction1 -> VampirismAPI.factionRegistry().getPredicate(faction1, true, true, false, false, null)));
        }

        return super.shouldExecute();
    }
}