package paulevs.vbe.block;

import net.minecraft.block.BaseBlock;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.BlockStateView;

import java.util.List;

public interface StairsShape {
	List<Box> vbe_getStairsShape(BlockStateView view, int x, int y, int z, BlockState state);
	
	static StairsShape cast(BaseBlock block) {
		return (StairsShape) block;
	}
}
