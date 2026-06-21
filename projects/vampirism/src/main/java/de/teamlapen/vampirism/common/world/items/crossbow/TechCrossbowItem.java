package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TechCrossbowItem extends HunterCrossbowItem {

    public static final int MAGAZINE_SIZE = 16;

    @SafeVarargs
    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>>... requiredSkills) {
        super(properties.repairable(Tags.Items.INGOTS_IRON).component(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(VampirismTags.Factions.IS_HUNTER).skill(requiredSkills).build()), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return false;
    }

    @Override
    protected boolean usesQuarrelPouch() {
        return false;
    }

    @Override
    public boolean testProjectile(ItemStack crossbow, ItemStack projectile) {
        return projectile.is(ModItems.QUARREL_CLIP);
    }

    @Override
    protected List<ItemStack> getLoadingProjectiles(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.is(ModItems.QUARREL_CLIP)) {
            if (!shooter.hasInfiniteMaterials()) {
                projectileStack.shrink(1);
            }
            List<ItemStack> magazine = new ArrayList<>(MAGAZINE_SIZE);
            for (int i = 0; i < MAGAZINE_SIZE; i++) {
                magazine.add(ModItems.QUARREL_NORMAL.get().getDefaultInstance());
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
