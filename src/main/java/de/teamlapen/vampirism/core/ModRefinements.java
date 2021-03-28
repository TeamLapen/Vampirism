package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.player.refinements.Refinement;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.UUID;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModRefinements {

    public static final Refinement refinement_armor = getNull();
    public static final Refinement refinement_speed = getNull();
    public static final Refinement refinement_health = getNull();
    public static final Refinement half_invulnerable = getNull();


    public static void registerRefinements(IForgeRegistry<IRefinement> registry) {
        registry.register(new Refinement(Attributes.ARMOR,UUID.fromString("fe88a321-acba-4275-af04-e0e2a13bfeb0"),2, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID,"refinement_armor"));
        registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("7181faa0-7267-4497-baab-98e57ec5d8db"),2,(uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID,"refinement_speed"));
        registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("e8275eaf-87ed-4898-b7ea-622006318d58"), 2, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID,"refinement_health"));
        registry.register(new Refinement().setRegistryName(REFERENCE.MODID, "half_invulnerable"));
    }
}
