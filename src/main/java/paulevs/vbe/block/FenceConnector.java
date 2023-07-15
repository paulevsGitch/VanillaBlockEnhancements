package paulevs.vbe.block;

import net.minecraft.block.FenceBlock;
import net.modificationstation.stationapi.api.block.BlockState;

public interface FenceConnector {
	boolean vbe_canConnect(BlockState state);
	
	static FenceConnector cast(FenceBlock block) {
		return (FenceConnector) block;
	}
}
