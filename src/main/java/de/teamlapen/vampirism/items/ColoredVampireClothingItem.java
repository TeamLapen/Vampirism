package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.model.armor.CloakModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


public class ColoredVampireClothingItem extends VampireClothingItem {
    private final String baseName;
    private final EnumClothingColor color;
    private final EnumModel model;

    public ColoredVampireClothingItem(EquipmentSlotType slotType, EnumModel model, String baseRegName, EnumClothingColor color) {
        super(slotType);
        this.baseName = baseRegName;
        this.color = color;
        this.model = model;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        switch (model) {
            case CLOAK:
                return CloakModel.getRotatedCloak();
            default:
                return CloakModel.getRotatedCloak();
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s/%s_%s.png", baseName, baseName,
                color.getName());
    }

    public enum EnumModel {
        CLOAK
    }

    public enum EnumClothingColor implements IStringSerializable {
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
