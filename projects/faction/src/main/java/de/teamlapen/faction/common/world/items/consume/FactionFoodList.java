package de.teamlapen.faction.common.world.items.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import de.teamlapen.faction.common.core.FactionFoodBehaviours;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;

import java.util.List;

public record FactionFoodList(FoodProperties defaultFood, List<FactionFoodEntry> foodEntries) implements ConsumableListener {

    public FactionFoodList(FoodProperties defaultFood, FactionFoodEntry foodEntry) {
        this(defaultFood, List.of(foodEntry));
    }

    public FactionFoodList(FoodProperties defaultFood, FactionFoodEntry... foodEntries) {
        this(defaultFood, List.of(foodEntries));
    }

    public static final Codec<FactionFoodList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FoodProperties.DIRECT_CODEC.fieldOf("defaultFood").forGetter(FactionFoodList::defaultFood),
            FactionFoodEntry.CODEC.listOf().fieldOf("entries").forGetter(FactionFoodList::foodEntries)
    ).apply(instance, FactionFoodList::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionFoodList> STREAM_CODEC = StreamCodec.composite(
            FoodProperties.DIRECT_STREAM_CODEC, FactionFoodList::defaultFood,
            FactionFoodEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), FactionFoodList::foodEntries,
            FactionFoodList::new
    );

    @Override
    public void onConsume(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable) {
        List<FactionFoodEntry> matching = findMatchingEntries(livingEntity);
        if (!matching.isEmpty()) {
            matching.forEach(entry -> {
                IFactionFoodBehavior behavior = FactionRegistries.FOOD_BEHAVIOUR.get().getValue(entry.behaviour());
                if (behavior == null) behavior = FactionFoodBehaviours.DEFAULT.get();

                behavior.apply(level, livingEntity, itemStack, consumable, entry.foodProperties());
            });
        } else {
            FactionFoodBehaviours.DEFAULT.get().apply(level, livingEntity, itemStack, consumable, defaultFood);
        }
    }

    public List<FactionFoodEntry> findMatchingEntries(LivingEntity entity) {
        var entityFaction = IFactionHelper.get().getFaction(entity);
        return foodEntries.stream().filter(entry -> IFaction.is(entityFaction, entry.faction())).toList();
    }
}
