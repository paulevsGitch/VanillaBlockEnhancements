package paulevs.vbe.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.BaseBlock;
import net.modificationstation.stationapi.api.client.event.color.block.BlockColorsRegisterEvent;
import paulevs.vbe.block.VBEBlocks;

public class ClientListener {
	@EventListener
	private void onBlockColorsRegister(BlockColorsRegisterEvent event) {
		event.blockColors.registerColorProvider(
			(blockState, blockView, blockPos, index) -> BaseBlock.LEAVES.getBaseColor(0),
			VBEBlocks.OAK_LEAVES
		);
		event.blockColors.registerColorProvider(
			(blockState, blockView, blockPos, index) -> BaseBlock.LEAVES.getBaseColor(1),
			VBEBlocks.SPRUCE_LEAVES
		);
		event.blockColors.registerColorProvider(
			(blockState, blockView, blockPos, index) -> BaseBlock.LEAVES.getBaseColor(2),
			VBEBlocks.BIRCH_LEAVES
		);
	}
}
