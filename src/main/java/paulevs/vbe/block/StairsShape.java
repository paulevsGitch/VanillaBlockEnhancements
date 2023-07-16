package paulevs.vbe.block;

import net.minecraft.block.BaseBlock;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;

import java.util.List;

public interface StairsShape {
	List<Box> vbe_getStairsShape(BlockState state);
	
	static StairsShape cast(BaseBlock block) {
		return (StairsShape) block;
	}
}
