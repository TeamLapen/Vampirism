package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.ModelCloak;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Item Cloak
 *
 * @author cheaterpaul
 */
public class ItemVampireCloak extends ItemArmor {

    private final String registeredName = "vampire_cloak";

    public ItemVampireCloak() {
        super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST);
        this.hasSubtypes = true;
        this.setMaxDamage(0);
        this.setCreativeTab(VampirismMod.creativeTab);
        this.setRegistryName(REFERENCE.MODID, registeredName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + registeredName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return new ModelCloak();
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s/%s_%s", registeredName, registeredName,
                EnumCloakColor.byMetadata(stack.getMetadata()).getDyeColorName() + ".png");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab))
            subItems.add(new ItemStack(this, 1, 0));
        if (tab.equals(CreativeTabs.SEARCH)) {
            for (EnumCloakColor s : EnumCloakColor.values()) {
                if (s.getMetadata() == 0)
                    continue;
                subItems.add(new ItemStack(this, 1, s.getMetadata()));
            }
        }

    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + EnumCloakColor.byMetadata(i).getUnlocalizedName();
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        if (player.ticksExisted % 16 == 8) {
            if (Helper.isHunter(player)) {
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 20, 1));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (Helper.isHunter(playerIn)) {
            tooltip.add(TextFormatting.RED + UtilLib.translate("text.vampirism.poisonous_to_hunter"));
        }
    }

    public enum EnumCloakColor implements IStringSerializable {
        REDBLACK(0, "red_black"), BLACKRED(1, "black_red"), BLACKWHITE(2, "black_white"), WHITEBLACK(3,
                "white_black"), BLACKBLUE(4, "black_blue");

        private static final EnumCloakColor[] META_LOOKUP = new EnumCloakColor[values().length];
        private final int meta;
        private final String name;

        private EnumCloakColor(int metaIn, String nameIn) {
            this.meta = metaIn;
            this.name = nameIn;
        }

        /**
         * @return color index
         */
        public int getMetadata() {
            return this.meta;
        }

        /**
         * @return color name
         */
        @SideOnly(Side.CLIENT)
        public String getDyeColorName() {
            return this.name;
        }

        /**
         * search for color by the given index
         * 
         * @param index
         * @return color enumtype
         */
        public static EnumCloakColor byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        /**
         * @return color name
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return color unlocalized name
         */
        public String getUnlocalizedName() {
            return this.name;
        }

        static {
            for (EnumCloakColor enumdyecolor : values()) {
                META_LOOKUP[enumdyecolor.getMetadata()] = enumdyecolor;
            }
        }

    }
}
