package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModVillages;
import de.teamlapen.vampirism.entity.EntityFactionVillager;
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


public class EntityHunterFactionVillager extends EntityFactionVillager implements IHunter {

    private final static EntityVillager.ITradeList[][] TRADES = {
            {
                    new ItemsForSouls(new PriceInfo(10, 15), createTiers(IItemWithTier.TIER.NORMAL, ModItems.hunter_coat_feet, ModItems.obsidian_armor_feet, ModItems.armor_of_swiftness_feet), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(25, 35), createTiers(IItemWithTier.TIER.NORMAL, ModItems.hunter_coat_legs, ModItems.obsidian_armor_legs, ModItems.armor_of_swiftness_legs), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(30, 40), createTiers(IItemWithTier.TIER.NORMAL, ModItems.hunter_coat_chest, ModItems.obsidian_armor_chest, ModItems.armor_of_swiftness_chest), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(20, 30), createTiers(IItemWithTier.TIER.NORMAL, ModItems.hunter_coat_head, ModItems.obsidian_armor_head, ModItems.armor_of_swiftness_head), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(10, 20), ModItems.item_garlic, new PriceInfo(2, 5)),
                    new ItemsForSouls(new PriceInfo(50, 100), Items.DIAMOND, new PriceInfo(1, 1))
            },
            {
                    new ItemsForSouls(new PriceInfo(10, 15), createTiers(IItemWithTier.TIER.ENHANCED, ModItems.hunter_coat_feet, ModItems.obsidian_armor_feet, ModItems.armor_of_swiftness_feet), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(25, 35), createTiers(IItemWithTier.TIER.ENHANCED, ModItems.hunter_coat_legs, ModItems.obsidian_armor_legs, ModItems.armor_of_swiftness_legs), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(30, 40), createTiers(IItemWithTier.TIER.ENHANCED, ModItems.hunter_coat_chest, ModItems.obsidian_armor_chest, ModItems.armor_of_swiftness_chest), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(20, 30), createTiers(IItemWithTier.TIER.ENHANCED, ModItems.hunter_coat_head, ModItems.obsidian_armor_head, ModItems.armor_of_swiftness_head), new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(40, 90), Items.DIAMOND, new PriceInfo(1, 2))
            },
            {
                    new ItemsForSouls(new PriceInfo(100, 200), createTiers(IItemWithTier.TIER.ULTIMATE, ModItems.hunter_coat_feet, ModItems.obsidian_armor_feet, ModItems.armor_of_swiftness_feet, ModItems.hunter_coat_legs, ModItems.obsidian_armor_legs, ModItems.armor_of_swiftness_legs, ModItems.hunter_coat_chest, ModItems.obsidian_armor_chest, ModItems.armor_of_swiftness_chest, ModItems.hunter_coat_head, ModItems.obsidian_armor_head, ModItems.armor_of_swiftness_head), new PriceInfo(1, 1))
            }
    };

    private static ItemStack[] createTiers(IItemWithTier.TIER tier, IItemWithTier... items) {
        ItemStack[] stacks = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            stacks[i] = items[i].setTier(new ItemStack((Item) items[i]), tier);
        }
        return stacks;
    }

    public EntityHunterFactionVillager(World worldIn) {
        super(worldIn);
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.setProfession(ModVillages.profession_hunter_expert);
        return this.finalizeMobSpawn(difficulty, livingdata, false);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.isCreative() || Helper.isHunter(player)) {
            return super.processInteract(player, hand);
        } else {
            if (player.world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation(Helper.isVampire(player) ? "text.vampirism.hunter_villager.decline_trade_vampire" : "text.vampirism.hunter_villager.decline_trade_normal"), false);
            }
            return true;
        }
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        super.useRecipe(recipe);
        EntityPlayer player = getCustomer();
        if (player != null) {
            player.sendStatusMessage(new TextComponentTranslation("text.vampirism.hunter_villager.trade_successful"), false);
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

    private static class ItemsForSouls implements ITradeList {
        private ItemStack[] sellingStacks;
        private PriceInfo buying;
        private PriceInfo selling;

        ItemsForSouls(PriceInfo price, Item sell, PriceInfo amount) {
            this(price, new ItemStack[]{new ItemStack(sell)}, amount);
        }

        ItemsForSouls(PriceInfo price, ItemStack sell, PriceInfo amount) {
            this(price, new ItemStack[]{sell}, amount);

        }

        ItemsForSouls(PriceInfo price, ItemStack[] sellingStacks, PriceInfo amount) {
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
            ItemStack first = new ItemStack(ModItems.soul_orb_vampire, price);
            ItemStack second = ItemStack.EMPTY;
            if (price > 64) {
                second = new ItemStack(ModItems.soul_orb_vampire, price - 64);
                first.setCount(price - second.getCount());
            }
            recipeList.add(new MerchantRecipe(first, second, selling));
        }
    }
}
