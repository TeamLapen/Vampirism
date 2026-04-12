package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.tags.ModVillagerTradeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.VillagerTradeTags;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.concurrent.CompletableFuture;

import static de.teamlapen.vampirism.common.core.ModTrades.*;

public class ModVillageTradesTagProvider extends KeyTagProvider<VillagerTrade> {

    public ModVillageTradesTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.VILLAGER_TRADE, lookupProvider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        this.tag(ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_1)
                .add(VAMPIRE_EXPERT_1_EMERALD_MAP)
                .add(VAMPIRE_EXPERT_1_ORCHID_EMERALD)
                .add(VAMPIRE_EXPERT_1_HEART_PURE_BLOOD);

        this.tag(ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_2)
                .add(VAMPIRE_EXPERT_2_HEART_PURE_BLOOD)
                .add(VAMPIRE_EXPERT_2_HEART_INFUSED_IRON)
                .add(VAMPIRE_EXPERT_2_HEART_COFFIN)
                .add(VAMPIRE_EXPERT_2_HEART_CLOAK);

        this.tag(ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_3)
                .add(VAMPIRE_EXPERT_3_HEART_PURE_BLOOD)
                .add(VAMPIRE_EXPERT_3_HEART_INFUSED_IRON)
                .add(VAMPIRE_EXPERT_3_HEART_HEART_SEEKER)
                .add(VAMPIRE_EXPERT_3_HEART_HEART_STRIKER)
                .add(VAMPIRE_EXPERT_3_HEART_CLOAK)
                ;
        this.tag(ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_4)
                .add(VAMPIRE_EXPERT_4_HEART_PURE_BLOOD)
                .add(VAMPIRE_EXPERT_4_EMERALD_CRYPT_MAP)
                ;
        this.tag(ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_5)
                .add(VAMPIRE_EXPERT_5_HEART_PURE_BLOOD)
                .add(VAMPIRE_EXPERT_5_HEART_HEART_SEEKER)
                .add(VAMPIRE_EXPERT_5_HEART_HEART_STRIKER)
                ;

        this.tag(ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_1)
                .add(HUNTER_EXPERT_1_EMERALD_GARLIC)
                .add(HUNTER_EXPERT_1_EMERALD_MAP);

        this.tag(ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_2)
                .add(HUNTER_EXPERT_2_EMERALD_FANG)
                .add(HUNTER_EXPERT_2_SOULS_ARROW_NORMAL)
                .add(HUNTER_EXPERT_2_SOULS_ARROW_VAMPIRE_KILLER)
                .add(HUNTER_EXPERT_2_SOULS_ARROW_SPITFIRE)
                .add(HUNTER_EXPERT_2_SOULS_ARROW_TELEPORT)
                .add(HUNTER_EXPERT_2_SOUL_SWIFTNESS_HEAD)
                .add(HUNTER_EXPERT_2_SOUL_SWIFTNESS_CHEST)
                .add(HUNTER_EXPERT_2_SOUL_SWIFTNESS_LEGS)
                .add(HUNTER_EXPERT_2_SOUL_SWIFTNESS_FEET)
        ;

        this.tag(ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_3)
                .add(HUNTER_EXPERT_3_EMERALD_BLOOD)
                .add(HUNTER_EXPERT_3_SOUL_COAT_HEAD)
                .add(HUNTER_EXPERT_3_SOUL_COAT_CHEST)
                .add(HUNTER_EXPERT_3_SOUL_COAT_LEGS)
                .add(HUNTER_EXPERT_3_SOUL_COAT_FEET)
                .add(HUNTER_EXPERT_3_SOUL_SWIFTNESS_HEAD)
                .add(HUNTER_EXPERT_3_SOUL_SWIFTNESS_CHEST)
                .add(HUNTER_EXPERT_3_SOUL_SWIFTNESS_LEGS)
                .add(HUNTER_EXPERT_3_SOUL_SWIFTNESS_FEET)
        ;

        this.tag(ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_4)
                .add(HUNTER_EXPERT_4_SOUL_COAT_HEAD)
                .add(HUNTER_EXPERT_4_SOUL_COAT_CHEST)
                .add(HUNTER_EXPERT_4_SOUL_COAT_LEGS)
                .add(HUNTER_EXPERT_4_SOUL_COAT_FEET)
                .add(HUNTER_EXPERT_4_SOUL_SWIFTNESS_HEAD)
                .add(HUNTER_EXPERT_4_SOUL_SWIFTNESS_CHEST)
                .add(HUNTER_EXPERT_4_SOUL_SWIFTNESS_LEGS)
                .add(HUNTER_EXPERT_4_SOUL_SWIFTNESS_FEET)
        ;

        this.tag(ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_5)
                .add(HUNTER_EXPERT_5_SOUL_COAT_HEAD)
                .add(HUNTER_EXPERT_5_SOUL_COAT_CHEST)
                .add(HUNTER_EXPERT_5_SOUL_COAT_LEGS)
                .add(HUNTER_EXPERT_5_SOUL_COAT_FEET)
                .add(HUNTER_EXPERT_5_SOUL_AXE)
                .add(HUNTER_EXPERT_5_SOUL_CROSSBOW)
                .add(HUNTER_EXPERT_5_SOUL_CROSSBOW_DOUBLE)
        ;

        this.tag(ModVillagerTradeTags.PRIEST_LEVEL_1)
                .add(PRIEST_1_CANDLE_EMERALD)
                .add(PRIEST_1_EMERALD_GARLIC)
                .add(PRIEST_1_EMERALD_SALT);

        this.tag(ModVillagerTradeTags.PRIEST_LEVEL_2)
                .add(PRIEST_2_EMERALD_HONEY)
                .add(PRIEST_2_HOLY_EMERALD)
        ;
        this.tag(ModVillagerTradeTags.PRIEST_LEVEL_3)
                .add(PRIEST_3_EMERALD_GOLD)
                .add(PRIEST_3_HOLY_EMERALD)
                .add(PRIEST_3_EMERALD_CRUCIFIX);

        this.tag(ModVillagerTradeTags.PRIEST_LEVEL_4)
                .add(PRIEST_4_HOLY_EMERALD)
                .add(PRIEST_4_EMERALD_CRUCIFIX);

        this.tag(ModVillagerTradeTags.PRIEST_LEVEL_5)
                .add(PRIEST_5_CANDELABRA_EMERALD)
                .add(PRIEST_5_EMERALD_CRUCIFIX);

        this.tag(VillagerTradeTags.FARMER_LEVEL_1)
                .add(FARMER_1_EMERALD_GARLIC);

        this.tag(VillagerTradeTags.BUTCHER_LEVEL_3)
                .add(BUTCHER_3_EMERALD_HEART);

        this.tag(VillagerTradeTags.MASON_LEVEL_2)
                .add(MANSON_2_EMERALD_STONE)
                .add(MANSON_2_EMERALD_CHISELED);

        this.tag(VillagerTradeTags.WANDERING_TRADER_COMMON)
                .add(WANDERER_1_EMERALD_ORCHID)
                .add(WANDERER_1_EMERALD_ROOTS)
                .add(WANDERER_1_EMERALD_GARLIC)
                .add(WANDERER_1_EMERALD_DARK_SAPLING)
                .add(WANDERER_1_EMERALD_CURSED_SAPLING)
                .add(WANDERER_1_EMERALD_EARTH);

        this.tag(ModVillagerTradeTags.VAMPIRE_VILLAGER)
                .add(VAMPIRE_VILLAGER_EMERALDS_HEARTS)
                .add(VAMPIRE_VILLAGER_HEARTS_EMERALDS)
                .add(VAMPIRE_VILLAGER_BLOOD_BOTTLES);
    }
}
