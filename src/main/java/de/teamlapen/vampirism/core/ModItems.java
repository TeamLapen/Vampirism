package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.inventory.HunterWeaponCraftingManager;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

/**
 * Handles all item registrations and reference.
 */
public class ModItems {

    public static ItemVampireFang vampireFang;
    public static ItemHumanHeart humanHeart;
    public static ItemHumanHeartWeak humanHeartWeak;
    public static ItemBloodBottle bloodBottle;
    public static ItemTent itemTent;
    public static ItemBattleAxe battleAxe;
    public static ItemCoffin itemCoffin;
    public static ItemPureBlood pureBlood;
    public static ItemHunterIntel hunterIntel;
    public static ItemGarlic itemGarlic;
    public static ItemInjection injection;
    public static ItemMedChair itemMedChair;
    public static ItemPitchfork pitchfork;
    public static ItemSimpleCrossbow basicCrossbow;
    public static ItemDoubleCrossbow basicDoubleCrossbow;
    public static ItemSimpleCrossbow enhancedCrossbow;
    public static ItemDoubleCrossbow enhancedDoubleCrossbow;
    public static ItemCrossbowArrow crossbowArrow;
    public static ItemStake stake;
    public static ItemVampireBloodBottle vampireBlood;
    public static ItemBloodPotion bloodPotion;
    public static ItemTechCrossbow basicTechCrossbow;
    public static ItemTechCrossbow enhancedTechCrossbow;
    public static VampirismItem techCrossbowAmmoPackage;
    public static ItemVampireBook vampireBook;

    public static ItemArmorOfSwiftness armorOfSwiftness_helmet;
    public static ItemArmorOfSwiftness armorOfSwiftness_chest;
    public static ItemArmorOfSwiftness armorOfSwiftness_legs;
    public static ItemArmorOfSwiftness armorOfSwiftness_boots;

    public static ItemHunterHat hunterHat0;
    public static ItemHunterHat hunterHat1;

    public static ItemHunterAxe hunterAxe;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerItems();
                break;
            case INIT:
                registerCraftingRecipes();
                break;
            default:
                break;
        }

    }

    private static void registerCraftingRecipes() {
        HunterWeaponCraftingManager weaponCraftingManager = HunterWeaponCraftingManager.getInstance();

        if (!Configs.autoConvertGlasBottles) {
            GameRegistry.addRecipe(new ItemStack(bloodBottle, 1, 0), "   ", "XYX", " X ", 'X', Blocks.GLASS, 'Y', Items.ROTTEN_FLESH);
        }
        GameRegistry.addShapelessRecipe(new ItemStack(Items.GLASS_BOTTLE), new ItemStack(bloodBottle, 1, 0));
        GameRegistry.addRecipe(new ItemStack(injection, 1, 0), " X ", " X ", " Y ", 'X', Blocks.GLASS, 'Y', Blocks.GLASS_PANE);
        GameRegistry.addShapelessRecipe(new ItemStack(injection, 1, ItemInjection.META_GARLIC), new ItemStack(injection, 1, 0), ModItems.itemGarlic);
        GameRegistry.addShapelessRecipe(new ItemStack(injection, 1, ItemInjection.META_SANGUINARE), new ItemStack(injection, 1, 0), vampireFang, vampireFang, vampireFang, vampireFang, vampireFang, vampireFang, vampireFang, vampireFang);
        GameRegistry.addRecipe(new ItemStack(itemMedChair), "XYX", "XXX", "XZX", 'X', Items.IRON_INGOT, 'Y', Blocks.WOOL, 'Z', Items.GLASS_BOTTLE);
        GameRegistry.addRecipe(new ItemStack(itemCoffin), "XXX", "YYY", "XXX", 'X', Blocks.PLANKS, 'Y', Blocks.WOOL);
        weaponCraftingManager.addRecipe(new ItemStack(ModItems.basicCrossbow), 1, (ISkill<IHunterPlayer>) null, 1, "YXXY", " ZZ ", " ZZ ", 'X', Items.IRON_INGOT, 'Y', Items.STRING, 'Z', Blocks.PLANKS);
        weaponCraftingManager.addRecipe(new ItemStack(ModItems.basicDoubleCrossbow), 1, HunterSkills.doubleCrossbow, 1, "YXXY", "YXXY", " ZZ ", " ZZ ", 'X', Items.IRON_INGOT, 'Y', Items.STRING, 'Z', Blocks.PLANKS);
        weaponCraftingManager.addRecipe(new ItemStack(ModItems.enhancedCrossbow), 1, HunterSkills.enhancedCrossbow, 2, "YXXY", " XX ", " XX", 'X', Items.IRON_INGOT, 'Y', Items.STRING);
        weaponCraftingManager.addRecipe(new ItemStack(ModItems.enhancedDoubleCrossbow), 1, new ISkill[]{HunterSkills.doubleCrossbow, HunterSkills.enhancedCrossbow}, 3, "YXXY", "YXXY", " XX ", " XX ", 'X', Items.IRON_INGOT, 'Y', Items.STRING);
        GameRegistry.addShapedRecipe(new ItemStack(ModItems.crossbowArrow, 6), "X", "Y", 'X', Items.IRON_INGOT, 'Y', Items.STICK);
        weaponCraftingManager.addRecipe(ItemCrossbowArrow.setType(new ItemStack(crossbowArrow, 2), ItemCrossbowArrow.EnumArrowType.VAMPIRE_KILLER), 1, (ISkill<IHunterPlayer>) null, 1, " X  ", "XYX ", " Z  ", " W  ", 'X', itemGarlic, 'Y', Items.GOLD_INGOT, 'Z', Items.STICK, 'W', Items.FEATHER);
        weaponCraftingManager.addRecipe(new ItemStack(techCrossbowAmmoPackage), 1, (ISkill<IHunterPlayer>) null, 1, " XZ ", "YYYY", "YYYY", "YYYY", 'X', Items.IRON_INGOT, 'Y', crossbowArrow, 'Z', Blocks.PLANKS);
        RecipeSorter.register("vampirism:shapelessFillCrossbow", ItemTechCrossbow.ShapelessFillRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        GameRegistry.addRecipe(new ItemTechCrossbow.ShapelessFillRecipe(basicTechCrossbow, new ItemStack(techCrossbowAmmoPackage)));
        GameRegistry.addRecipe(new ItemTechCrossbow.ShapelessFillRecipe(enhancedTechCrossbow, new ItemStack(techCrossbowAmmoPackage)));
        weaponCraftingManager.addRecipe(new ItemStack(basicTechCrossbow), 1, HunterSkills.techWeapons, 5, "XYYX", "YZZY", " YY ", " YY ", 'X', Items.STRING, 'Y', Items.IRON_INGOT, 'Z', Items.DIAMOND);
        weaponCraftingManager.addRecipe(new ItemStack(enhancedTechCrossbow), 1, HunterSkills.techWeapons, 5, "XYYX", "YZZY", "YZZY", " YY ", 'X', Items.STRING, 'Y', Items.IRON_INGOT, 'Z', Items.DIAMOND);
        GameRegistry.addShapedRecipe(new ItemStack(stake), " X ", " Y ", " X ", 'X', Items.STICK, 'Y', Blocks.PLANKS);
        weaponCraftingManager.addRecipe(new ItemStack(pitchfork), 1, (ISkill<IHunterPlayer>) null, 0, "X X ", "YYY ", " Y  ", " Y  ", 'X', Items.IRON_INGOT, 'Y', Items.STICK);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_helmet), ItemArmorOfSwiftness.TYPE.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "XXXX", "XYYX", "XZZX", "    ", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_chest), ItemArmorOfSwiftness.TYPE.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "XZZX", "XXXX", "XYYX", "XXXX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_legs), ItemArmorOfSwiftness.TYPE.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "XXXX", "XYYX", "XZZX", "X  X", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_boots), ItemArmorOfSwiftness.TYPE.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "    ", "XXXX", "XYYX", "XZZX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_helmet), ItemArmorOfSwiftness.TYPE.ENHANCED), 1, HunterSkills.enhancedArmor, 3, "XXXX", "XYYX", "XZZX", "    ", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_chest), ItemArmorOfSwiftness.TYPE.NORMAL), 1, HunterSkills.enhancedArmor, 3, "XZZX", "XXXX", "XYYX", "XXXX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_legs), ItemArmorOfSwiftness.TYPE.NORMAL), 1, HunterSkills.enhancedArmor, 3, "XXXX", "XYYX", "XZZX", "X  X", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
        weaponCraftingManager.addRecipe(ItemArmorOfSwiftness.setType(new ItemStack(armorOfSwiftness_boots), ItemArmorOfSwiftness.TYPE.NORMAL), 1, HunterSkills.enhancedArmor, 3, "    ", "XXXX", "XYYX", "XZZX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
        weaponCraftingManager.addRecipe(new ItemStack(ModItems.hunterHat0), 1, (ISkill<IHunterPlayer>) null, 0, "    ", " XX ", "YYYY", "    ", 'X', new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLACK.getMetadata()), 'Y', Items.IRON_INGOT);
        weaponCraftingManager.addRecipe(new ItemStack(ModItems.hunterHat1), 1, (ISkill<IHunterPlayer>) null, 0, "    ", " XX ", " XX ", "YYYY", 'X', new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLACK.getMetadata()), 'Y', Items.IRON_INGOT);
        weaponCraftingManager.addRecipe(ItemHunterAxe.setType(new ItemStack(ModItems.hunterAxe), ItemHunterAxe.TYPE.NORMAL), 1, (ISkill<IHunterPlayer>) null, 3, "XYX ", "XYX ", "XYX ", " Y  ", 'X', Items.IRON_INGOT, 'Y', Items.STICK);
        weaponCraftingManager.addRecipe(ItemHunterAxe.setType(new ItemStack(ModItems.hunterAxe), ItemHunterAxe.TYPE.ENHANCED), 1, (ISkill<IHunterPlayer>) null, 5, "XZX ", "XZX ", "XYX ", " Y  ", 'X', Items.IRON_INGOT, 'Y', Items.STICK, 'Z', Items.DIAMOND);

    }

    private static void registerItems() {
        vampireFang = registerItem(new ItemVampireFang());
        humanHeart = registerItem(new ItemHumanHeart());
        humanHeartWeak = registerItem(new ItemHumanHeartWeak());
        bloodBottle = registerItem(new ItemBloodBottle());
        itemTent = registerItem(new ItemTent());
        battleAxe = registerItem(new ItemBattleAxe());
        itemCoffin = registerItem(new ItemCoffin());
        pureBlood = registerItem(new ItemPureBlood());
        hunterIntel = registerItem(new ItemHunterIntel());
        itemGarlic = registerItem(new ItemGarlic());
        itemMedChair = registerItem(new ItemMedChair());
        injection = registerItem(new ItemInjection());
        pitchfork = registerItem(new ItemPitchfork());
        basicCrossbow = registerItem(new ItemSimpleCrossbow("basicCrossbow", 1, 20, 300));
        basicCrossbow.setEnchantability(Item.ToolMaterial.WOOD);
        basicDoubleCrossbow = registerItem(new ItemDoubleCrossbow("basicDoubleCrossbow", 1, 20, 300));
        basicDoubleCrossbow.setEnchantability(Item.ToolMaterial.WOOD);
        enhancedCrossbow = registerItem(new ItemSimpleCrossbow("enhancedCrossbow", 1.5F, 15, 350));
        enhancedCrossbow.setEnchantability(Item.ToolMaterial.IRON);
        enhancedDoubleCrossbow = registerItem(new ItemDoubleCrossbow("enhancedDoubleCrossbow", 1.5F, 15, 350));
        enhancedDoubleCrossbow.setEnchantability(Item.ToolMaterial.IRON);
        crossbowArrow = registerItem(new ItemCrossbowArrow());
        stake = registerItem(new ItemStake());
        vampireBlood = registerItem(new ItemVampireBloodBottle());
        bloodPotion = registerItem(new ItemBloodPotion());
        basicTechCrossbow = registerItem(new ItemTechCrossbow("basicTechCrossbow", 1.6F, 6, 300));
        basicTechCrossbow.setEnchantability(Item.ToolMaterial.DIAMOND);
        enhancedTechCrossbow = registerItem(new ItemTechCrossbow("enhancedTechCrossbow", 1.7F, 4, 450));
        enhancedTechCrossbow.setEnchantability(Item.ToolMaterial.DIAMOND);
        techCrossbowAmmoPackage = registerItem(new VampirismItem("techCrossbowAmmoPackage"));
        vampireBook = registerItem(new ItemVampireBook());

        armorOfSwiftness_helmet = registerItem(new ItemArmorOfSwiftness(EntityEquipmentSlot.HEAD));
        armorOfSwiftness_chest = registerItem(new ItemArmorOfSwiftness(EntityEquipmentSlot.CHEST));
        armorOfSwiftness_legs = registerItem(new ItemArmorOfSwiftness(EntityEquipmentSlot.LEGS));
        armorOfSwiftness_boots = registerItem(new ItemArmorOfSwiftness(EntityEquipmentSlot.FEET));

        hunterHat0 = registerItem(new ItemHunterHat(0));
        hunterHat1 = registerItem(new ItemHunterHat(1));

        hunterAxe = registerItem(new ItemHunterAxe());
    }

    private static <T extends Item> T registerItem(T item) {
        GameRegistry.register(item);
        return item;
    }
}
