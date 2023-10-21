package paulevs.vbe.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.block.BlockState;

public interface CustomBreakingRender {
	@Environment(EnvType.CLIENT)
	default BlockState vbe_getBreakingState(BlockState state) {
		return state;
	}
	
	@Environment(EnvType.CLIENT)
	default void vbe_setSelection(BlockState state, float dx, float dy, float dz) {}
}
