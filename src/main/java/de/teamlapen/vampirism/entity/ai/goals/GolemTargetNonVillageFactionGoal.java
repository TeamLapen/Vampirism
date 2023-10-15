package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.TotemHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */
public class GolemTargetNonVillageFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final Map<IFaction<?>, Predicate<LivingEntity>> predicates = new HashMap<>();
    private final @NotNull IronGolem golem;
    private IFaction<?> faction;

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

    @SuppressWarnings({"unchecked", "rawtypes"})
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
        IFaction<?> faction = VReference.HUNTER_FACTION;
        if (VampirismConfig.BALANCE.golemAttackNonVillageFaction.get()) {
            Optional<TotemBlockEntity> tile = TotemHelper.getTotemNearPos(((ServerLevel) this.golem.level()), this.golem.blockPosition(), true);
            faction = tile.map(TotemBlockEntity::getControllingFaction).orElse(((IFaction) VReference.HUNTER_FACTION)); // Raw type because otherwise it does not work
        }

        if (faction != this.faction) {
            this.targetConditions.selector(predicates.computeIfAbsent(this.faction = faction, faction1 -> VampirismAPI.factionRegistry().getPredicate(faction1, true, true, false, false, null)));
            return true;
        }
        return false;
    }
}