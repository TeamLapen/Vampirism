package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.Helper;
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
public abstract class VampirismHunterWeapon extends VampirismItemWeapon implements IFactionLevelItem, IFactionSlayerItem, IVampireFinisher, IFactionExclusiveItem {


    public VampirismHunterWeapon(String regName, IItemTier material, int attackDamage, float attackSpeed, Properties props) {
        super(regName, material, attackDamage, attackSpeed, props);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (getUsingFaction(stack) != null || getMinLevel(stack) > 0 || getRequiredSkill(stack) != null) {
            PlayerEntity player = VampirismMod.proxy.getClientPlayer();
            TextFormatting color = player != null && player.isAlive() && Helper.canUseFactionItem(stack, this, FactionPlayerHandler.get(player)) ? TextFormatting.BLUE : TextFormatting.DARK_RED;
            IFaction f = getUsingFaction(stack);
            tooltip.add(((f == null ? new TranslationTextComponent("text.vampirism.all") : f.getNamePlural())).appendText("@" + getMinLevel(stack)).applyTextStyle(color));
            ISkill reqSkill = this.getRequiredSkill(stack);
            if (reqSkill != null) {
                tooltip.add(new TranslationTextComponent("text.vampirism.required_skill", new TranslationTextComponent(reqSkill.getTranslationKey())).applyTextStyle(color));
            }
        }
    }


    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
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
}
