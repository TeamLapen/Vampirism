package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModVillages;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;


public class EntityVampireFactionVillager extends EntityVampireFactionVillagerBase implements IVampire {

    private final static VillagerEntity.ITradeList[][] TRADES = {
            {
                    new ItemsForHeart(new PriceInfo(10, 15), new ItemStack(ModItems.pure_blood_0, 1), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(25, 35), new ItemStack(ModItems.pure_blood_1, 1), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(30, 40), new ItemStack(ModItems.pure_blood_2, 1), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(1, 5), ModItems.item_coffin, new PriceInfo(1, 1))
            }, {
            new ItemsForHeart(new PriceInfo(3, 12), bloodBottle(1, 9), new PriceInfo(1, 15)),
            new ItemsForHeart(new PriceInfo(30, 40), new ItemStack(ModItems.pure_blood_4, 1), new PriceInfo(1, 1)),
            new ItemsForHeart(new PriceInfo(20, 30), new ItemStack(ModItems.pure_blood_3, 1), new PriceInfo(1, 1))
            }, {
            new ItemsForHeart(new PriceInfo(10, 30), new ItemStack[]{
                    new ItemStack(ModItems.vampire_cloak_black_blue, 1),
                    new ItemStack(ModItems.vampire_cloak_black_red, 1),
                    new ItemStack(ModItems.vampire_cloak_black_white, 1),
                    new ItemStack(ModItems.vampire_cloak_red_black, 1),
                    new ItemStack(ModItems.vampire_cloak_white_black, 1)}, new PriceInfo(1, 1))
            }
    };

    public EntityVampireFactionVillager(EntityType<? extends EntityVampireFactionVillager> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(DifficultyInstance difficulty, @Nullable ILivingEntityData livingdata, @Nullable CompoundNBT itemNbt) {
        this.setProfession(ModVillages.profession_vampire_expert);
        return this.finalizeMobSpawn(difficulty, livingdata, itemNbt, false);
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        if (player.isCreative() || Helper.isVampire(player)) {
            return super.processInteract(player, hand);
        } else {
            if (player.world.isRemote) {
                player.sendStatusMessage(new TranslationTextComponent(Helper.isHunter(player) ? "text.vampirism.vampire_villager.decline_trade_vampire" : "text.vampirism.vampire_villager.decline_trade_normal"), false);
            }
            return true;
        }
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        super.useRecipe(recipe);
        PlayerEntity player = getCustomer();
        if (player != null) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.vampire_villager.trade_successful"), false);
        }
    }

    @Nonnull
    @Override
    protected ITradeList[] getTrades(int level) {
        if (level >= 2) {
            return ArrayUtils.addAll(TRADES[1], TRADES[2]);
        }
        return TRADES[level];//Must not be >2
    }

    @Deprecated
    private static ItemStack bloodBottle(int count, int damage) {
        ItemStack bottle = new ItemStack(ModItems.blood_bottle, count);
        bottle.setDamage(damage);
        return bottle;
    }

    private static class ItemsForHeart implements ITradeList {
        private ItemStack[] sellingStacks;
        private PriceInfo buying;
        private PriceInfo selling;

        ItemsForHeart(PriceInfo price, Item sell, PriceInfo amount) {
            this(price, new ItemStack[] { new ItemStack(sell) }, amount);
        }

        ItemsForHeart(PriceInfo price, ItemStack sell, PriceInfo amount) {
            this(price, new ItemStack[] { sell }, amount);

        }

        ItemsForHeart(PriceInfo price, ItemStack[] sellingStacks, PriceInfo amount) {
            this.sellingStacks = sellingStacks;
            this.buying = price;
            this.selling = amount;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int price = this.buying.getPrice(random);
            int count = this.selling.getPrice(random);
            ItemStack selling = sellingStacks[random.nextInt(sellingStacks.length)].copy();
            selling.setCount(count);
            ItemStack first = new ItemStack(ModItems.human_heart, price);
            ItemStack second = ItemStack.EMPTY;
            if (price > 64) {
                second = new ItemStack(ModItems.human_heart, price - 64);
                first.setCount(price - second.getCount());
            }
            recipeList.add(new MerchantRecipe(first, second, selling));
        }
    }
}
