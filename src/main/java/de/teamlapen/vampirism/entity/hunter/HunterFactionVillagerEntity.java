package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.FactionVillagerEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;


public class HunterFactionVillagerEntity extends FactionVillagerEntity implements IHunter {

    private final static VillagerTrades.ITrade[][] TRADES = {
            {
                    new ItemsForSouls(new Price(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_normal), new ItemStack(ModItems.obsidian_armor_feet_normal), new ItemStack(ModItems.armor_of_swiftness_feet_normal)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_normal), new ItemStack(ModItems.obsidian_armor_legs_normal), new ItemStack(ModItems.armor_of_swiftness_legs_normal)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_normal), new ItemStack(ModItems.obsidian_armor_chest_normal), new ItemStack(ModItems.armor_of_swiftness_chest_normal)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_normal), new ItemStack(ModItems.obsidian_armor_head_normal), new ItemStack(ModItems.armor_of_swiftness_head_normal)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(10, 20), ModItems.item_garlic, new Price(2, 5)),
                    new ItemsForSouls(new Price(50, 100), Items.DIAMOND, new Price(1, 1))
            },
            {
                    new ItemsForSouls(new Price(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_enhanced), new ItemStack(ModItems.obsidian_armor_feet_enhanced), new ItemStack(ModItems.armor_of_swiftness_feet_enhanced)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_enhanced), new ItemStack(ModItems.obsidian_armor_legs_enhanced), new ItemStack(ModItems.armor_of_swiftness_legs_enhanced)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_enhanced), new ItemStack(ModItems.obsidian_armor_chest_enhanced), new ItemStack(ModItems.armor_of_swiftness_chest_enhanced)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_enhanced), new ItemStack(ModItems.obsidian_armor_head_enhanced), new ItemStack(ModItems.armor_of_swiftness_head_enhanced)}, new Price(1, 1)),
                    new ItemsForSouls(new Price(40, 90), Items.DIAMOND, new Price(1, 2))
            },
            {
                    new ItemsForSouls(new Price(100, 200), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_ultimate), new ItemStack(ModItems.obsidian_armor_feet_ultimate), new ItemStack(ModItems.armor_of_swiftness_feet_ultimate), new ItemStack(ModItems.hunter_coat_legs_ultimate), new ItemStack(ModItems.obsidian_armor_legs_ultimate), new ItemStack(ModItems.armor_of_swiftness_legs_ultimate), new ItemStack(ModItems.hunter_coat_chest_ultimate), new ItemStack(ModItems.obsidian_armor_chest_ultimate), new ItemStack(ModItems.armor_of_swiftness_chest_ultimate), new ItemStack(ModItems.hunter_coat_head_ultimate), new ItemStack(ModItems.obsidian_armor_head_ultimate), new ItemStack(ModItems.armor_of_swiftness_head_ultimate)}, new Price(1, 1))
            }
    };

    public HunterFactionVillagerEntity(EntityType<? extends HunterFactionVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVillagerData(this.getVillagerData().withProfession(ModEntities.hunter_expert));
        return spawnDataIn;
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        if (player.isCreative() || Helper.isHunter(player)) {
            return super.processInteract(player, hand);
        } else {
            if (player.world.isRemote) {
                player.sendStatusMessage(new TranslationTextComponent(Helper.isVampire(player) ? "text.vampirism.hunter_villager.decline_trade_vampire" : "text.vampirism.hunter_villager.decline_trade_normal"), false);
            }
            return true;
        }
    }

    @Override
    protected void populateTradeData() {
        super.populateTradeData();
        VillagerData villagerData = this.getVillagerData();
        VillagerTrades.ITrade[] trades = TRADES[villagerData.getLevel()];
        if (trades != null) {
            MerchantOffers merchantOffers = this.getOffers();
            this.func_213717_a(merchantOffers, trades, 2);//AbstractVillager#229
        }
    }

    private static class ItemsForSouls implements VillagerTrades.ITrade {
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
            return new MerchantOffer(new ItemStack(ModItems.soul_orb_vampire, price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }
}
