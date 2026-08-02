package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModEnchantmentTags;
import de.teamlapen.vampirism.common.util.ModEnchantmentHelper;
import de.teamlapen.vampirism.common.world.items.component.EnchantmentOverride;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TechCrossbowItem extends HunterCrossbowItem {

    @SafeVarargs
    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<? extends ISkill<?>>... requiredSkills) {
        super(properties.repairable(Tags.Items.INGOTS_IRON).component(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(VampirismTags.Factions.IS_HUNTER).skill(requiredSkills).build()).component(ModDataComponents.ENCHANTMENT_OVERRIDE, new EnchantmentOverride(ModEnchantmentTags.SEMI_AUTOMATIC_HUNTER_CROSSBOW_COMPATIBLE)), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public int getMaxLoadedProjectiles() {
        return 16; // If a bigger clip is ever added, scale this value
    }

    @Override
    public Collection<Item> getSelectableAmmo() {
        return QuarrelHandler.getClips();
    }

    @Override
    protected boolean usesQuarrelPouch() {
        return false;
    }

    @Override
    public boolean testProjectile(ItemStack crossbow, ItemStack projectile) {
        return projectile.has(ModDataComponents.CONTAINED_PROJECTILES.get()) && getAmmunition(crossbow).map(projectile::is).orElse(true);
    }

    @Override
    protected List<ItemStack> getLoadingProjectiles(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        ItemStackTemplate contained = projectileStack.get(ModDataComponents.CONTAINED_PROJECTILES.get());
        if (contained != null) {
            if (!shooter.hasInfiniteMaterials()) {
                projectileStack.shrink(1);
            }
            List<ItemStack> magazine = new ArrayList<>(contained.count());
            for (int i = 0; i < contained.count(); i++) {
                magazine.add(new ItemStack(contained.item(), 1, contained.components()));
            }

            return magazine;
        }

        return super.getLoadingProjectiles(crossbowStack, projectileStack, shooter);
    }

    @Override
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack weapon) {
        return ModItems.QUARREL_CLIP.get().getDefaultInstance();
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
    protected List<ItemStack> getShootingProjectiles(ServerLevel serverLevel, ItemStack crossbow, List<ItemStack> availableProjectiles, @Nullable Boolean sharedFrugality) {
        boolean frugality = sharedFrugality != null ? sharedFrugality : ModEnchantmentHelper.processFrugality(serverLevel, crossbow);
        if (frugality) {
            crossbow.set(ModDataComponents.CROSSBOW_FRUGALITY_TRIGGERED, Unit.INSTANCE);
            return List.of(availableProjectiles.getFirst());
        }

        return List.of(availableProjectiles.removeFirst());
    }

    @Override
    protected Boolean rollSharedFrugality(ServerLevel level, ItemStack crossbow) {
        return ModEnchantmentHelper.processFrugality(level, crossbow, 2); // half chance, but both crossbows skip consuming together
    }

    @Override
    public int getChargeDurationMod(ItemStack crossbow, Level level) {
        return applyChargeMultiplier(crossbow, this.chargeTime);
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
