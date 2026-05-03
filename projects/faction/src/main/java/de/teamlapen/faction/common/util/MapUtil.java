package de.teamlapen.faction.common.util;

import de.teamlapen.faction.api.util.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class MapUtil {

    public static String getTranslation(String namespace, String name) {
        return "filled_map." + namespace + "." + name;
    }

    public static String getModTranslation(String name) {
        return getTranslation(REFERENCE.MOD_ID, name);
    }

    @Nullable
    public static ItemStack getMap(Entity entity, TagKey<Structure> destination, String displayName, Holder<MapDecorationType> decorationType, int radius) {
        if (entity.level() instanceof ServerLevel serverlevel) {
            BlockPos targetPos = serverlevel.findNearestMapStructure(destination, entity.blockPosition(), radius, true);
            if (targetPos != null) {
                ItemStack itemStack = MapItem.create(serverlevel, targetPos.getX(), targetPos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverlevel, itemStack);
                MapItemSavedData.addTargetDecoration(itemStack, targetPos, "+", decorationType);
                itemStack.set(DataComponents.ITEM_NAME, Component.translatable(displayName));
                return itemStack;
            }
        }

        return null;
    }

    public static ItemStackTemplate getPreviewMap(String displayName, Holder<MapDecorationType> decorationType) {
        return new ItemStackTemplate(Items.FILLED_MAP, DataComponentPatch.builder()
                .set(DataComponents.MAP_COLOR, new MapItemColor(decorationType.value().mapColor()))
                .set(DataComponents.ITEM_NAME, Component.translatable(displayName))
                .build());
    }

    public static boolean hasDecoration(Holder<MapDecorationType> mapDecoration, ItemStack itemStack, Level level) {
        MapItemSavedData savedData = MapItem.getSavedData(itemStack, level);
        if (savedData != null) {
            for (MapDecoration decoration : savedData.getDecorations()) {
                if (decoration.type() == mapDecoration) {
                    return true;
                }
            }
        }
        return false;
    }
}
