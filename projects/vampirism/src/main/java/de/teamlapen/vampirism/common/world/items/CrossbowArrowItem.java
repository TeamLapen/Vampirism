package de.teamlapen.vampirism.common.world.items;


import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.CrossbowArrowEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class CrossbowArrowItem extends ArrowItem implements IVampirismCrossbowArrow<CrossbowArrowEntity> {

    private final ICrossbowArrowBehavior behavior;

    public CrossbowArrowItem(ICrossbowArrowBehavior behavior, Properties properties) {
        super(properties.factions$restrictFaction(VampirismTags.Factions.IS_HUNTER));
        this.behavior = behavior;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        Component effectDescription = this.behavior.getEffectDescription();
        if (!Objects.equals(effectDescription, Component.empty())) {
            tooltipAdder.accept(CommonComponents.EMPTY);
            tooltipAdder.accept(Component.translatable("tooltip.vampirism.quarrel_effect").withStyle(ChatFormatting.GRAY));
            tooltipAdder.accept(Component.literal(" ").append(effectDescription).withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity entity, @Nullable ItemStack weapon) {
        return createArrow(level, stack, entity, entity.position().add(0, entity.getEyeHeight(), 0), weapon);
    }

    public AbstractArrow createArrow(Level level, ItemStack stack, Position position, @Nullable ItemStack weapon) {
        return createArrow(level, stack, null, position, weapon);
    }

    public AbstractArrow createArrow(Level level, ItemStack stack, @Nullable LivingEntity shooter, Position position, @Nullable ItemStack weapon) {
        CrossbowArrowEntity arrowEntity = new CrossbowArrowEntity(level, position.x(), position.y(), position.z(), stack, weapon);
        arrowEntity.setBaseDamage(this.behavior.baseDamage(level, stack, shooter) * ModConfig.balance().crossbowDamageMult.get());
        this.behavior.modifyArrow(level, stack, shooter, arrowEntity);
        if (shooter instanceof Player || shooter == null) {
            arrowEntity.pickup = this.behavior.pickupBehavior();
        } else {
            arrowEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
        }
        arrowEntity.setOwner(shooter);
        return arrowEntity;
    }

    public ICrossbowArrowBehavior getBehavior() {
        return this.behavior;
    }

    public int tintIndex() {
        return this.behavior.color();
    }

    @Override
    public boolean isCanBeInfinite() {
        return this.behavior.canBeInfinite();
    }

    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, @Nullable Entity shootingEntity) {
        this.behavior.onHitBlock(arrow, blockPos, (AbstractArrow) arrowEntity, shootingEntity, Direction.UP);
    }

    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        this.behavior.onHitBlock(arrow, blockPos, (AbstractArrow) arrowEntity, shootingEntity, direction);
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity shooter, IEntityCrossbowArrow arrowEntity, Entity shootingEntity) {
        this.behavior.onHitEntity(arrow, shooter, (AbstractArrow) arrowEntity, shootingEntity);
    }
}
