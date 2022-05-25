package de.teamlapen.vampirism.blocks;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;

/**
 * Vampirism's flowers. To add one add it to {@link TYPE}
 */
public class VampirismFlowerBlock extends FlowerBlock {
    @SuppressWarnings("FieldCanBeLocal")
    private final TYPE type;

    public VampirismFlowerBlock(TYPE type) {
        super(type.effect, type.duration, Properties.of(Material.PLANT).instabreak().noCollission().sound(SoundType.GRASS));
        this.type = type;
    }


    public enum TYPE implements StringRepresentable {
        ORCHID("vampire_orchid", MobEffects.BLINDNESS, 7);

        private final String name;
        private final MobEffect effect;
        private final int duration;

        TYPE(String name, MobEffect effect, int duration) {
            this.name = name;
            this.effect = effect;
            this.duration = duration;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }

    }
}
