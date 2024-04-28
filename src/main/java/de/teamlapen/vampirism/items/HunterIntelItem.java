package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Item used in the hunter leveling process. Is created in a hunter table.
 */
public class HunterIntelItem extends Item {


    private final static Logger LOGGER = LogManager.getLogger();

    public static @NotNull HunterIntelItem getIntelForExactlyLevel(int level) {
        return getIntelForLevel(level - 5);
    }

    public static @NotNull HunterIntelItem getIntelForLevel(int level) {
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
    private Component tooltip;

    public HunterIntelItem(int level) {
        super(new Properties());
        this.level = level;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag flagIn) {
        if (this.tooltip == null) {
            this.tooltip = Component.translatable("text.vampirism.for_up_to_level").append(Component.literal(": " + (level + 5))).withStyle(ChatFormatting.RED);
        }
        tooltips.add(this.tooltip);
    }

    public @NotNull Component getCustomName() {
        return Component.translatable(this.getOrCreateDescriptionId()).append(Component.literal(" ")).append(Component.translatable("text.vampirism.for_up_to_level").append(Component.literal(" " + (level + 5))));
    }

    public int getLevel() {
        return level;
    }

    public boolean isFoil(@NotNull ItemStack stack) {
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
