package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.ItemPropertiesExtension;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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
        return switch (level) {
            case 0 -> ModItems.HUNTER_INTEL_0.get();
            case 1 -> ModItems.HUNTER_INTEL_1.get();
            case 2 -> ModItems.HUNTER_INTEL_2.get();
            case 3 -> ModItems.HUNTER_INTEL_3.get();
            case 4 -> ModItems.HUNTER_INTEL_4.get();
            case 5 -> ModItems.HUNTER_INTEL_5.get();
            case 6 -> ModItems.HUNTER_INTEL_6.get();
            case 7 -> ModItems.HUNTER_INTEL_7.get();
            case 8 -> ModItems.HUNTER_INTEL_8.get();
            case 9 -> ModItems.HUNTER_INTEL_9.get();
            default -> {
                LOGGER.warn("HunterIntel of level {} does not exist", level);
                yield ModItems.HUNTER_INTEL_9.get();
            }
        };
    }

    private final int level;

    public HunterIntelItem(int level, Properties properties) {
        super(ItemPropertiesExtension.descriptionWithout(properties, "_\\d"));
        this.level = level;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltips, TooltipFlag flagIn) {
        tooltips.add(Component.translatable("text.vampirism.for_up_to_level").append(Component.literal(": " + (level + 5))).withStyle(ChatFormatting.RED));
    }

    public Component getCustomName() {
        return Component.translatable(this.getDescriptionId()).append(Component.literal(" ")).append(Component.translatable("text.vampirism.for_up_to_level").append(Component.literal(" " + (level + 5))));
    }

    public int getLevel() {
        return level;
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

}
