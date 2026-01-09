package de.teamlapen.faction.common.world.items.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

public record FactionFoodProperties(int nutrition, float saturation, boolean canAlwaysEat, HolderSet<IFaction<?>> faction) implements FactionConsumableListener {

    public static final Codec<FactionFoodProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FactionFoodProperties::nutrition),
            Codec.FLOAT.fieldOf("saturation").forGetter(FactionFoodProperties::saturation),
            Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FactionFoodProperties::canAlwaysEat),
            RegistryCodecs.homogeneousList(FactionRegistries.Keys.FACTION).fieldOf("faction").forGetter(FactionFoodProperties::faction)
    ).apply(instance, FactionFoodProperties::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionFoodProperties> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FactionFoodProperties::nutrition,
            ByteBufCodecs.FLOAT, FactionFoodProperties::saturation,
            ByteBufCodecs.BOOL, FactionFoodProperties::canAlwaysEat,
            ByteBufCodecs.holderSet(FactionRegistries.Keys.FACTION), FactionFoodProperties::faction,
            FactionFoodProperties::new
    );

    @Override
    public void onConsume(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable) {
        RandomSource randomsource = livingEntity.getRandom();
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, randomsource.triangle(1.0F, 0.4F));
        if (livingEntity instanceof Player player) {
            player.getFoodData().eat(nutrition, saturation);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, Mth.randomBetween(randomsource, 0.9F, 1.0F));
        }
    }

    @Override
    public boolean isCorrectFaction(Holder<? extends IFaction<?>> entityFaction) {
        return IFaction.contains(faction, entityFaction);
    }

    @Override
    public TagKey<IFaction<?>> getTargetFaction() {
        return Faction;
    }

    public static class Builder {

        private final HolderSet<IFaction<?>> faction;
        private int nutrition;
        private float saturationModifier;
        private boolean canAlwaysEat;

        public Builder(HolderSet<IFaction<?>> faction) {
            this.faction = faction;
        }

        @SuppressWarnings("unchecked")
        public Builder(DeferredFaction<?, ?> faction) {
            this.faction = HolderSet.direct((Holder<IFaction<?>>) faction);
        }

        public FactionFoodProperties.Builder nutrition(int nutrition) {
            this.nutrition = nutrition;
            return this;
        }

        public FactionFoodProperties.Builder saturationModifier(float saturationModifier) {
            this.saturationModifier = saturationModifier;
            return this;
        }

        public FactionFoodProperties.Builder alwaysEdible() {
            this.canAlwaysEat = true;
            return this;
        }

        public FactionFoodProperties build() {
            float saturation = FoodConstants.saturationByModifier(this.nutrition, this.saturationModifier);
            return new FactionFoodProperties(this.nutrition, saturation, this.canAlwaysEat, this.faction);
        }
    }
}
