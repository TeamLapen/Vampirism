package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> SUN_DAMAGE = createKey("sun_damage");
    public static final ResourceKey<DamageType> VAMPIRE_ON_FIRE = createKey("vampire_on_fire");
    public static final ResourceKey<DamageType> VAMPIRE_IN_FIRE = createKey("vampire_in_fire");
    public static final ResourceKey<DamageType> HOLY_WATER = createKey("holy_water");
    public static final ResourceKey<DamageType> NO_BLOOD = createKey("blood_loss");
    public static final ResourceKey<DamageType> DBNO = createKey("dbno");
    public static final ResourceKey<DamageType> MOTHER = createKey("mother");
    public static final ResourceKey<DamageType> STAKE = createKey("stake");
    public static final ResourceKey<DamageType> BLEEDING = createKey("bleeding");
    public static final ResourceKey<DamageType> RITUAL_KNIFE = createKey("ritual_knife");

    private static ResourceKey<DamageType> createKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, VIdentifier.mod(name));
    }

    static void createDamageTypes(BootstrapContext<DamageType> context) {
        context.register(SUN_DAMAGE, new DamageType(mod("sun"), 0.1F));
        context.register(VAMPIRE_ON_FIRE, new DamageType(mod("vampire_on_fire"), 0.1F, DamageEffects.BURNING));
        context.register(VAMPIRE_IN_FIRE, new DamageType(mod("vampire_in_fire"), 0.1F, DamageEffects.BURNING));
        context.register(HOLY_WATER, new DamageType(mod("holy_water"), 0.1F));
        context.register(NO_BLOOD, new DamageType(mod("blood_loss"), 0.0F));
        context.register(DBNO, new DamageType(mod("dbno"), DamageScaling.NEVER, 0.0F));
        context.register(MOTHER, new DamageType(mod("mother"), DamageScaling.NEVER, 0.0F, DamageEffects.THORNS));
        context.register(STAKE, new DamageType(mod("stake"), DamageScaling.NEVER, 0.0F));
        context.register(BLEEDING, new DamageType(mod("bleeding"), 0.2F));
        context.register(RITUAL_KNIFE, new DamageType(mod("ritual_knife"), DamageScaling.NEVER, 0.0F));
    }
    
    private static String mod(String id) {
        return REFERENCE.MODID + "." + id;
    }
}
