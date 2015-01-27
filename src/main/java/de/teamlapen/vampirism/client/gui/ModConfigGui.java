package de.teamlapen.vampirism.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.util.REFERENCE;

public class ModConfigGui extends GuiConfig{

	public ModConfigGui(GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(), REFERENCE.MODID,true,false, REFERENCE.NAME+" Config");
	}
	
	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.addAll(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_GENERAL)).getChildElements());
		list.addAll(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_VILLAGE)).getChildElements());
		list.add(new DummyConfigElement.DummyCategoryElement("balance","category.vampirism.balance",BalanceEntry.class));
		return list;
		
	}
	
	public static class BalanceEntry extends CategoryEntry{

		public BalanceEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}
		
		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(this.owningScreen,(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_BALANCE))).getChildElements(),this.owningScreen.modID,Configs.CATEGORY_BALANCE,false,false,REFERENCE.NAME+" Balance");
		}
		
	}

}
