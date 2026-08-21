package de.teamlapen.faction.common.world.items;

import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.faction.common.util.DescriptionUtil;
import de.teamlapen.faction.common.world.effects.OblivionMobEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
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

    public static final int MAX_PORTIONS = 24;

    public OblivionPotionItem(Properties properties) {
        super(properties);
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> void applyEffect(T factionPlayer, int portions) {
        Player player = factionPlayer.asEntity();
        ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
        if (((SkillHandler<?>) skillHandler).noSkillEnabled()) {
            return;
        }

        int amplifier = FMLEnvironment.isProduction() ? 4 : 100;
        int duration;
        if (player.isCreative()) {
            duration = Integer.MAX_VALUE;
        } else {
            if (portions <= 0) {
                return;
            }
            duration = portions * OblivionMobEffect.getTickDuration(amplifier);
        }

        player.addEffect(new MobEffectInstance(FactionEffects.OBLIVION, duration, amplifier));
        factionPlayer.sync();
    }

    public static int portionsOf(ItemStack stack) {
        Integer portions = stack.get(FactionDataComponents.OBLIVION_PORTIONS);
        return portions == null ? 0 : portions;
    }

    public static int countPortions(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            Integer portions = inventory.getItem(i).get(FactionDataComponents.OBLIVION_PORTIONS);
            if (portions != null && portions > 0) {
                total += portions;
            }
        }

        return total;
    }

    public static void consumePortions(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            consumePortion(player);
        }
    }

    public static void consumePortion(Player player) {
        int lowestId = -1;
        int lowestPortions = Integer.MAX_VALUE;

        Inventory inventory = player.getInventory();

        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack stack = inventory.getItem(i);
            Integer portions = stack.get(FactionDataComponents.OBLIVION_PORTIONS);
            if (portions != null && portions > 0 && (portions < lowestPortions || portions == lowestPortions && Inventory.isHotbarSlot(i))) {
                lowestPortions = portions;
                lowestId = i;
            }
        }

        if (lowestId != -1) {
            ItemStack potion = inventory.getItem(lowestId);
            if (lowestPortions <= 1) {
                potion.shrink(1);
            } else {
                potion.set(FactionDataComponents.OBLIVION_PORTIONS, lowestPortions - 1);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.factionapi.oblivion_potion.portion", portionsOf(stack), MAX_PORTIONS).withStyle(ChatFormatting.GRAY));
        DescriptionUtil.normalizeTextWidth(I18n.get("tooltip.factionapi.oblivion_potion.desc")).stream().map(line -> Component.literal(line).withStyle(ChatFormatting.DARK_GRAY)).forEach(tooltipComponents);
    }
}
