package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.common.factions.PlayerFactionPredicate;
import de.teamlapen.faction.common.util.IntRange;
import de.teamlapen.faction.common.world.items.consume.FactionBasedConsumeEffect;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.faction.common.world.items.consume.PlayerFactionConsumeEffect;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class PureBloodItem extends Item {

    public static final int COUNT = 5;
    private final static Logger LOGGER = LogManager.getLogger();

    public PureBloodItem(int level, Properties properties) {
        super(properties.stacksTo(16).overrideDescription(Util.makeDescriptionId("item", VIdentifier.mod("pure_blood")))
                .factions$factionFood(new FactionFoodList(
                        new FoodProperties.Builder().build(),
                        new FactionFoodEntry(VampirismTags.Factions.IS_VAMPIRE, new FoodProperties.Builder().nutrition(50).saturationModifier(0.4f + (0.15f * level)).build(), ModFoodBehaviours.VAMPIRE_FOOD)
                ), Consumables.defaultDrink()
                        .onConsume(FactionBasedConsumeEffect
                                .builder(VampirismTags.Factions.IS_VAMPIRE)
                                .add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.SATURATION))).build())
                        .onConsume(PlayerFactionConsumeEffect
                                .when(PlayerFactionPredicate.builder()
                                        .lordLevelRange(IntRange.lowerBound(1)))
                                .with(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.WHISPERS_OF_THE_VEIL, 20 + level * 20, level)))
                                .build())
                        .build())
                .component(ModDataComponents.PURE_LEVEL, new PureLevel(level)));
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

    public static Holder<Item> getPureBloodHolder(int level) {
        return switch (level) {
            case 0 -> ModItems.PURE_BLOOD_0;
            case 1 -> ModItems.PURE_BLOOD_1;
            case 2 -> ModItems.PURE_BLOOD_2;
            case 3 -> ModItems.PURE_BLOOD_3;
            case 4 -> ModItems.PURE_BLOOD_4;
            default -> throw new IllegalArgumentException("Pure blood of level " + level + " does not exist");
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.vampirism.purity", getLevel(stack) + 1 + "/" + COUNT).withStyle(ChatFormatting.RED));
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

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        int playerLevel = FactionsApi.factionPlayerHandler(player).getCurrentLevel(ModFactions.VAMPIRE);
        if (VampireLeveling.getInfusionRequirement(playerLevel).filter(x -> x.pureBloodLevel() <= getLevel(getDefaultInstance())).isPresent()) {
            player.startUsingItem(hand);
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.use(level, player, hand);
    }
}
