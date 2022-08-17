package de.teamlapen.vampirism.entity.villager;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Trades {
    public static final VillagerTrades.ItemListing[] converted_trades = new VillagerTrades.ItemListing[]{new net.minecraft.world.entity.npc.VillagerTrades.EmeraldForItems(ModItems.HUMAN_HEART.get(), 9, 2, 2), new VillagerTrades.ItemsForEmeralds(ModItems.HUMAN_HEART.get(), 3, 9, 2), new ItemsForEmeraldsTradeWithDamage(BloodBottleItem.getStackWithDamage(9), 1, 3, 12, 2)};

    /**
     * copy of {@link VillagerTrades.ItemsForEmeralds} with damage to {@link ItemStack}
     */
    public static class ItemsForEmeraldsTradeWithDamage implements VillagerTrades.ItemListing {
        private final ItemStack result;
        private final int emeraldAmount;
        private final int resultAmount;
        private final int maxUses;
        private final int givenXP;
        private final float priceMultiplier;

        public ItemsForEmeraldsTradeWithDamage(Item result, int emeraldAmount, int resultAmount, int givenXP) {
            this(new ItemStack(result), emeraldAmount, resultAmount, 12, givenXP);
        }

        public ItemsForEmeraldsTradeWithDamage(Item result, int emeraldAmount, int resultAmount, int maxUses, int givenXP) {
            this(new ItemStack(result), emeraldAmount, resultAmount, maxUses, givenXP);
        }

        public ItemsForEmeraldsTradeWithDamage(ItemStack result, int emeraldAmount, int resultAmount, int maxUses, int givenXP) {
            this(result, emeraldAmount, resultAmount, maxUses, givenXP, 0.05F);
        }

        public ItemsForEmeraldsTradeWithDamage(ItemStack result, int emeraldAmount, int resultAmount, int maxUses, int givenXP, float priceMultiplier) {
            this.result = result;
            this.emeraldAmount = emeraldAmount;
            this.resultAmount = resultAmount;
            this.maxUses = maxUses;
            this.givenXP = givenXP;
            this.priceMultiplier = priceMultiplier;
        }

        @NotNull
        @Override
        public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
            ItemStack second = new ItemStack(result.getItem(), resultAmount);
            second.setDamageValue(result.getDamageValue());
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldAmount), second, this.maxUses, this.givenXP, this.priceMultiplier);
        }
    }

    public static class ItemsForSouls implements VillagerTrades.ItemListing {
        private final int xp;
        private final Price price;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;

        public ItemsForSouls(Price priceIn, @NotNull ItemLike sellingItemIn, Price sellingIn) {
            this(priceIn, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, 2, 8);
        }

        public ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn) {
            this(priceIn, sellingItemIn, sellingIn, 2, 8);
        }

        public ItemsForSouls(Price priceIn, @NotNull ItemLike sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = new ItemStack[]{new ItemStack(sellingItemIn.asItem())};
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        public ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = sellingItemIn;
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
            return new MerchantOffer(new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get(), price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    public static class ItemsForHeart implements VillagerTrades.ItemListing {
        private final int xp;
        private final Price price;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;

        public ItemsForHeart(Price priceIn, @NotNull ItemLike sellingItemIn, Price sellingIn) {
            this(priceIn, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, 2, 8);
        }

        public ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn) {
            this(priceIn, sellingItemIn, sellingIn, 2, 8);
        }

        public ItemsForHeart(Price priceIn, @NotNull ItemLike sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = new ItemStack[]{new ItemStack(sellingItemIn.asItem())};
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        public ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = sellingItemIn;
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
            return new MerchantOffer(new ItemStack(ModItems.HUMAN_HEART.get(), price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    public static class BloodBottleForHeart implements VillagerTrades.ItemListing {
        private final int xp;
        private final Price price;
        private final Price selling;
        private final int damage;
        private final int maxUses;

        public BloodBottleForHeart(Price priceIn, Price sellingIn, int damageIn) {
            this(priceIn, sellingIn, damageIn, 2, 8);
        }

        BloodBottleForHeart(Price priceIn, Price sellingIn, int damageIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.selling = sellingIn;
            this.damage = damageIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
            ItemStack bottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), selling.getPrice(random));
            bottle.setDamageValue(damage);
            return new MerchantOffer(new ItemStack(ModItems.HUMAN_HEART.get(), price.getPrice(random)), bottle, maxUses, xp, 0.2F);
        }
    }

    public static class BiomeMapForEmeralds implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final ResourceKey<Biome> destination;
        private final int maxUses;
        private final int villagerXp;

        public BiomeMapForEmeralds(int pEmeraldCost, ResourceKey<Biome> pDestination, int pMaxUses, int pVillagerXp) {
            this.emeraldCost = pEmeraldCost;
            this.destination = pDestination;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
        }

        @Nullable
        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRand) {
            if (!(pTrader.level instanceof ServerLevel serverlevel)) {
                return null;
            } else {
                Biome biome = serverlevel.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(destination).orElse(null);
                if(biome==null){
                    LogManager.getLogger().warn("Cannot find destination biome in registry{}",destination);
                    return null;
                }
                Pair<BlockPos, Holder<Biome>> blockpos = serverlevel.findClosestBiome3d(b -> b.is(destination), pTrader.blockPosition(), 2400, 8, 16); //TOD check
                if (blockpos != null) {
                    ItemStack itemstack = MapItem.create(serverlevel, blockpos.getFirst().getX(), blockpos.getFirst().getZ(), (byte)3, true, true);
                    MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                    MapItemSavedData.addTargetDecoration(itemstack, blockpos.getFirst(), "+", MapDecoration.Type.TARGET_POINT);
                    itemstack.setHoverName(Component.translatable("biome."+destination.location().getNamespace()+ "."+destination.location().getPath()));
                    return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
                } else {
                    return null;
                }
            }
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
            if (min >= max) return min;
            else return min + rand.nextInt(max - min);
        }
    }
}
