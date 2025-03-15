package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

/**
 * Vampirism's flowers
 */
public class VampirismFlowerBlock extends FlowerBlock {

    private final boolean poisoningBees;

    public VampirismFlowerBlock(Properties properties, Holder<MobEffect> effect, float duration, boolean poisoningBees) {
        super(effect, duration, properties.mapColor(MapColor.PLANT).isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).instabreak().noCollission().sound(SoundType.GRASS));
        this.poisoningBees = poisoningBees;
    }

    @Nullable
    @Override
    public MobEffectInstance getBeeInteractionEffect() {
        return poisoningBees ? new MobEffectInstance(MobEffects.POISON, 30) : super.getBeeInteractionEffect();
    }
}
