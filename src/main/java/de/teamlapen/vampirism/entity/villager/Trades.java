package de.teamlapen.vampirism.entity.villager;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Trades {
    public static final VillagerTrades.ItemListing[] converted_trades = new VillagerTrades.ItemListing[]{new net.minecraft.world.entity.npc.VillagerTrades.EmeraldForItems(ModItems.HUMAN_HEART.get(), 9, 2, 2), new VillagerTrades.ItemsForEmeralds(ModItems.HUMAN_HEART.get(), 3, 9, 2), new ItemsForEmeraldsTradeWithDamage(BloodBottleItem.getStackWithDamage(9), 1, 1, 20, 2)};

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

    /**
     * Provides a trade selling a map to the nearest vampire forest for emeralds.
     * Only works for {@link ConvertedVillagerEntity}, otherwise it will create a null offer
     */
    public static class VampireForestMapTrade implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;

        public VampireForestMapTrade(int pEmeraldCost, int pMaxUses, int pVillagerXp) {
            this.emeraldCost = pEmeraldCost;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
        }

        @Nullable
        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRand) {
            if (pTrader instanceof ConvertedVillagerEntity convertedVillager && pTrader.level instanceof ServerLevel serverLevel){
                //This may block for a short amount of time if the vampire villager has not completed its forest search yet
                return convertedVillager.getClosestVampireForest(pTrader.level, pTrader.blockPosition()).map(blockPos -> {
                    ItemStack itemstack = MapItem.create(pTrader.level, blockPos.getX(), blockPos.getZ(), (byte) 3, true, true);
                    MapItem.renderBiomePreviewMap(serverLevel, itemstack);
                    MapItemSavedData.addTargetDecoration(itemstack, blockPos, "+", MapDecoration.Type.TARGET_POINT);
                    itemstack.setHoverName(Component.translatable("biome.vampirism.vampire_forest"));
                    return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
                }).orElse(null);
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
