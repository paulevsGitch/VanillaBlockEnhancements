package paulevs.vbe.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import paulevs.bhcreative.registry.TabRegistryEvent;
import paulevs.vbe.block.VBEBlocks;

public class CreativeListener {
	@EventListener
	private void onCreativeTabsInit(TabRegistryEvent event) {
		BakedModel model = StationRenderAPI.getBakedModelManager().getBlockModels().getModel(VBEBlocks.STONE_SLAB_HALF.getDefaultState());
		System.out.println("Model " + model);
		model = StationRenderAPI.getBakedModelManager().getBlockModels().getModel(VBEBlocks.STONE_SLAB_FULL.getDefaultState());
		System.out.println("Model " + model);
		/*List<ItemStack> items = VanillaTabListener.tabOtherBlocks.getItems();
		for (int i = 0; i < items.size(); i++) {
			ItemStack stack = items.get(i);
			if (!(stack.getType() instanceof BlockItem blockItem)) continue;
			if (blockItem.getBlock() != BaseBlock.STONE_SLAB) continue;
			items.set(i, new ItemStack(VBEBlocks.STONE_SLAB));
			return;
		}*/
	}
}
