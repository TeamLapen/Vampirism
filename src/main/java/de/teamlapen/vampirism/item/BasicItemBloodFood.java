package de.teamlapen.vampirism.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public abstract class BasicItemBloodFood extends ItemFood {

	protected int bloodAmount;
	public BasicItemBloodFood(String name,int amount) {
		super(0,0,false);
		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(name);
		this.setAlwaysEdible();
		bloodAmount=amount;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
		Logger.i("test", "test");
        --stack.stackSize;
        VampirePlayer.get(player).getBloodStats().addBlood(bloodAmount);
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        return stack;
    }

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}
}
