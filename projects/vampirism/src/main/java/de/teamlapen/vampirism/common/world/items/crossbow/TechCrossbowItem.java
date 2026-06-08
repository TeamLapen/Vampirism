package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.tags.ModEnchantmentTags;
import de.teamlapen.vampirism.common.util.ModEnchantmentHelper;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
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

import java.util.ArrayList;
import java.util.List;

public class TechCrossbowItem extends HunterCrossbowItem {

    private static final int MAGAZINE_SIZE = 12;

    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>> requiredSkill) {
        super(properties.repairable(Tags.Items.INGOTS_IRON).component(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(VampirismTags.Factions.IS_HUNTER).skill(requiredSkill).build()), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public boolean testProjectile(ItemStack crossbow, ItemStack projectile) {
        if (projectile.getItem() instanceof QuarrelPouchItem) {
            QuarrelPouchContents contents = projectile.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
            ItemStack quarrel = getAmmunition(crossbow).map(contents::getSpecific).filter(q -> !q.isEmpty()).orElseGet(contents::getFirst);
            return !quarrel.isEmpty() && quarrel.getItem() instanceof IVampirismQuarrel<?>;
        }
        return false;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
    }

    @Override
    protected List<ItemStack> getLoadingProjectiles(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.getItem() instanceof QuarrelPouchItem) {
            if (shooter.hasInfiniteMaterials()) {
                projectileStack = projectileStack.copy();
            }
            QuarrelPouchContents contents = projectileStack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
            if (contents.isEmpty()) {
                return List.of();
            }
            Item selected = getAmmunition(crossbowStack).filter(item -> !contents.getSpecific(item).isEmpty()).orElse(null);
            QuarrelPouchContents.Mutable mutable = contents.asMutable();
            List<ItemStack> magazine = new ArrayList<>(MAGAZINE_SIZE);
            for (int i = 0; i < MAGAZINE_SIZE; i++) {
                ItemStack quarrel = selected != null ? mutable.getSpecific(selected) : mutable.getFirst();
                if (quarrel.isEmpty()) {
                    break;
                }
                magazine.add(quarrel);
            }
            projectileStack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, mutable.toImmutable());
            return magazine;
        }
        return super.getLoadingProjectiles(crossbowStack, projectileStack, shooter);
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
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return doubleCrossbow ? 4.5f : 2f;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(ModEnchantmentTags.SEMI_AUTOMATIC_HUNTER_CROSSBOW_COMPATIBLE);
    }
}
