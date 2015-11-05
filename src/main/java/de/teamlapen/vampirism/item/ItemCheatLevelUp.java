package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Item for creative mode to cheat yourself one level
 */
public class ItemCheatLevelUp extends BasicItem {
    public final static String name = "cheat_level_up";

    public ItemCheatLevelUp() {
        super(name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World p_77659_2_, EntityPlayer player) {
        VampirePlayer.get(player).levelUp();
        if (!p_77659_2_.isRemote) {
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(player.getCommandSenderName() + " changed his vampire level to " + VampirePlayer.get(player).getLevel()));
        }
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return stack;
    }
}
