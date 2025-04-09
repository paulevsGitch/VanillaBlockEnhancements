package paulevs.vbe.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import paulevs.vbe.block.StairsShape;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;
import paulevs.vbe.utils.CreativeUtil;

public class CommonListener {
	@EventListener
	public void onBlockRegister(BlockRegistryEvent event) {
		StairsShape.init();
		VBEBlocks.init();
	}
	
	@EventListener
	public void onItemRegister(ItemRegistryEvent event) {
		VBEItems.init();
	}
	
	@EventListener
	public void onRecipesRegister(RecipeRegisterEvent event) {
		if (event.recipeId != RecipeRegisterEvent.Vanilla.SMELTING.type()) return;
		CreativeUtil.registerBlockConverters();
	}
}
