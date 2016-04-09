package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.IFactionLevelItem;
import de.teamlapen.vampirism.api.IFactionSlayerItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Basic sword for vampire hunters
 */
public abstract class VampirismHunterSword extends ItemSword implements IFactionLevelItem, IFactionSlayerItem {
    private final String regName;

    public VampirismHunterSword(String regName, ToolMaterial material) {
        super(material);
        this.regName = regName;
        this.setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(playerIn);
        TextFormatting color = handler.isInFaction(getUsingFaction()) && handler.getCurrentLevel() >= getMinLevel() ? TextFormatting.BLUE : TextFormatting.DARK_RED;
        tooltip.add(color + I18n.format(getUsingFaction().getUnlocalizedNamePlural()) + ": " + getMinLevel() + "+");
    }

    @Override
    public boolean canUse(IFactionPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public IFaction getSlayedFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public IPlayableFaction getUsingFaction() {
        return VReference.HUNTER_FACTION;
    }

    public static class SimpleHunterSword extends VampirismHunterSword {
        private final int minLevel;
        private final float damageMult;

        public SimpleHunterSword(String regName, ToolMaterial material, int minLevel, float damageMult) {
            super(regName, material);
            this.minLevel = minLevel;
            this.damageMult = damageMult;
        }

        @Override
        public float getDamageMultiplier(ItemStack stack) {
            return damageMult;
        }

        @Override
        public int getMinLevel() {
            return minLevel;
        }
    }
}
