package de.teamlapen.faction.common.world.items.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import de.teamlapen.faction.common.core.FactionFoodBehaviours;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;

public record FactionFoodEntry(TagKey<IFaction<?>> faction, FoodProperties foodProperties, Holder<IFactionFoodBehavior> behaviour) {

    public FactionFoodEntry(TagKey<IFaction<?>> faction, FoodProperties foodProperties) {
        this(faction, foodProperties, FactionFoodBehaviours.DEFAULT);
    }

    public static final Codec<FactionFoodEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(FactionRegistries.Keys.FACTION).fieldOf("faction").forGetter(FactionFoodEntry::faction),
            FoodProperties.DIRECT_CODEC.fieldOf("foodProperties").forGetter(FactionFoodEntry::foodProperties),
            FactionRegistries.FOOD_BEHAVIOUR.get().holderByNameCodec().fieldOf("behaviour").forGetter(FactionFoodEntry::behaviour)
    ).apply(instance, FactionFoodEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionFoodEntry> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(FactionRegistries.Keys.FACTION), FactionFoodEntry::faction,
            FoodProperties.DIRECT_STREAM_CODEC, FactionFoodEntry::foodProperties,
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.FOOD_BEHAVIOUR), FactionFoodEntry::behaviour,
            FactionFoodEntry::new
    );
}
