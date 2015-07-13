package de.teamlapen.vampirism.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Similar to ItemMonsterPlacer, but for Vampirism
 */
public class ItemSpawnEgg extends BasicItem {
	public final static String name="spawn_egg";
	private List<String> entities;
	public ItemSpawnEgg(List<String> entities) {
		super(name);
		setCreativeTab(CreativeTabs.tabMisc);
		this.entities=entities;
		this.hasSubtypes=true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String s = ("" + StatCollector.translateToLocal(Items.spawn_egg.getUnlocalizedName() + ".name")).trim();

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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		else {
			if (stack.getItemDamage()>=entities.size()) {
				return false;
			}
			String name = entities.get(stack.getItemDamage());
			Block block = world.getBlock(x, y, z);
			x += Facing.offsetsXForSide[side];
			y += Facing.offsetsYForSide[side];
			z += Facing.offsetsZForSide[side];
			double d0 = 0.0D;

			if (side == 1 && block.getRenderType() == 11)
				d0 = 0.5D;

			Entity entity = spawnMob(world, x + 0.5D, y + d0, z + 0.5D,name);

			if (entity != null) {
				if (entity instanceof EntityLivingBase && stack.hasDisplayName())
					((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());

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
					int i = movingobjectposition.blockX;
					int j = movingobjectposition.blockY;
					int k = movingobjectposition.blockZ;

					if (!world.canMineBlock(player, i, j, k))
						return stack;

					if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, stack))
						return stack;

					if (world.getBlock(i, j, k) instanceof BlockLiquid) {
						Entity entity = spawnMob(world, i, j, k,name);

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
			entityliving.onSpawnWithEgg((IEntityLivingData) null);
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
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		return Items.spawn_egg.getIconFromDamageForRenderPass(meta, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
	}
}