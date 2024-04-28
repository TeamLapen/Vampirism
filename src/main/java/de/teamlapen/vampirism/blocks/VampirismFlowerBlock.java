package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Vampirism's flowers. To add one add it to {@link TYPE}
 */
public class VampirismFlowerBlock extends FlowerBlock {
    @SuppressWarnings("FieldCanBeLocal")
    private final @NotNull TYPE type;

    public VampirismFlowerBlock(@NotNull TYPE type) {
        super(type.effect, type.duration, Properties.of().mapColor(MapColor.PLANT).isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).instabreak().noCollission().sound(SoundType.GRASS));
        this.type = type;
    }


    public enum TYPE implements StringRepresentable {
        ORCHID("vampire_orchid", MobEffects.BLINDNESS, 7);

        private final String name;
        private final Holder<MobEffect> effect;
        private final int duration;

        TYPE(String name, Holder<MobEffect> effect, int duration) {
            this.name = name;
            this.effect = effect;
            this.duration = duration;
        }

        public @NotNull String getName() {
            return this.getSerializedName();
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }

    }
}
