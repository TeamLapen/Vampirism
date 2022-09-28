package de.teamlapen.vampirism.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;


public class DBNODamageSource extends DamageSource {

    @Nonnull
    private final Component originalSource;
    public DBNODamageSource(@Nonnull Component originalSource) {
        super("vampirism_dbno");
        this.originalSource = originalSource;
    }

    @Nonnull
    @Override
    public Component getLocalizedDeathMessage(@Nonnull LivingEntity entityLivingBaseIn) {
        String s = "death.attack.vampirism_dbno";
        return Component.translatable(s, this.originalSource);
    }
}
