package de.teamlapen.vampirism.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemNightClock extends BasicItem {
	public static final String name = "nightClock";

	public ItemNightClock() {
		super(name);
		this.maxStackSize = 1;
	}

	@Override
	public boolean hasEffect(ItemStack s, int pass) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (world.isDaytime()) {
				if (world.playerEntities.size() > 1) {
					player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism:other_players_preventing"));
				} else {
					for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j) {
						MinecraftServer.getServer().worldServers[j].setWorldTime(13000);
					}
				}
			} else {
				player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism:cant_use_at_night"));
			}

		}
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon("clock");
	}
}
