package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.IItemRegistrable;
import de.teamlapen.vampirism.util.IVanillaExt;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Similar to ItemMonsterPlacer, but for Vampirism
 */
public class ItemSpawnEgg extends BasicItem implements IItemRegistrable.IItemFlexibleRegistrable,IVanillaExt{
	public final static String name="spawn_egg";
	private final List<String> entities;
	public ItemSpawnEgg(List<String> entities) {
		super(name);
		setCreativeTab(CreativeTabs.tabMisc);
		this.entities=entities;
		this.hasSubtypes=true;
		this.setUnlocalizedName("monsterPlacer");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String s = super.getItemStackDisplayName(stack);//("" + StatCollector.translateToLocal(Items.spawn_egg.getUnlocalizedName() + ".name")).trim();
		if (stack.getItemDamage()<entities.size()) {
			String entity = entities.get(stack.getItemDamage());
			s = s + " " + StatCollector.translateToLocal("entity." + entity + ".name");
		}

		return s;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 0 ? 0x22FF99: (stack.getItemDamage()+10)*20<<2;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.isRemote)
			return true;
		else if(!player.canPlayerEdit(pos.offset(side),side,stack)){
			return false;
		}
		else {
			if (stack.getItemDamage()>=entities.size()) {
				return false;
			}
			String name = entities.get(stack.getItemDamage());
			IBlockState blockState = world.getBlockState(pos);
			pos.offset(side);

			double d0 = 0.0D;

			if (side == EnumFacing.UP && blockState instanceof BlockFence)
			{
				d0 = 0.5D;
			}

			Entity entity = spawnMob(world,pos.getX()+ 0.5D, pos.getY() + d0, pos.getZ() + 0.5D,name);

			if (entity != null) {
				if (entity instanceof EntityLivingBase && stack.hasDisplayName())
					(entity).setCustomNameTag(stack.getDisplayName());

				if (!player.capabilities.isCreativeMode)
					stack.stackSize--;
			}

			return true;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote)
			return stack;
		else {
			if (stack.getItemDamage()>=entities.size()) {
				return stack;
			}
			String name = entities.get(stack.getItemDamage());
			MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);

			if (movingobjectposition == null)
				return stack;
			else {
				if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					BlockPos pos=movingobjectposition.getBlockPos();

					if (!world.isBlockModifiable(player,pos))
						return stack;

					if (!player.canPlayerEdit(pos, movingobjectposition.sideHit, stack))
						return stack;

					if (world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
						Entity entity = spawnMob(world,pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,name);

						if (entity != null) {
							if (entity instanceof EntityLivingBase && stack.hasDisplayName())
								((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());

							if (!player.capabilities.isCreativeMode)
								stack.stackSize--;
						}
					}
				}

				return stack;
			}
		}
	}

	private Entity spawnMob(World world, double x, double y, double z,String name) {

		Entity entity= EntityList.createEntityByName(name,world);
		if (entity != null && entity instanceof EntityLivingBase) {
			EntityLiving entityliving = (EntityLiving) entity;
			entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
			entityliving.rotationYawHead = entityliving.rotationYaw;
			entityliving.renderYawOffset = entityliving.rotationYaw;
			entityliving.func_180482_a(world.getDifficultyForLocation(new BlockPos(entityliving)),null);
			world.spawnEntityInWorld(entity);
			entityliving.playLivingSound();
		}

		return entity;
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
		for(int i=0;i<entities.size();i++){
			list.add(new ItemStack(item,1,i));
		}

	}

	@Override
	public String[] getModelVariants() {
		return new String[]{"spawn_egg"};
	}

	@Override
	public Helper.StackToString getModelMatcher() {
		return new Helper.StackToString() {
			@Override
			public String match(ItemStack stack) {
				return "spawn_egg";
			}
		};
	}
}