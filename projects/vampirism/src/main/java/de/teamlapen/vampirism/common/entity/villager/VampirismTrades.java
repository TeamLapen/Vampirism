package de.teamlapen.vampirism.common.entity.villager;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.factions.common.util.MapUtil;
import de.teamlapen.vampirism.common.core.ModBiomes;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.common.items.component.BottleBlood;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class VampirismTrades {
    // TODO: Maybe just leave these only to specific professions like a butcher?
    public static VillagerTrades.ItemListing[] getConvertedTrades() {
        return new VillagerTrades.ItemListing[]{
                new VillagerTrades.EmeraldForItems(ModItems.HUMAN_HEART.get(), 8, 2, 2),
                new VillagerTrades.ItemsForEmeralds(ModItems.HUMAN_HEART.get(), 3, 8, 2),
                new BloodBottleForEmeralds(1, 1, 16, 2)
        };
    }

    public static class ItemsForCurrency implements VillagerTrades.ItemListing {
        private final Price price;
        private final Item currency;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;
        private final int xp;

        public ItemsForCurrency(Price priceIn, Item currency, @NotNull ItemLike sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            this(priceIn, currency, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, new Price(numberOfItems, numberOfItems), maxUsesIn, xpIn);
        }

        public ItemsForCurrency(Price priceIn, Item currency, @NotNull ItemLike sellingItemIn, Price sellingIn, int maxUsesIn, int xpIn) {
            this(priceIn, currency, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, maxUsesIn, xpIn);
        }

        public ItemsForCurrency(Price priceIn, Item currency, ItemStack[] sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            this(priceIn, currency, sellingItemIn, new Price(numberOfItems, numberOfItems), maxUsesIn, xpIn);
        }

        public ItemsForCurrency(Price priceIn, Item currency, ItemStack[] sellingItemIn, Price sellingIn, int maxUsesIn, int xpIn) {
            this.price = priceIn;
            this.currency = currency;
            this.sellingItem = sellingItemIn;
            this.selling = sellingIn;
            this.maxUses = maxUsesIn;
            this.xp = xpIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            int cost = price.getPrice(random);
            return new MerchantOffer(new ItemCost(currency, Math.min(cost, 64)), cost > 64 ? Optional.of(new ItemCost(currency, cost - 64)) : Optional.empty(), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    public static class MultipleItemsForCurrency implements VillagerTrades.ItemListing {
        private final ItemsForCurrency[] offers;

        public MultipleItemsForCurrency(ItemsForCurrency[] offers) {
            this.offers = offers;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            return offers[random.nextInt(0, offers.length)].getOffer(trader, random);
        }
    }

    public static class ConditionalTrade implements VillagerTrades.ItemListing {
        private final VillagerTrades.ItemListing offer;
        private final Predicate<Entity> condition;

        public ConditionalTrade(VillagerTrades.ItemListing offer, Predicate<Entity> condition) {
            this.offer = offer;
            this.condition = condition;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            return condition.test(trader) ? offer.getOffer(trader, random) : null;
        }
    }

    public static class ItemsForEmeraldPrice extends ItemsForCurrency {
        public ItemsForEmeraldPrice(Price priceIn, @NotNull ItemLike sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            super(priceIn, Items.EMERALD, sellingItemIn, numberOfItems, maxUsesIn, xpIn);
        }
    }

    public static class ItemsForHeart extends ItemsForCurrency {
        public ItemsForHeart(Price priceIn, @NotNull ItemLike sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.HUMAN_HEART.get(), sellingItemIn, numberOfItems, maxUsesIn, xpIn);
        }

        public ItemsForHeart(Price priceIn, @NotNull ItemLike sellingItemIn, Price sellingIn, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.HUMAN_HEART.get(), sellingItemIn, sellingIn, maxUsesIn, xpIn);
        }

        public ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.HUMAN_HEART.get(), sellingItemIn, numberOfItems, maxUsesIn, xpIn);
        }

        public ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.HUMAN_HEART.get(), sellingItemIn, sellingIn, maxUsesIn, xpIn);
        }
    }

    public static class ItemsForSouls extends ItemsForCurrency {
        public ItemsForSouls(Price priceIn, @NotNull ItemLike sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.SOUL_ORB_VAMPIRE.get(), sellingItemIn, numberOfItems, maxUsesIn, xpIn);
        }

        public ItemsForSouls(Price priceIn, @NotNull ItemLike sellingItemIn, Price sellingIn, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.SOUL_ORB_VAMPIRE.get(), sellingItemIn, sellingIn, maxUsesIn, xpIn);
        }

        public ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, int numberOfItems, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.SOUL_ORB_VAMPIRE.get(), sellingItemIn, numberOfItems, maxUsesIn, xpIn);
        }

        public ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn, int maxUsesIn, int xpIn) {
            super(priceIn, ModItems.SOUL_ORB_VAMPIRE.get(), sellingItemIn, sellingIn, maxUsesIn, xpIn);
        }
    }

    public static class BloodBottleForEmeralds implements VillagerTrades.ItemListing {
        private final int emeraldAmount;
        private final int resultAmount;
        private final int maxUses;
        private final int givenXP;
        private final float priceMultiplier;

        public BloodBottleForEmeralds(int emeraldAmount, int resultAmount, int maxUses, int givenXP) {
            this(emeraldAmount, resultAmount, maxUses, givenXP, 0.05F);
        }

        public BloodBottleForEmeralds(int emeraldAmount, int resultAmount, int maxUses, int givenXP, float priceMultiplier) {
            this.emeraldAmount = emeraldAmount;
            this.resultAmount = resultAmount;
            this.maxUses = maxUses;
            this.givenXP = givenXP;
            this.priceMultiplier = priceMultiplier;
        }

        @NotNull
        @Override
        public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
            ItemStack bottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), resultAmount);
            bottle.set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(9));
            return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldAmount), bottle, this.maxUses, this.givenXP, this.priceMultiplier);
        }
    }

    public static class BloodBottleForHeart implements VillagerTrades.ItemListing {
        private final int xp;
        private final Price price;
        private final Price selling;
        private final int blood;
        private final int maxUses;

        public BloodBottleForHeart(Price priceIn, Price sellingIn, int damageIn) {
            this(priceIn, sellingIn, damageIn, 8, 2);
        }

        public BloodBottleForHeart(Price priceIn, Price sellingIn, int damageIn, int maxUsesIn, int xpIn) {
            this.price = priceIn;
            this.selling = sellingIn;
            this.blood = damageIn;
            this.maxUses = maxUsesIn;
            this.xp = xpIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
            ItemStack bottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), selling.getPrice(random));
            bottle.set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(blood));
            return new MerchantOffer(new ItemCost(ModItems.HUMAN_HEART.get(), price.getPrice(random)), bottle, maxUses, xp, 0.1F);
        }
    }

    /**
     * Provides a trade selling a map to the nearest vampire forest for emeralds.
     * Only works for {@link ConvertedVillagerEntity}, otherwise it will create a null offer
     */
    public static class VampireForestMapTrade implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final String displayName;
        private final Holder<MapDecorationType> decorationType;
        private final int maxUses;
        private final int villagerXp;

        public VampireForestMapTrade(int emeraldCost, String displayName, Holder<MapDecorationType> decorationType, int maxUses, int villagerXp) {
            this.emeraldCost = emeraldCost;
            this.displayName = displayName;
            this.decorationType = decorationType;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
        }

        @Nullable
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            if (trader instanceof ConvertedVillagerEntity convertedVillager && trader.level() instanceof ServerLevel serverLevel) {
                //This may block for a short amount of time if the vampire villager has not completed its forest search yet
                return convertedVillager.getClosestVampireForest(trader.level(), trader.blockPosition()).map(blockPos -> {
                    ItemStack itemstack = MapItem.create(serverLevel, blockPos.getX(), blockPos.getZ(), (byte) 3, true, true);
                    MapItem.renderBiomePreviewMap(serverLevel, itemstack);
                    MapItemSavedData.addTargetDecoration(itemstack, blockPos, "+", decorationType);
                    itemstack.set(DataComponents.ITEM_NAME, Component.translatable(this.displayName));
                    return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), itemstack, this.maxUses, this.villagerXp, 0.2F);
                }).orElse(null);
            }
            return null;
        }
    }

    public static class BiomeMapForEmeralds implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final ResourceKey<Biome> biome;
        private final String displayName;
        private final Holder<MapDecorationType> decorationType;
        private final int maxUses;
        private final int villagerXp;

        public BiomeMapForEmeralds(int emeraldCost, ResourceKey<Biome> biome, String displayName, Holder<MapDecorationType> decorationType, int maxUses, int villagerXp) {
            this.emeraldCost = emeraldCost;
            this.biome = biome;
            this.displayName = displayName;
            this.decorationType = decorationType;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
        }

        public BiomeMapForEmeralds(int emeraldCost, String displayName, Holder<MapDecorationType> decorationType, int maxUses, int villagerXp) {
            this(emeraldCost, ModBiomes.VAMPIRE_FOREST, displayName, decorationType, maxUses, villagerXp);
        }

        @Nullable
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            if (trader.level() instanceof ServerLevel serverLevel) {
                BlockPos targetPos = locateBiome(serverLevel, trader.blockPosition(), biome);
                if (targetPos != null) {
                    ItemStack itemStack = MapItem.create(serverLevel, targetPos.getX(), targetPos.getZ(), (byte)2, true, true);
                    MapItem.renderBiomePreviewMap(serverLevel, itemStack);
                    MapItemSavedData.addTargetDecoration(itemStack, targetPos, "+", decorationType);
                    itemStack.set(DataComponents.ITEM_NAME, Component.translatable(displayName));
                    return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), itemStack, this.maxUses, this.villagerXp, 0.2F);
                }
            }
            return null;
        }

        private static @Nullable BlockPos locateBiome(ServerLevel level, BlockPos locatorPos, ResourceKey<Biome> locatedBiome) {
            Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
            Pair<BlockPos, Holder<Biome>> pair = level.findClosestBiome3d(biome -> biome.is(locatedBiome), locatorPos, 6400, 32, 64);
            stopwatch.stop();
            return pair != null ? pair.getFirst() : null;
        }
    }

    public static class TreasureMapForEmeralds implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final TagKey<Structure> destination;
        private final String displayName;
        private final Holder<MapDecorationType> decorationType;
        private final int maxUses;
        private final int villagerXp;

        public TreasureMapForEmeralds(int emeraldCost, TagKey<Structure> destination, String displayName, Holder<MapDecorationType> decorationType, int maxUses, int villagerXp) {
            this.emeraldCost = emeraldCost;
            this.destination = destination;
            this.displayName = displayName;
            this.decorationType = decorationType;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            ItemStack itemStack = MapUtil.getMap(trader, destination, displayName, decorationType, 100);
            if (itemStack != null) {
                return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), itemStack, this.maxUses, this.villagerXp, 0.2F);
            }

            return null;
        }
    }

    public static class Price {
        private final int min;
        private final int max;

        public Price(int minIn, int maxIn) {
            this.max = maxIn;
            this.min = minIn;
        }

        int getPrice(@NotNull RandomSource rand) {
            if (min >= max) {
                return min;
            } else {
                return min + rand.nextInt(max - min);
            }
        }
    }
}
