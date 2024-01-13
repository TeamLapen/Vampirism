package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> SUN_DAMAGE = createKey("sun_damage");
    public static final ResourceKey<DamageType> VAMPIRE_ON_FIRE = createKey("vampire_on_fire");
    public static final ResourceKey<DamageType> VAMPIRE_IN_FIRE = createKey("vampire_in_fire");
    public static final ResourceKey<DamageType> HOLY_WATER = createKey("holy_water");
    public static final ResourceKey<DamageType> NO_BLOOD = createKey("blood_loss");
    public static final ResourceKey<DamageType> MINION = createKey("minion");
    public static final ResourceKey<DamageType> DBNO = createKey("dbno");
    public static final ResourceKey<DamageType> MOTHER = createKey("mother");
    public static final ResourceKey<DamageType> STAKE = createKey("stake");
    public static final ResourceKey<DamageType> BLEEDING = createKey("bleeding");

    private static ResourceKey<DamageType> createKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(REFERENCE.MODID, name));
    }

    public static void createDamageTypes(BootstapContext<DamageType> context) {
        context.register(SUN_DAMAGE, new DamageType("sun", 0.1F));
        context.register(VAMPIRE_ON_FIRE, new DamageType("vampire_on_fire", 0.1F, DamageEffects.BURNING));
        context.register(VAMPIRE_IN_FIRE, new DamageType("vampire_in_fire", 0.1F, DamageEffects.BURNING));
        context.register(HOLY_WATER, new DamageType("holy_water", 0.1F));
        context.register(NO_BLOOD, new DamageType("blood_loss", 0.0F));
        context.register(MINION, new DamageType("minion", DamageScaling.NEVER, 0.1F, DamageEffects.HURT, DeathMessageType.DEFAULT));
        context.register(DBNO, new DamageType(REFERENCE.MODID + ".dbno", DamageScaling.NEVER, 0.0F, DamageEffects.HURT, DeathMessageType.DEFAULT));
        context.register(MOTHER, new DamageType(REFERENCE.MODID + ".mother", DamageScaling.NEVER, 0.0F, DamageEffects.THORNS, DeathMessageType.DEFAULT));
        context.register(STAKE, new DamageType(REFERENCE.MODID + ".stake", DamageScaling.NEVER, 0.0F, DamageEffects.HURT, DeathMessageType.DEFAULT));
        context.register(BLEEDING, new DamageType(REFERENCE.MODID + ".bleeding", 0.2F));
    }
}
