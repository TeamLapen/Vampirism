package de.teamlapen.vampirism.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ArmorModifier {

    public static final Collection<ResourceLocation> ARMOR_IDS = Arrays.stream(ArmorType.values()).map(type -> ResourceLocation.withDefaultNamespace("armor." + type.getName())).toList();
}
