package de.teamlapen.vampirism.data.provider.models;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.colors.CrossbowArrowTint;
import de.teamlapen.vampirism.client.colors.OilBottleTint;
import de.teamlapen.vampirism.client.colors.RefinementTint;
import de.teamlapen.vampirism.client.models.items.properties.BloodFilled;
import de.teamlapen.vampirism.client.models.items.properties.ClipFilled;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VResourceLocation.mod;

public class ModItemModelGenerators extends ItemModelGenerators {

    public ModItemModelGenerators(ItemModelGenerators generator) {
        super(generator.itemModelOutput, generator.modelOutput);
    }

    @Override
    public void run() {
        getFlatItems().forEach(item -> generateFlatItem(item, ModModelTemplates.FLAT_ITEM));
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
        createAlchemicalFire();
    }

    protected void generateNonTemplateItems() {
        this.itemModelOutput.accept(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.GARLIC_DIFFUSER_CORE.create(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get(), new TextureMapping().put(TextureSlot.TEXTURE, VResourceLocation.mod("block/garlic_diffuser_inside_improved")), this.modelOutput)));
    }

    protected void createDefaultModels() {
        Stream.of(ModItems.STAKE, ModItems.HUNTER_HAT_TALL, ModItems.HUNTER_HAT_BROAD, ModItems.GARLIC_DIFFUSER_CORE, ModItems.PITCHFORK, ModItems.UMBRELLA).map(DeferredHolder::get).forEach(this::createDefaultModel);
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
        return ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(texture), this.modelOutput);
    }

    protected ResourceLocation createFlatItemWithTexture(ResourceLocation item, ResourceLocation texture) {
        return ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.decorateItemModelLocation(item.toString()), TextureMapping.layer0(texture), this.modelOutput);
    }

    protected void generateCrucifix() {
        generateCrucifix(ModItems.CRUCIFIX_NORMAL.get(), VResourceLocation.mod("item/crucifix_wooden"));
        generateCrucifix(ModItems.CRUCIFIX_ENHANCED.get(), VResourceLocation.mod("item/crucifix_iron"));
        generateCrucifix(ModItems.CRUCIFIX_ULTIMATE.get(), VResourceLocation.mod("item/crucifix_gold"));
    }

    protected void generateCrucifix(Item item, ResourceLocation texture) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(ModModelTemplates.CRUCIFIX.create(ModelLocationUtils.getModelLocation(item), TextureMapping.defaultTexture(texture).put(TextureSlot.PARTICLE, texture), this.modelOutput)));
    }

    protected void generateArrows() {
        Stream.of(ModItems.CROSSBOW_ARROW_NORMAL, ModItems.CROSSBOW_ARROW_SPITFIRE, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER, ModItems.CROSSBOW_ARROW_TELEPORT, ModItems.CROSSBOW_ARROW_BLEEDING, ModItems.CROSSBOW_ARROW_GARLIC).map(DeferredHolder::get).forEach(item -> {
            var model = ModModelTemplates.TWO_LAYERED_ITEM.create(item, TextureMapping.layered(VResourceLocation.mod("item/crossbow_arrow"), VResourceLocation.mod("item/crossbow_arrow_tip")), this.modelOutput);
            this.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, BLANK_LAYER, new CrossbowArrowTint()));
        });
    }

    protected void generateOilBottle() {
        var bloodBottle = ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.OIL_BOTTLE.asItem(), TextureMapping.layered(mod("item/oil_bottle"), mod("item/oil_bottle_overlay")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.OIL_BOTTLE.asItem(), ItemModelUtils.tintedModel(bloodBottle, BLANK_LAYER, new OilBottleTint()));
    }

    protected void generateSpawnEggs() {
        this.generateSpawnEgg(ModItems.VAMPIRE_SPAWN_EGG.get(), 0x881d99, 0x5e1975);
        this.generateSpawnEgg(ModItems.ADVANCED_VAMPIRE_SPAWN_EGG.get(), 0x881d99, 0xedbb24);
        this.generateSpawnEgg(ModItems.VAMPIRE_BARON_SPAWN_EGG.get(), 0x9a1b1b, 0x252525);
        this.generateSpawnEgg(ModItems.TASK_MASTER_VAMPIRE_SPAWN_EGG.get(), 0x881d99, 0xcecdd1);

        this.generateSpawnEgg(ModItems.VAMPIRE_HUNTER_SPAWN_EGG.get(), 0x173f9c, 142163);
        this.generateSpawnEgg(ModItems.ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG.get(), 0x173f9c, 0xedbb24);
        this.generateSpawnEgg(ModItems.HUNTER_TRAINER_SPAWN_EGG.get(), 0x99989c, 0x252525);
        this.generateSpawnEgg(ModItems.TASK_MASTER_HUNTER_SPAWN_EGG.get(), 0x173f9c, 0xcecdd1);

        this.generateSpawnEgg(ModItems.GHOST_SPAWN_EGG.get(), 0xcecdd1, 0xb5b3ba);
    }

    public void generateSpawnEgg(Item spawnEggItem, int primaryColor, int secondaryColor) {
        ResourceLocation resourcelocation = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");
        this.itemModelOutput
                .accept(spawnEggItem, ItemModelUtils.tintedModel(resourcelocation, ItemModelUtils.constantTint(primaryColor), ItemModelUtils.constantTint(secondaryColor)));
    }

    protected void generateCrossbows() {
        ResourceLocation basicModel = ModModelTemplates.CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_CROSSBOW.get()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow")).put(ModTextureSlots.STRING, mod("item/crossbow_part_string")).put(ModTextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation basicModelUnloaded = ModModelTemplates.CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow")).put(ModTextureSlots.STRING, mod("item/crossbow_part_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(basicModel), ItemModelUtils.override(ItemModelUtils.plainModel(basicModelUnloaded), 0.99f)));

        ResourceLocation enhancedModel = ModModelTemplates.CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_CROSSBOW.get()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_enhanced")).put(ModTextureSlots.STRING, mod("item/crossbow_part_string")).put(ModTextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation enhancedModelUnloaded = ModModelTemplates.CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_enhanced")).put(ModTextureSlots.STRING, mod("item/crossbow_part_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedModelUnloaded), 0.99f)));

        ResourceLocation doubleModel = ModModelTemplates.DOUBLE_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_DOUBLE_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double")).put(ModTextureSlots.STRING, mod("item/crossbow_part_double_string")).put(ModTextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation doubleModelUnloaded = ModModelTemplates.DOUBLE_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_DOUBLE_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double")).put(ModTextureSlots.STRING, mod("item/crossbow_part_double_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_DOUBLE_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(doubleModel), ItemModelUtils.override(ItemModelUtils.plainModel(doubleModelUnloaded), 0.99f)));

        ResourceLocation enhancedDoubleModel = ModModelTemplates.DOUBLE_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_DOUBLE_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double_enhanced")).put(ModTextureSlots.STRING, mod("item/crossbow_part_double_string")).put(ModTextureSlots.ARROW, mod("item/crossbow_part_arrow")), this.modelOutput);
        ResourceLocation enhancedDoubleModelUnloaded = ModModelTemplates.DOUBLE_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/crossbow_double_enhanced")).put(ModTextureSlots.STRING, mod("item/crossbow_part_double_string")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_DOUBLE_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedDoubleModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedDoubleModelUnloaded), 0.99f)));

        ResourceLocation techModel = ModModelTemplates.TECH_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_TECH_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow")).put(ModTextureSlots.STRING, mod("item/crossbow_part_tech_string")), this.modelOutput);
        ResourceLocation techModelUnloaded = ModModelTemplates.TECH_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_TECH_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow")).put(ModTextureSlots.STRING, mod("item/crossbow_part_tech_string_unloaded")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_TECH_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(techModel), ItemModelUtils.override(ItemModelUtils.plainModel(techModelUnloaded), 0.99f)));

        ResourceLocation enhancedTechModel = ModModelTemplates.TECH_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_TECH_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow_enhanced")).put(ModTextureSlots.STRING, mod("item/crossbow_part_tech_string")), this.modelOutput);
        ResourceLocation enhancedTechModelUnloaded = ModModelTemplates.TECH_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_TECH_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, mod("item/tech_crossbow_enhanced")).put(ModTextureSlots.STRING, mod("item/crossbow_part_tech_string_unloaded")), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_TECH_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedTechModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedTechModelUnloaded), 0.99f)));
    }

    protected void generateBloodBottle() {
        this.itemModelOutput.accept(ModItems.BLOOD_BOTTLE.asItem(),
                ItemModelUtils.rangeSelect(new BloodFilled(),
                        ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_0", ModModelTemplates.FLAT_ITEM)),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_1", ModModelTemplates.FLAT_ITEM)), 0.20f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_2", ModModelTemplates.FLAT_ITEM)), 0.40f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_3", ModModelTemplates.FLAT_ITEM)), 0.60f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_4", ModModelTemplates.FLAT_ITEM)), 0.80f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemModel(ModItems.BLOOD_BOTTLE.get(), "_5", ModModelTemplates.FLAT_ITEM)), 0.99f)
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
        ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.decorateItemModelLocation(VResourceLocation.mod("hunter_intel").toString()), TextureMapping.layer0(mod("item/hunter_intel")), this.modelOutput);

        Stream.of(ModItems.HUNTER_INTEL_0, ModItems.HUNTER_INTEL_1, ModItems.HUNTER_INTEL_2, ModItems.HUNTER_INTEL_3, ModItems.HUNTER_INTEL_4, ModItems.HUNTER_INTEL_5, ModItems.HUNTER_INTEL_6, ModItems.HUNTER_INTEL_7, ModItems.HUNTER_INTEL_8, ModItems.HUNTER_INTEL_9).map(DeferredItem::asItem).forEach(item ->
                this.itemModelOutput.accept(item, ItemModelUtils.plainModel(mod("item/hunter_intel")))
        );
    }

    protected void generateAccessories() {
        this.itemModelOutput.accept(ModItems.RING.asItem(), ItemModelUtils.tintedModel(ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.RING.asItem(), TextureMapping.layered(VResourceLocation.mod("item/vampire_ring_layer0"), VResourceLocation.mod("item/vampire_ring_layer1")), this.modelOutput), BLANK_LAYER, new RefinementTint()));
        this.itemModelOutput.accept(ModItems.AMULET.asItem(), ItemModelUtils.tintedModel(ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.AMULET.asItem(), TextureMapping.layered(VResourceLocation.mod("item/vampire_amulet_layer0"), VResourceLocation.mod("item/vampire_amulet_layer1")), this.modelOutput), BLANK_LAYER, new RefinementTint()));
        this.itemModelOutput.accept(ModItems.OBI_BELT.asItem(), ItemModelUtils.tintedModel(ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.OBI_BELT.asItem(), TextureMapping.layered(VResourceLocation.mod("item/vampire_obi_belt_layer0"), VResourceLocation.mod("item/vampire_obi_belt_layer1")), this.modelOutput), BLANK_LAYER, new RefinementTint()));
    }

    protected void generateWeapons() {
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_NORMAL.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_NORMAL.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_3, VResourceLocation.mod("item/heart_seeker_normal")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_ENHANCED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_ENHANCED.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_3, VResourceLocation.mod("item/heart_seeker_enhanced")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_ULTIMATE.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_3, VResourceLocation.mod("item/heart_seeker_ultimate")), this.modelOutput)));

        this.itemModelOutput.accept(ModItems.HEART_STRIKER_NORMAL.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_NORMAL.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_2, VResourceLocation.mod("item/heart_striker_normal")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_STRIKER_ENHANCED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_ENHANCED.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_2, VResourceLocation.mod("item/heart_striker_enhanced")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_STRIKER_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_ULTIMATE.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_2, VResourceLocation.mod("item/heart_striker_ultimate")), this.modelOutput)));

        this.itemModelOutput.accept(ModItems.HUNTER_AXE_NORMAL.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_NORMAL.asItem(), TextureMapping.defaultTexture(VResourceLocation.mod("item/hunter_axe_normal")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HUNTER_AXE_ENHANCED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_ENHANCED.asItem(), TextureMapping.defaultTexture(VResourceLocation.mod("item/hunter_axe_enhanced")), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HUNTER_AXE_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_ULTIMATE.asItem(), TextureMapping.defaultTexture(VResourceLocation.mod("item/hunter_axe_ultimate")), this.modelOutput)));
    }

    protected void createAlchemicalFire() {
        var model = ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.ITEM_ALCHEMICAL_FIRE.asItem(), new TextureMapping().put(TextureSlot.LAYER0, VResourceLocation.mod("item/alchemical_fire_layer0")).put(TextureSlot.LAYER1, VResourceLocation.mod("item/alchemical_fire_layer1")), this.modelOutput));
        this.itemModelOutput.accept(ModItems.ITEM_ALCHEMICAL_FIRE.get(), model);
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
                ModItems.PURE_SALT,
                ModItems.PURE_SALT_WATER,
                ModItems.HUMAN_HEART,
                ModItems.SYRINGE_EMPTY,
                ModItems.SYRINGE_BLOOD,
                ModItems.INJECTION_GARLIC,
                ModItems.INJECTION_SANGUINARE,
                ModItems.PURIFIED_GARLIC,
                ModItems.SOUL_ORB_VAMPIRE,
                ModItems.VAMPIRE_BLOOD_BOTTLE,
                ModItems.VAMPIRE_CLOAK_WHITE,
                ModItems.VAMPIRE_CLOAK_LIGHT_GRAY,
                ModItems.VAMPIRE_CLOAK_GRAY,
                ModItems.VAMPIRE_CLOAK_BLACK,
                ModItems.VAMPIRE_CLOAK_BROWN,
                ModItems.VAMPIRE_CLOAK_RED,
                ModItems.VAMPIRE_CLOAK_ORANGE,
                ModItems.VAMPIRE_CLOAK_YELLOW,
                ModItems.VAMPIRE_CLOAK_LIME,
                ModItems.VAMPIRE_CLOAK_GREEN,
                ModItems.VAMPIRE_CLOAK_LIGHT_BLUE,
                ModItems.VAMPIRE_CLOAK_CYAN,
                ModItems.VAMPIRE_CLOAK_BLUE,
                ModItems.VAMPIRE_CLOAK_PURPLE,
                ModItems.VAMPIRE_CLOAK_MAGENTA,
                ModItems.VAMPIRE_CLOAK_PINK,
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
                ModItems.FABRIC_FILTER,
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
