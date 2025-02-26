package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class MapUtil {
    public static String getTranslation(String namespace, String name) {
        return "filled_map." + namespace + "." + name;
    }

    public static String getModTranslation(String name) {
        return getTranslation(REFERENCE.MODID, name);
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

    public static ItemStack getPreviewMap(String displayName, Holder<MapDecorationType> decorationType) {
        ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
        itemStack.set(DataComponents.MAP_COLOR, new MapItemColor(decorationType.value().mapColor()));
        itemStack.set(DataComponents.ITEM_NAME, Component.translatable(displayName));
        return itemStack;
    }
}
