package de.teamlapen.vampirism.common.blocks;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import org.jetbrains.annotations.Nullable;

/**
 * Vampirism's flowers
 */
public class VampireOrchidBlock extends FlowerBlock {

    private final boolean poisoningBees;

    public VampireOrchidBlock(Properties properties, Holder<MobEffect> effect, float duration, boolean poisoningBees) {
        super(effect, duration, properties);
        this.poisoningBees = poisoningBees;
    }

    @Nullable
    @Override
    public MobEffectInstance getBeeInteractionEffect() {
        return poisoningBees ? new MobEffectInstance(MobEffects.POISON, 30) : super.getBeeInteractionEffect();
    }
}
