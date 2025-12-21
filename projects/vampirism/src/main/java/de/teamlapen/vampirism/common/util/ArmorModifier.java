package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.Arrays;
import java.util.Collection;

public class ArmorModifier {

    public static final Collection<Identifier> ARMOR_IDS = Arrays.stream(ArmorType.values()).map(type -> VResourceLocation.mc("armor." + type.getName())).toList();
}
