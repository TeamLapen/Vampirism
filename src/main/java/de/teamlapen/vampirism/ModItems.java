package de.teamlapen.vampirism;

import de.teamlapen.vampirism.block.BlockCastleSlab;
import de.teamlapen.vampirism.item.*;
import de.teamlapen.vampirism.proxy.CommonProxy;
import de.teamlapen.vampirism.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModItems {

	public static ItemArmor.ArmorMaterial ARMOR_BLOOD_IRON = EnumHelper.addArmorMaterial("BLOOD_IRON", REFERENCE.MODID+":blood_iron", 15, new int[]{2, 5, 4, 1}, 12);
	public static ItemSword leechSword = new ItemLeechSword();

	public static ItemBloodBottle bloodBottle = new ItemBloodBottle();

	public static ItemVampireFang vampireFang = new ItemVampireFang();
	public static ItemWeakVampireFang weakVampireFang = new ItemWeakVampireFang();
	public static ItemPitchfork pitchfork = new ItemPitchfork();

	public static ItemTorch torch = new ItemTorch();
	public static ItemHumanHeart humanHeart = new ItemHumanHeart();
	public static ItemWeakHumanHeart weakHumanHeart = new ItemWeakHumanHeart();
	public static ItemPureBlood pureBlood = new ItemPureBlood();
	public static ItemSunscreen sunscreen = new ItemSunscreen();
	public static ItemCoffin coffin = new ItemCoffin();
	public static ItemVampireArmor vampireHelmet = new ItemVampireArmor(1,0);
	public static ItemVampireArmor vampireChestplate = new ItemVampireArmor(1,1);
	public static ItemVampireArmor vampireLeggings = new ItemVampireArmor(2,2);
	public static ItemVampireArmor vampireBoots = new ItemVampireArmor(1,3);
	public static ItemGemOfBinding gemOfBinding = new ItemGemOfBinding();
	public static ItemMinionNameTag minionNameTag = new ItemMinionNameTag();
	public static ItemBloodEye bloodEye=new ItemBloodEye();
	public static ItemTent tent = new ItemTent();
	/**
	 * Holds the vampirism spawn egg after {@link CommonProxy#init()}
	 */
	public static @Nullable ItemSpawnEgg spawn_egg;

	public static void preInit() {
		GameRegistry.registerItem(leechSword, ItemLeechSword.name);
		GameRegistry.registerItem(bloodBottle, ItemBloodBottle.name);
		GameRegistry.registerItem(vampireFang, ItemVampireFang.NAME);
		GameRegistry.registerItem(pitchfork, ItemPitchfork.name);
		// GameRegistry.registerItem(torch, ItemTorch.name);
		GameRegistry.registerItem(humanHeart, ItemHumanHeart.name);
		GameRegistry.registerItem(pureBlood, ItemPureBlood.name);
		GameRegistry.registerItem(sunscreen, ItemSunscreen.name);
		GameRegistry.registerItem(coffin, ItemCoffin.name);
		GameRegistry.registerItem(vampireHelmet, vampireHelmet.getRegisterItemName());
		GameRegistry.registerItem(vampireChestplate, vampireChestplate.getRegisterItemName());
		GameRegistry.registerItem(vampireLeggings, vampireLeggings.getRegisterItemName());
		GameRegistry.registerItem(vampireBoots, vampireBoots.getRegisterItemName());
		GameRegistry.registerItem(gemOfBinding, ItemGemOfBinding.name);
		GameRegistry.registerItem(minionNameTag, ItemMinionNameTag.name);
		GameRegistry.registerItem(bloodEye,ItemBloodEye.name);
		GameRegistry.registerItem(tent, ItemTent.name);
		GameRegistry.registerItem(weakHumanHeart, ItemWeakHumanHeart.name);
		GameRegistry.registerItem(weakVampireFang, ItemWeakVampireFang.name);
	}


	@SideOnly(Side.CLIENT)
	private static List<Item> itemsToRegister;
	@SideOnly(Side.CLIENT)
	public static void preInitClient(){
		itemsToRegister=new ArrayList<Item>();
		for(Field f:ModItems.class.getDeclaredFields()){
			if(Item.class.isAssignableFrom(f.getType())){
				try{
					Item item= (Item) f.get(null);
					if(item instanceof IItemRegistrable){
						itemsToRegister.add(item);
						if(item instanceof IItemRegistrable.IItemMetaRegistrable){
							IItemRegistrable.IItemMetaRegistrable item_meta= (IItemRegistrable.IItemMetaRegistrable) item;
							List<String> names=new ArrayList<String>();
							for(int i=0;i<item_meta.getMetaCount();i++){
								Helper.IntToString matcher=item_meta.getMetaMatcher();
								names.add(getPrefix(item) + item_meta.getBaseName() + (matcher == null ? i : matcher.match(i)));
							}
							ModelBakery.addVariantName(item, names.toArray(new String[names.size()]));
							//Logger.t("Add variants for %s: %s",item, ArrayUtils.toString(names.toArray(new String[names.size()])));
						}
						else if(item instanceof IItemRegistrable.IItemFlexibleRegistrable){
							String[] variants=((IItemRegistrable.IItemFlexibleRegistrable) item).getModelVariants();
							ModelBakery.addVariantName(item, Helper.prefix(getPrefix(item), variants));
							//Logger.t("Add variants for %s: %s", item,ArrayUtils.toString(Helper.prefix(getPrefix(item),variants)));
						}
					}
				} catch (IllegalAccessException e) {
					Logger.e("ModItems","Failed to retrieve item for %s",f);
				}
			}
		}
	}

	private static String getPrefix(Item i){
		return (i instanceof IVanillaExt)?"":(REFERENCE.MODID+":");
	}

	@SideOnly(Side.CLIENT)
	public static void initClient(){
		ItemModelMesher mesher= Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		if(spawn_egg!=null){
			itemsToRegister.add(spawn_egg);
		}
		for(Item item:itemsToRegister){
			if(item instanceof IItemRegistrable.IItemMetaRegistrable){
				IItemRegistrable.IItemMetaRegistrable item_meta= (IItemRegistrable.IItemMetaRegistrable) item;
				for(int i=0;i<item_meta.getMetaCount();i++){
					Helper.IntToString matcher=item_meta.getMetaMatcher();
					ModelResourceLocation res = new ModelResourceLocation(getPrefix(item)+item_meta.getBaseName()+(matcher==null?i:matcher.match(i)),"inventory");
					mesher.register(item, i, res);
					//Logger.t("Register meta item %s wit %d %s",item,i,res);
				}
			}
			else if(item instanceof IItemRegistrable.IItemFlexibleRegistrable){
				final Helper.StackToString matcher=((IItemRegistrable.IItemFlexibleRegistrable) item).getModelMatcher();
				final String pref=getPrefix(item);
				mesher.register(item, new ItemMeshDefinition() {
					@Override
					public ModelResourceLocation getModelLocation(ItemStack stack) {
						return new ModelResourceLocation(pref+matcher.match(stack),"inventory");
					}
				});
				Logger.t("Registering %s with pref %s",item,pref);
			}
			else{
				mesher.register(item,0,new ModelResourceLocation(getPrefix(item)+((IItemRegistrable)item).getBaseName(),"inventory"));
			}
		}


	}


	public static void registerRecipes() {
		GameRegistry.addRecipe(new ItemStack(bloodBottle, 1, 0), "   ", "XYX", " X ", 'X', Blocks.glass, 'Y', Items.rotten_flesh);
		GameRegistry.addRecipe(new ItemStack(leechSword, 1), "XYX", "XYX", " Z ", 'X', vampireFang, 'Y', Items.iron_ingot, 'Z', Items.stick);
		GameRegistry.addRecipe(new ItemStack(sunscreen, 1), "XYX", "YZY", "XYX", 'X', ModBlocks.vampireFlower, 'Y', Items.gold_nugget, 'Z', humanHeart);
		GameRegistry.addRecipe(new ItemStack(coffin, 1), "XXX", "Y Y", "XXX", 'X', Blocks.planks, 'Y', Blocks.wool);
		GameRegistry
				.addRecipe(new ItemStack(vampireHelmet, 1), "XXX", "YYY", "YZY", 'X', ModBlocks.vampireFlower, 'Y', Items.iron_ingot, 'Z', new ItemStack(bloodBottle, 1, ItemBloodBottle.MAX_BLOOD));
		GameRegistry.addRecipe(new ItemStack(vampireBoots, 1), "YZY", "Y Y", 'Y', Items.iron_ingot, 'Z', new ItemStack(bloodBottle, 1, ItemBloodBottle.MAX_BLOOD));
		GameRegistry.addRecipe(new ItemStack(vampireLeggings, 1), "YYY", "YZY", "Y Y", 'Y', Items.iron_ingot, 'Z', new ItemStack(bloodBottle, 1, ItemBloodBottle.MAX_BLOOD));
		GameRegistry.addRecipe(new ItemStack(vampireChestplate, 1), "YZY", "YYY", "YYY", 'Y', Items.iron_ingot, 'Z', new ItemStack(bloodBottle, 1, ItemBloodBottle.MAX_BLOOD));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.glass_bottle), new ItemStack(bloodBottle, 1, 0));
		GameRegistry.addRecipe(new ItemStack(gemOfBinding, 1), " X ", "YZY", " V ", 'X', ModItems.humanHeart, 'Y', new ItemStack(bloodBottle, 1, ItemBloodBottle.MAX_BLOOD), 'Z', Items.diamond, 'V',
				ModBlocks.vampireFlower);
		GameRegistry.addShapelessRecipe(new ItemStack(minionNameTag), Items.paper, ModBlocks.vampireFlower);
		GameRegistry.addShapelessRecipe(new ItemStack(bloodEye),Items.ender_eye,new ItemStack(bloodBottle,1,ItemBloodBottle.MAX_BLOOD));
		GameRegistry.addShapelessRecipe(new ItemStack(humanHeart), ModItems.weakHumanHeart, ModItems.weakHumanHeart, ModItems.weakHumanHeart, ModItems.weakHumanHeart, ModItems.weakHumanHeart, ModItems.weakHumanHeart);
		GameRegistry.addShapelessRecipe(new ItemStack(vampireFang), ModItems.weakVampireFang, ModItems.weakVampireFang, ModItems.weakVampireFang, ModItems.weakVampireFang, ModItems.weakVampireFang, ModItems.weakVampireFang);
	}
}
