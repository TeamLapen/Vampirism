package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.util.ToolMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Basic sword for vampire hunters
 */
public abstract class HunterSwordItem extends VampirismSwordItem implements IFactionLevelItem<IHunterPlayer>, IFactionSlayerItem, IVampireFinisher, IFactionExclusiveItem {

    public HunterSwordItem(@NotNull ToolMaterial.Tiered material, int attackDamage, float attackSpeed, @NotNull Properties props) {
        super(material, attackDamage, attackSpeed, props);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addFactionToolTips(stack, context, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@NotNull ItemStack stack) {
        return null;
    }

    @Override
    public IFaction<?> getSlayedFaction() {
        return VReference.VAMPIRE_FACTION;
    }
}
