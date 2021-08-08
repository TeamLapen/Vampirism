package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.model.armor.CloakModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


public class ColoredVampireClothingItem extends VampireClothingItem {
    private final String baseName;
    private final EnumClothingColor color;
    private final EnumModel model;

    public ColoredVampireClothingItem(EquipmentSlot slotType, EnumModel model, String baseRegName, EnumClothingColor color) {
        super(slotType, baseRegName + "_" + color.getName());
        this.baseName = baseRegName;
        this.color = color;
        this.model = model;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    public HumanoidModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
        switch (model) {
            case CLOAK:
                return CloakModel.getRotatedCloak();
            default:
                return CloakModel.getRotatedCloak();
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s/%s_%s.png", baseName, baseName,
                color.getName());
    }

    public enum EnumModel {
        CLOAK
    }

    public enum EnumClothingColor implements StringRepresentable {
        REDBLACK("red_black"), BLACKRED("black_red"), BLACKWHITE("black_white"), WHITEBLACK(
                "white_black"), BLACKBLUE("black_blue");


        private final String name;

        EnumClothingColor(String nameIn) {
            this.name = nameIn;
        }

        public String getName() {
            return getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }


    }
}
