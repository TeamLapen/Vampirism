package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.jetbrains.annotations.NotNull;


public class ColoredVampireClothingItem extends VampireClothingItem {

    public ColoredVampireClothingItem(@NotNull ArmorItem.Type type, EnumClothingColor color) {
        super(type, color.armorMaterial);
    }

    public enum EnumClothingColor implements StringRepresentable {
        REDBLACK("red_black", ModArmorMaterials.VAMPIRE_CLOAK_RED_BLACK),
        BLACKRED("black_red", ModArmorMaterials.VAMPIRE_CLOAK_BLACK_RED),
        BLACKWHITE("black_white", ModArmorMaterials.VAMPIRE_CLOAK_BLACK_WHITE),
        WHITEBLACK("white_black", ModArmorMaterials.VAMPIRE_CLOAK_WHITE_BLACK),
        BLACKBLUE("black_blue", ModArmorMaterials.VAMPIRE_CLOAK_BLACK_BLUE);


        private final String name;
        private final Holder<ArmorMaterial> armorMaterial;

        EnumClothingColor(String nameIn, Holder<ArmorMaterial> armorMaterial) {
            this.name = nameIn;
            this.armorMaterial = armorMaterial;
        }

        public @NotNull String getName() {
            return getSerializedName();
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return this.name;
        }


    }
}
