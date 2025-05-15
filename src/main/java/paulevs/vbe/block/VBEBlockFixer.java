package paulevs.vbe.block;

import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;

public class VBEBlockFixer {
	public static BlockState fixDoor(BlockState state, int meta) {
		if (skipFix(state, meta)) return state;
		boolean bottom = meta < 8;
		boolean open = (meta & 4) == 0;
		Direction dir = Direction.fromHorizontal(meta & 3).getOpposite();
		return state
			.with(VBEBlockProperties.TOP_BOTTOM, bottom ? TopBottom.BOTTOM : TopBottom.TOP)
			.with(Properties.HORIZONTAL_FACING, dir)
			.with(VBEBlockProperties.OPENED, open);
	}
	
	public static int getDoorMeta(BlockState state) {
		int meta = state.get(Properties.HORIZONTAL_FACING).getHorizontal() & 3;
		if (!state.get(VBEBlockProperties.OPENED)) meta |= 0b100;
		if (state.get(VBEBlockProperties.TOP_BOTTOM) == TopBottom.TOP) meta |= 0b1000;
		return meta;
	}
	
	public static BlockState fixStairs(BlockState state, int meta) {
		if (skipFix(state, meta)) return state;
		boolean bottom = meta < 8;
		boolean open = (meta & 4) == 0;
		Direction dir = Direction.fromHorizontal(meta & 3).getOpposite();
		return state
			.with(VBEBlockProperties.TOP_BOTTOM, bottom ? TopBottom.BOTTOM : TopBottom.TOP)
			.with(Properties.HORIZONTAL_FACING, dir)
			.with(VBEBlockProperties.OPENED, open);
	}
	
	private static boolean skipFix(BlockState state, int meta) {
		return meta == 0 || state != state.getBlock().getDefaultState();
	}
}
