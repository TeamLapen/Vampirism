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

public class PureBloodItem extends Item {

    public static final int COUNT = 5;
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String name = "pure_blood";

    public static Item getBloodItemForLevel(int level) {
        switch (level) {
            case 0:
                return ModItems.PURE_BLOOD_0.get();
            case 1:
                return ModItems.PURE_BLOOD_1.get();
            case 2:
                return ModItems.PURE_BLOOD_2.get();
            case 3:
                return ModItems.PURE_BLOOD_3.get();
            case 4:
                return ModItems.PURE_BLOOD_4.get();
            default:
                LOGGER.warn("Pure blood of level {} does not exist", level);
                return ModItems.PURE_BLOOD_4.get();
        }
    }

    private final int level;

    public PureBloodItem(int level) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.level = level;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.vampirism.pure_blood.purity").append(new StringTextComponent(": " + (level + 1 + "/" + COUNT))).withStyle(TextFormatting.RED));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            VampirePlayer.getOpt((PlayerEntity) entityLiving).ifPresent(v -> {
                v.drinkBlood(50, 0.3f, false);
                entityLiving.addEffect(new EffectInstance(ModEffects.SATURATION.get()));
                stack.shrink(1);
                checkWingConditions(v);
            });
        }
        return stack;
    }

    public int getLevel() {
        return this.level;
    }

    public ITextComponent getCustomName() {
        return new TranslationTextComponent(this.getOrCreateDescriptionId()).append(new StringTextComponent(" " + (level + 1)));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
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
        net.minecraft.entity.player.PlayerEntity e = p.getRepresentingPlayer();
        if (!e.abilities.instabuild && !e.level.isClientSide()) {
            if (e.getItemBySlot(net.minecraft.inventory.EquipmentSlotType.CHEST).getItem() instanceof VampireClothingItem) {
                p.triggerWings();
            }
        }
    }

    private String descriptionId;
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = super.getOrCreateDescriptionId().replaceAll("_\\d", "");
        }

        return this.descriptionId;
    }
}
