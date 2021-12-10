package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.armor.HunterHatModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

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
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
                                return (A) (type == 0 ? HunterHatModel.getInstance0() : HunterHatModel.getInstance1());
                            }
                        }
        );
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "vampirism:textures/entity/hunter_extra.png";
    }


}
