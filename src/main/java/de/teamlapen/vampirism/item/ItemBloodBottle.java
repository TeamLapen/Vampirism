package de.teamlapen.vampirism.item;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * 
 * @author WILLIAM
 *
 */
public class ItemBloodBottle extends ItemGlassBottle {

	public static final String name = "bloodBottle";
	public static final String textureBaseName = "blood_bottle_";
	public static final int NUM_ICONS = 10;
	public static final int MAX_BLOOD = NUM_ICONS - 1;
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
	
	public ItemBloodBottle() {
		setUnlocalizedName(REFERENCE.MODID + ":" + name);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabMisc);
		this.maxStackSize = 1; // TODO: I want this to not stack when blood is in bottle, but stack when empty
	}
	
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     * Add code here to:
     * 1) Shift + right click adds blood from blood bar
     * 2) Right click removes blood and puts it in blood bar
     * 3) TODO: Fill blood bottle from any other blood containers (if made)
     */
	@Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		
		if (!world.isRemote) {
			VampirePlayer vampire = VampirePlayer.get(player);
			
			// Remove blood from blood bar and add to bottle on shift + right click
			if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				Logger.i(REFERENCE.MODID, "Shift + Right click pressed!");
				int bloodBottle = getBlood(stack);
				int bloodBar = vampire.getBlood();
				if (bloodBottle < MAX_BLOOD && bloodBar > 0) {
					addBlood(stack, 1);
					vampire.getBloodStats().consumeBlood(1);
				}
			}
			// Add blood to blood bar from bottle on right click
			else {
				Logger.i(REFERENCE.MODID, "Right click pressed!");
				int bloodBottle = getBlood(stack);
				int bloodBar = vampire.getBlood();
				if (bloodBottle > 0 && bloodBar < VampirePlayer.MAXBLOOD) {
					removeBlood(stack, 1);
					vampire.getBloodStats().addBlood(1);
				}
			}
		}
		return stack;
    }
	
	public static int getBlood(ItemStack stack) {
		return stack.getItemDamage();
	}
	
	public static void addBlood(ItemStack stack, int a) {
		stack.setItemDamage(Math.min(stack.getItemDamage() + a, ItemBloodBottle.MAX_BLOOD));
	}
	
	public static void removeBlood(ItemStack stack, int a) {
		stack.setItemDamage(Math.max(stack.getItemDamage() - a, 0));		
	}
	
    @SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1) {
    	icons = new IIcon[NUM_ICONS];
    	
    	for (int i = 0; i < icons.length; i++) {
    		icons[i] = par1.registerIcon(REFERENCE.MODID + ":" + textureBaseName + Integer.toString(i));
    	}
    }
    
    /**
     * Gets an icon based on an item's damage value
     */
	@Override
    public IIcon getIconFromDamage(int index)
    {
        return icons[index];
    }
	
	@SuppressWarnings({"unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
	@Override	
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		// Put all the different levels of filled bottles in creative inventory
//		for (int i = 0; i < NUM_ICONS; i++) {
//			list.add(new ItemStack(this, 1, i));
//		}
		
		// OR just have a full bottle in creative inventory
		list.add(new ItemStack(this, 1, MAX_BLOOD)); 
	}

	// Returns the first bottle found in the player's hotbar (doesn't need to be held)
	public static ItemStack getBloodBottleInInventory(InventoryPlayer inventory) {
		int hotbarSize = InventoryPlayer.getHotbarSize();
		for (int i = 0; i < hotbarSize; i++) {
			ItemStack itemStack = inventory.getStackInSlot(i);
			if (itemStack != null && itemStack.getUnlocalizedName().compareTo(ModItems.bloodBottle.getUnlocalizedName()) == 0) {
				return itemStack;
			}
		}
		return null;
	}
}
