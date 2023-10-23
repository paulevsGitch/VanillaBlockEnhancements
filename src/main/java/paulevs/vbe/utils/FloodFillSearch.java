package paulevs.vbe.utils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FloodFillSearch {
	private final List<IntList> buffers = new ArrayList<>();
	private final boolean[] mask;
	private final short[] steps;
	private final int offset;
	private final int side;
	private final int side2;
	private final int center;
	
	public FloodFillSearch(int radius) {
		offset = radius;
		side = radius << 1 | 1;
		side2 = side * side;
		mask = new boolean[side * side2];
		steps = new short[mask.length];
		center = getIndex(offset, offset, offset);
		buffers.add(new IntArrayList(mask.length >> 2));
		buffers.add(new IntArrayList(mask.length >> 2));
	}
	
	public int search(Level level, int x, int y, int z, Function<BlockState, Boolean> criteria, Function<BlockState, Boolean> filter) {
		IntList starts = buffers.get(0);
		
		starts.clear();
		starts.add(center);
		
		Arrays.fill(mask, false);
		mask[center] = true;
		
		x -= offset;
		y -= offset;
		z -= offset;
		
		byte index = 0;
		while (!starts.isEmpty()) {
			starts = buffers.get(index);
			index = (byte) ((index + 1) & 1);
			IntList ends = buffers.get(index);
			ends.clear();
			
			for (int pos : starts) {
				int dx = getX(pos);
				int dy = getY(pos);
				int dz = getZ(pos);
				for (byte i = 0; i < 6; i++) {
					Direction side = Direction.byId(i);
					
					int px = dx + side.getOffsetX();
					if (!isInBound(px)) continue;
					int py = dy + side.getOffsetY();
					if (!isInBound(py)) continue;
					int pz = dz + side.getOffsetZ();
					if (!isInBound(pz)) continue;
					
					int cellIndex = getIndex(px, py, pz);
					if (mask[cellIndex]) continue;
					
					BlockState state = level.getBlockState(x + px, y + py, z + pz);
					if (criteria.apply(state)) return steps[pos] + 1;
					if (!filter.apply(state)) {
						mask[cellIndex] = true;
						continue;
					}
					
					steps[cellIndex] = (short) (steps[pos] + 1);
					mask[cellIndex] = true;
					ends.add(cellIndex);
				}
			}
		}
		
		return -1;
	}
	
	public void transform(Level level, int x, int y, int z, Function<BlockState, Boolean> criteria, Function<BlockState, BlockState> transformer) {
		IntList starts = buffers.get(0);
		
		starts.clear();
		starts.add(center);
		
		Arrays.fill(mask, false);
		mask[center] = true;
		
		x -= offset;
		y -= offset;
		z -= offset;
		
		byte index = 0;
		while (!starts.isEmpty()) {
			starts = buffers.get(index);
			index = (byte) ((index + 1) & 1);
			IntList ends = buffers.get(index);
			ends.clear();
			
			for (int pos : starts) {
				int dx = getX(pos);
				int dy = getY(pos);
				int dz = getZ(pos);
				for (byte i = 0; i < 6; i++) {
					Direction side = Direction.byId(i);
					
					int px = dx + side.getOffsetX();
					if (!isInBound(px)) continue;
					int py = dy + side.getOffsetY();
					if (!isInBound(py)) continue;
					int pz = dz + side.getOffsetZ();
					if (!isInBound(pz)) continue;
					
					int cellIndex = getIndex(px, py, pz);
					if (mask[cellIndex]) continue;
					
					BlockState state = level.getBlockState(x + px, y + py, z + pz);
					if (criteria.apply(state)) {
						BlockState transformed = transformer.apply(state);
						if (transformed != state) {
							LevelUtil.setBlockSilent(level, x + px, y + py, z + pz, transformed);
						}
						ends.add(cellIndex);
					}
					mask[cellIndex] = true;
				}
			}
		}
	}
	
	private boolean isInBound(int value) {
		return value >= 0 && value < side;
	}
	
	private int getIndex(int x, int y, int z) {
		return x * side2 + y * side + z;
	}
	
	private int getX(int pos) {
		return pos / side2;
	}
	
	private int getY(int pos) {
		return (pos / side) % side;
	}
	
	private int getZ(int pos) {
		return pos % side;
	}
}
