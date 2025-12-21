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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        return Stream.of(
            Stream.of(
                new RepairData(ItemTags.IRON_TOOL_MATERIALS,
                    ModItems.HUNTER_AXE_NORMAL.toStack(),
                    ModItems.HUNTER_AXE_ENHANCED.toStack(),
                    ModItems.HUNTER_AXE_ULTIMATE.toStack(),
                    ModItems.BASIC_TECH_CROSSBOW.toStack(),
                    ModItems.ENHANCED_TECH_CROSSBOW.toStack(),
                    ModItems.HUNTER_COAT_HEAD_NORMAL.toStack(),
                    ModItems.HUNTER_COAT_HEAD_ENHANCED.toStack(),
                    ModItems.HUNTER_COAT_HEAD_ULTIMATE.toStack(),
                    ModItems.HUNTER_COAT_CHEST_NORMAL.toStack(),
                    ModItems.HUNTER_COAT_CHEST_ENHANCED.toStack(),
                    ModItems.HUNTER_COAT_CHEST_ULTIMATE.toStack(),
                        ModItems.HUNTER_COAT_LEGS_NORMAL.toStack(),
                        ModItems.HUNTER_COAT_LEGS_ENHANCED.toStack(),
                        ModItems.HUNTER_COAT_LEGS_ULTIMATE.toStack(),
                        ModItems.HUNTER_COAT_FEET_NORMAL.toStack(),
                        ModItems.HUNTER_COAT_FEET_ENHANCED.toStack(),
                        ModItems.HUNTER_COAT_FEET_ULTIMATE.toStack()),
                new RepairData(Tags.Items.STRINGS,
                        ModItems.BASIC_CROSSBOW.toStack(),
                        ModItems.BASIC_DOUBLE_CROSSBOW.toStack(),
                        ModItems.ENHANCED_CROSSBOW.toStack(),
                        ModItems.ENHANCED_DOUBLE_CROSSBOW.toStack()),
                new RepairData(Tags.Items.LEATHERS,
                        ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.toStack(),
                        ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.toStack()),
            new RepairData(ModItemTags.HEART,
                    ModItems.VAMPIRE_CLOTHING_CROWN.toStack(),
                    ModItems.VAMPIRE_CLOTHING_HAT.toStack(),
                    ModItems.VAMPIRE_CLOTHING_LEGS.toStack(),
                    ModItems.VAMPIRE_CLOAK_WHITE.toStack(),
                    ModItems.VAMPIRE_CLOAK_ORANGE.toStack(),
                    ModItems.VAMPIRE_CLOAK_LIGHT_BLUE.toStack(),
                    ModItems.VAMPIRE_CLOAK_YELLOW.toStack(),
                    ModItems.VAMPIRE_CLOAK_LIME.toStack(),
                    ModItems.VAMPIRE_CLOAK_PINK.toStack(),
                    ModItems.VAMPIRE_CLOAK_GRAY.toStack(),
                    ModItems.VAMPIRE_CLOAK_LIGHT_GRAY.toStack(),
                    ModItems.VAMPIRE_CLOAK_CYAN.toStack(),
                    ModItems.VAMPIRE_CLOAK_PURPLE.toStack(),
                    ModItems.VAMPIRE_CLOAK_BLUE.toStack(),
                    ModItems.VAMPIRE_CLOAK_BROWN.toStack(),
                    ModItems.VAMPIRE_CLOAK_GREEN.toStack(),
                    ModItems.VAMPIRE_CLOAK_RED.toStack(),
                    ModItems.VAMPIRE_CLOAK_BLACK.toStack())),
                IntStream.of(0,1,2,3,4).mapToObj(PureLevel::new).flatMap(x-> Stream.of(
                        new RepairData(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, x, ModItems.BLOOD_INFUSED_IRON_INGOT).display(),
                                ModItems.HEART_SEEKER_NORMAL.toStack().vampirism$with(ModDataComponents.PURE_LEVEL, x),
                                ModItems.HEART_STRIKER_NORMAL.toStack().vampirism$with(ModDataComponents.PURE_LEVEL, x)),
                        new RepairData(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, x, ModItems.BLOOD_INFUSED_DIAMOND).display(),
                                ModItems.HEART_SEEKER_ENHANCED.toStack().vampirism$with(ModDataComponents.PURE_LEVEL, x),
                                ModItems.HEART_STRIKER_ENHANCED.toStack().vampirism$with(ModDataComponents.PURE_LEVEL, x)),
                        new RepairData(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, x, ModItems.BLOOD_INFUSED_NETHERITE_INGOT).display(),
                                ModItems.HEART_SEEKER_ULTIMATE.toStack().vampirism$with(ModDataComponents.PURE_LEVEL, x),
                                ModItems.HEART_STRIKER_ULTIMATE.toStack().vampirism$with(ModDataComponents.PURE_LEVEL, x))

                ))
        ).reduce(Stream.of(), Stream::concat);
    }

    private static class  RepairData {
        private final SlotDisplay repairIngredient;
        private final List<ItemStack> repairables;

        public RepairData(TagKey<Item> repairTag, ItemStack... repairables) {
            this.repairIngredient = new SlotDisplay.TagSlotDisplay(repairTag);
            this.repairables = List.of(repairables);
        }

        public RepairData(SlotDisplay repairIngredient, ItemStack... repairables) {
            this.repairIngredient = repairIngredient;
            this.repairables = List.of(repairables);
        }

        public SlotDisplay getRepairIngredient() {
            return repairIngredient;
        }

        public List<ItemStack> getRepairables() {
            return repairables;
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
        List<ItemStack> repairables = repairData.getRepairables();

        Minecraft minecraft = Minecraft.getInstance();
        ContextMap contextmap = SlotDisplayContext.fromLevel(Objects.requireNonNull(minecraft.level));
        List<ItemStack> repairMaterials = repairIngredient.resolveForStacks(contextmap);

        return repairables.stream()
                .mapMulti((itemStack, consumer) -> {
                    String uid = getStringName(itemStack);
                    String ingredientIdPath = sanitizePath(uid);
                    String itemModId = ingredientHelper.getResourceLocation(itemStack).getNamespace();

                    ItemStack damagedThreeQuarters = itemStack.copy();
                    damagedThreeQuarters.setDamageValue(damagedThreeQuarters.getMaxDamage() * 3 / 4);
                    ItemStack damagedHalf = itemStack.copy();
                    damagedHalf.setDamageValue(damagedHalf.getMaxDamage() / 2);

                    var damagedThreeQuartersSingletonList = List.of(damagedThreeQuarters);

                    IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(
                            damagedThreeQuartersSingletonList,
                            damagedThreeQuartersSingletonList,
                            List.of(damagedHalf),
                            ResourceLocation.fromNamespaceAndPath(itemModId, "anvil.self_repair." + ingredientIdPath)
                    );
                    consumer.accept(repairWithSame);

                    if (!repairMaterials.isEmpty()) {
                        ItemStack damagedFully = itemStack.copy();
                        damagedFully.setDamageValue(damagedFully.getMaxDamage());
                        IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(
                                List.of(damagedFully),
                                repairMaterials,
                                damagedThreeQuartersSingletonList,
                                ResourceLocation.fromNamespaceAndPath(itemModId, "anvil.materials_repair." + ingredientIdPath)
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
            if (!ResourceLocation.validPathChar(c)) {
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
                String s = enchantmentResourceKey.orElseThrow().location() + ".lvl" + enchantments.getLevel(e);
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
