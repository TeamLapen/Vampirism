package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */

public class GolemTargetNonVillageFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final Map<IFaction<?>, Predicate<LivingEntity>> predicates = new HashMap<>();

    private IFaction<?> faction;

    public GolemTargetNonVillageFactionGoal(IronGolemEntity creature) {
        super(creature, LivingEntity.class, 4, false, false, null);
    }

    @Override
    public boolean shouldExecute() {
        IFaction<?> faction = VReference.HUNTER_FACTION;
        if (VampirismConfig.BALANCE.golemAttackNonVillageFaction.get()) {
            StructureStart<?> structureStart = UtilLib.getStructureStartAt(goalOwner, Structure.field_236381_q_);
            if (structureStart != null && structureStart.isValid()) {
                BlockPos pos = TotemTileEntity.getTotemPosition(structureStart);
                if (pos != null) {
                    TotemTileEntity tile = ((TotemTileEntity) goalOwner.world.getTileEntity(pos));
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