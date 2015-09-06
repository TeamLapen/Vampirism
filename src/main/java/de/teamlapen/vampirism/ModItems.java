package de.teamlapen.vampirism;

import de.teamlapen.vampirism.block.BlockCastleSlab;
import de.teamlapen.vampirism.item.*;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public static ItemSlab castleSlabItem;
	public static ItemBloodEye bloodEye=new ItemBloodEye();
	public static ItemTent tent = new ItemTent();

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
		GameRegistry.registerItem((castleSlabItem=new ItemSlab(ModBlocks.castleSlab,ModBlocks.castleSlab,ModBlocks.doubleCastleSlab,false)), BlockCastleSlab.name);
		GameRegistry.registerItem(bloodEye,ItemBloodEye.name);
		GameRegistry.registerItem(tent, ItemTent.name);
		GameRegistry.registerItem(weakHumanHeart, ItemWeakHumanHeart.name);
		GameRegistry.registerItem(weakVampireFang, ItemWeakVampireFang.name);
	}

	@SideOnly(Side.CLIENT)
	public static void initClient(){
		ItemModelMesher mesher= Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		registerSimpleModels(mesher,bloodEye,coffin,gemOfBinding,humanHeart,minionNameTag,sunscreen,vampireFang,weakHumanHeart,weakVampireFang,pitchfork,vampireBoots,vampireChestplate,vampireHelmet,vampireLeggings);
		regWithMeta(mesher, ItemPureBlood.COUNT, "pureBlood_", pureBlood, null);
		regWithMeta(mesher, ItemBloodBottle.MAX_BLOOD, "blood_bottle_", bloodBottle, new Helper.IntToInt() {
			@Override
			public int match(int i) {
				return i/2;
			}
		});

	}

	@SideOnly(Side.CLIENT)
	private static void registerSimpleModels(ItemModelMesher mesher,Item... items){
		for(Item item:items){
			ModelResourceLocation res = new ModelResourceLocation(REFERENCE.MODID+":"+item.getUnlocalizedName().substring(5), "inventory");
			mesher.register(item,0,res);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void regWithMeta(ItemModelMesher mesher,int meta_count,String base_name,Item item,Helper.IntToInt matcher){
		for(int i=0;i<meta_count;i++){
			ModelResourceLocation res = new ModelResourceLocation(REFERENCE.MODID+":"+base_name+(matcher==null?i:matcher.match(i)));
			mesher.register(item,i,res);
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
