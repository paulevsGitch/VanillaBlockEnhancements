package paulevs.vbe.block;

import net.minecraft.block.DoorBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.world.chunk.ChunkSection;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;

public class VBEBlockFixer {
	public static void fixChunkSection(ChunkSection section) {
		for (short i = 0; i < 4096; i++) {
			byte dx = (byte) (i & 15);
			byte dy = (byte) ((i >> 4) & 15);
			byte dz = (byte) (i >> 8);
			BlockState state = section.getBlockState(dx, dy, dz);
			if (state.getBlock() instanceof DoorBlock && VBE.ENHANCED_DOORS.getValue()) {
				int meta = section.getMeta(dx, dy, dz);
				BlockState fixed = fixDoor(state, meta);
				if (state == fixed) continue;
				section.setBlockState(dx, dy, dz, fixed);
			}
			else if (state.getBlock() instanceof StairsBlock && VBE.ENHANCED_STAIRS.getValue()) {
				int meta = section.getMeta(dx, dy, dz);
				BlockState fixed = fixStairs(state, meta);
				if (state == fixed) continue;
				section.setBlockState(dx, dy, dz, fixed);
			}
			else if (state.getBlock() instanceof TrapdoorBlock && VBE.ENHANCED_TRAPDOORS.getValue()) {
				int meta = section.getMeta(dx, dy, dz);
				BlockState fixed = fixTrapdoor(state, meta);
				if (state == fixed) continue;
				section.setBlockState(dx, dy, dz, fixed);
			}
		}
	}
	
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
	
	private static BlockState fixStairs(BlockState state, int meta) {
		if (skipFix(state, meta)) return state;
		Direction dir = switch (meta & 3) {
			case 0 -> Direction.SOUTH;
			case 2 -> Direction.WEST;
			case 3 -> Direction.EAST;
			default -> Direction.NORTH;
		};
		return state.with(Properties.HORIZONTAL_FACING, dir);
	}
	
	private static BlockState fixTrapdoor(BlockState state, int meta) {
		if (skipFix(state, meta)) return state;
		Direction dir = switch (meta & 3) {
			case 1 -> Direction.EAST;
			case 2 -> Direction.SOUTH;
			case 3 -> Direction.NORTH;
			default -> Direction.WEST;
		};
		boolean open = (meta & 4) != 0;
		return state.with(Properties.HORIZONTAL_FACING, dir).with(VBEBlockProperties.OPENED, open);
	}
	
	private static boolean skipFix(BlockState state, int meta) {
		return meta == 0 || state != state.getBlock().getDefaultState();
	}
}
