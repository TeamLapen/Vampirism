package de.teamlapen.vampirism.data.provider.models;

import de.teamlapen.faction.client.color.tint.RefinementTint;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.color.item.QuarrelTint;
import de.teamlapen.vampirism.client.color.item.OilBottleTint;
import de.teamlapen.vampirism.client.models.items.properties.BloodFilled;
import de.teamlapen.vampirism.client.models.items.properties.ClipFilled;
import de.teamlapen.vampirism.common.components.predicates.ChargedRitualKnifePredicate;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.properties.conditional.ComponentMatches;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VIdentifier.mod;

public class ModItemModelGenerators extends ItemModelGenerators {

    public ModItemModelGenerators(ItemModelGenerators generator) {
        super(generator.itemModelOutput, generator.modelOutput);
    }

    @Override
    public void run() {
        getFlatItems().forEach(item -> generateFlatItem(item, ModModelTemplates.FLAT_ITEM));
        getFlatItemWithTexture().forEach(this::generateFlatItemWithTexture);
        generateTippedQuarrels();
        generateBloodBottle();
        generateQuarrelPouch();
        generateHunterIntel();
        generateAccessories();
        generateWeapons();
        generateCrossbows();
        generateOilBottle();
        generateSerumInjection();
        generateCrucifix();
        createDefaultModels();
        createAlchemicalFire();
        createRitualKnife();
    }

    protected void createDefaultModels() {
        Stream.of(ModItems.STAKE, ModItems.PITCHFORK, ModItems.UMBRELLA).map(DeferredHolder::get).forEach(this::createDefaultModel);
    }

    protected void createDefaultModel(Item item) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(getDefaultModelLocation(item)));
    }

    protected Identifier getDefaultModelLocation(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    protected void generateFlatItemWithTexture(Item item, Identifier texture) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(createFlatItemWithTexture(item, texture)));
    }

    protected Identifier createFlatItemWithTexture(Item item, Identifier texture) {
        return ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(new Material(texture)), this.modelOutput);
    }

    protected Identifier createFlatItemWithTexture(Identifier item, Identifier texture) {
        return ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.decorateItemModelLocation(item.toString()), TextureMapping.layer0(new Material(texture)), this.modelOutput);
    }

    protected void generateCrucifix() {
        generateCrucifix(ModItems.CRUCIFIX_NORMAL.get(), VIdentifier.mod("item/crucifix_wooden"));
        generateCrucifix(ModItems.CRUCIFIX_ENHANCED.get(), VIdentifier.mod("item/crucifix_iron"));
        generateCrucifix(ModItems.CRUCIFIX_ULTIMATE.get(), VIdentifier.mod("item/crucifix_gold"));
    }

    protected void generateCrucifix(Item item, Identifier texture) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(ModModelTemplates.CRUCIFIX.create(ModelLocationUtils.getModelLocation(item), TextureMapping.defaultTexture(new Material(texture)).put(TextureSlot.PARTICLE, new Material(texture)), this.modelOutput)));
    }

    protected void generateTippedQuarrels() {
        generateFlatItemWithTexture(ModItems.QUARREL_NORMAL.get(), VIdentifier.mod("item/quarrel"));
        Stream.of(ModItems.QUARREL_SPITFIRE, ModItems.QUARREL_VAMPIRE_KILLER, ModItems.QUARREL_TELEPORT, ModItems.QUARREL_BLEEDING, ModItems.QUARREL_GARLIC).map(DeferredHolder::get).forEach(item -> {
            var model = ModModelTemplates.TWO_LAYERED_ITEM.create(item, TextureMapping.layered(new Material(VIdentifier.mod("item/quarrel")), new Material(VIdentifier.mod("item/quarrel_tip"))), this.modelOutput);
            this.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, BLANK_LAYER, new QuarrelTint()));
        });
    }

    protected void generateOilBottle() {
        var bloodBottle = ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.OIL_BOTTLE.asItem(), TextureMapping.layered(new Material(mod("item/oil_bottle")), new Material(mod("item/oil_bottle_overlay"))), this.modelOutput);
        this.itemModelOutput.accept(ModItems.OIL_BOTTLE.asItem(), ItemModelUtils.tintedModel(bloodBottle, BLANK_LAYER, new OilBottleTint()));
    }

    public void generateSerumInjection() {
        Identifier identifier = this.generateLayeredItem(ModItems.SERUM_INJECTION.get(), new Material(ModelLocationUtils.getModelLocation(ModItems.SERUM_INJECTION.get()).withSuffix("_overlay")), new Material(ModelLocationUtils.getModelLocation(ModItems.SERUM_INJECTION.get())));
        this.addPotionTint(ModItems.SERUM_INJECTION.get(), identifier);
    }

    protected void generateCrossbows() {
        Identifier basicModel = ModModelTemplates.CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_CROSSBOW.get()), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_string"))).put(ModTextureSlots.ARROW, new Material(mod("item/crossbow_part_arrow"))), this.modelOutput);
        Identifier basicModelUnloaded = ModModelTemplates.CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_string"))), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(basicModel), ItemModelUtils.override(ItemModelUtils.plainModel(basicModelUnloaded), 0.99f)));

        Identifier enhancedModel = ModModelTemplates.CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_CROSSBOW.get()), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow_enhanced"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_string"))).put(ModTextureSlots.ARROW, new Material(mod("item/crossbow_part_arrow"))), this.modelOutput);
        Identifier enhancedModelUnloaded = ModModelTemplates.CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow_enhanced"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_string"))), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedModelUnloaded), 0.99f)));

        Identifier doubleModel = ModModelTemplates.DOUBLE_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_DOUBLE_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow_double"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_double_string"))).put(ModTextureSlots.ARROW, new Material(mod("item/crossbow_part_arrow"))), this.modelOutput);
        Identifier doubleModelUnloaded = ModModelTemplates.DOUBLE_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_DOUBLE_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow_double"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_double_string"))), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_DOUBLE_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(doubleModel), ItemModelUtils.override(ItemModelUtils.plainModel(doubleModelUnloaded), 0.99f)));

        Identifier enhancedDoubleModel = ModModelTemplates.DOUBLE_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_DOUBLE_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow_double_enhanced"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_double_string"))).put(ModTextureSlots.ARROW, new Material(mod("item/crossbow_part_arrow"))), this.modelOutput);
        Identifier enhancedDoubleModelUnloaded = ModModelTemplates.DOUBLE_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/crossbow_double_enhanced"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_double_string"))), this.modelOutput);
        this.itemModelOutput.accept(ModItems.ENHANCED_DOUBLE_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(enhancedDoubleModel), ItemModelUtils.override(ItemModelUtils.plainModel(enhancedDoubleModelUnloaded), 0.99f)));

        Identifier techModel = ModModelTemplates.TECH_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_TECH_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/tech_crossbow"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_tech_string"))), this.modelOutput);
        Identifier techModelUnloaded = ModModelTemplates.TECH_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.BASIC_TECH_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/tech_crossbow"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_tech_string_unloaded"))), this.modelOutput);
        this.itemModelOutput.accept(ModItems.BASIC_TECH_CROSSBOW.asItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), ItemModelUtils.plainModel(techModel), ItemModelUtils.override(ItemModelUtils.plainModel(techModelUnloaded), 0.99f)));

        Identifier enhancedTechModel = ModModelTemplates.TECH_CROSSBOW.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_TECH_CROSSBOW.asItem()), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/tech_crossbow_enhanced"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_tech_string"))), this.modelOutput);
        Identifier enhancedTechModelUnloaded = ModModelTemplates.TECH_CROSSBOW_UNLOADED.create(ModelLocationUtils.getModelLocation(ModItems.ENHANCED_TECH_CROSSBOW.get()).withSuffix("_unloaded"), new TextureMapping().put(TextureSlot.TEXTURE, new Material(mod("item/tech_crossbow_enhanced"))).put(ModTextureSlots.STRING, new Material(mod("item/crossbow_part_tech_string_unloaded"))), this.modelOutput);
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

    protected void generateQuarrelPouch() {
        this.itemModelOutput.accept(ModItems.QUARREL_POUCH.asItem(),
                ItemModelUtils.rangeSelect(new ClipFilled(),
                        ItemModelUtils.plainModel(createFlatItemWithTexture(VIdentifier.mod("quarrel_pouch_0"), VIdentifier.mod("item/quarrel_pouch_0"))),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VIdentifier.mod("quarrel_pouch_1"), VIdentifier.mod("item/quarrel_pouch_1"))), 0.01f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VIdentifier.mod("quarrel_pouch_2"), VIdentifier.mod("item/quarrel_pouch_2"))), 0.55f),
                        ItemModelUtils.override(ItemModelUtils.plainModel(createFlatItemWithTexture(VIdentifier.mod("quarrel_pouch_3"), VIdentifier.mod("item/quarrel_pouch_3"))), 0.99f)
                ));
    }

    protected void generateHunterIntel() {
        ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.decorateItemModelLocation(VIdentifier.mod("hunter_intel").toString()), TextureMapping.layer0(new Material(mod("item/hunter_intel"))), this.modelOutput);

        Stream.of(ModItems.HUNTER_INTEL_0, ModItems.HUNTER_INTEL_1, ModItems.HUNTER_INTEL_2, ModItems.HUNTER_INTEL_3, ModItems.HUNTER_INTEL_4, ModItems.HUNTER_INTEL_5, ModItems.HUNTER_INTEL_6, ModItems.HUNTER_INTEL_7, ModItems.HUNTER_INTEL_8, ModItems.HUNTER_INTEL_9).map(DeferredItem::asItem).forEach(item ->
                this.itemModelOutput.accept(item, ItemModelUtils.plainModel(mod("item/hunter_intel")))
        );
    }

    protected void generateAccessories() {
        this.itemModelOutput.accept(ModItems.RING.asItem(), ItemModelUtils.tintedModel(ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.RING.asItem(), TextureMapping.layered(new Material(VIdentifier.mod("item/vampire_ring_layer0")), new Material(VIdentifier.mod("item/vampire_ring_layer1"))), this.modelOutput), BLANK_LAYER, new RefinementTint()));
        this.itemModelOutput.accept(ModItems.AMULET.asItem(), ItemModelUtils.tintedModel(ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.AMULET.asItem(), TextureMapping.layered(new Material(VIdentifier.mod("item/vampire_amulet_layer0")), new Material(VIdentifier.mod("item/vampire_amulet_layer1"))), this.modelOutput), BLANK_LAYER, new RefinementTint()));
        this.itemModelOutput.accept(ModItems.OBI_BELT.asItem(), ItemModelUtils.tintedModel(ModModelTemplates.TWO_LAYERED_ITEM.create(ModItems.OBI_BELT.asItem(), TextureMapping.layered(new Material(VIdentifier.mod("item/vampire_obi_belt_layer0")), new Material(VIdentifier.mod("item/vampire_obi_belt_layer1"))), this.modelOutput), BLANK_LAYER, new RefinementTint()));
    }

    protected void generateWeapons() {
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_NORMAL.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_NORMAL.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_3, new Material(VIdentifier.mod("item/heart_seeker_normal"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_ENHANCED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_ENHANCED.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_3, new Material(VIdentifier.mod("item/heart_seeker_enhanced"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_SEEKER_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_SEEKER.create(ModItems.HEART_SEEKER_ULTIMATE.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_3, new Material(VIdentifier.mod("item/heart_seeker_ultimate"))), this.modelOutput)));

        this.itemModelOutput.accept(ModItems.HEART_STRIKER_NORMAL.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_NORMAL.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_2, new Material(VIdentifier.mod("item/heart_striker_normal"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_STRIKER_ENHANCED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_ENHANCED.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_2, new Material(VIdentifier.mod("item/heart_striker_enhanced"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HEART_STRIKER_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HEART_STRIKER.create(ModItems.HEART_STRIKER_ULTIMATE.asItem(), new TextureMapping().put(ModTextureSlots.TEXTURE_2, new Material(VIdentifier.mod("item/heart_striker_ultimate"))), this.modelOutput)));

        this.itemModelOutput.accept(ModItems.HUNTER_AXE_NORMAL.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_NORMAL.asItem(), TextureMapping.defaultTexture(new Material(VIdentifier.mod("item/hunter_axe_normal"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HUNTER_AXE_ENHANCED.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_ENHANCED.asItem(), TextureMapping.defaultTexture(new Material(VIdentifier.mod("item/hunter_axe_enhanced"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.HUNTER_AXE_ULTIMATE.asItem(), ItemModelUtils.plainModel(ModModelTemplates.HUNTER_AXE.create(ModItems.HUNTER_AXE_ULTIMATE.asItem(), TextureMapping.defaultTexture(new Material(VIdentifier.mod("item/hunter_axe_ultimate"))), this.modelOutput)));
    }

    protected void createAlchemicalFire() {
        var model = ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.ITEM_ALCHEMICAL_FIRE.asItem(), new TextureMapping().put(TextureSlot.LAYER0, new Material(VIdentifier.mod("item/alchemical_fire_layer0"))).put(TextureSlot.LAYER1, new Material(VIdentifier.mod("item/alchemical_fire_layer1"))), this.modelOutput));
        this.itemModelOutput.accept(ModItems.ITEM_ALCHEMICAL_FIRE.get(), model);
    }

    protected void createRitualKnife() {
        var model = ItemModelUtils.conditional(new ComponentMatches(new DataComponentPredicate.Single<>(ModDataComponents.CHARGED_RITUAL_KNIFE_PREDICATE.get(), ChargedRitualKnifePredicate.INSTANCE)),
                ItemModelUtils.plainModel(ModModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(ModItems.RITUAL_KNIFE.asItem(), "_charged"), new TextureMapping().put(TextureSlot.LAYER0, new Material(VIdentifier.mod("item/ritual_knife_heart"))), this.modelOutput)),
                ItemModelUtils.plainModel(ModModelTemplates.FLAT_ITEM.create(ModItems.RITUAL_KNIFE.asItem(), new TextureMapping().put(TextureSlot.LAYER0, new Material(VIdentifier.mod("item/ritual_knife"))), this.modelOutput)));
        this.itemModelOutput.accept(ModItems.RITUAL_KNIFE.get(), model);
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
                ModItems.HUNTER_HAT_TALL,
                ModItems.HUNTER_HAT_BROAD,
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
                ModItems.VAMPIRE_BOOK,
                ModItems.WEAK_HUMAN_HEART,
                ModItems.GARLIC_BREAD,
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
                ModItems.MOTHER_CORE,
                ModItems.BLOOD_INFUSED_RAW_IRON,
                ModItems.BLOOD_INFUSED_RAW_GOLD,
                ModItems.BLOOD_INFUSED_IRON_INGOT,
                ModItems.BLOOD_INFUSED_GOLD_INGOT,
                ModItems.BLOOD_INFUSED_DIAMOND,
                ModItems.BLOOD_INFUSED_NETHERITE_INGOT,
                ModItems.VAMPIRE_SPAWN_EGG,
                ModItems.ADVANCED_VAMPIRE_SPAWN_EGG,
                ModItems.VAMPIRE_BARON_SPAWN_EGG,
                ModItems.TASK_MASTER_VAMPIRE_SPAWN_EGG,
                ModItems.VAMPIRE_HUNTER_SPAWN_EGG,
                ModItems.ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG,
                ModItems.HUNTER_TRAINER_SPAWN_EGG,
                ModItems.TASK_MASTER_HUNTER_SPAWN_EGG,
                ModItems.GHOST_SPAWN_EGG,
                ModItems.HOLY_WATER_BOTTLE_NORMAL,
                ModItems.HOLY_WATER_BOTTLE_ENHANCED,
                ModItems.HOLY_WATER_BOTTLE_ULTIMATE,
                ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL,
                ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED,
                ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE,
                ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL,
                ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED,
                ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE,
                ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL,
                ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED,
                ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE,
                ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL,
                ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED,
                ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE,
                ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL,
                ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED,
                ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE,
                ModItems.QUARREL_CLIP
                ).map(DeferredItem::asItem);
    }

    protected Map<Item, Identifier> getFlatItemWithTexture() {
        return new HashMap<>() {{
            put(ModItems.ITEM_TENT_SPAWNER.get(), mod("item/item_tent"));
            put(ModBlocks.DIRECT_CURSED_BARK.asItem(), mod("block/cursed_bark"));
        }};
    }
}
