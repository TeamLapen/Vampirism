package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.util.serialization.StreamCodecExtension;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Cheaterpaul
 */
public class ShapedWeaponTableRecipe implements IWeaponTableRecipe {

    private final CraftingRecipe.CraftingBookInfo category;
    private final CommonInfo commonInfo;
    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate recipeOutput;
    private final int requiredLevel;
    private final List<Holder<ISkill<?>>> requiredSkills;
    private final int requiredLava;
    @Nullable
    private PlacementInfo placementInfo;

    public ShapedWeaponTableRecipe(CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo category, ShapedRecipePattern pattern, ItemStackTemplate recipeOutputIn, int requiredLevel, List<Holder<ISkill<?>>> requiredSkills, int requiredLava) {
        this.category = category;
        this.commonInfo = commonInfo;
        this.pattern = pattern;
        this.recipeOutput = recipeOutputIn;
        this.requiredLevel = requiredLevel;
        this.requiredSkills = requiredSkills;
        this.requiredLava = requiredLava;
    }

    @Override
    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    @Override
    public int getRequiredLevel() {
        return requiredLevel;
    }

    @Override
    public List<Holder<ISkill<?>>> getRequiredSkills() {
        return requiredSkills;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.recipeOutput.create();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
        }
        return this.placementInfo;
    }

    @Override
    public boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    @Override
    public String group() {
        return this.category.group();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.pattern.width(),
                        this.pattern.height(),
                        this.pattern.ingredients().stream()
                                .map(x -> x.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE))
                                .toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.recipeOutput),
                        new SlotDisplay.ItemSlotDisplay(ModBlocks.WEAPON_TABLE.asItem())
                )
        );
    }

    @Override
    public List<Ingredient> getIngredients() {
        return pattern.ingredients().stream().flatMap(Optional::stream).toList();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public RecipeSerializer<? extends ShapedWeaponTableRecipe> getSerializer() {
        return ModRecipes.SHAPED_CRAFTING_WEAPONTABLE.get();
    }

    public static final MapCodec<ShapedWeaponTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                CommonInfo.MAP_CODEC.forGetter(s -> s.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(p_311732_ -> p_311732_.category),
                ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.recipeOutput),
                Codec.INT.optionalFieldOf("level", 1).forGetter(p -> p.requiredLevel),
                ModRegistries.SKILLS.holderByNameCodec().listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(p -> p.requiredSkills),
                Codec.INT.optionalFieldOf("lava", 0).forGetter(p -> p.requiredLava)
        ).apply(inst, ShapedWeaponTableRecipe::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedWeaponTableRecipe> STREAM_CODEC = StreamCodecExtension.composite(
            CommonInfo.STREAM_CODEC, s -> s.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, s -> s.category,
            ShapedRecipePattern.STREAM_CODEC, s -> s.pattern,
            ItemStackTemplate.STREAM_CODEC, s -> s.recipeOutput,
            ByteBufCodecs.VAR_INT, s -> s.requiredLevel,
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), s -> s.requiredSkills,
            ByteBufCodecs.VAR_INT, s -> s.requiredLava,
            ShapedWeaponTableRecipe::new
    );
}
