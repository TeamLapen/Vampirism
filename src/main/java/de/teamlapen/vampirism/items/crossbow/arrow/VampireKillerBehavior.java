package de.teamlapen.vampirism.items.crossbow.arrow;

import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VampireKillerBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior, IVampireFinisher {
    @Override
    public int color() {
        return 0x7A0073;
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        if (entity instanceof IVampireMob || (entity instanceof Player player && Helper.isVampire(player))) {
            if(shootingEntity instanceof LivingEntity shooter && StakeItem.canKillInstant(entity, shooter)) {
                DamageHandler.hurtModded(entity, s -> s.stake(shooter), 10000F);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {
        textComponents.add(Component.translatable("item.vampirism.crossbow_arrow_vampire_killer.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canBeInfinite() {
        return VampirismConfig.BALANCE.allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }

    @Override
    public @NotNull Item asItem() {
        return ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get();
    }
}
