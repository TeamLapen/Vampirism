package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.player.refinements.Refinement;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModRefinements {

    public static final Refinement refinement_armor = getNull();
    public static final Refinement refinement_speed = getNull();
    public static final Refinement refinement_health = getNull();
    public static final Refinement half_invulnerable = getNull();


    public static void registerRefinements(IForgeRegistry<IRefinement> registry) {
        registry.register(new Refinement(Attributes.ARMOR, (uuid) -> new AttributeModifier(uuid, "refinement_armor", 2, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID,"refinement_armor"));
        registry.register(new Refinement(Attributes.MOVEMENT_SPEED, (uuid) -> new AttributeModifier(uuid, "refinement_speed", 2, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID,"refinement_speed"));
        registry.register(new Refinement(Attributes.MAX_HEALTH, (uuid) -> new AttributeModifier(uuid, "refinement_health", 2, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID,"refinement_health"));
        registry.register(new Refinement().setRegistryName(REFERENCE.MODID, "half_invulnerable"));
    }
}
