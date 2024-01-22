package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
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

    private final int level;

    public PureBloodItem(int level) {
        super(new Properties().stacksTo(16));
        this.level = level;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.vampirism.pure_blood.purity").append(Component.literal(": " + (level + 1 + "/" + COUNT))).withStyle(ChatFormatting.RED));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (entityLiving instanceof Player player) {
            VampirePlayer vampire = VampirePlayer.get(player);
            vampire.drinkBlood(50, 0.4f + (0.15f * getLevel()), false, new DrinkBloodContext(stack));
            entityLiving.addEffect(new MobEffectInstance(ModEffects.SATURATION.get()));
            stack.shrink(1);
        }
        return stack;
    }

    public int getLevel() {
        return this.level;
    }

    public @NotNull Component getCustomName() {
        return Component.translatable(this.getOrCreateDescriptionId()).append(Component.literal(" " + (level + 1)));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 30;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        int playerLevel = VampirismAPI.factionPlayerHandler(playerIn).getCurrentLevel(VReference.VAMPIRE_FACTION);
        if (VampireLeveling.getInfusionRequirement(playerLevel).filter(x -> x.pureBloodLevel() < getLevel()).isPresent()) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.sidedSuccess(playerIn.getItemInHand(handIn), worldIn.isClientSide);
        }
        return super.use(worldIn, playerIn, handIn);
    }


    private String descriptionId;

    @Override
    @NotNull
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = super.getOrCreateDescriptionId().replaceAll("_\\d", "");
        }

        return this.descriptionId;
    }
}
