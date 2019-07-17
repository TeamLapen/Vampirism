package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;


/**
 * We need to duplicate some functionality of EntityVillager for several reasons here.
 * a) The profession registry looks quite prone to mod compatibility issues. So we can't just create our own faciton
 * b) The trading parts are all private
 * c) We want some changed behaviour
 */
public abstract class FactionVillagerEntity extends VampirismVillagerEntity implements IFactionEntity {

    private int tradingLevel = 0;

    public FactionVillagerEntity(EntityType<? extends FactionVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public FactionVillagerEntity(EntityType<? extends FactionVillagerEntity> type, World worldIn, IVillagerType villagerType) {
        super(type, worldIn, villagerType);
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
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    protected void populateTradeData() {
    }
}
