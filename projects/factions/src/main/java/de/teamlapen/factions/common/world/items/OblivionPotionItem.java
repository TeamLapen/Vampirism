package de.teamlapen.factions.common.world.items;

import de.teamlapen.factions.api.factions.skills.ISkillHandler;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.core.FactionEffects;
import de.teamlapen.factions.common.factions.skills.SkillHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
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

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("item.factions.oblivion_potion.description").withStyle(ChatFormatting.GRAY));
    }
}
