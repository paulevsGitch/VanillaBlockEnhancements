package paulevs.vbe.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import paulevs.vbe.block.StairsShape;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;
import paulevs.vbe.utils.CreativeUtil;

public class CommonListener {
	public static boolean blockRegistered = false;
	
	@EventListener
	private void onBlockRegister(BlockRegistryEvent event) {
		StairsShape.init();
		VBEBlocks.init();
	}
	
	@EventListener
	private void onItemRegister(ItemRegistryEvent event) {
		VBEItems.init();
	}
	
	@EventListener
	private void onRecipesRegister(RecipeRegisterEvent event) {
		SmeltingRecipeRegistry.getInstance().addSmeltingRecipe(VBEBlocks.OAK_LOG.id, new ItemStack(Item.coal, 1, 1));
		SmeltingRecipeRegistry.getInstance().addSmeltingRecipe(VBEBlocks.SPRUCE_LOG.id, new ItemStack(Item.coal, 1, 1));
		SmeltingRecipeRegistry.getInstance().addSmeltingRecipe(VBEBlocks.BIRCH_LOG.id, new ItemStack(Item.coal, 1, 1));
		CreativeUtil.registerBlockConverters();
	}
}
