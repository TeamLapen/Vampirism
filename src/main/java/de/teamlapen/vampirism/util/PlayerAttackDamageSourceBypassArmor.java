package de.teamlapen.vampirism.util;

import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class PlayerAttackDamageSourceBypassArmor extends DamageSource {

    public PlayerAttackDamageSourceBypassArmor(Holder<DamageType> damageType, @Nullable Entity entity) {
        super(damageType, entity);
    }

    @Override
    public boolean is(TagKey<DamageType> key) {
        if (key == DamageTypeTags.BYPASSES_ARMOR) {
            return true;
        }
        return super.is(key);
    }
}
