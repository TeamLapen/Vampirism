package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModVillages;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;


public class EntityVampireFactionVillager extends EntityVampireFactionVillagerBase implements IVampire {

    //TODO create realistic trades
    private final static EntityVillager.ITradeList[][] TRADES = {
            {
                    new ItemsForHeart(new PriceInfo(10, 15), new ItemStack(ModItems.pure_blood, 1, 0), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(25, 35), new ItemStack(ModItems.pure_blood, 1, 2), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(30, 40), ModItems.item_coffin, new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(20, 30), new ItemStack(ModItems.pure_blood, 1, 1), new PriceInfo(1, 1))
            }, {
                    new ItemsForHeart(new PriceInfo(10, 15), new ItemStack(ModItems.blood_bottle, 3, 4), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(25, 35), new ItemStack(ModItems.pure_blood, 1, 3), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(30, 40), new ItemStack(ModItems.pure_blood, 1, 4), new PriceInfo(1, 1)),
                    new ItemsForHeart(new PriceInfo(20, 30), new ItemStack(ModItems.blood_bottle, 3, 9), new PriceInfo(1, 1))
            }, {
                    new ItemsForHeart(new PriceInfo(100, 200), ModItems.vampire_cloak, new PriceInfo(1, 1))
            }
    };

    private static ItemStack[] createTiers(IItemWithTier.TIER tier, IItemWithTier... items) {
        ItemStack[] stacks = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            stacks[i] = items[i].setTier(new ItemStack((Item) items[i]), tier);
        }
        return stacks;
    }

    public EntityVampireFactionVillager(World worldIn) {
        super(worldIn);
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.setProfession(ModVillages.profession_vampire_expert);
        return this.finalizeMobSpawn(difficulty, livingdata, false);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.isCreative() || Helper.isVampire(player)) {
            return super.processInteract(player, hand);
        } else {
            if (player.world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation(Helper.isHunter(player) ? "text.vampirism.vampire_villager.decline_trade_vampire" : "text.vampirism.vampire_villager.decline_trade_normal"), false);
            }
            return true;
        }
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        super.useRecipe(recipe);
        EntityPlayer player = getCustomer();
        if (player != null) {
            player.sendStatusMessage(new TextComponentTranslation("text.vampirism.vampire_villager.trade_successful"), false);
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
