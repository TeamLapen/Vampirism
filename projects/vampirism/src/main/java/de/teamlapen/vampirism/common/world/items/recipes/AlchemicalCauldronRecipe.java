package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.serialization.StreamCodecExtension;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AlchemicalCauldronRecipe implements Recipe<AlchemicalCauldronRecipeInput> {

    protected final String group;
    private final Either<Ingredient, FluidStackTemplate> fluid;
    protected final Ingredient ingredient;
    protected final ItemStackTemplate result;
    private final List<Holder<ISkill<?>>> skills;
    private final int reqLevel;
    protected final float experience;
    protected final int cookingTime;
    @Nullable
    private PlacementInfo placementInfo;
    private final CommonInfo info;

    public AlchemicalCauldronRecipe(CommonInfo info, String groupIn, Ingredient ingredientIn, Either<Ingredient, FluidStackTemplate> fluidIn, ItemStackTemplate resultIn, List<Holder<ISkill<?>>> skillsIn, int reqLevelIn, int cookTimeIn, float exp) {
        this.info = info;
        this.group = groupIn;
        this.ingredient = ingredientIn;
        this.result = resultIn;
        this.experience = exp;
        this.cookingTime = cookTimeIn;
        this.fluid = fluidIn;
        this.skills = skillsIn;
        this.reqLevel = reqLevelIn;
    }

    @Override
    public boolean showNotification() {
        return this.info.showNotification();
    }

    public boolean canBeCooked(int level, ISkillHandler<IHunterPlayer> skillHandler) {
        if (level < reqLevel) return false;
        return Helper.areSkillsEnabled(skillHandler, skills);
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredient);
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.ALCHEMICAL_CAULDRON_CATEGORY.get();
    }

    @Override
    public ItemStack assemble(AlchemicalCauldronRecipeInput p_345149_) {
        return this.result.create();
    }

    public float getExperience() {
        return this.experience;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    @Override
    public RecipeType<AlchemicalCauldronRecipe> getType() {
        return ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get();
    }

    public Either<Ingredient, FluidStackTemplate> getFluid() {
        return fluid;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getRequiredLevel() {
        return reqLevel;
    }

    public List<Holder<ISkill<?>>> getRequiredSkills() {
        return skills;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public String group() {
        return this.group;
    }



    @Override
    public RecipeSerializer<AlchemicalCauldronRecipe> getSerializer() {
        return ModRecipes.ALCHEMICAL_CAULDRON.get();
    }

    @Override
    public boolean matches(AlchemicalCauldronRecipeInput inv, Level worldIn) {
        boolean match = this.ingredient.test(inv.ingredient());
        var fluidMatch = !inv.fluid().isEmpty() && fluid.map(ingredient1 -> ingredient1.test(inv.fluid()), fluid1 -> {
            try (var transaction = Transaction.openRoot()) {
                var inputHandler = inv.fluid().getCapability(Capabilities.Fluid.ITEM, ItemStackMatch.forRecipeStack(inv.fluid()));
                var extracted = ResourceHandlerUtil.extractFirst(inputHandler, x -> x.is(fluid1.fluid()), fluid1.amount(), transaction);
                return extracted != null && extracted.amount() >= fluid1.amount();
            }
        });
        return switch (inv.testType()) {
            case INPUT_1 -> match;
            case INPUT_2 -> fluidMatch;
            case BOTH -> match && fluidMatch;
        } && inv.skills().map(s -> s.areSkillsEnabled(this.skills)).orElse(true);
    }

    public static final MapCodec<AlchemicalCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    CommonInfo.MAP_CODEC.fieldOf("info").forGetter(p -> p.info),
                    Codec.STRING.optionalFieldOf("group", "").forGetter(p_300832_ -> p_300832_.group),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(p_300833_ -> p_300833_.ingredient),
                    Codec.either(Ingredient.CODEC, FluidStackTemplate.CODEC).fieldOf("fluid").forGetter(s -> s.fluid),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(p_300827_ -> p_300827_.result),
                    ModRegistries.SKILLS.holderByNameCodec().listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(p -> p.skills),
                    Codec.INT.optionalFieldOf("level", 1).forGetter(p -> p.reqLevel),
                    Codec.INT.optionalFieldOf("cookTime", 200).forGetter(p -> p.cookingTime),
                    Codec.FLOAT.optionalFieldOf("experience", 0.2F).forGetter(p -> p.experience)
            ).apply(inst, AlchemicalCauldronRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemicalCauldronRecipe> STREAM_CODEC = StreamCodecExtension.composite(
            CommonInfo.STREAM_CODEC, x -> x.info,
            ByteBufCodecs.STRING_UTF8, AlchemicalCauldronRecipe::group,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemicalCauldronRecipe::getIngredient,
            ByteBufCodecs.either(Ingredient.CONTENTS_STREAM_CODEC, FluidStackTemplate.STREAM_CODEC), AlchemicalCauldronRecipe::getFluid,
            ItemStackTemplate.STREAM_CODEC, AlchemicalCauldronRecipe::result,
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), AlchemicalCauldronRecipe::getRequiredSkills,
            ByteBufCodecs.INT, AlchemicalCauldronRecipe::getRequiredLevel,
            ByteBufCodecs.INT, AlchemicalCauldronRecipe::getCookingTime,
            ByteBufCodecs.FLOAT, AlchemicalCauldronRecipe::getExperience,
            AlchemicalCauldronRecipe::new
    );
}
