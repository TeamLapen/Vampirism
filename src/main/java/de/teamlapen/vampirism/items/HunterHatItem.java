package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.armor.HunterHatModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

/**
 * Simple headwear that look like a hunter head
 */
public class HunterHatItem extends VampirismHunterArmor {
    private static final String baseRegName = "hunter_hat";
    private final int type;

    public HunterHatItem(int type) {
        super(baseRegName, "" + type, ArmorMaterials.IRON, EquipmentSlot.HEAD, new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public HumanoidModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
        return type == 0 ? HunterHatModel.hat0 : HunterHatModel.hat1;
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "vampirism:textures/entity/hunter_extra.png";
    }


    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        return 2;
    }
}
