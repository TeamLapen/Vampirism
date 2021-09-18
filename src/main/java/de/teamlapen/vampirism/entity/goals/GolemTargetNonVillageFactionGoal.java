package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.blockentity.TotemHelper;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */
public class GolemTargetNonVillageFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final Map<IFaction<?>, Predicate<LivingEntity>> predicates = new HashMap<>();
    private final IronGolem golem;
    private IFaction<?> faction;

    public GolemTargetNonVillageFactionGoal(IronGolem creature) {
        super(creature, LivingEntity.class, 4, false, false, null);
        this.golem = creature;
    }

    @Override
    public boolean canUse() {
        IFaction<?> faction = VReference.HUNTER_FACTION;
        if (VampirismConfig.BALANCE.golemAttackNonVillageFaction.get()) {
            Optional<TotemBlockEntity> tile = TotemHelper.getTotemNearPos(((ServerLevel) this.golem.level), this.golem.blockPosition(), true);
            faction = tile.map(TotemBlockEntity::getControllingFaction).orElse(((IPlayableFaction) VReference.HUNTER_FACTION));
        }

        if (faction != this.faction) {
            this.targetConditions.selector(predicates.computeIfAbsent(this.faction = faction, faction1 -> VampirismAPI.factionRegistry().getPredicate(faction1, true, true, false, false, null)));
        }

        return super.canUse();
    }
}