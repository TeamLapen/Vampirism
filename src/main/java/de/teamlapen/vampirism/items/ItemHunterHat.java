package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.ModelHunterHat;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Simple headwear that look like a hunter head
 */
public class ItemHunterHat extends VampirismHunterArmor {
    private static final String baseRegName = "hunter_hat";
    private final int type;

    public ItemHunterHat(int type) {
        super(baseRegName, "" + type, ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, new Properties().group(VampirismMod.creativeTab));
        this.type = type;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return type == 0 ? ModelHunterHat.hat0 : ModelHunterHat.hat1;
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "vampirism:textures/entity/vampire_hunter_extra.png";
    }


    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        return 2;
    }
}
