package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Item used in the hunter leveling process. Is created in a hunter table.
 */
public class HunterIntelItem extends Item {


    private final static Logger LOGGER = LogManager.getLogger();

    public static HunterIntelItem getIntelForExactlyLevel(int level) {
        return getIntelForLevel(level - 5);
    }

    public static HunterIntelItem getIntelForLevel(int level) {
        switch (level) {
            case 0:
                return ModItems.HUNTER_INTEL_0.get();
            case 1:
                return ModItems.HUNTER_INTEL_1.get();
            case 2:
                return ModItems.HUNTER_INTEL_2.get();
            case 3:
                return ModItems.HUNTER_INTEL_3.get();
            case 4:
                return ModItems.HUNTER_INTEL_4.get();
            case 5:
                return ModItems.HUNTER_INTEL_5.get();
            case 6:
                return ModItems.HUNTER_INTEL_6.get();
            case 7:
                return ModItems.HUNTER_INTEL_7.get();
            case 8:
                return ModItems.HUNTER_INTEL_8.get();
            case 9:
                return ModItems.HUNTER_INTEL_9.get();
            default:
                LOGGER.warn("HunterIntel of level {} does not exist", level);
                return ModItems.HUNTER_INTEL_9.get();

        }
    }

    private final int level;
    private Component tooltip;

    public HunterIntelItem(int level) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.level = level;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flagIn) {
        if (this.tooltip == null) {
            this.tooltip = new TranslatableComponent("text.vampirism.for_up_to_level").append(new TextComponent(": " + (level + 5))).withStyle(ChatFormatting.RED);
        }
        tooltips.add(this.tooltip);
    }

    public Component getCustomName() {
        return new TranslatableComponent(this.getOrCreateDescriptionId()).append(new TextComponent(" ")).append(new TranslatableComponent("text.vampirism.for_up_to_level").append(new TextComponent(" " + (level + 5))));
    }

    /**
     * @return Level of this hunter intel 0 - {@link HunterLevelingConf#HUNTER_INTEL_COUNT}-1
     */
    public int getLevel() {
        return level;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
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
