package de.teamlapen.vampirism.common.world.items.crossbow.arrow;

import de.teamlapen.vampirism.api.world.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.items.StakeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class VampireKillerBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {

    @Override
    public int color() {
        return 0xFF7A0073;
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        if (entity.level() instanceof ServerLevel level && (entity instanceof IVampireMob || (entity instanceof Player player && Helper.isVampire(player)))) {
            if (shootingEntity instanceof LivingEntity shooter && StakeItem.canKillInstantly(entity, shooter)) {
                DamageHandler.hurtModded(level, entity, s -> s.stake(shooter), 10000F);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> textComponents, TooltipFlag tooltipFlag) {
        textComponents.accept(Component.translatable("tooltip.vampirism.quarrel_vampire_killer").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canBeInfinite() {
        return ModConfig.balance().allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }
}
