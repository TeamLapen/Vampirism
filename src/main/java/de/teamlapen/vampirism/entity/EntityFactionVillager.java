package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;


/**
 * We need to duplicate some functionality of EntityVillager for several reasons here.
 * a) The profession registry looks quite prone to mod compatibility issues. So we can't just create our own faciton
 * b) The trading parts are all private
 * c) We want some changed behaviour
 */
public abstract class EntityFactionVillager extends EntityVillagerVampirism implements IFactionEntity {

    private int tradingLevel = 0;

    public EntityFactionVillager(World worldIn) {
        super(worldIn);
    }

    public EntityFactionVillager(World worldIn, int professionId) {
        super(worldIn, professionId);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        ITextComponent name = super.getDisplayName();
        name.getStyle().setColor(getFaction().getChatColor());
        return name;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Nonnull
    protected ITradeList[] getTrades(int level) {
        return new ITradeList[0];
    }

    @Override
    protected final void populateBuyingList() {
        this.tradingLevel++;

        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }
        ITradeList[] trades = this.getTrades(tradingLevel - 1);
        for (EntityVillager.ITradeList trade : trades) {
            trade.addMerchantRecipe(this, this.buyingList, this.rand);
        }

    }
}
