package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
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
        super(type.effect, type.duration, Properties.create(Material.PLANTS).zeroHardnessAndResistance().doesNotBlockMovement().sound(SoundType.PLANT));
        this.type = type;
        this.setRegistryName(REFERENCE.MODID, type.getName());
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

        @Nonnull
        @Override
        public String getName() {
            return name;
        }

    }
}
