package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.client.model.CloakModel;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VampireCloakItem extends ArmorItem implements IFactionExclusiveItem {

    private final String registeredName = "vampire_cloak";
    private final EnumCloakColor color;

    public VampireCloakItem(EnumCloakColor color) {
        super(ArmorMaterial.LEATHER, EquipmentSlotType.CHEST, new Properties().defaultMaxDamage(0).group(VampirismMod.creativeTab));
        this.setRegistryName(REFERENCE.MODID, registeredName + "_" + color.getName());
        this.color = color;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PlayerEntity playerEntity = VampirismMod.proxy.getClientPlayer();
        this.addFactionPoisonousToolTip(stack,worldIn,tooltip,flagIn,playerEntity);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        return CloakModel.getRotatedCloak();
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s/%s_%s", registeredName, registeredName,
                color.getName() + ".png");
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);
        if (player.ticksExisted % 16 == 8) {
            if (!Helper.isVampire(player)) {
                player.addPotionEffect(new EffectInstance(ModEffects.poison, 20, 1));
            }
        }
    }

    public enum EnumCloakColor implements IStringSerializable {
        REDBLACK("red_black"), BLACKRED("black_red"), BLACKWHITE("black_white"), WHITEBLACK(
                "white_black"), BLACKBLUE("black_blue");


        private final String name;

        EnumCloakColor(String nameIn) {
            this.name = nameIn;
        }

        @Override
        public String getString() {
            return this.name;
        }


        public String getName() {
            return getString();
        }


    }
}
