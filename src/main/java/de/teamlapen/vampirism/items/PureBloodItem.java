package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
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

    private final static Logger LOGGER = LogManager.getLogger();
    public static final int COUNT = 5;
    private final static String name = "pure_blood";
    private final int level;

    public PureBloodItem(int level) {
        super(name + "_" + level, new Properties().group(VampirismMod.creativeTab));
        this.level = level;
        this.setTranslation_key(name);
    }

    @Override
    public ITextComponent getCustomName() {
        return super.getCustomName().appendText(" " + (level + 1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.vampirism.pure_blood.purity").appendText(": " + (level + 1 + "/" + COUNT)).applyTextStyle(TextFormatting.RED));
    }

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

}
