package de.dafuqs.artis.compat.emi;

import dev.emi.emi.api.stack.*;
import net.id.incubus_core.recipe.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class EMIHelper {
	
	public static List<EmiIngredient> ofIngredientStacks(List<IngredientStack> ingredientStacks) {
		List<EmiIngredient> list = new ArrayList<>();
		for(IngredientStack stack : ingredientStacks) {
			list.add(ofIngredientStack(stack));
		}
		return list;
	}
	
	public static EmiIngredient ofIngredientStack(@NotNull IngredientStack ingredientStack) {
		return EmiIngredient.of(ingredientStack.getStacks().stream().map(EmiStack::of).toList());
	}
	
}
