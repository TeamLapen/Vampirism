package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.common.items.component.PureLevel;
import de.teamlapen.vampirism.common.items.consume.BloodConsume;
import de.teamlapen.vampirism.common.items.consume.FactionBasedConsumeEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PureBloodItem extends Item {

    public static final int COUNT = 5;
    private final static Logger LOGGER = LogManager.getLogger();

    public PureBloodItem(int level, Properties properties) {
        super(properties.stacksTo(16).overrideDescription(Util.makeDescriptionId("item", VResourceLocation.mod("pure_blood"))).component(DataComponents.CONSUMABLE, Consumables.defaultDrink()
                .onConsume(
                        FactionBasedConsumeEffect.builder(ModFactionTags.IS_VAMPIRE)
                                .add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.SATURATION)))
                                .add(new BloodConsume(50, 0.4f + (0.15f * level), false))
                                .build()
                ).build()).component(ModDataComponents.PURE_LEVEL, new PureLevel(level)));
    }

    public static PureBloodItem getBloodItemForLevel(int level) {
        return switch (level) {
            case 0 -> ModItems.PURE_BLOOD_0.get();
            case 1 -> ModItems.PURE_BLOOD_1.get();
            case 2 -> ModItems.PURE_BLOOD_2.get();
            case 3 -> ModItems.PURE_BLOOD_3.get();
            case 4 -> ModItems.PURE_BLOOD_4.get();
            default -> {
                LOGGER.warn("Pure blood of level {} does not exist", level);
                yield ModItems.PURE_BLOOD_4.get();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.vampirism.pure_blood.purity").append(Component.literal(": " + (getLevel(stack) + 1 + "/" + COUNT))).withStyle(ChatFormatting.RED));
    }

    public int getLevel(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.LOW).level();
    }

    public Component getCustomName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId().replaceAll("_\\d", "")).append(Component.literal(" " + (getLevel(stack) + 1)));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 30;
    }

    @NotNull
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        int playerLevel = VampirismAPI.factionPlayerHandler(player).getCurrentLevel(ModFactions.VAMPIRE);
        if (VampireLeveling.getInfusionRequirement(playerLevel).filter(x -> x.pureBloodLevel() < getLevel(getDefaultInstance())).isPresent()) {
            player.startUsingItem(hand);
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.use(level, player, hand);
    }
}
