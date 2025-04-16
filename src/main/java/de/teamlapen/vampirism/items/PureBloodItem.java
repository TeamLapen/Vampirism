package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.items.component.PureLevel;
import de.teamlapen.vampirism.items.consume.BloodConsume;
import de.teamlapen.vampirism.items.consume.BloodFoodProperties;
import de.teamlapen.vampirism.items.consume.FactionBasedConsumeEffect;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PureBloodItem extends Item {

    public static final int COUNT = 5;
    private final static Logger LOGGER = LogManager.getLogger();

    public static @NotNull PureBloodItem getBloodItemForLevel(int level) {
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

    public PureBloodItem(int level, Properties properties) {
        super(properties.stacksTo(16).overrideDescription(Util.makeDescriptionId("item", VResourceLocation.mod("pure_blood"))).component(DataComponents.CONSUMABLE, Consumables.defaultDrink()
                .onConsume(
                        FactionBasedConsumeEffect.builder(ModFactionTags.IS_VAMPIRE)
                                .add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.SATURATION)))
                                .add(new BloodConsume(50, 0.4f + (0.15f * level), false))
                                .build()
                ).build()).component(ModDataComponents.PURE_LEVEL, new PureLevel(level)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.vampirism.pure_blood.purity").append(Component.literal(": " + (getLevel(stack) + 1 + "/" + COUNT))).withStyle(ChatFormatting.RED));
    }

    public int getLevel(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.LOW).level();
    }

    public @NotNull Component getCustomName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId().replaceAll("_\\d", "")).append(Component.literal(" " + (getLevel(stack) + 1)));
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
        return 30;
    }

    @NotNull
    @Override
    public ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        int playerLevel = VampirismAPI.factionPlayerHandler(playerIn).getCurrentLevel(ModFactions.VAMPIRE);
        if (VampireLeveling.getInfusionRequirement(playerLevel).filter(x -> x.pureBloodLevel() < getLevel(getDefaultInstance())).isPresent()) {
            playerIn.startUsingItem(handIn);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(worldIn, playerIn, handIn);
    }

}
