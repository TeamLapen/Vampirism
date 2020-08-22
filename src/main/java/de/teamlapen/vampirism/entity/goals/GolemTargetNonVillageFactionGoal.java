package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */

public class GolemTargetNonVillageFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final Map<IFaction<?>, Predicate<LivingEntity>> predicates = new HashMap<>();

    private IFaction<?> faction;
    private final IronGolemEntity golem;

    public GolemTargetNonVillageFactionGoal(IronGolemEntity creature) {
        super(creature, LivingEntity.class, 4, false, false, null);
        this.golem = creature;
    }

    @Override
    public boolean shouldExecute() {
        IFaction<?> faction = VReference.HUNTER_FACTION;
        if (VampirismConfig.BALANCE.golemAttackNonVillageFaction.get()) {
            Collection<PointOfInterest> points = ((ServerWorld)this.golem.world).getPointOfInterestManager().func_219146_b(p->true,this.golem.getPosition(),35, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
            if (points.size()>0) {
                BlockPos pos = TotemHelper.getTotemPosition(points);
                if (pos != null && this.golem.world.getChunkProvider().isChunkLoaded(new ChunkPos(pos))) {
                    TotemTileEntity tile = ((TotemTileEntity) golem.world.getTileEntity(pos));
                    if (tile != null) {
                        if (tile.getControllingFaction() != null) {
                            faction = tile.getControllingFaction();
                        }
                    }
                }
            }
        }

        if (faction != this.faction) {
            this.targetEntitySelector.setCustomPredicate(predicates.computeIfAbsent(this.faction = faction, faction1 -> VampirismAPI.factionRegistry().getPredicate(faction1, true, true, false, false, null)));
        }

        return super.shouldExecute();
    }
}