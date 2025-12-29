package de.teamlapen.vampirism.common.world.entity.ai.goals;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionPredicate;
import de.teamlapen.factions.common.util.TotemHelper;
import de.teamlapen.factions.common.world.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModFactions;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */
public class GolemTargetNonVillageFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final Map<Holder<? extends IFaction<?>>, TargetingConditions.Selector> predicates = new HashMap<>();
    private final @NotNull IronGolem golem;
    private Holder<? extends IFaction<?>> faction;

    public GolemTargetNonVillageFactionGoal(@NotNull IronGolem creature) {
        super(creature, LivingEntity.class, 4, false, false, null);
        this.golem = creature;
    }

    @Override
    public boolean canContinueToUse() {
        if (golem.tickCount % 16 == 0) {
            if (determineGolemFaction()) {
                return false;
            }
        }
        return super.canContinueToUse();
    }


    @Override
    public boolean canUse() {
        if (golem.tickCount < 20) return false; // Some delay to allow nearby totems to load
        return super.canUse();
    }

    @Override
    protected void findTarget() {
        determineGolemFaction();
        super.findTarget();
    }

    /**
     * Determine the faction of the golem by checking for nearby totems. Update the targetConditions accordingly
     *
     * @return Whether the faction has changed
     */
    private boolean determineGolemFaction() {
        Holder<? extends IFaction<?>> faction = ModFactions.HUNTER;
        if (ModConfig.balance().golemAttackNonVillageFaction.get()) {
            Optional<Holder<? extends IFaction<?>>> tile = TotemHelper.getTotemNearPos(((ServerLevel) this.golem.level()), this.golem.blockPosition(), true).map(TotemBlockEntity::getControllingFaction);
            if (tile.isPresent()) {
                faction = tile.get();
            }
        }

        if (IFaction.is(faction, this.faction)) {
            this.targetConditions.selector(predicates.computeIfAbsent(this.faction = faction, faction1 -> IFactionPredicate.builder(faction1).notNeutral().build()));
            return true;
        }
        return false;
    }
}