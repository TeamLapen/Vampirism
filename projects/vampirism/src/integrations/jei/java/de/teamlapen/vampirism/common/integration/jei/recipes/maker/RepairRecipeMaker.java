package de.teamlapen.vampirism.common.integration.jei.recipes.maker;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RepairRecipeMaker {

    public static List<IJeiAnvilRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        IIngredientHelper<ItemStack> ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        return getRepairData().flatMap(x -> getRepairRecipes(x, vanillaRecipeFactory, ingredientHelper)).toList();
    }


    private static Stream<RepairData> getRepairData() {
        return Stream.concat(
                Stream.of(
                        new RepairData(ItemTags.IRON_TOOL_MATERIALS,
                                ModItems.HUNTER_AXE_NORMAL,
                                ModItems.HUNTER_AXE_ENHANCED,
                                ModItems.HUNTER_AXE_ULTIMATE,
                                ModItems.BASIC_TECH_CROSSBOW,
                                ModItems.ENHANCED_TECH_CROSSBOW,
                                ModItems.HUNTER_COAT_HEAD_NORMAL,
                                ModItems.HUNTER_COAT_HEAD_ENHANCED,
                                ModItems.HUNTER_COAT_HEAD_ULTIMATE,
                                ModItems.HUNTER_COAT_CHEST_NORMAL,
                                ModItems.HUNTER_COAT_CHEST_ENHANCED,
                                ModItems.HUNTER_COAT_CHEST_ULTIMATE,
                                ModItems.HUNTER_COAT_LEGS_NORMAL,
                                ModItems.HUNTER_COAT_LEGS_ENHANCED,
                                ModItems.HUNTER_COAT_LEGS_ULTIMATE,
                                ModItems.HUNTER_COAT_FEET_NORMAL,
                                ModItems.HUNTER_COAT_FEET_ENHANCED,
                                ModItems.HUNTER_COAT_FEET_ULTIMATE),
                        new RepairData(Tags.Items.STRINGS,
                                ModItems.BASIC_CROSSBOW,
                                ModItems.BASIC_DOUBLE_CROSSBOW,
                                ModItems.ENHANCED_CROSSBOW,
                                ModItems.ENHANCED_DOUBLE_CROSSBOW),
                        new RepairData(Tags.Items.LEATHERS,
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
                                ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE),
                        new RepairData(ModItemTags.HEART,
                                ModItems.VAMPIRE_CLOTHING_CROWN,
                                ModItems.VAMPIRE_CLOTHING_HAT,
                                ModItems.VAMPIRE_CLOTHING_LEGS,
                                ModItems.VAMPIRE_CLOAK_WHITE,
                                ModItems.VAMPIRE_CLOAK_ORANGE,
                                ModItems.VAMPIRE_CLOAK_LIGHT_BLUE,
                                ModItems.VAMPIRE_CLOAK_YELLOW,
                                ModItems.VAMPIRE_CLOAK_LIME,
                                ModItems.VAMPIRE_CLOAK_PINK,
                                ModItems.VAMPIRE_CLOAK_GRAY,
                                ModItems.VAMPIRE_CLOAK_LIGHT_GRAY,
                                ModItems.VAMPIRE_CLOAK_CYAN,
                                ModItems.VAMPIRE_CLOAK_PURPLE,
                                ModItems.VAMPIRE_CLOAK_BLUE,
                                ModItems.VAMPIRE_CLOAK_BROWN,
                                ModItems.VAMPIRE_CLOAK_GREEN,
                                ModItems.VAMPIRE_CLOAK_RED,
                                ModItems.VAMPIRE_CLOAK_BLACK)),
                IntStream.of(0, 1, 2, 3, 4).mapToObj(PureLevel::new).flatMap(x -> Stream.of(
                        of(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, x, ModItems.BLOOD_INFUSED_IRON_INGOT).display(),
                                new ItemStackTemplate(ModItems.HEART_SEEKER_NORMAL, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), x).build()),
                                new ItemStackTemplate(ModItems.HEART_STRIKER_NORMAL, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), x).build())),
                        of(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, x, ModItems.BLOOD_INFUSED_DIAMOND).display(),
                                new ItemStackTemplate(ModItems.HEART_SEEKER_ENHANCED, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), x).build()),
                                new ItemStackTemplate(ModItems.HEART_STRIKER_ENHANCED, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), x).build())),
                        of(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, x, ModItems.BLOOD_INFUSED_NETHERITE_INGOT).display(),
                                new ItemStackTemplate(ModItems.HEART_SEEKER_ULTIMATE, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), x).build()),
                                new ItemStackTemplate(ModItems.HEART_STRIKER_ULTIMATE, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), x).build()))

                ))
        );
    }


    private static RepairData of(SlotDisplay repairTag, ItemStackTemplate... templates) {
        return new RepairData(repairTag, Arrays.stream(templates).toList());
    }

    private record RepairData(SlotDisplay repairIngredient, List<ItemStackTemplate> repairable) {

        @SafeVarargs
        public RepairData(TagKey<Item> repairTag, Holder<Item>... itemStacks) {
            this(new SlotDisplay.TagSlotDisplay(repairTag), Arrays.stream(itemStacks).map(ItemStackTemplate::new).toList());
        }

        public RepairData(SlotDisplay repairTag, ItemStackTemplate... templates) {
            this(repairTag, Arrays.stream(templates).toList());
        }

        public SlotDisplay getRepairIngredient() {
            return repairIngredient;
        }

        public List<ItemStackTemplate> getRepairable() {
            return repairable;
        }
    }

    /**
     * from {@link AnvilRecipeMaker}
     */
    @SuppressWarnings("JavadocReference")
    private static Stream<IJeiAnvilRecipe> getRepairRecipes(
            RepairData repairData,
            IVanillaRecipeFactory vanillaRecipeFactory,
            IIngredientHelper<ItemStack> ingredientHelper
    ) {
        SlotDisplay repairIngredient = repairData.getRepairIngredient();
        List<ItemStackTemplate> repairables = repairData.getRepairable();

        Minecraft minecraft = Minecraft.getInstance();
        ContextMap contextmap = SlotDisplayContext.fromLevel(Objects.requireNonNull(minecraft.level));
        List<ItemStack> repairMaterials = repairIngredient.resolveForStacks(contextmap);

        return repairables.stream()
                .mapMulti((template, consumer) -> {
                    ItemStack itemStack = template.create();
                    String uid = getStringName(itemStack);
                    String ingredientIdPath = sanitizePath(uid);
                    String itemModId = template.typeHolder().unwrapKey().orElseThrow().identifier().getNamespace();

                    var damagedThreeQuarters = template.apply(DataComponentPatch.builder().set(DataComponents.DAMAGE, itemStack.getMaxDamage() * 3 / 4).build());
                    var damagedHalf = template.apply(DataComponentPatch.builder().set(DataComponents.DAMAGE, itemStack.getMaxDamage() / 2).build());

                    var damagedThreeQuartersSingletonList = List.of(damagedThreeQuarters);

                    IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(
                            damagedThreeQuartersSingletonList,
                            damagedThreeQuartersSingletonList,
                            List.of(damagedHalf),
                            Identifier.fromNamespaceAndPath(itemModId, "anvil.self_repair." + ingredientIdPath)
                    );
                    consumer.accept(repairWithSame);

                    if (!repairMaterials.isEmpty()) {
                        ItemStack damagedFully = template.apply(DataComponentPatch.builder().set(DataComponents.DAMAGE, itemStack.getMaxDamage()).build());
                        IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(
                                List.of(damagedFully),
                                repairMaterials,
                                damagedThreeQuartersSingletonList,
                                Identifier.fromNamespaceAndPath(itemModId, "anvil.materials_repair." + ingredientIdPath)
                        );
                        consumer.accept(repairWithMaterial);
                    }
                });
    }

    private static String sanitizePath(String path) {
        char[] charArray = path.toCharArray();
        boolean valid = true;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (!Identifier.validPathChar(c)) {
                charArray[i] = '.';
                valid = false;
            }
        }
        if (valid) {
            return path;
        }
        return new String(charArray);
    }

    private static String getStringName(ItemStack itemStack) {
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
        if (enchantments.isEmpty()) {
            return "";
        }
        List<String> strings = new ArrayList<>();
        for (Holder<Enchantment> e : enchantments.keySet()) {
            Optional<ResourceKey<Enchantment>> enchantmentResourceKey = e.unwrapKey();
            if (enchantmentResourceKey.isPresent()) {
                String s = enchantmentResourceKey.orElseThrow().identifier() + ".lvl" + enchantments.getLevel(e);
                strings.add(s);
            }
        }

        StringJoiner joiner = new StringJoiner(",", "[", "]");
        strings.sort(null);
        for (String s : strings) {
            joiner.add(s);
        }
        return joiner.toString();
    }
}
