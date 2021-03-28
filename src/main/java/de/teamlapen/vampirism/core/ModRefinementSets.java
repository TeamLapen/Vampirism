package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(REFERENCE.MODID)
public class ModRefinementSets {

    public static void registerRefinementSets(IForgeRegistry<IRefinementSet> registry) {
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, ModRefinements.refinement_armor).setRegistryName(REFERENCE.MODID, "armor_refinement_set"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, ModRefinements.refinement_health).setRegistryName(REFERENCE.MODID, "health_refinement_set"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, ModRefinements.refinement_speed).setRegistryName(REFERENCE.MODID, "speed_refinement_set"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, ModRefinements.refinement_speed, ModRefinements.refinement_armor, ModRefinements.refinement_health).setRegistryName(REFERENCE.MODID, "attribute_refinement_set"));
    }
}
