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
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

/**
 * Basic sword for vampire hunters
 */
public abstract class VampirismHunterWeapon extends VampirismItemWeapon implements IFactionLevelItem<IHunterPlayer>, IFactionSlayerItem, IVampireFinisher, IFactionExclusiveItem {


    public VampirismHunterWeapon(@NotNull Tier material, int attackDamage, float attackSpeed, @NotNull Properties props) {
        super(material, attackDamage, attackSpeed, props);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (getExclusiveFaction(stack) != null || getMinLevel(stack) > 0 || getRequiredSkill(stack) != null) {
            Player player = VampirismMod.proxy.getClientPlayer();
            addFactionLevelToolTip(stack, worldIn, tooltip, flagIn, player);
        }
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
