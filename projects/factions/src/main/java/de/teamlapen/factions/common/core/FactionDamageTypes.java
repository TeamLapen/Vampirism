package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.util.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class FactionDamageTypes {

    public static final ResourceKey<DamageType> MINION = createKey("minion");
    public static final ResourceKey<DamageType> LEAVE_FACTION = createKey("leave_faction");

    private static ResourceKey<DamageType> createKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, FResourceLocation.mod(name));
    }

    static void createDamageTypes(BootstrapContext<DamageType> context) {
        context.register(MINION, new DamageType(mod("minion"), DamageScaling.NEVER, 0.1F));
        context.register(LEAVE_FACTION, new DamageType(mod("leave_faction"), 0.0F));
    }

    private static String mod(String id) {
        return REFERENCE.MOD_ID + "." + id;
    }

}
