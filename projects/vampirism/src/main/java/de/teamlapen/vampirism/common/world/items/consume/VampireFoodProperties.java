package de.teamlapen.vampirism.common.world.items.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.items.consume.FactionConsumableListener;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.BloodStats;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

public record VampireFoodProperties(int blood, float saturation, boolean canAlwaysEat) implements FactionConsumableListener {

    public static final Codec<VampireFoodProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("blood").forGetter(VampireFoodProperties::blood),
            Codec.FLOAT.fieldOf("saturation").forGetter(VampireFoodProperties::saturation),
            Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(VampireFoodProperties::canAlwaysEat)
    ).apply(instance, VampireFoodProperties::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, VampireFoodProperties> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, VampireFoodProperties::blood,
            ByteBufCodecs.FLOAT, VampireFoodProperties::saturation,
            ByteBufCodecs.BOOL, VampireFoodProperties::canAlwaysEat,
            VampireFoodProperties::new
    );

    @Override
    public void onConsume(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable) {
        RandomSource randomsource = livingEntity.getRandom();
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, randomsource.triangle(1.0F, 0.4F));
        if (livingEntity instanceof Player player) {
            FactionPlayerHandler.get(player).factionPlayer(ModFactions.VAMPIRE).ifPresent(vampire -> {
                ((BloodStats) vampire.getBloodStats()).addBlood(blood, saturation);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, Mth.randomBetween(randomsource, 0.9F, 1.0F));
            });
        } else if (livingEntity instanceof VampireMinionEntity minion) {
            minion.eat(blood);
        }
    }

    @Override
    public boolean isCorrectFaction(Holder<? extends IFaction<?>> entityFaction) {
        return IFaction.is(ModFactions.VAMPIRE, entityFaction);
    }

    public static class Builder {

        private int blood;
        private float saturationModifier;
        private boolean canAlwaysEat;

        public Builder() {
        }

        public VampireFoodProperties.Builder blood(int blood) {
            this.blood = blood;
            return this;
        }

        public VampireFoodProperties.Builder saturationModifier(float saturationModifier) {
            this.saturationModifier = saturationModifier;
            return this;
        }

        public VampireFoodProperties.Builder alwaysEdible() {
            this.canAlwaysEat = true;
            return this;
        }

        public VampireFoodProperties build() {
            float saturation = FoodConstants.saturationByModifier(this.blood, this.saturationModifier);
            return new VampireFoodProperties(this.blood, saturation, this.canAlwaysEat);
        }
    }
}
