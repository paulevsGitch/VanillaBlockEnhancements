package paulevs.vbe.block;

import net.minecraft.block.FenceBlock;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface FenceConnector {
	boolean vbe_canConnect(BlockState state, Direction face);
	
	static FenceConnector cast(FenceBlock block) {
		return (FenceConnector) block;
	}
}
