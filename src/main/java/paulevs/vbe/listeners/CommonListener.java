package paulevs.vbe.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import paulevs.vbe.block.VBEBlocks;

public class CommonListener {
	public static boolean blockRegistered = false;
	
	@EventListener
	private void onBlockRegister(BlockRegistryEvent event) {
		VBEBlocks.init();
		blockRegistered = true;
	}
}
