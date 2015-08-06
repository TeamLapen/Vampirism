package de.teamlapen.vampirism.guide;

import amerifrance.guideapi.api.GuideRegistry;
import amerifrance.guideapi.api.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.abstraction.EntryAbstract;
import amerifrance.guideapi.api.abstraction.IPage;
import amerifrance.guideapi.api.base.Book;
import amerifrance.guideapi.api.util.BookBuilder;
import amerifrance.guideapi.api.util.PageHelper;
import amerifrance.guideapi.categories.CategoryItemStack;
import amerifrance.guideapi.entries.EntryUniText;
import amerifrance.guideapi.pages.*;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar4;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.REFERENCE.KEY;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class VampirismGuide{
	public static Book vampirismGuide;
	private static List recipeList;
	public static List<CategoryAbstract> categories = new ArrayList<CategoryAbstract>();
	private static HashMap<String, EntryAbstract> linkedEntries = new HashMap<String, EntryAbstract>();

	public static EntryAbstract getLinkedEntry(String identifier) {
		return linkedEntries.get(identifier);
	}

	/**
	 * Adds a entry and it's identifier to the link list, which is used to bind the entries, after the guide is fully registered.
	 *
	 * @param identifier
	 * @param entry
	 * @return
	 */
	private static EntryAbstract addLink(String identifier, EntryAbstract entry) {
		linkedEntries.put(identifier, entry);
		return entry;
	}
	public static void registerGuide(){
		recipeList=CraftingManager.getInstance().getRecipeList();
		registerGettingStarted();
		registerItemsAndBlocks();
		registerLevels();
		registerMobs();
		registerVP();
		registerVampireLord();
		BookBuilder builder = new BookBuilder();
		builder.setCategories(categories).setUnlocBookTitle("guide.vampirism.book.title").setUnlocWelcomeMessage("guide.vampirism.welcomeMessage").setUnlocDisplayName("guide.vamprism.book.name").setBookColor(new Color(137,8,163)).setAuthor("--Team Lapen");
		builder.setSpawnWithBook(true);
		vampirismGuide = builder.build();
		GuideRegistry.registerBook(vampirismGuide);
	}
	
	private static void registerGettingStarted(){
		List<EntryAbstract> entries = new ArrayList<EntryAbstract>();
		
		entries.add(createUnlocLongTextEntry("guide.vampirism.gettingStarted.overview.title", "guide.vampirism.gettingStarted.overview.text"));
		
		entries.add(createUnlocLongTextEntry("guide.vampirism.gettingStarted.firstSteps.title", "guide.vampirism.gettingStarted.firstSteps.text"));
		
		entries.add(createUnlocLongTextEntry("guide.vampirism.gettingStarted.asAVampire.title","guide.vampirism.gettingStarted.asAVampire.text"));
		
		
		categories.add(new CategoryItemStack(entries, "guide.vampirism.gettingStarted.category", new ItemStack(ModItems.bloodBottle, 1, ItemBloodBottle.MAX_BLOOD)));
	}
	
	private static void registerItemsAndBlocks(){
		List<EntryAbstract> entries = new ArrayList<EntryAbstract>();
		
		ArrayList<IPage> found=new ArrayList<IPage>();
		found.add(new PageUnlocItemStack("guide.vampirism.itemsAndBlocks.found.vampireFang",ModItems.vampireFang));
		found.add(new PageUnlocItemStack("guide.vampirism.itemsAndBlocks.found.humanHearth",ModItems.humanHeart));
		found.add(new PageUnlocItemStack("guide.vampirism.itemsAndBlocks.found.vampireFlower",ModBlocks.vampireFlower));
		found.add(new PageUnlocItemStack("guide.vampirism.itemsAndBlocks.found.bloodAltar1",ModBlocks.bloodAltar1));
		found.add(new PageUnlocItemStack("guide.vampirism.itemsAndBlocks.found.pureBlood",ModItems.pureBlood));
		entries.add(new EntryUniText(found,"guide.vampirism.itemsAndBlocks.found.title"));
		
		String bBottle=locAndFormat("guide.vampirism.itemsAndBlocks.bBottle.text", VampirismMod.proxy.getKey(KEY.AUTO));
		entries.add(createCraftableStackEntryLoc(new ItemStack(ModItems.bloodBottle),bBottle));
		
		entries.add(createCraftableStackEntry(new ItemStack(ModItems.sunscreen),"guide.vampirism.itemsAndBlocks.sunscreen.text"));
		
		ArrayList<IPage> armor = new ArrayList<IPage>();
		armor.add(new PageUnlocText("guide.vampirism.itemsAndBlocks.armor.text"));
		armor.add(new PageIRecipe(getRecipe(ModItems.vampireHelmet)));
		armor.add(new PageIRecipe(getRecipe(ModItems.vampireChestplate)));
		armor.add(new PageIRecipe(getRecipe(ModItems.vampireLeggings)));
		armor.add(new PageIRecipe(getRecipe(ModItems.vampireBoots)));
		entries.add(new EntryUniText(armor,"guide.vampirism.itemsAndBlocks.armor.title"));
		
		ArrayList<IPage> altar = new ArrayList<IPage>();
		altar.add(new PageLocText(locAndFormat("guide.vampirism.itemsAndBlocks.altars.altar2.text", TileEntityBloodAltar2.MIN_LEVEL, TileEntityBloodAltar2.MAX_LEVEL)));
		altar.add(new PageIRecipe(getRecipe(ModBlocks.bloodAltar2)));
		altar.add(new PageUnlocText(locAndFormat("guide.vampirism.itemsAndBlocks.altars.altar4.text", loc(ModBlocks.bloodAltar4.getUnlocalizedName() + ".name"), TileEntityBloodAltar4.MIN_LEVEL)));
		altar.add(new PageIRecipe(getRecipe(ModBlocks.bloodAltar4)));
		altar.add(new PageIRecipe(getRecipe(ModBlocks.bloodAltar4Tip)));
		entries.add(new EntryUniText(altar, "guide.vampirism.itemsAndBlocks.altars.title"));

		entries.add(createCraftableStackEntry(new ItemStack(ModItems.coffin), "guide.vampirism.itemsAndBlocks.coffin.text"));

		EntryAbstract gemOfBinding = createCraftableStackEntry(new ItemStack(ModItems.gemOfBinding), "guide.vampirism.itemsAndBlocks.used_for_minions");
		addLink("items_gemOfBinding", gemOfBinding);
		entries.add(gemOfBinding);

		EntryAbstract nameTag = createCraftableStackEntry(new ItemStack(ModItems.minionNameTag), "guide.vampirism.itemsAndBlocks.used_for_minions");
		addLink("items_minion_name_tag", nameTag);
		entries.add(nameTag);

		EntryAbstract bloodEye = createCraftableStackEntry(new ItemStack(ModItems.bloodEye), "guide.vampirism.itemsAndBlocks.blood_eye.text");
		addLink("items_blood_eye", bloodEye);
		entries.add(bloodEye);

		
		categories.add(new CategoryItemStack(entries,"guide.vampirism.itemsAndBlocks.category",new ItemStack(ModItems.leechSword,1)));
	}
	
	private static void registerLevels(){
		List<EntryAbstract> entries = new ArrayList<EntryAbstract>();
		
		entries.add(createUnlocLongTextEntry("guide.vampirism.levels.introduction.title", "guide.vampirism.levels.introduction.text"));
		
		String at2t=locAndFormat("guide.vampirism.levels.altar2.title",TileEntityBloodAltar2.MIN_LEVEL,TileEntityBloodAltar2.MAX_LEVEL);
		ArrayList<IPage> pagesAt2 = new ArrayList<IPage>();
		String at2=loc("guide.vampirism.levels.altar2.text");
		at2+="\n";
		for(int i=TileEntityBloodAltar2.MIN_LEVEL;i<=TileEntityBloodAltar2.MAX_LEVEL;i++){
			at2+=loc("text.vampirism.entity_level")+" "+i+": "+BALANCE.LEVELING.A2_getRequiredBlood(i)+" "+loc("text.vampirism.blood")+"\n";
		}
		pagesAt2.add(new PageIRecipe(getRecipe(ModBlocks.bloodAltar2)));
		pagesAt2.addAll(PageHelper.pagesForLongText(at2));
		entries.add(new EntryUniText(pagesAt2,at2t));
		
		String at4t=locAndFormat("guide.vampirism.levels.altar4.title",TileEntityBloodAltar4.MIN_LEVEL);
		ArrayList<IPage> pagesAt4 = new ArrayList<IPage>();
		pagesAt4.add(new PageIRecipe(getRecipe(ModBlocks.bloodAltar4)));
		pagesAt4.add(new PageIRecipe(getRecipe(ModBlocks.bloodAltar4Tip)));
		pagesAt4.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.levels.altar4.text")));
		pagesAt4.add(createItemRequirementsAltar4());
		pagesAt4.add(createStructureRequirementsAltar4());
		pagesAt4.add(new PageUnlocImage("guide.vampirism.levels.altar4.structure1.name",new ResourceLocation(REFERENCE.MODID+":guide/screenshots/altar4_structure1.png"),false));
		pagesAt4.add(new PageUnlocImage("guide.vampirism.levels.altar4.structure2.name",new ResourceLocation(REFERENCE.MODID+":guide/screenshots/altar4_structure2.png"), false));
		entries.add(addLink("level_altar_4", new EntryUniText(pagesAt4, at4t)));
		
		categories.add(new CategoryItemStack(entries,"guide.vampirism.levels.category",new ItemStack(ModBlocks.bloodAltar2)));
	}
	
	private static IPage createItemRequirementsAltar4(){
		PageTable.Builder builder=new PageTable.Builder(3);
		builder.addUnlocLine("text.vampirism.entity_level", ModItems.pureBlood.getUnlocalizedName() + ".name", ModItems.humanHeart.getUnlocalizedName() + ".name");
		builder.addLine(4, 0, 5);
		builder.addLine(5,"1 Purity(1)",0);
		builder.addLine(6, "1 Purity(1)", 5);
		builder.addLine(7, "1 Purity(2)", 0);
		builder.addLine(8, "1 Purity(2)", 5);
		builder.addLine(9, "1 Purity(3)", 5);
		builder.addLine(10, "1 Purity(3)", 5);
		builder.addLine(11, "1 Purity(4)", 10);
		builder.addLine(12, "1 Purity(4)", 5);
		builder.addLine(13, "1 Purity(5)", 0);
		builder.setHeadline(loc("guide.vampirism.levels.altar4.item_req"));
		return builder.build();
	}
	
	private static IPage createStructureRequirementsAltar4(){
		PageTable.Builder builder=new PageTable.Builder(3);
		builder.addUnlocLine("text.vampirism.entity_level","text.vampirism.structure","text.vampirism.pillar_blocks");
		builder.addLine(4,1,Blocks.stonebrick.getLocalizedName());
		builder.addLine(5,1,Blocks.stonebrick.getLocalizedName());
		builder.addLine(6,1,Blocks.stonebrick.getLocalizedName());
		builder.addLine(7,1,Blocks.iron_block.getLocalizedName());
		builder.addLine(8,1,Blocks.iron_block.getLocalizedName());
		builder.addLine(9,2,Blocks.iron_block.getLocalizedName());
		builder.addLine(10,2,Blocks.iron_block.getLocalizedName());
		builder.addLine(11,2,Blocks.gold_block.getLocalizedName());
		builder.addLine(12,2,Blocks.gold_block.getLocalizedName());
		builder.addLine(13,2,Blocks.gold_block.getLocalizedName());
		builder.setHeadline(loc("guide.vampirism.levels.altar4.struc_req"));
		return builder.build();
	}

	private static void registerMobs(){
		List<EntryAbstract> entries = new ArrayList<EntryAbstract>();

		ArrayList<IPage> hunter =new ArrayList<IPage>();
		hunter.add(new PageImage(new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/hunter.png")));
		hunter.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.mobs.hunter.text", BALANCE.VAMPIRE_HUNTER_ATTACK_LEVEL)));
		entries.add(new EntryUniText(hunter, "guide.vampirism.mobs.hunter.title"));

		ArrayList<IPage> vampire_npc =new ArrayList<IPage>();
		vampire_npc.add(new PageImage(new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/vampire.png")));
		vampire_npc.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.mobs.vampire.text", BALANCE.VAMPIRE_FRIENDLY_LEVEL)));
		entries.add(new EntryUniText(vampire_npc, "guide.vampirism.mobs.vampire.title"));

		ArrayList<IPage> vampire_lord =new ArrayList<IPage>();
		vampire_lord.add(new PageImage(new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/vampire_lord.png")));
		vampire_lord.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.mobs.vampire_lord.text", true)));
		entries.add(new EntryUniText(vampire_lord, "guide.vampirism.mobs.vampire_lord.title"));

		ArrayList<IPage> vampire_minion =new ArrayList<IPage>();
		vampire_minion.add(new PageImage(new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/vampire_minion.png")));
		vampire_minion.add(new PageImage(new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/minion.png")));
		vampire_minion.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.mobs.vampire_minion.text", true)));
		entries.add(new EntryUniText(vampire_minion, "guide.vampirism.mobs.vampire_minion.title"));

		ArrayList<IPage> ghost =new ArrayList<IPage>();
		ghost.add(new PageImage(new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/ghost.png")));
		ghost.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.mobs.ghost.text", true)));

		entries.add(new EntryUniText(ghost, "guide.vampirism.mobs.ghost.title"));

		categories.add(new CategoryItemStack(entries, "guide.vampirism.mobs.category", new ItemStack(ModItems.vampireFang)));
	}

	private static void registerVP(){
		List<EntryAbstract> entries =new ArrayList<EntryAbstract>();

		ArrayList<IPage> general=new ArrayList<IPage>();
		String s=locAndFormat("guide.vampirism.vplayer.general.text", REFERENCE.HIGHEST_REACHABLE_LEVEL, VampirismMod.proxy.getKey(KEY.VISION), VampirismMod.proxy.getKey(KEY.SUCK),
				VampirismMod.proxy.getKey(KEY.AUTO));
		if(BALANCE.VAMPIRE_PLAYER_LOOSE_LEVEL){
			s+="\n\n"+loc("guide.vampirism.vplayer.general.loose_level");
		}
		general.addAll(PageHelper.pagesForLongText(s));
		entries.add(new EntryUniText(general, "guide.vampirism.vplayer.general.title"));

		ArrayList<IPage> sun_damage=new ArrayList<IPage>();
		sun_damage.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.vplayer.sun_damage.text")));
		entries.add(new EntryUniText(sun_damage, "guide.vampirism.vplayer.sun_damage.title"));

		ArrayList<IPage> skills=new ArrayList<IPage>();
		skills.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.vplayer.skills.text")));
		skills.add(new PageLocImage(loc("guide.vampirism.vplayer.skills.menu_text"), new ResourceLocation(REFERENCE.MODID + ":guide/screenshots/skills_menu.png"), false));
		skills.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.vplayer.skills.bat_transformation")));
		skills.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.vplayer.skills.revive_fallen")));
		skills.add((new PageLocText(loc("guide.vampirism.vplayer.skills.end", true))));
		entries.add(addLink("vampire_skills", new EntryUniText(skills, "guide.vampirism.vplayer.skills.title")));

		
		categories.add(new CategoryItemStack(entries,"guide.vampirism.vplayer.category",new ItemStack(Items.skull, 1, 3)));
	}

	private static void registerVampireLord() {
		List<EntryAbstract> entries = new ArrayList<EntryAbstract>();
		ArrayList<IPage> intro = new ArrayList<IPage>();
		intro.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.vlord.introduction.text", true)));
		entries.add(new EntryUniText(intro, "guide.vampirism.vlord.introduction.title"));

		ArrayList<IPage> becomeOne = new ArrayList<IPage>();
		becomeOne.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.vlord.become.text", REFERENCE.HIGHEST_REACHABLE_LEVEL, loc(ModItems.bloodEye.getUnlocalizedName() + ".name"))));
		addLinksToPages(becomeOne, "items_blood_eye");
		entries.add(new EntryUniText(becomeOne, "guide.vampirism.vlord.become.title"));

		ArrayList<IPage> about = new ArrayList<IPage>();
		about.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.vlord.about.text", true)));
		addLinksToPages(about, "lord_skills", "lord_minions", "lord_multiplayer");
		entries.add(new EntryUniText(about, "guide.vampirism.vlord.about.title"));

		ArrayList<IPage> multiplayer = new ArrayList<IPage>();
		multiplayer.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.vlord.multiplayer.text", true)));
		entries.add(addLink("lord_multiplayer", new EntryUniText(multiplayer, "guide.vampirism.vlord.multiplayer.title")));

		ArrayList<IPage> skills = new ArrayList<IPage>();
		skills.addAll(PageHelper.pagesForLongText(loc("guide.vampirism.vlord.skills.text", true)));
		addLinksToPages(skills, "vampire_skills");
		entries.add(addLink("lord_skills", new EntryUniText(skills, "guide.vampirism.vlord.skills.title")));

		ArrayList<IPage> minions = new ArrayList<IPage>();
		minions.addAll(PageHelper.pagesForLongText(locAndFormat("guide.vampirism.vlord.minions.text", VampirismMod.proxy.getKey(KEY.MINION_CONTROL), loc(ModItems.gemOfBinding.getUnlocalizedName() + ".name"))));
		addLinksToPages(minions, "items_gemOfBinding", "items_minion_name_tag");
		entries.add(addLink("lord_minions", new EntryUniText(minions, "guide.vampirism.vlord.minions.title")));

		categories.add(new CategoryItemStack(entries, "guide.vampirism.vlord.category", new ItemStack(ModItems.gemOfBinding)));

	}

	/**
	 * Simply translate the given string
	 * @param unLoc Unlocalized String
	 * @return
	 */
	private static String loc(String unLoc){
		return StatCollector.translateToLocal(unLoc);
	}

	/**
	 * Translatets the string and optionally replaces e.g. /n by \n
	 * @param unLoc
	 * @param replace
	 * @return
	 */
	private static String loc(String unLoc,boolean replace){
		String s=loc(unLoc);
		if(replace){
			s=s.replaceAll("/n","\n");
		}
		return s;
	}
	/**
	 * Firstly translates the given string, replaces e.g. /n by \n and then uses String.format with the given objects on it
	 * @param unLoc
	 * @param objects
	 * @return
	 */
	private static String locAndFormat(String unLoc,Object...objects){
		return String.format(loc(unLoc,true),objects);
	}
	
	/**
	 * Creates a item entry including a localized text and the crafting recipe
	 * @param item
	 * @param locText
	 * @return
	 */
	private static EntryAbstract createCraftableStackEntryLoc(ItemStack item,String locText){
		ArrayList<IPage> pages=new ArrayList<IPage>();
		pages.addAll(PageHelper.pagesForLongText(locText,item));
		pages.add(new PageIRecipe(getRecipe(item)));
		return new EntryUniText(pages,item.getUnlocalizedName()+".name");
	}
	
	/**
	 * Creates a simple text entry with unLoc title and long unlocText
	 * @param unlocTitle
	 * @param unlocText
	 * @return
	 */
	private static EntryAbstract createUnlocLongTextEntry(String unlocTitle,String unlocText){
		ArrayList<IPage> pages=new ArrayList<IPage>();
		pages.addAll(PageHelper.pagesForLongText(loc(unlocText)));
		return new EntryUniText(pages,unlocTitle);
	}
	
	/**
	 * Creates a item entry including an unlocalized text and the crafting recipe
	 * @param item
	 * @param unlocText
	 * @return
	 */
	private static EntryAbstract createCraftableStackEntry(ItemStack item,String unlocText){
		return createCraftableStackEntryLoc(item,loc(unlocText));
	}
	
	
	private static IRecipe getRecipe(Item item){
		return getRecipe(new ItemStack(item, 1));
	}
	private static IRecipe getRecipe(Block block){
		return getRecipe(new ItemStack(block,1));
	}
	private static IRecipe getRecipe(ItemStack stack)
	{		
		for(Object obj : recipeList)
		{
			IRecipe recipe = (IRecipe)obj;
			if(recipe.getRecipeOutput() != null && stack.isItemEqual(recipe.getRecipeOutput()))
			{
				return recipe;
			}
		}
		
		return null;
	}

	/**
	 * Adds links to all included pages by replacing them with @link{PageHolderWithLinks}
	 *
	 * @param pages
	 * @param links
	 */
	private static void addLinksToPages(ArrayList<IPage> pages, String... links) {
		ListIterator<IPage> it = pages.listIterator();
		while (it.hasNext()) {
			PageHolderWithLinks newP = new PageHolderWithLinks(it.next());
			for (String s : links) {
				newP.addLink(s);
			}
			it.set(newP);
		}
	}
}
