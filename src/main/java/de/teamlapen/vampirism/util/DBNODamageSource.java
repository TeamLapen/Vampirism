package de.teamlapen.vampirism.util;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DBNODamageSource extends DamageSource {

    @Nullable
    private final Component originalSource;

    public DBNODamageSource(Holder<DamageType> damageType, @Nullable Component originalSource) {
        super(damageType);
        this.originalSource = originalSource;
    }

    @NotNull
    @Override
    public Component getLocalizedDeathMessage(@NotNull LivingEntity entityLivingBaseIn) {
        if (this.originalSource == null) {
            return Component.translatable("death.attack.vampirism_dbno.missing");
        } else {
            return Component.translatable("death.attack.vampirism_dbno", this.originalSource);
        }
    }
}
