package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
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

    public VampirismHunterWeapon(String regName, ToolMaterial material) {
        super(regName, material);
    }

    public VampirismHunterWeapon(String regName, ToolMaterial material, float attackSpeedMod) {
        super(regName, material, attackSpeedMod);
    }

    public VampirismHunterWeapon(String regName, ToolMaterial material, float attackSpeedMod, float attackDamage) {
        super(regName, material, attackSpeedMod, attackDamage);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (getUsingFaction(stack) != null || getMinLevel(stack) > 0 || getRequiredSkill(stack) != null) {
            TextFormatting color = Minecraft.getInstance().player != null && Helper.canUseFactionItem(stack, this, FactionPlayerHandler.get(Minecraft.getInstance().player)) ? TextFormatting.BLUE : TextFormatting.DARK_RED;
            tooltip.add(color + UtilLib.translateFormatted(getUsingFaction(stack) == null ? "text.vampirism.all" : getUsingFaction(stack).getUnlocalizedNamePlural()) + ": " + getMinLevel(stack) + "+");
            ISkill reqSkill = this.getRequiredSkill(stack);
            if (reqSkill != null) {
                tooltip.add(color + UtilLib.translateFormatted("text.vampirism.required_skill", UtilLib.translate(reqSkill.getUnlocalizedName())));
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

        public SimpleHunterSword(String regName, ToolMaterial material, int minLevel, float damageMult) {
            super(regName, material);
            this.minLevel = minLevel;
            this.damageMult = damageMult;
        }

        @Override
        public float getDamageMultiplierForFaction(@Nonnull ItemStack stack) {
            return damageMult;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, IBlockState state) {
            Block block = state.getBlock();

            if (block == Blocks.WEB) {
                return 15.0F;
            } else {
                Material material = state.getMaterial();
                return material != Material.PLANTS && material != Material.VINE && material != Material.CORAL && material != Material.LEAVES && material != Material.GOURD ? 1.0F : 1.5F;
            }
        }

        @Override
        public int getMinLevel(@Nonnull ItemStack stack) {
            return minLevel;
        }
    }
}
