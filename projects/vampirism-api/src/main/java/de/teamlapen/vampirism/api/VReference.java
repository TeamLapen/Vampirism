package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Holds constants (or at init set variables)
 */
public class VReference {
    public static final String MODID = "vampirism";
    public static final Identifier VAMPIRE_FACTION_ID = VResourceLocation.mod("vampire");
    public static final Identifier HUNTER_FACTION_ID = VResourceLocation.mod("hunter");
    /**
     * One blood in the players blood stats represents this amount of mB fluid blood
     */
    public static final int FOOD_TO_FLUID_BLOOD = 100;

    public static final Supplier<Fluid> BLOOD = DeferredHolder.create(Registries.FLUID, VResourceLocation.mod("blood"));

    public static final Identifier PERMANENT_INVISIBLE_MOB_EFFECT = VResourceLocation.mod("permanent");
    public static final Identifier VAMPIRE_NIGHT_VISION_EFFECT = VResourceLocation.mod("night_vision");

}
