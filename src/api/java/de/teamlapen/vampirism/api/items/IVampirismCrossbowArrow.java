package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IVampirismCrossbowArrow<T extends AbstractArrowEntity & IEntityCrossbowArrow> extends IFactionExclusiveItem {

    /**
     * @deprecated use {@link net.minecraft.item.ArrowItem#createArrow(net.minecraft.world.World, net.minecraft.item.ItemStack, net.minecraft.entity.LivingEntity)}
     */
    @Deprecated //TODO 1.19 remove
    T createEntity(ItemStack stack, World world, PlayerEntity player, double heightOffset, double centerOffset, boolean rightHand);

    @Nonnull
    @Override
    default IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    boolean isCanBeInfinite();

    void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);

    void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);
}
