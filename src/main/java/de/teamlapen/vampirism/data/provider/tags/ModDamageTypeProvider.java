package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModDamageTypes;
import de.teamlapen.vampirism.core.tags.ModDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeProvider extends TagsProvider<DamageType> {

    public ModDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, provider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.SUN_DAMAGE, ModDamageTypes.NO_BLOOD, ModDamageTypes.VAMPIRE_ON_FIRE, ModDamageTypes.DBNO, ModDamageTypes.MOTHER, ModDamageTypes.STAKE);
        this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(ModDamageTypes.DBNO, ModDamageTypes.STAKE);
        this.tag(DamageTypeTags.IS_FIRE).add(ModDamageTypes.VAMPIRE_ON_FIRE, ModDamageTypes.VAMPIRE_IN_FIRE);
        this.tag(DamageTypeTags.WITCH_RESISTANT_TO).add(ModDamageTypes.SUN_DAMAGE, ModDamageTypes.VAMPIRE_ON_FIRE, ModDamageTypes.VAMPIRE_IN_FIRE, ModDamageTypes.NO_BLOOD, ModDamageTypes.HOLY_WATER);
        this.tag(ModDamageTypeTags.ENTITY_PHYSICAL).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO, DamageTypes.MOB_PROJECTILE, DamageTypes.ARROW, DamageTypes.STING, DamageTypes.THORNS);
        this.tag(ModDamageTypeTags.REMAINS_INVULNERABLE).add(DamageTypes.IN_WALL, DamageTypes.DROWN);
        this.tag(ModDamageTypeTags.MOTHER_RESISTANT_TO).add(DamageTypes.ON_FIRE, DamageTypes.IN_FIRE, ModDamageTypes.HOLY_WATER, DamageTypes.FREEZE, DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC);
        this.tag(ModDamageTypeTags.VAMPIRE_IMMORTAL).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK, DamageTypes.DROWN, DamageTypes.ON_FIRE, DamageTypes.CRAMMING, DamageTypes.FALL, DamageTypes.FLY_INTO_WALL, DamageTypes.MAGIC, DamageTypes.MAGIC, DamageTypes.WITHER, DamageTypes.FALLING_ANVIL, DamageTypes.FALLING_BLOCK, DamageTypes.DRAGON_BREATH, DamageTypes.SWEET_BERRY_BUSH, DamageTypes.TRIDENT, DamageTypes.ARROW, DamageTypes.FIREWORKS, DamageTypes.FIREBALL, DamageTypes.WITHER_SKULL, DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION, DamageTypes.THROWN, DamageTypes.INDIRECT_MAGIC, ModDamageTypes.VAMPIRE_ON_FIRE, DamageTypes.STING, DamageTypes.FALLING_STALACTITE, DamageTypes.STALAGMITE, DamageTypes.FREEZE)
                .addOptional(VResourceLocation.loc("mekanism", "radiation"));
    }
}
