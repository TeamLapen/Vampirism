package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class DoubleCrossbowItem extends VampirismCrossbowItem {

    public DoubleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack -> stack.getItem() instanceof IVampirismCrossbowArrow<?>);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.DOUBLE_CROSSBOW.get();
    }

    @Override
    public void releaseUsing(@Nonnull ItemStack p_77615_1_, @Nonnull Level p_77615_2_, @Nonnull LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_); // get mod power
        if (f >= 1.0F && !isCharged(p_77615_1_)) {
            boolean first = tryLoadProjectilesMod(p_77615_3_, p_77615_1_);
            boolean second = tryLoadProjectilesMod(p_77615_3_, p_77615_1_);
            if (first || second) { //load two projectiles or only one
                SoundSource soundcategory = p_77615_3_ instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
                p_77615_2_.playSound(null, p_77615_3_.getX(), p_77615_3_.getY(), p_77615_3_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (p_77615_2_.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
            }
        }
    }

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float speed, float angle, @org.jetbrains.annotations.Nullable LivingEntity p_331602_) {
        if (!level.isClientSide()) {
            if (shooter instanceof Player player && net.neoforged.neoforge.event.EventHooks.onArrowLoose(crossbow, shooter.level(), player, 1, true) < 0) return;
            ChargedProjectiles chargedprojectiles = crossbow.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            if (!chargedprojectiles.isEmpty()) {
                List<ItemStack> arrows = chargedprojectiles.getItems().subList(0, 2);
                this.shoot(level, shooter, hand, crossbow, arrows, speed, angle, shooter instanceof Player, p_331602_);
                if (shooter instanceof ServerPlayer serverplayer) {
                    CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, crossbow);
                    serverplayer.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
                }
                crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(chargedprojectiles.getItems().stream().filter(s -> !arrows.contains(s)).toList()));
            }
        }
    }
}
