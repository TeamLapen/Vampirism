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
        //TODO think of cool registry names
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xff00ff,ModRefinements.refinement_armor).setRegistryName(REFERENCE.MODID, "armor_refinement_set"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON,0xffff00, ModRefinements.half_invulnerable).setRegistryName(REFERENCE.MODID, "vampire_half_invulnerability"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x00ffff,ModRefinements.refinement_health).setRegistryName(REFERENCE.MODID, "health_refinement_set"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON,0x00ff00, ModRefinements.refinement_speed).setRegistryName(REFERENCE.MODID, "speed_refinement_set"));
        registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x0000ff,ModRefinements.refinement_speed, ModRefinements.refinement_armor, ModRefinements.refinement_health).setRegistryName(REFERENCE.MODID, "attribute_refinement_set"));
    }
}
