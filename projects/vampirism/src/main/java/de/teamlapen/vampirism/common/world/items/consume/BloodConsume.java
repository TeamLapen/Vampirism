package de.teamlapen.vampirism.common.world.items.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.DrinkBloodContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public record BloodConsume(int blood, float saturation, boolean useRemaining) implements ConsumeEffect {

    public static final MapCodec<BloodConsume> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("blood").forGetter(BloodConsume::blood),
            Codec.FLOAT.fieldOf("saturation").forGetter(BloodConsume::saturation),
            Codec.BOOL.fieldOf("useRemaining").forGetter(BloodConsume::useRemaining)
    ).apply(inst, BloodConsume::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BloodConsume> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BloodConsume::blood,
            ByteBufCodecs.FLOAT, BloodConsume::saturation,
            ByteBufCodecs.BOOL, BloodConsume::useRemaining,
            BloodConsume::new
    );

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return ModItems.CONSUME_BLOOD_EFFECT.get();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        IVampire vampire = null;
        if (entity instanceof Player player) {
            vampire = VampirePlayer.get(player);
        } else if (entity instanceof IVampire vampireEntity) {
            vampire = vampireEntity;
        }
        if (vampire != null) {
            vampire.drinkBlood(blood, saturation, useRemaining, new DrinkBloodContext(stack));
        }
        return false;
    }
}
