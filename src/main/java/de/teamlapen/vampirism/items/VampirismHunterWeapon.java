package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Basic sword for vampire hunters
 */
public abstract class VampirismHunterWeapon extends VampirismItemWeapon implements IFactionLevelItem, IFactionSlayerItem, IVampireFinisher {

    public VampirismHunterWeapon(String regName, IItemTier material, Properties props) {
        super(regName, material, props);
    }

    public VampirismHunterWeapon(String regName, IItemTier material, float attackSpeedMod, Properties props) {
        super(regName, material, attackSpeedMod, props);
    }

    public VampirismHunterWeapon(String regName, IItemTier material, int attackDamage, float attackSpeedMod, Properties props) {
        super(regName, material, attackDamage, attackSpeedMod, props);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (getUsingFaction(stack) != null || getMinLevel(stack) > 0 || getRequiredSkill(stack) != null) {
            PlayerEntity player = VampirismMod.proxy.getClientPlayer();
            TextFormatting color = player != null && Helper.canUseFactionItem(stack, this, FactionPlayerHandler.get(player)) ? TextFormatting.BLUE : TextFormatting.DARK_RED;
            IFaction f = getUsingFaction(stack);
            tooltip.add(((f == null ? new TranslationTextComponent("text.vampirism.all") : f.getNamePlural())).appendText(":" + getMinLevel(stack)).applyTextStyle(color));
            ISkill reqSkill = this.getRequiredSkill(stack);
            if (reqSkill != null) {
                tooltip.add(new TranslationTextComponent("text.vampirism.required_skill", new TranslationTextComponent(reqSkill.getTranslationKey())).applyTextStyle(color));
            }
        }
    }


    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return null;
    }

    @Override
    public IFaction getSlayedFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public IPlayableFaction getUsingFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    public static class SimpleHunterSword extends VampirismHunterWeapon {
        private final int minLevel;
        private final float damageMult;

        public SimpleHunterSword(String regName, IItemTier material, int minLevel, float damageMult, Properties prop) {
            super(regName, material, prop);
            this.minLevel = minLevel;
            this.damageMult = damageMult;
        }

        @Override
        public float getDamageMultiplierForFaction(@Nonnull ItemStack stack) {
            return damageMult;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
            Block block = state.getBlock();

            if (block == Blocks.COBWEB) {
                return 15.0F;
            } else {
                Material material = state.getMaterial();
                return material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.CORAL && material != Material.LEAVES && material != Material.GOURD ? 1.0F : 1.5F;
            }
        }

        @Override
        public int getMinLevel(@Nonnull ItemStack stack) {
            return minLevel;
        }
    }
}
