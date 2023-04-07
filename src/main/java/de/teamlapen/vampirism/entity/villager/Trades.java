package de.teamlapen.vampirism.entity.villager;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.BloodBottleItem;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import javax.annotation.Nullable;
import java.util.Random;

public class Trades {
    public static final VillagerTrades.ITrade[] converted_trades = new VillagerTrades.ITrade[]{new net.minecraft.entity.merchant.villager.VillagerTrades.EmeraldForItemsTrade(ModItems.HUMAN_HEART.get(), 9, 2, 2), new VillagerTrades.ItemsForEmeraldsTrade(ModItems.HUMAN_HEART.get(), 3, 9, 2), new ItemsForEmeraldsTradeWithDamage(BloodBottleItem.getStackWithDamage(9), 1, 3, 12, 2)};

    /**
     * copy of {@link VillagerTrades.ItemsForEmeraldsTrade} with damage of itemstack
     */
    public static class ItemsForEmeraldsTradeWithDamage implements VillagerTrades.ITrade {
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

        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack second = new ItemStack(result.getItem(), resultAmount);
            second.setDamageValue(result.getDamageValue());
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldAmount), second, this.maxUses, this.givenXP, this.priceMultiplier);
        }
    }

    public static class ItemsForSouls implements VillagerTrades.ITrade {
        private final int xp;
        private final Price price;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;

        public ItemsForSouls(Price priceIn, IItemProvider sellingItemIn, Price sellingIn) {
            this(priceIn, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, 2, 8);
        }

        public ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn) {
            this(priceIn, sellingItemIn, sellingIn, 2, 8);
        }

        public ItemsForSouls(Price priceIn, IItemProvider sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
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
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get(), price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    public static class ItemsForHeart implements VillagerTrades.ITrade {
        private final int xp;
        private final Price price;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;

        public ItemsForHeart(Price priceIn, IItemProvider sellingItemIn, Price sellingIn) {
            this(priceIn, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, 2, 8);
        }

        public ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn) {
            this(priceIn, sellingItemIn, sellingIn, 2, 8);
        }

        public ItemsForHeart(Price priceIn, IItemProvider sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
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
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(ModItems.HUMAN_HEART.get(), price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    public static class BloodBottleForHeart implements VillagerTrades.ITrade {
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
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack bottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), selling.getPrice(random));
            bottle.setDamageValue(damage);
            return new MerchantOffer(new ItemStack(ModItems.HUMAN_HEART.get(), price.getPrice(random)), bottle, maxUses, xp, 0.2F);
        }
    }

    public static class VampireForestTrade implements VillagerTrades.ITrade {
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;

        public VampireForestTrade(int pEmeraldCost, int pMaxUses, int pVillagerXp) {
            this.emeraldCost = pEmeraldCost;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
        }

        @Nullable
        public MerchantOffer getOffer(Entity pTrader, Random rand) {
            if(pTrader.level instanceof ServerWorld){
                return TotemHelper.getTotemNearPos((ServerWorld) pTrader.level, pTrader.blockPosition(), true).flatMap(TotemTileEntity::getVampireForestLocation).map(blockpos ->  {
                    ItemStack itemstack = FilledMapItem.create(pTrader.level, blockpos.getX(), blockpos.getZ(), (byte)3, true, true);
                    FilledMapItem.renderBiomePreviewMap((ServerWorld) pTrader.level, itemstack);
                    MapData.addTargetDecoration(itemstack, blockpos, "+", MapDecoration.Type.TARGET_POINT);
                    itemstack.setHoverName(new TranslationTextComponent("biome.vampirism.vampire_forest"));
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

        int getPrice(Random rand) {
            if (min >= max) return min;
            else return min + rand.nextInt(max - min);
        }
    }
}
