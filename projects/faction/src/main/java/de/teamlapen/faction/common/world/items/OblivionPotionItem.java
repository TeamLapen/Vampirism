package de.teamlapen.faction.common.world.items;

import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public class OblivionPotionItem extends Item {

    public OblivionPotionItem(Properties properties) {
        super(properties);
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> void applyEffect(T factionPlayer) {
        Player player = factionPlayer.asEntity();
        ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
        if (((SkillHandler<?>) skillHandler).noSkillEnabled()) {
            return;
        }
        player.addEffect(new MobEffectInstance(FactionEffects.OBLIVION, Integer.MAX_VALUE, FMLEnvironment.isProduction() ? 4 : 100));
        factionPlayer.sync();
    }

    public static int countCharges(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            Integer charges = inventory.getItem(i).get(FactionDataComponents.OBLIVION_CHARGES);
            if (charges != null && charges > 0) {
                total += charges;
            }
        }

        return total;
    }

    public static void consumeCharges(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            consumeCharge(player);
        }
    }

    public static void consumeCharge(Player player) {
        int lowestId = -1;
        int lowestCharges = Integer.MAX_VALUE;

        Inventory inventory = player.getInventory();

        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack stack = inventory.getItem(i);
            Integer charges = stack.get(FactionDataComponents.OBLIVION_CHARGES);
            if (charges != null && charges > 0 && (charges < lowestCharges || charges == lowestCharges && Inventory.isHotbarSlot(i))) {
                lowestCharges = charges;
                lowestId = i;
            }
        }

        if (lowestId != -1) {
            ItemStack potion = inventory.getItem(lowestId);
            if (lowestCharges <= 1) {
                potion.shrink(1);
            } else {
                potion.set(FactionDataComponents.OBLIVION_CHARGES, lowestCharges - 1);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.factionapi.oblivion_potion.desc").withStyle(ChatFormatting.GRAY));
    }
}
