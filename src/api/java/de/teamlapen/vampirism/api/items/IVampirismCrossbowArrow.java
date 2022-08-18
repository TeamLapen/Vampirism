package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IVampirismCrossbowArrow<T extends AbstractArrow & IEntityCrossbowArrow> extends IFactionExclusiveItem {

    T createEntity(ItemStack stack, Level world, Player player, double heightOffset, double centerOffset, boolean rightHand);

    @Nullable
    @Override
    default IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    boolean isCanBeInfinite();

    void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, @Nullable Entity shootingEntity);

    void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);
}
