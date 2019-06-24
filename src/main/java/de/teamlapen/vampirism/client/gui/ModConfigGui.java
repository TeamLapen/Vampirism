package de.teamlapen.vampirism.client.gui;

/*
 * Mod config gui
 *
 * TODO 1.13 migrate to new config system first
 */
/*
@OnlyIn(Dist.CLIENT)
public class ModConfigGui extends GuiConfig {//TODO GUI config
    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.addAll(new ConfigElement(Configs.getMainConfig().getCategory(Configs.CATEGORY_GENERAL)).getChildElements());
        list.add(new DummyConfigElement.DummyCategoryElement("worldgen", "category.vampirism.worldgen", new ConfigElement(Configs.getMainConfig().getCategory(Configs.CATEGORY_WORLDGEN)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement("village", "category.vampirism.village", new ConfigElement(Configs.getMainConfig().getCategory(Configs.CATEGORY_VILLAGE)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement("gui", "category.vampirism.gui", new ConfigElement(Configs.getMainConfig().getCategory(Configs.CATEGORY_GUI)).getChildElements()));
        list.add(new DummyConfigElement.DummyCategoryElement("balance", "category.vampirism.balance", BalanceEntry.class));
        list.add(new DummyConfigElement.DummyCategoryElement("disable", "category.vampirism.disable", new ConfigElement(Configs.getMainConfig().getCategory(Configs.CATEGORY_DISABLE)).getChildElements()));
        return list;
    }

    public ModConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), REFERENCE.MODID, true, true, REFERENCE.NAME, "Main Configuration");
    }

    public static class BalanceEntry extends GuiConfigEntries.CategoryEntry {

        @SuppressWarnings("rawtypes")
        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> list = new ArrayList<>();
            Collection<BalanceValues> categories = Balance.getCategories().values();
            for (BalanceValues values : categories) {
                list.add(createDummyElement(values));
            }

            return list;

        }

        private static DummyConfigElement.DummyCategoryElement createDummyElement(BalanceValues balance) {
            return new DummyConfigElement.DummyCategoryElement("balance_" + balance.getName(), "category.vampirism.balance_" + balance.getName(), new ConfigElement(balance.getConfigCategory()).getChildElements());
        }

        public BalanceEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen, getConfigElements(), this.owningScreen.modID, Configs.CATEGORY_BALANCE, true, true, this.owningScreen.title,
                    ((this.owningScreen.titleLine2 == null ? "" : this.owningScreen.titleLine2) + " > " + this.name));
        }

    }
}

 */
