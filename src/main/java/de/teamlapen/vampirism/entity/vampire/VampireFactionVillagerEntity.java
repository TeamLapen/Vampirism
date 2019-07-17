package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class VampireFactionVillagerEntity extends VampireFactionVillagerBaseEntity implements IVampire {

    private final static VillagerTrades.ITrade[][] TRADES = {
            {
                    new ItemsForHeart(new Price(10, 15), ModItems.pure_blood_0, new Price(1, 1)),
                    new ItemsForHeart(new Price(25, 30), ModItems.pure_blood_1, new Price(1, 1)),
                    new ItemsForHeart(new Price(30, 40), ModItems.pure_blood_2, new Price(1, 1)),
                    new ItemsForHeart(new Price(1, 5), ModItems.item_coffin, new Price(1, 1))
            }, {
            new BloodBottleForHeart(new Price(3, 12), new Price(1, 15), 9),
            new ItemsForHeart(new Price(30, 40), ModItems.pure_blood_4, new Price(1, 1)),
            new ItemsForHeart(new Price(20, 30), ModItems.pure_blood_3, new Price(1, 1))
            }, {
            new ItemsForHeart(new Price(10, 30), new ItemStack[]{
                    new ItemStack(ModItems.vampire_cloak_black_blue),
                    new ItemStack(ModItems.vampire_cloak_black_red),
                    new ItemStack(ModItems.vampire_cloak_black_white),
                    new ItemStack(ModItems.vampire_cloak_red_black),
                    new ItemStack(ModItems.vampire_cloak_white_black)}, new Price(1, 1))
            }
    };

    public VampireFactionVillagerEntity(EntityType<? extends VampireFactionVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVillagerData(this.getVillagerData().withProfession(ModEntities.vampire_expert));
        return spawnDataIn;
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
    protected void populateTradeData() {
        super.populateTradeData();
        VillagerData villagerData = this.getVillagerData();
        VillagerTrades.ITrade[] trades = TRADES[villagerData.getLevel()];
        if (trades != null) {
            MerchantOffers merchantOffers = this.getOffers();
            this.addTrades(merchantOffers, trades, 2);
        }
    }
}
