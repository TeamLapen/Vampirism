package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.advancements.critereon.FactionSubPredicate;
import de.teamlapen.vampirism.advancements.critereon.PlayerFactionSubPredicate;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;


public class ModEnchantments {
    public static final ResourceKey<Enchantment> VAMPIRE_SLAYER = ResourceKey.create(Registries.ENCHANTMENT, VResourceLocation.mod("vampire_slayer"));
    public static final ResourceKey<Enchantment> ARROW_FRUGALITY = ResourceKey.create(Registries.ENCHANTMENT, VResourceLocation.mod("arrow_frugality"));

    public static void createEnchantments(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        context.register(VAMPIRE_SLAYER,
                new Enchantment.Builder(
                        Enchantment.definition(
                                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                items.getOrThrow(ModTags.Items.VAMPIRE_SLAYER_ITEMS),
                                5,
                                5,
                                Enchantment.dynamicCost(3, 10),
                                Enchantment.dynamicCost(20, 10),
                                2,
                                EquipmentSlotGroup.MAINHAND)
                )
                        .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(1F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(FactionSubPredicate.faction(VReference.VAMPIRE_FACTION))
                                )
                        ).withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.constant(2F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(FactionSubPredicate.faction(VReference.VAMPIRE_FACTION))
                                )
                        )
                        .build(VAMPIRE_SLAYER.location()));
        context.register(ARROW_FRUGALITY,
                new Enchantment.Builder(
                        Enchantment.definition(
                                items.getOrThrow(ModTags.Items.CROSSBOW_ENCHANTABLE),
                                1,
                                2,
                                Enchantment.dynamicCost(10, 5),
                                Enchantment.constantCost(50),
                                2,
                                EquipmentSlotGroup.MAINHAND))
                        .exclusiveWith(enchantments.getOrThrow(ModTags.Enchantments.CROSSBOW_INCOMPATIBLE))
                        .build(ARROW_FRUGALITY.location()));
    }
}
