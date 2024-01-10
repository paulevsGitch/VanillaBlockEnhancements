package paulevs.vbe.mixin.common;

import net.minecraft.block.StairsBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeRegistry;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEHalfSlabBlock;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(RecipeRegistry.class)
public class RecipeRegistryMixin {
	@Shadow private List recipes;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void vbe_changeItemsCount(CallbackInfo info) {
		this.recipes.forEach(entry -> {
			if (!(entry instanceof ShapedRecipe recipe)) return;
			ItemStack result = recipe.getOutput();
			if (!(result.getType() instanceof BlockItem blockItem)) return;
			if (blockItem.getBlock() instanceof StairsBlock) {
				result.count = 6;
			}
			else if (VBE.BETTER_SLABS_RECIPE.getValue() && (
					blockItem.getBlock() instanceof VBEHalfSlabBlock ||
					blockItem.getBlock() instanceof StoneSlabBlock
			)) {
				result.count = 6;
			}
		});
	}
}
