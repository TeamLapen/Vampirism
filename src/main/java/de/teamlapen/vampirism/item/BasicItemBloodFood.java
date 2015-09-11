package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.IItemRegistrable;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BasicItemBloodFood extends ItemFood implements IItemRegistrable{

	protected int bloodAmount;
	final String name;
	public BasicItemBloodFood(String name, int amount) {
		super(0, 0, false);
		this.name=name;
		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(REFERENCE.MODID + "." + name);
		this.setAlwaysEdible();
		bloodAmount = amount;
	}


	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
		--stack.stackSize;
		VampirePlayer.get(player).getBloodStats().addBlood(bloodAmount);
		world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		this.onFoodEaten(stack, world, player);
		return stack;
	}

	@Override
	public String getBaseName() {
		return name;
	}
}
