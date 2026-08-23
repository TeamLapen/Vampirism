package de.teamlapen.vampirism.api;

import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class VEnums {
    /**
     * Hunter creatures are of this creature type. But when they are counted for spawning they belong to {@link net.minecraft.world.entity.MobCategory#MONSTER}
     */
    public static final EnumProxy<MobCategory> HUNTER_CATEGORY = new EnumProxy<>(MobCategory.class, "vampirism:hunter", 15, false, false, 128);
    /**
     * Vampire creatures are of this creature type. But when they are counted for spawning they belong to {@link net.minecraft.world.entity.MobCategory#MONSTER}
     */
    public static final EnumProxy<MobCategory> VAMPIRE_CATEGORY = new EnumProxy<>(MobCategory.class, "vampirism:vampire", 30, false, false, 128);

    public static final EnumProxy<ItemDisplayContext> PEDESTAL = new EnumProxy<>(ItemDisplayContext.class, 9, "vampirism:pedestal", "GROUND");

    public static final UnaryOperator<Style> SOVEREIGN_STYLE = style -> style.withColor(0xde0d73);

    public static final EnumProxy<Rarity> SOVEREIGN = new EnumProxy<>(Rarity.class, 0, "vampirism:sovereign", SOVEREIGN_STYLE);
}
