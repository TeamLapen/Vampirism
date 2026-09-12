package de.teamlapen.faction.common.util;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class RegUtil {

    public static Identifier id(Holder<?> key) {
        return key.unwrapKey().orElseThrow().identifier();
    }

    public static Identifier id(ResourceKey<?> key) {
        return key.identifier();
    }

    @SuppressWarnings("DataFlowIssue")
    public static Identifier id(IAction<?> action) {
        return ModRegistries.ACTIONS.getKey(action);
    }

    public static Identifier id(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    @SuppressWarnings("DataFlowIssue")
    public static Identifier id(IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.getKey(minionTask);
    }

    @SuppressWarnings("DataFlowIssue")
    public static Identifier id(IRefinement refinement) {
        return ModRegistries.REFINEMENTS.getKey(refinement);
    }

    @SuppressWarnings("DataFlowIssue")
    public static Identifier id(IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.getKey(refinementSet);
    }

}
