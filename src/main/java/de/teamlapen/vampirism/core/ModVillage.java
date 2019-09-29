package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import de.teamlapen.vampirism.items.BloodBottleIItem;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.schedule.ScheduleBuilder;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Random;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModVillage {

    public static final VillagerProfession hunter_expert = getNull();
    public static final VillagerProfession vampire_expert = getNull();

    public static final PointOfInterestType hunter_faction = getNull();
    public static final PointOfInterestType vampire_faction = getNull();

    public static final Schedule converted_default = getNull();

    public static final VillagerTrades.ITrade[] converted_trades = new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(ModItems.human_heart, 9, 2, 2), new VillagerTrades.ItemsForEmeraldsTrade(ModItems.human_heart, 3, 9, 2), new ModVillage.ItemsForEmeraldsTradeWithDamage(BloodBottleIItem.getStackWithDamage(9), 1, 3, 12, 2)};

    static void registerProfessions(IForgeRegistry<VillagerProfession> registry) {
        VillagerProfession vampire_expert = new VillagerProfession("vampire_expert", vampire_faction, ImmutableSet.of(), ImmutableSet.of()).setRegistryName(REFERENCE.MODID, "vampire_expert");
        VillagerProfession hunter_expert = new VillagerProfession("hunter_expert", hunter_faction, ImmutableSet.of(), ImmutableSet.of()).setRegistryName(REFERENCE.MODID, "hunter_expert");
        registry.register(vampire_expert);
        registry.register(hunter_expert);
        VillagerTrades.field_221239_a.computeIfAbsent(hunter_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{new ItemsForSouls(new Price(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_normal), new ItemStack(ModItems.obsidian_armor_feet_normal), new ItemStack(ModItems.armor_of_swiftness_feet_normal)}, new Price(1, 1)), new ItemsForSouls(new Price(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_normal), new ItemStack(ModItems.obsidian_armor_legs_normal), new ItemStack(ModItems.armor_of_swiftness_legs_normal)}, new Price(1, 1)), new ItemsForSouls(new Price(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_normal), new ItemStack(ModItems.obsidian_armor_chest_normal), new ItemStack(ModItems.armor_of_swiftness_chest_normal)}, new Price(1, 1)), new ItemsForSouls(new Price(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_normal), new ItemStack(ModItems.obsidian_armor_head_normal), new ItemStack(ModItems.armor_of_swiftness_head_normal)}, new Price(1, 1)), new ItemsForSouls(new Price(10, 20), ModItems.item_garlic, new Price(2, 5)), new ItemsForSouls(new Price(50, 100), Items.DIAMOND, new Price(1, 1))},
                2, new VillagerTrades.ITrade[]{new ItemsForSouls(new Price(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_enhanced), new ItemStack(ModItems.obsidian_armor_feet_enhanced), new ItemStack(ModItems.armor_of_swiftness_feet_enhanced)}, new Price(1, 1)), new ItemsForSouls(new Price(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_enhanced), new ItemStack(ModItems.obsidian_armor_legs_enhanced), new ItemStack(ModItems.armor_of_swiftness_legs_enhanced)}, new Price(1, 1)), new ItemsForSouls(new Price(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_enhanced), new ItemStack(ModItems.obsidian_armor_chest_enhanced), new ItemStack(ModItems.armor_of_swiftness_chest_enhanced)}, new Price(1, 1)), new ItemsForSouls(new Price(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_enhanced), new ItemStack(ModItems.obsidian_armor_head_enhanced), new ItemStack(ModItems.armor_of_swiftness_head_enhanced)}, new Price(1, 1)), new ItemsForSouls(new Price(40, 90), Items.DIAMOND, new Price(1, 2))},
                3, new VillagerTrades.ITrade[]{new ItemsForSouls(new Price(100, 200), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_ultimate), new ItemStack(ModItems.obsidian_armor_feet_ultimate), new ItemStack(ModItems.armor_of_swiftness_feet_ultimate), new ItemStack(ModItems.hunter_coat_legs_ultimate), new ItemStack(ModItems.obsidian_armor_legs_ultimate), new ItemStack(ModItems.armor_of_swiftness_legs_ultimate), new ItemStack(ModItems.hunter_coat_chest_ultimate), new ItemStack(ModItems.obsidian_armor_chest_ultimate), new ItemStack(ModItems.armor_of_swiftness_chest_ultimate), new ItemStack(ModItems.hunter_coat_head_ultimate), new ItemStack(ModItems.obsidian_armor_head_ultimate), new ItemStack(ModItems.armor_of_swiftness_head_ultimate)}, new Price(1, 1))}
        ));
        VillagerTrades.field_221239_a.computeIfAbsent(vampire_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{new ItemsForHeart(new Price(10, 15), ModItems.pure_blood_0, new Price(1, 1)), new ItemsForHeart(new Price(25, 30), ModItems.pure_blood_1, new Price(1, 1)), new ItemsForHeart(new Price(30, 40), ModItems.pure_blood_2, new Price(1, 1)), new ItemsForHeart(new Price(1, 5), ModBlocks.coffin, new Price(1, 1))},
                2, new VillagerTrades.ITrade[]{new BloodBottleForHeart(new Price(3, 12), new Price(1, 15), 9), new ItemsForHeart(new Price(30, 40), ModItems.pure_blood_4, new Price(1, 1)), new ItemsForHeart(new Price(20, 30), ModItems.pure_blood_3, new Price(1, 1))},
                3, new VillagerTrades.ITrade[]{new ItemsForHeart(new Price(10, 30), new ItemStack[]{new ItemStack(ModItems.vampire_cloak_black_blue), new ItemStack(ModItems.vampire_cloak_black_red), new ItemStack(ModItems.vampire_cloak_black_white), new ItemStack(ModItems.vampire_cloak_red_black), new ItemStack(ModItems.vampire_cloak_white_black)}, new Price(1, 1))}
        ));
    }

    static void registerVillagePointOfInterestType(IForgeRegistry<PointOfInterestType> registry) {
        PointOfInterestType hunter_faction = new PointOfInterestType("hunter_faction", ImmutableSet.of(ModBlocks.totem_top_hunter.getStateContainer().getBaseState()), 1, null, 1).setRegistryName(REFERENCE.MODID, "hunter_faction");
        PointOfInterestType vampire_faction = new PointOfInterestType("vampire_faction", ImmutableSet.of(ModBlocks.totem_top_vampire.getStateContainer().getBaseState()), 1, null, 1).setRegistryName(REFERENCE.MODID, "vampire_faction");
        registry.register(hunter_faction);
        registry.register(vampire_faction);
        PointOfInterestType.func_221052_a(hunter_faction);
        PointOfInterestType.func_221052_a(vampire_faction);
    }

    static void registerSchedule(IForgeRegistry<Schedule> registry) {
        registry.register(new ScheduleBuilder(new Schedule()).add(12000, Activity.IDLE).add(10, Activity.REST).add(14000, Activity.WORK).add(21000, Activity.MEET).add(23000, Activity.IDLE).build().setRegistryName(REFERENCE.MODID, "converted_default"));
    }

    /**
     * copy of {@link VillagerTrades.ItemsForEmeraldsTrade} with damage of itemstack
     */
    public static class ItemsForEmeraldsTradeWithDamage implements VillagerTrades.ITrade {
        private final ItemStack result;
        private final int emeraldAmount;
        private final int resultAmount;
        private final int maxUses;
        private final int givenXP;
        private final float priceMultiplier;

        public ItemsForEmeraldsTradeWithDamage(Item result, int emeraldAmount, int resultAmount, int givenXP) {
            this(new ItemStack(result), emeraldAmount, resultAmount, 12, givenXP);
        }

        public ItemsForEmeraldsTradeWithDamage(Item result, int emeraldAmount, int resultAmount, int maxUses, int givenXP) {
            this(new ItemStack(result), emeraldAmount, resultAmount, maxUses, givenXP);
        }

        public ItemsForEmeraldsTradeWithDamage(ItemStack result, int emeraldAmount, int resultAmount, int maxUses, int givenXP) {
            this(result, emeraldAmount, resultAmount, maxUses, givenXP, 0.05F);
        }

        public ItemsForEmeraldsTradeWithDamage(ItemStack result, int emeraldAmount, int resultAmount, int maxUses, int givenXP, float priceMultiplier) {
            this.result = result;
            this.emeraldAmount = emeraldAmount;
            this.resultAmount = resultAmount;
            this.maxUses = maxUses;
            this.givenXP = givenXP;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack second = new ItemStack(result.getItem(), resultAmount);
            second.setDamage(result.getDamage());
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldAmount), second, this.maxUses, this.givenXP, this.priceMultiplier);
        }
    }

    static class ItemsForSouls implements VillagerTrades.ITrade {
        private final int xp;
        private final Price price;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;

        ItemsForSouls(Price priceIn, IItemProvider sellingItemIn, Price sellingIn) {
            this(priceIn, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, 2, 8);
        }

        ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn) {
            this(priceIn, sellingItemIn, sellingIn, 2, 8);
        }

        ItemsForSouls(Price priceIn, IItemProvider sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = new ItemStack[]{new ItemStack(sellingItemIn.asItem())};
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        ItemsForSouls(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = sellingItemIn;
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(ModItems.soul_orb_vampire, price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    static class ItemsForHeart implements VillagerTrades.ITrade {
        private final int xp;
        private final Price price;
        private final ItemStack[] sellingItem;
        private final Price selling;
        private final int maxUses;

        ItemsForHeart(Price priceIn, IItemProvider sellingItemIn, Price sellingIn) {
            this(priceIn, new ItemStack[]{new ItemStack(sellingItemIn.asItem())}, sellingIn, 2, 8);
        }

        ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn) {
            this(priceIn, sellingItemIn, sellingIn, 2, 8);
        }

        ItemsForHeart(Price priceIn, IItemProvider sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = new ItemStack[]{new ItemStack(sellingItemIn.asItem())};
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        ItemsForHeart(Price priceIn, ItemStack[] sellingItemIn, Price sellingIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.sellingItem = sellingItemIn;
            this.selling = sellingIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(ModItems.human_heart, price.getPrice(random)), new ItemStack(sellingItem[random.nextInt(sellingItem.length)].getItem(), selling.getPrice(random)), maxUses, xp, 0.2F);
        }
    }

    static class BloodBottleForHeart implements VillagerTrades.ITrade {
        private final int xp;
        private final Price price;
        private final Price selling;
        private final int damage;
        private final int maxUses;

        BloodBottleForHeart(Price priceIn, Price sellingIn, int damageIn) {
            this(priceIn, sellingIn, damageIn, 2, 8);
        }

        BloodBottleForHeart(Price priceIn, Price sellingIn, int damageIn, int xpIn, int maxUsesIn) {
            this.price = priceIn;
            this.selling = sellingIn;
            this.damage = damageIn;
            this.xp = xpIn;
            this.maxUses = maxUsesIn;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack bottle = new ItemStack(ModItems.blood_bottle, selling.getPrice(random));
            bottle.setDamage(damage);
            return new MerchantOffer(new ItemStack(ModItems.human_heart, price.getPrice(random)), bottle, maxUses, xp, 0.2F);
        }
    }

    static class Price {
        private final int min;
        private final int max;

        Price(int minIn, int maxIn) {
            this.max = maxIn;
            this.min = minIn;
        }

        int getPrice(Random rand) {
            if (min >= max) return min;
            else return min + rand.nextInt(max - min);
        }
    }
}
