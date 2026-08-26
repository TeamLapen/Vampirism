package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import de.teamlapen.vampirism.common.world.potions.BasePotion;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handle general client side events
 */
public class ClientEventHandler {

    @SubscribeEvent
    public void onFovOffsetUpdate(@NotNull ComputeFovModifierEvent event) {
        if (!ModConfig.client().correctVampireFOV.get() && Helper.isVampire(event.getPlayer())) {
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

        AppliedOilContent.addTooltipIfExist(player, stack, event.getContext(), tooltip, event.getFlags());

        ItemStackTemplate containedProjectiles = stack.get(ModDataComponents.CONTAINED_PROJECTILES.get());
        if (containedProjectiles != null) {
            tooltip.add(Component.translatable("tooltip.vampirism.contained_projectiles", containedProjectiles.count(), containedProjectiles.create().getHoverName()).withStyle(ChatFormatting.GRAY));
        }

        if (BasePotion.isHunterPotion(stack, true).map(Potion::getEffects).map(effectInstances -> effectInstances.stream().map(MobEffectInstance::getEffect).anyMatch(s -> s.value().isBeneficial())).orElse(false) && (player == null || !Helper.isHunter(player))) {
            tooltip.add(Component.translatable("tooltip.vampirism.deadly_hunter_potion").withStyle(ChatFormatting.DARK_RED));
        }

        if (stack.is(ModItemTags.NO_SPAWN)) {
            tooltip.add(Component.translatable("tooltip.vampirism.no_entity_spawn").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        } else if (stack.is(ModItemTags.VAMPIRE_SPAWN)) {
            tooltip.add(Component.translatable("tooltip.vampirism.only_vampire_spawn").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }

    public static void onModelRegistry(ModelEvent.RegisterStandalone standalone) {
        ModModels.COFFIN_BOTTOM_KEYS.forEach((color, key) -> {
            standalone.register(key, SimpleUnbakedStandaloneModel.simpleModelWrapper(VIdentifier.mod("block/coffin_bottom_" + color.getName())));
        });
        standalone.register(ModModels.COFFIN_TOP_KEY, SimpleUnbakedStandaloneModel.simpleModelWrapper(VIdentifier.mod("block/coffin_top")));
    }
}
