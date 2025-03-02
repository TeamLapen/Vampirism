package de.teamlapen.vampirism.data.provider.model;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.color.item.CrossbowArrowTint;
import de.teamlapen.vampirism.client.color.item.OilBottleTint;
import de.teamlapen.vampirism.client.renderer.item.properties.BloodFilled;
import de.teamlapen.vampirism.client.renderer.item.properties.ClipFilled;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VResourceLocation.mod;

public class ItemModelGenerators extends net.minecraft.client.data.models.ItemModelGenerators {

    public ItemModelGenerators(net.minecraft.client.data.models.ItemModelGenerators generator) {
        super(generator.itemModelOutput, generator.modelOutput);
    }

    public ItemModelGenerators(ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        super(itemModelOutput, modelOutput);
    }

    @Override
    public void run() {
        getFlatItems().forEach(item -> generateFlatItem(item, ModelTemplates.FLAT_ITEM));
        getFlatItemWithTexture().forEach(this::generateFlatItemWithTexture);
        generateArrows();
        generateSpawnEggs();
        generateBloodBottle();
        generateAmmoClip();
        generateQuarrelPouch();
        generateHunterIntel();
        generateAccessories();
        generateWeapons();
        generateCrossbows();
        generateOilBottle();
        generateCrucifix();
        generateNonTemplateItems();
        createDefaultModels();
    }

    protected void generateNonTemplateItems() {
        this.itemModelOutput.accept(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.asItem(), ItemModelUtils.plainModel(ModelTemplates.GARLIC_DIFFUSER_CORE.create(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get(), new TextureMapping().put(TextureSlot.TEXTURE, VResourceLocation.mod("block/garlic_diffuser_inside_improved")), this.modelOutput)));
    }

    protected void createDefaultModels() {
        Stream.of(ModItems.STAKE, ModItems.HUNTER_HAT_HEAD_0, ModItems.HUNTER_HAT_HEAD_1, ModItems.GARLIC_DIFFUSER_CORE, ModItems.PITCHFORK, ModItems.UMBRELLA).map(DeferredHolder::get).forEach(this::createDefaultModel);
    }

    protected void createDefaultModel(Item item) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(getDefaultModelLocation(item)));
    }

    protected ResourceLocation getDefaultModelLocation(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    protected void generateFlatItemWithTexture(Item item, ResourceLocation texture) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(createFlatItemWithTexture(item, texture)));
    }

    protected ResourceLocation createFlatItemWithTexture(Item item, ResourceLocation texture) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(texture), this.modelOutput);
    }

    protected ResourceLocation createFlatItemWithTexture(ResourceLocation item, ResourceLocation texture) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.decorateItemModelLocation(item.toString()), TextureMapping.layer0(texture), this.modelOutput);
    }

    protected void generateCrucifix() {
        generateCrucifix(ModItems.CRUCIFIX_NORMAL.get(), VResourceLocation.mod("item/crucifix_wooden"));
        generateCrucifix(ModItems.CRUCIFIX_ENHANCED.get(), VResourceLocation.mod("item/crucifix_iron"));
        generateCrucifix(ModItems.CRUCIFIX_ULTIMATE.get(), VResourceLocation.mod("item/crucifix_gold"));
    }

    protected void generateCrucifix(Item item, ResourceLocation texture) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(ModelTemplates.CRUCIFIX.create(ModelLocationUtils.getModelLocation(item), TextureMapping.defaultTexture(texture).put(TextureSlot.PARTICLE, texture), this.modelOutput)));
    }

    protected void generateArrows() {
        Stream.of(ModItems.CROSSBOW_ARROW_NORMAL, ModItems.CROSSBOW_ARROW_SPITFIRE, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER, ModItems.CROSSBOW_ARROW_TELEPORT, ModItems.CROSSBOW_ARROW_BLEEDING, ModItems.CROSSBOW_ARROW_GARLIC).map(DeferredHolder::get).forEach(item -> {
            var model = ModelTemplates.TWO_LAYERED_ITEM.create(item, TextureMapping.layered(VResourceLocation.mod("item/crossbow_arrow"), VResourceLocation.mod("item/crossbow_arrow_tip")), this.modelOutput);
            this.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, BLANK_LAYER, new CrossbowArrowTint()));
        });
    }

    protected void generateOilBottle() {
        var bloodBottle = ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.OIL_BOTTLE.asItem(), TextureMapping.layered(mod("item/oil_bottle"), mod("item/oil_bottle_overlay")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.OIL_BOTTLE.asItem(), ItemModelUtils.tintedModel(bloodBottle, BLANK_LAYER, new OilBottleTint()));
    }

    protected void generateSpawnEggs() {
        this.generateSpawnEgg(ModItems.VAMPIRE_SPAWN_EGG.get(), 0x8B15A3, 0xa735e3);
        this.generateSpawnEgg(ModItems.ADVANCED_VAMPIRE_SPAWN_EGG.get(), 0x8B15A3, 0x560a7e);
        this.generateSpawnEgg(ModItems.VAMPIRE_HUNTER_SPAWN_EGG.get(), 0x2d05f2, 0x2600e0);
        this.generateSpawnEgg(ModItems.ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG.get(), 0x2d05f2, 0x1a028c);
        this.generateSpawnEgg(ModItems.VAMPIRE_BARON_SPAWN_EGG.get(), 0x8B15A3, 0x15acda);
        this.generateSpawnEgg(ModItems.HUNTER_TRAINER_SPAWN_EGG.get(), 0x2d05f2, 0x1cdb49);
    }

    protected void generateCrossbows() {
        ResourceLocation basicModel = ModelTemplates.CROSSBOW.create(ModItems.BASIC_CROSSBOW.asItem(), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow")).put(TextureSlots.STRING, mod("item/crossbow_part_string")).put(TextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation basicModelUnloaded = ModelTemplates.CROSSBOW_UNLOADED.create(ModItems.BASIC_CROSSBOW.getId().withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow")).put(TextureSlots.STRING, mod("item/crossbow_part_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(basicModel), ItemModelUtils.override(ItemModelUtils.plainModel(basicModelUnloaded), 0.99f)));

        ResourceLocation enhancedModel = ModelTemplates.CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_CROSSBOW.get()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_enhanced")).put(TextureSlots.STRING, mod("item/crossbow_part_string")).put(TextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation enhancedModelUnloaded = ModelTemplates.CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_enhanced")).put(TextureSlots.STRING, mod("item/crossbow_part_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedModelUnloaded), 0.99f)));

        ResourceLocation doubleModel = ModelTemplates.DOUBLE_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_DOUBLE_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double")).put(TextureSlots.STRING, mod("item/crossbow_part_double_string")).put(TextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation doubleModelUnloaded = ModelTemplates.DOUBLE_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_DOUBLE_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double")).put(TextureSlots.STRING, mod("item/crossbow_part_double_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_DOUBLE_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(doubleModel), ItemModelUtils.override(ItemModelUtils.plainModel(doubleModelUnloaded), 0.99f)));

        ResourceLocation enhancedDoubleModel = ModelTemplates.DOUBLE_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_DOUBLE_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double_enhanced")).put(TextureSlots.STRING, mod("item/crossbow_part_double_string")).put(TextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation enhancedDoubleModelUnloaded = ModelTemplates.DOUBLE_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double_enhanced")).put(TextureSlots.STRING, mod("item/crossbow_part_double_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_DOUBLE_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedDoubleModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedDoubleModelUnloaded), 0.99f)));

        ResourceLocation techModel = ModelTemplates.TECH_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_TECH_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow")).put(TextureSlots.STRING, mod("item/crossbow_part_tech_string")), this.modelOutput);
        ResourceLocation techModelUnloaded = ModelTemplates.TECH_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_TECH_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow")).put(TextureSlots.STRING, mod("item/crossbow_part_tech_string_unloaded")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_TECH_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(techModel), ItemModelUtils.override(ItemModelUtils.plainModel(techModelUnloaded), 0.99f)));

        ResourceLocation enhancedTechModel = ModelTemplates.TECH_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_TECH_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow_enhanced")).put(TextureSlots.STRING, mod("item/crossbow_part_tech_string")), this.modelOutput);
        ResourceLocation enhancedTechModelUnloaded = ModelTemplates.TECH_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_TECH_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow_enhanced")).put(TextureSlots.STRING, mod("item/crossbow_part_tech_string_unloaded")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_TECH_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedTechModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedTechModelUnloaded), 0.99f)));
    }

    protected void generateBloodBottle() {
        this.itemModelOutput.accept(ModItems.BLOOD_BOTTLE.asItem(),
                ItemModelUtils.rangeSelect(new BloodFilled(),
                        ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_0", ModelTemplates.FLAT_ITEM)),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_1", ModelTemplates.FLAT_ITEM)), 0.11f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_2", ModelTemplates.FLAT_ITEM)), 0.22f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_3", ModelTemplates.FLAT_ITEM)), 0.33f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_4", ModelTemplates.FLAT_ITEM)), 0.44f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_5", ModelTemplates.FLAT_ITEM)), 0.55f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_6", ModelTemplates.FLAT_ITEM)), 0.66f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_7", ModelTemplates.FLAT_ITEM)), 0.77f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_8", ModelTemplates.FLAT_ITEM)), 0.88f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_9", ModelTemplates.FLAT_ITEM)), 0.99f)
                ));
    }

    protected void generateAmmoClip() {
        this.itemModelOutput.accept(ModItems.ARROW_CLIP.asItem(),
                ItemModelUtils.rangeSelect(new ClipFilled(),
                        ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("arrow_clip_0"), VResourceLocation.mod("item/arrow_clip0"))),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("arrow_clip_1"), VResourceLocation.mod("item/arrow_clip1"))), 0.01f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("arrow_clip_2"), VResourceLocation.mod("item/arrow_clip2"))), 0.55f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("arrow_clip_3"), VResourceLocation.mod("item/arrow_clip3"))), 0.99f)
                ));
    }
    protected void generateQuarrelPouch() {
        this.itemModelOutput.accept(ModItems.QUARREL_POUCH.asItem(),
                ItemModelUtils.rangeSelect(new ClipFilled(),
                        ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("quarrel_pouch_0"), VResourceLocation.mod("item/arrow_clip0"))),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("quarrel_pouch_1"), VResourceLocation.mod("item/arrow_clip1"))), 0.01f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("quarrel_pouch_2"), VResourceLocation.mod("item/arrow_clip2"))), 0.55f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VResourceLocation.mod("quarrel_pouch_3"), VResourceLocation.mod("item/arrow_clip3"))), 0.99f)
                ));
    }

    protected void generateHunterIntel() {
        Stream.of(ModItems.HUNTER_INTEL_0, ModItems.HUNTER_INTEL_1, ModItems.HUNTER_INTEL_2, ModItems.HUNTER_INTEL_3, ModItems.HUNTER_INTEL_4, ModItems.HUNTER_INTEL_5, ModItems.HUNTER_INTEL_6, ModItems.HUNTER_INTEL_7, ModItems.HUNTER_INTEL_8, ModItems.HUNTER_INTEL_9).map(DeferredItem::asItem).forEach(item -> {
            this.itemModelOutput.accept(item, ItemModelUtils.plainModel(createFlatItemModel(item, ModelTemplates.HUNTER_INTEL)));
        });
    }

    protected void generateAccessories() {
        this.itemModelOutput.accept(ModItems.RING.asItem(), ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.RING.asItem(), TextureMapping.layered(VResourceLocation.mod("item/vampire_ring_layer0"), VResourceLocation.mod("item/vampire_ring_layer1")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.AMULET.asItem(), ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.AMULET.asItem(), TextureMapping.layered(VResourceLocation.mod("item/vampire_amulet_layer0"), VResourceLocation.mod("item/vampire_amulet_layer1")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.OBI_BELT.asItem(), ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.OBI_BELT.asItem(), TextureMapping.layered(VResourceLocation.mod("item/vampire_obi_belt_layer0"), VResourceLocation.mod("item/vampire_obi_belt_layer1")), this.modelOutput)));
    }

    protected void generateWeapons() {
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_NORMAL.asItem(), ItemModelUtils.plainModel(ModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_NORMAL.asItem(), new TextureMapping().put(TextureSlots.TEXTURE_3, VResourceLocation.mod("item/heart_seeker_normal")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_ENHANCED.asItem(), ItemModelUtils.plainModel(ModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_ENHANCED.asItem(), new TextureMapping().put(TextureSlots.TEXTURE_3, VResourceLocation.mod("item/heart_seeker_enhanced")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_ULTIMATE.asItem(), new TextureMapping().put(TextureSlots.TEXTURE_3, VResourceLocation.mod("item/heart_seeker_ultimate")), this.modelOutput)));

        this.itemModelOutput.accept(ModItems.HEART_STRIKER_NORMAL.asItem(), ItemModelUtils.plainModel(ModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_NORMAL.asItem(), new TextureMapping().put(TextureSlots.TEXTURE_2, VResourceLocation.mod("item/heart_striker_normal")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_STRIKER_ENHANCED.asItem(), ItemModelUtils.plainModel(ModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_ENHANCED.asItem(), new TextureMapping().put(TextureSlots.TEXTURE_2, VResourceLocation.mod("item/heart_striker_enhanced")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_STRIKER_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_ULTIMATE.asItem(), new TextureMapping().put(TextureSlots.TEXTURE_2, VResourceLocation.mod("item/heart_striker_ultimate")), this.modelOutput)));

        this.itemModelOutput.accept(ModItems.HUNTER_AXE_NORMAL.asItem(), ItemModelUtils.plainModel(ModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_NORMAL.asItem(), TextureMapping.defaultTexture(VResourceLocation.mod("item/hunter_axe_normal")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HUNTER_AXE_ENHANCED.asItem(), ItemModelUtils.plainModel(ModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_ENHANCED.asItem(), TextureMapping.defaultTexture(VResourceLocation.mod("item/hunter_axe_enhanced")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HUNTER_AXE_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_ULTIMATE.asItem(), TextureMapping.defaultTexture(VResourceLocation.mod("item/hunter_axe_ultimate")), this.modelOutput)));
    }

    protected Stream<Item> getFlatItems() {
        return Stream.of(
                ModItems.HUNTER_COAT_CHEST_NORMAL,
                ModItems.HUNTER_COAT_CHEST_ENHANCED,
                ModItems.HUNTER_COAT_CHEST_ULTIMATE,
                ModItems.HUNTER_COAT_FEET_NORMAL,
                ModItems.HUNTER_COAT_FEET_ENHANCED,
                ModItems.HUNTER_COAT_FEET_ULTIMATE,
                ModItems.HUNTER_COAT_HEAD_NORMAL,
                ModItems.HUNTER_COAT_HEAD_ENHANCED,
                ModItems.HUNTER_COAT_HEAD_ULTIMATE,
                ModItems.HUNTER_COAT_LEGS_NORMAL,
                ModItems.HUNTER_COAT_LEGS_ENHANCED,
                ModItems.HUNTER_COAT_LEGS_ULTIMATE,
                ModItems.BLOOD_BUCKET,
                ModItems.IMPURE_BLOOD_BUCKET,
                ModItems.PURE_SALT,
                ModItems.PURE_SALT_WATER,
                ModItems.HUMAN_HEART,
                ModItems.INJECTION_EMPTY,
                ModItems.INJECTION_GARLIC,
                ModItems.INJECTION_SANGUINARE,
                ModItems.PURIFIED_GARLIC,
                ModItems.SOUL_ORB_VAMPIRE,
                ModItems.VAMPIRE_BLOOD_BOTTLE,
                ModItems.VAMPIRE_CLOAK_BLACK_BLUE,
                ModItems.VAMPIRE_CLOAK_BLACK_RED,
                ModItems.VAMPIRE_CLOAK_BLACK_WHITE,
                ModItems.VAMPIRE_CLOAK_WHITE_BLACK,
                ModItems.VAMPIRE_CLOAK_RED_BLACK,
                ModItems.VAMPIRE_FANG,
                ModItems.WEAK_HUMAN_HEART,
                ModItems.ITEM_TENT,
                ModItems.PURE_BLOOD_0,
                ModItems.PURE_BLOOD_1,
                ModItems.PURE_BLOOD_2,
                ModItems.PURE_BLOOD_3,
                ModItems.PURE_BLOOD_4,
                ModItems.VAMPIRE_MINION_BINDING,
                ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE,
                ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED,
                ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL,
                ModItems.HUNTER_MINION_EQUIPMENT,
                ModItems.HUNTER_MINION_UPGRADE_SIMPLE,
                ModItems.HUNTER_MINION_UPGRADE_ENHANCED,
                ModItems.HUNTER_MINION_UPGRADE_SPECIAL,
                ModItems.OBLIVION_POTION,
                ModItems.VAMPIRE_CLOTHING_HAT,
                ModItems.VAMPIRE_CLOTHING_BOOTS,
                ModItems.VAMPIRE_CLOTHING_LEGS,
                ModItems.VAMPIRE_CLOTHING_CROWN,
                ModItems.GARLIC_FINDER,
                ModItems.DARK_SPRUCE_BOAT,
                ModItems.CURSED_SPRUCE_BOAT,
                ModItems.DARK_SPRUCE_CHEST_BOAT,
                ModItems.CURSED_SPRUCE_CHEST_BOAT,
                ModItems.FEEDING_ADAPTER,
                ModItems.BLOOD_INFUSED_RAW_IRON,
                ModItems.BLOOD_INFUSED_RAW_GOLD,
                ModItems.BLOOD_INFUSED_IRON_INGOT,
                ModItems.BLOOD_INFUSED_GOLD_INGOT,
                ModItems.BLOOD_INFUSED_DIAMOND,
                ModItems.BLOOD_INFUSED_NETHERITE_INGOT
                ).map(DeferredItem::asItem);
    }

    protected Map<Item, ResourceLocation> getFlatItemWithTexture() {
        return new HashMap<>() {{
            put(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), mod("item/holy_water_normal"));
            put(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), mod("item/holy_water_enhanced"));
            put(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), mod("item/holy_water_ultimate"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), mod("item/holy_water_splash_normal"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), mod("item/holy_water_splash_enhanced"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get(), mod("item/holy_water_splash_ultimate"));
            put(ModItems.GARLIC_BREAD.get(), mod("item/garlic_bread"));
            put(ModItems.ITEM_ALCHEMICAL_FIRE.get(), mod("item/alchemical_fire"));
            put(ModBlocks.MED_CHAIR.get().asItem(), mod("item/med_chair"));
            put(ModItems.ITEM_TENT_SPAWNER.get(), mod("item/item_tent"));
            put(ModItems.VAMPIRE_BOOK.get(), mod("item/vampire_book"));
            put(ModBlocks.DIRECT_CURSED_BARK.get().asItem(), mod("block/cursed_bark"));
            put(ModItems.MOTHER_CORE.get(), mod("item/mother_core"));
            put(ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.asItem(), mod("item/armor_of_swiftness_head"));
            put(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.asItem(), mod("item/armor_of_swiftness_head_enhanced"));
            put(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.asItem(), mod("item/armor_of_swiftness_head_ultimate"));
            put(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.asItem(), mod("item/armor_of_swiftness_chest"));
            put(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.asItem(), mod("item/armor_of_swiftness_chest_enhanced"));
            put(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.asItem(), mod("item/armor_of_swiftness_chest_ultimate"));
            put(ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.asItem(), mod("item/armor_of_swiftness_legs"));
            put(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.asItem(), mod("item/armor_of_swiftness_legs_enhanced"));
            put(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.asItem(), mod("item/armor_of_swiftness_legs_ultimate"));
            put(ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.asItem(), mod("item/armor_of_swiftness_feet"));
            put(ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.asItem(), mod("item/armor_of_swiftness_feet_enhanced"));
            put(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.asItem(), mod("item/armor_of_swiftness_feet_ultimate"));
        }};
    }


}
