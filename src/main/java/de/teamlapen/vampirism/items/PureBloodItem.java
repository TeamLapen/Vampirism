package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;


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
        super(name + "_" + level, new Properties().group(VampirismMod.creativeTab));
        this.level = level;
        this.setTranslation_key(name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.vampirism.pure_blood.purity").append(new StringTextComponent(": " + (level + 1 + "/" + COUNT))).mergeStyle(TextFormatting.RED));
    }

    public ITextComponent getCustomName() {
        return new TranslationTextComponent(this.getDefaultTranslationKey()).append(new StringTextComponent(" " + (level + 1)));
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        int playerLevel = VampirismAPI.getFactionPlayerHandler(playerIn).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
        if (VampireLevelingConf.getInstance().isLevelValidForAltarInfusion(playerLevel)) {
            int pureLevel = VampireLevelingConf.getInstance().getAltarInfusionRequirements(playerLevel).pureBloodLevel;
            if (getLevel() < pureLevel) {
                playerIn.setActiveHand(handIn);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            VampirePlayer.getOpt((PlayerEntity) entityLiving).ifPresent(v -> {
                v.drinkBlood(50, 0.3f, false);
                entityLiving.addPotionEffect(new EffectInstance(ModEffects.saturation));
                stack.shrink(1);
                checkWingConditions(v);
            });
        }
        return stack;
    }

    private void checkWingConditions(VampirePlayer p) {
        net.minecraft.entity.player.PlayerEntity e = p.getRepresentingPlayer();
        if (!e.abilities.isCreativeMode && !e.world.isRemote()) {
            if (e.getItemStackFromSlot(net.minecraft.inventory.EquipmentSlotType.CHEST).getItem() instanceof VampireCloakItem) {
                p.triggerWings();
            }
        }
    }
}
