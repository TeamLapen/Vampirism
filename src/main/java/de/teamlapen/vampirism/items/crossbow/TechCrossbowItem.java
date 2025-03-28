package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.tags.ModEnchantmentTags;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.QuarrelPouch;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import de.teamlapen.vampirism.util.ModEnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TechCrossbowItem extends HunterCrossbowItem {

    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>> requiredSkill) {
        super(properties.repairable(Tags.Items.INGOTS_IRON).component(ModDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(ModFactionTags.IS_HUNTER).skill(requiredSkill).build()), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public boolean testProjectile(ItemStack crossbow, ItemStack projectile) {
        if (projectile.getItem() instanceof IArrowContainer container) {
            return !container.getArrows(projectile).isEmpty();
        }
        return false;
    }

    @Override
    protected void onShoot(LivingEntity shooter, ItemStack crossbow) {
        super.onShoot(shooter, crossbow);
        if (shooter instanceof Player player) {
            boolean faster = HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.CROSSBOW_TECHNIQUE);
            player.getCooldowns().addCooldown(crossbow, faster ? 5 : 10); // add cooldown if projectiles left
        }
    }

    @Override
    protected List<ItemStack> getShootingProjectiles(ServerLevel serverLevel, ItemStack crossbow, List<ItemStack> availableProjectiles) {
        if (ModEnchantmentHelper.processFrugality(serverLevel, crossbow)) {
            crossbow.set(ModDataComponents.CROSSBOW_FRUGALITY_TRIGGERED, Unit.INSTANCE);
        } else {
            return List.of(availableProjectiles.removeFirst());
        }
        return List.of(availableProjectiles.getFirst());
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

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return doubleCrossbow ? 4.5f : 2f;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(ModEnchantmentTags.SEMI_AUTOMATIC_HUNTER_CROSSBOW_COMPATIBLE);
    }
}
