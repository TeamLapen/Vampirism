package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.factions.common.skills.ClientSkillTreeData;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.common.items.component.AppliedOilContent;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.vampirism.common.potions.BasePotion;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handle general client side events
 */
public class ClientEventHandler {

    @SubscribeEvent
    public void onFovOffsetUpdate(@NotNull ComputeFovModifierEvent event) {
        if (ModConfig.CLIENT.disableFovChange.get() && Helper.isVampire(event.getPlayer())) {
            AttributeInstance speed = event.getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeModifier vampirespeed = speed.getModifier(LevelAttributeModifier.ID);
            if (vampirespeed == null) {
                return;
            }
            //removes speed buffs, add speed buffs without the vampire speed
            event.setNewFovModifier((float) (((double) (event.getFovModifier()) * ((vampirespeed.amount() + 1) * (double) (event.getPlayer().getAbilities().getWalkingSpeed()) + speed.getValue())) / ((vampirespeed.amount() + 1) * ((double) (event.getPlayer().getAbilities().getWalkingSpeed()) + speed.getValue()))));
        }
    }

    @SubscribeEvent
    public void onItemToolTip(@NotNull ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        Player player = event.getEntity();

        AppliedOilContent.addTooltipIfExist(player, stack, tooltip, event.getFlags());
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);

        if (BasePotion.isHunterPotion(stack, true).map(Potion::getEffects).map(effectInstances -> effectInstances.stream().map(MobEffectInstance::getEffect).anyMatch(s -> s.value().isBeneficial())).orElse(false) && (player == null || !Helper.isHunter(player))) {
            tooltip.add(Component.translatable("text.vampirism.hunter_potion.deadly").withStyle(ChatFormatting.DARK_RED));
        }

        if (stack.is(ModItemTags.NO_SPAWN)) {
            tooltip.add(Component.translatable("block.vampirism.castle_block.no_spawn").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        } else if (stack.is(ModItemTags.VAMPIRE_SPAWN)) {
            tooltip.add(Component.translatable("block.vampirism.castle_block.vampire_spawn").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    public void onWorldClosed(LevelEvent.Unload event) {
        VampirismModClient.services().bossInfoOverlay().clear();
    }

    @SubscribeEvent
    public void onJoined(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSkillTreeData.reset();
    }

    public static void onModelRegistry(ModelEvent.RegisterStandalone standalone) {
        for (var cell : ModModels.COFFIN_KEYS.cellSet()) {
            standalone.register(cell.getValue(), SimpleUnbakedStandaloneModel.blockStateModel(VResourceLocation.mod("block/coffin/coffin" + cell.getRowKey().getModelSuffix() + "_" + cell.getColumnKey().getName())));
        }
    }

}
