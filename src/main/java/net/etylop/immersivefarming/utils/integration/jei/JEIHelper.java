package net.etylop.immersivefarming.utils.integration.jei;

import blusunrize.immersiveengineering.common.util.compat.jei.IEFluidTooltipCallback;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.api.crafting.IFCachedRecipeList;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.gui.ComposterScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nonnull;
import java.util.List;

@JeiPlugin
public class JEIHelper implements IModPlugin{

	private RecipeType<ComposterRecipe> composter_type;
	public static IDrawableStatic slotDrawable;
	public static IDrawableStatic fluidDrawable;
	public static IDrawableStatic fluidOverlay;
	public static IRecipeSlotTooltipCallback fluidTooltipCallback = new IEFluidTooltipCallback();

	@Override
	@Nonnull
	public ResourceLocation getPluginUid(){
		return new ResourceLocation(ImmersiveFarming.MOD_ID);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		ComposterRecipeCategory composter = new ComposterRecipeCategory(guiHelper);

		this.composter_type = composter.getRecipeType();

		registration.addRecipeCategories(composter);

		slotDrawable = guiHelper.getSlotDrawable();
		ResourceLocation background = new ResourceLocation(ImmersiveFarming.MOD_ID, "textures/gui/composter.png");
		fluidDrawable = guiHelper.drawableBuilder(background, 107, 18, 20, 51).build();
		fluidOverlay = guiHelper.drawableBuilder(background, 177, 31, 20, 51).build();
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration){
		registration.addRecipes(this.composter_type, getRecipes(ComposterRecipe.RECIPES));
	}

	private <T extends Recipe<?>> List<T> getRecipes(IFCachedRecipeList<T> cachedList)
	{
		return List.copyOf(cachedList.getRecipes(Minecraft.getInstance().level));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(new ItemStack(IFBlocks.COMPOSTER.get()), this.composter_type);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addRecipeClickArea(ComposterScreen.class, 132, 30, 19, 30, this.composter_type);
	}
}