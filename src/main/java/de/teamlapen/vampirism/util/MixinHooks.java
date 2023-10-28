package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MixinHooks {
    /**
     * JigsawPieces in this list only will be generated once per village
     * <p>
     * holds {@link StructurePoolElement#toString()}'s
     */
    private static final List<String> onlyOneStructure = Lists.newArrayList();
    /**
     * Used by mixins as a fast check if reduced blood vision is enabled
     */
    public static boolean enforcingGlowing_bloodVision = false;

    public static float armorLayerPartialTicks;

    public static void addSingleInstanceStructure(@NotNull List<ResourceLocation> structures) {
        onlyOneStructure.addAll(structures.stream().map(MixinHooks::singleJigsawString).toList());
    }

    public static void replaceSingleInstanceStructure(@NotNull List<ResourceLocation> structures) {
        onlyOneStructure.clear();
        onlyOneStructure.addAll(structures.stream().map(MixinHooks::singleJigsawString).toList());
    }


    public static boolean checkStructures(@NotNull List<? super PoolElementStructurePiece> pieces, @NotNull StructurePoolElement jigsawPiece) {
        if (!onlyOneStructure.contains(jigsawPiece.toString())) return false;
        return pieces.stream().anyMatch(structurePiece -> onlyOneStructure.stream().anyMatch(string -> ((PoolElementStructurePiece) structurePiece).getElement().toString().equals(string)));
    }

    public static float calculateVampireSlayerEnchantments(Entity entity, @NotNull ItemStack item) {
        if (!(entity instanceof Player)) return 0;
        if (!Helper.isVampire(entity)) return 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
        if (!enchantments.containsKey(ModEnchantments.VAMPIRESLAYER.get())) return 0;
        return ModEnchantments.VAMPIRESLAYER.get().getDamageBonus(enchantments.get(ModEnchantments.VAMPIRESLAYER.get()), VReference.VAMPIRE_CREATURE_ATTRIBUTE);
    }

    private static @NotNull String singleJigsawString(@NotNull ResourceLocation resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

}
