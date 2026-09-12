package de.teamlapen.vampirism.api;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class VEnums {

    public static final EnumProxy<MobCategory> HUNTER_CATEGORY = new EnumProxy<>(MobCategory.class, "vampirism:hunter", 15, false, false, 128);
    public static final EnumProxy<MobCategory> VAMPIRE_CATEGORY = new EnumProxy<>(MobCategory.class, "vampirism:vampire", 30, false, false, 128);

}
