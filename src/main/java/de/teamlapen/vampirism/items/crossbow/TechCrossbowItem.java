package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TechCrossbowItem extends VampirismCrossbowItem {

    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.TECH_WEAPONS.get();
    }

    @Nonnull
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof IArrowContainer && !((IArrowContainer) stack.getItem()).getArrows(stack).isEmpty();
    }

    @Override
    protected void onShoot(LivingEntity shooter, ItemStack crossbow) {
        super.onShoot(shooter, crossbow);
        if (shooter instanceof Player player) {
            player.getCooldowns().addCooldown(this, 10);
        }
    }

    @Override
    protected List<ItemStack> getShootingProjectiles(ItemStack crossbow, List<ItemStack> availableProjectiles) {
        return List.of(availableProjectiles.removeFirst());
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack crossbow, ItemStack repairItem) {
        return repairItem.is(Tags.Items.INGOTS_IRON);
    }

    @Override
    public int getChargeDurationMod(ItemStack crossbow, Level level) {
        return this.chargeTime;
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return false;
    }

    @Override
    public Optional<Item> getAmmunition(ItemStack crossbow) {
        return Optional.empty();
    }

}
