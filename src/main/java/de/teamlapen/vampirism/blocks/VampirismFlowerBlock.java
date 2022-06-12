package de.teamlapen.vampirism.blocks;

import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

/**
 * Vampirism's flowers. To add one add it to {@link TYPE}
 */
public class VampirismFlowerBlock extends FlowerBlock {
    private final TYPE type;

    public VampirismFlowerBlock(TYPE type) {
        super(type.effect, type.duration, Properties.of(Material.PLANT).instabreak().noCollission().sound(SoundType.GRASS));
        this.type = type;
    }


    public enum TYPE implements IStringSerializable {
        ORCHID("vampire_orchid", Effects.BLINDNESS, 7);

        private final String name;
        private final Effect effect;
        private final int duration;

        TYPE(String name, Effect effect, int duration) {
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
