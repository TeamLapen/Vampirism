package de.teamlapen.vampirism.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;


public class DBNODamageSource extends DamageSource {

    @Nonnull
    private final Component originalSource;
    public DBNODamageSource(@Nonnull Component originalSource) {
        super("vampirism_dbno");
        this.originalSource = originalSource;
        this.bypassArmor();
        this.bypassMagic();
    }

    @Nonnull
    @Override
    public Component getLocalizedDeathMessage(@Nonnull LivingEntity entityLivingBaseIn) {
        String s = "death.attack.vampirism_dbno";
        return new TranslatableComponent(s, this.originalSource);
    }
}
