package de.teamlapen.vampirism.api;

import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class VEnums {

    public static final EnumProxy<MobCategory> HUNTER_CATEGORY = new EnumProxy<>(MobCategory.class, "vampirism:hunter", 15, false, false, 128);
    public static final EnumProxy<MobCategory> VAMPIRE_CATEGORY = new EnumProxy<>(MobCategory.class, "vampirism:vampire", 30, false, false, 128);

    public static final UnaryOperator<Style> SOVEREIGN_STYLE = style -> style.withColor(0xde0d73);

    public static final EnumProxy<Rarity> SOVEREIGN = new EnumProxy<>(Rarity.class, 0, "vampirism:sovereign", SOVEREIGN_STYLE);
}
