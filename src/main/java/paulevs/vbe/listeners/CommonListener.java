package paulevs.vbe.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import paulevs.vbe.block.StairsShape;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class CommonListener {
	public static boolean blockRegistered = false;
	
	@EventListener
	private void onBlockRegister(BlockRegistryEvent event) {
		StairsShape.init();
		VBEBlocks.init();
		blockRegistered = true;
	}
	
	@EventListener
	private void onBlockRegister(ItemRegistryEvent event) {
		VBEItems.init();
	}
}
