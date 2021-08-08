package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class PureBloodItem extends VampirismItem {

    public static final int COUNT = 5;
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String name = "pure_blood";

    public static Item getBloodItemForLevel(int level) {
        switch (level) {
            case 0:
                return ModItems.pure_blood_0;
            case 1:
                return ModItems.pure_blood_1;
            case 2:
                return ModItems.pure_blood_2;
            case 3:
                return ModItems.pure_blood_3;
            case 4:
                return ModItems.pure_blood_4;
            default:
                LOGGER.warn("Pure blood of level {} does not exist", level);
                return ModItems.pure_blood_4;
        }
    }

    private final int level;

    public PureBloodItem(int level) {
        super(name + "_" + level, new Properties().tab(VampirismMod.creativeTab));
        this.level = level;
        this.setTranslation_key(name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.vampirism.pure_blood.purity").append(new TextComponent(": " + (level + 1 + "/" + COUNT))).withStyle(ChatFormatting.RED));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof Player) {
            VampirePlayer.getOpt((Player) entityLiving).ifPresent(v -> {
                v.drinkBlood(50, 0.3f, false);
                entityLiving.addEffect(new MobEffectInstance(ModEffects.saturation));
                stack.shrink(1);
                checkWingConditions(v);
            });
        }
        return stack;
    }

    public int getLevel() {
        return this.level;
    }

    public Component getCustomName() {
        return new TranslatableComponent(this.getOrCreateDescriptionId()).append(new TextComponent(" " + (level + 1)));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        int playerLevel = VampirismAPI.getFactionPlayerHandler(playerIn).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
        if (VampireLevelingConf.getInstance().isLevelValidForAltarInfusion(playerLevel)) {
            int pureLevel = VampireLevelingConf.getInstance().getAltarInfusionRequirements(playerLevel).pureBloodLevel;
            if (getLevel() < pureLevel) {
                playerIn.startUsingItem(handIn);
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    private void checkWingConditions(VampirePlayer p) {
        net.minecraft.world.entity.player.Player e = p.getRepresentingPlayer();
        if (!e.abilities.instabuild && !e.level.isClientSide()) {
            if (e.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).getItem() instanceof VampireClothingItem) {
                p.triggerWings();
            }
        }
    }
}
