package paulevs.vbe.block;

import net.minecraft.block.StairsBlock;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.block.VBEBlockProperties.StairsPart;

import java.util.ArrayList;
import java.util.List;

public interface StairsShape {
	List<List<Box>> SHAPES = new ArrayList<>(60);
	
	static void init() {
		for (int i = 0; i < 60; i++) {
			SHAPES.add(null);
		}
	}
	
	default List<Box> vbe_getStairsShape(BlockStateView view, int x, int y, int z, BlockState state) {
		final Direction facing = state.get(Properties.FACING);
		final StairsPart part = state.get(VBEBlockProperties.STAIRS_PART);
		
		byte corner = 0;
		
		BlockState sideState = view.getBlockState(
			x + facing.getOffsetX(),
			y + facing.getOffsetY(),
			z + facing.getOffsetZ()
		);
		
		if (sideState.getBlock() instanceof StairsBlock && sideState.get(VBEBlockProperties.STAIRS_PART) == part) {
			if (sideState.get(Properties.FACING) == facing.rotateClockwise(Axis.Y)) corner = 1;
			else if (sideState.get(Properties.FACING) == facing.rotateCounterclockwise(Axis.Y)) corner = 2;
		}
		
		if (corner == 0) {
			sideState = view.getBlockState(
				x - facing.getOffsetX(),
				y - facing.getOffsetY(),
				z - facing.getOffsetZ()
			);
			
			if (sideState.getBlock() instanceof StairsBlock && sideState.get(VBEBlockProperties.STAIRS_PART) == part) {
				if (sideState.get(Properties.FACING) == facing.rotateClockwise(Axis.Y)) corner = 3;
				else if (sideState.get(Properties.FACING) == facing.rotateCounterclockwise(Axis.Y)) corner = 4;
			}
		}
		
		if (corner == 1 || corner == 2) {
			Direction side = corner == 1 ? facing.rotateClockwise(Axis.Y) : facing.rotateCounterclockwise(Axis.Y);
			sideState = view.getBlockState(
				x - side.getOffsetX(),
				y - side.getOffsetY(),
				z - side.getOffsetZ()
			);
			if (
				sideState.getBlock() instanceof StairsBlock &&
					sideState.get(Properties.FACING) == facing &&
					sideState.get(VBEBlockProperties.STAIRS_PART) == part
			) {
				corner = 0;
			}
		}
		
		int index = (facing.getId() - 2) * 15 + part.ordinal() * 5 + corner;
		List<Box> shapes = SHAPES.get(index);
		if (shapes == null) {
			shapes = new ArrayList<>();
			SHAPES.set(index, shapes);
			
			switch (part) {
				case BOTTOM -> {
					shapes.add(Box.create(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F));
					switch (facing.getAxis()) {
						case X -> {
							float x1 = facing.getOffsetX() < 0 ? 0.0F : 0.5F;
							float x2 = facing.getOffsetX() < 0 ? 0.5F : 1.0F;
							switch (corner) {
								case 0 -> shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 1.0F));
								case 1 -> {
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 0.5F));
									else shapes.add(Box.create(x1, 0.5F, 0.5F, x2, 1.0F, 1.0F));
								}
								case 2 -> {
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.5F, 0.5F, x2, 1.0F, 1.0F));
									else shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 0.5F));
								}
								case 3 -> {
									shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 1.0F));
									x1 = facing.getOffsetX() < 0 ? 0.5F : 0.0F;
									x2 = facing.getOffsetX() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 0.5F));
									else shapes.add(Box.create(x1, 0.5F, 0.5F, x2, 1.0F, 1.0F));
								}
								case 4 -> {
									shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 1.0F));
									x1 = facing.getOffsetX() < 0 ? 0.5F : 0.0F;
									x2 = facing.getOffsetX() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.5F, 0.5F, x2, 1.0F, 1.0F));
									else shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 0.5F));
								}
							}
						}
						case Z -> {
							float z1 = facing.getOffsetZ() < 0 ? 0.0F : 0.5F;
							float z2 = facing.getOffsetZ() < 0 ? 0.5F : 1.0F;
							
							switch (corner) {
								case 0 -> shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
								case 1 -> {
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.5F, 0.5F, z1, 1.0F, 1.0F, z2));
									else shapes.add(Box.create(0.0F, 0.5F, z1, 0.5F, 1.0F, z2));
								}
								case 2 -> {
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.0F, 0.5F, z1, 0.5F, 1.0F, z2));
									else shapes.add(Box.create(0.5F, 0.5F, z1, 1.0F, 1.0F, z2));
								}
								case 3 -> {
									shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
									z1 = facing.getOffsetZ() < 0 ? 0.5F : 0.0F;
									z2 = facing.getOffsetZ() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.5F, 0.5F, z1, 1.0F, 1.0F, z2));
									else shapes.add(Box.create(0.0F, 0.5F, z1, 0.5F, 1.0F, z2));
								}
								case 4 -> {
									shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
									z1 = facing.getOffsetZ() < 0 ? 0.5F : 0.0F;
									z2 = facing.getOffsetZ() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.0F, 0.5F, z1, 0.5F, 1.0F, z2));
									else shapes.add(Box.create(0.5F, 0.5F, z1, 1.0F, 1.0F, z2));
								}
							}
						}
					}
				}
				case SIDE -> {
					float x1 = 0.0F;
					float z1 = 0.0F;
					float x2 = 1.0F;
					float z2 = 1.0F;
					switch (facing.getAxis()) {
						case X -> {
							x1 = facing.getOffsetX() < 0 ? 0.0F : 0.5F;
							x2 = facing.getOffsetX() < 0 ? 0.5F : 1.0F;
						}
						case Z -> {
							z1 = facing.getOffsetZ() < 0 ? 0.0F : 0.5F;
							z2 = facing.getOffsetZ() < 0 ? 0.5F : 1.0F;
						}
					}
					shapes.add(Box.create(x1, 0.0F, z1, x2, 1.0F, z2));
					Direction rotated = facing.rotateClockwise(Axis.Y);
					switch (rotated.getAxis()) {
						case X -> {
							x1 = rotated.getOffsetX() < 0 ? 0.0F : 0.5F;
							x2 = rotated.getOffsetX() < 0 ? 0.5F : 1.0F;
							z1 = z1 < 0.5F ? 0.5F : 0.0F;
							z2 = z2 > 0.5F ? 0.5F : 1.0F;
						}
						case Z -> {
							z1 = rotated.getOffsetZ() < 0 ? 0.0F : 0.5F;
							z2 = rotated.getOffsetZ() < 0 ? 0.5F : 1.0F;
							x1 = x1 < 0.5F ? 0.5F : 0.0F;
							x2 = x2 > 0.5F ? 0.5F : 1.0F;
						}
					}
					shapes.add(Box.create(x1, 0.0F, z1, x2, 1.0F, z2));
				}
				case TOP -> {
					shapes.add(Box.create(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F));
					switch (facing.getAxis()) {
						case X -> {
							float x1 = facing.getOffsetX() < 0 ? 0.0F : 0.5F;
							float x2 = facing.getOffsetX() < 0 ? 0.5F : 1.0F;
							switch (corner) {
								case 0 -> shapes.add(Box.create(x1, 0.0F, 0.0F, x2, 0.5F, 1.0F));
								case 1 -> {
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.0F, 0.0F, x2, 0.5F, 0.5F));
									else shapes.add(Box.create(x1, 0.0F, 0.5F, x2, 0.5F, 1.0F));
								}
								case 2 -> {
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.0F, 0.5F, x2, 0.5F, 1.0F));
									else shapes.add(Box.create(x1, 0.0F, 0.0F, x2, 0.5F, 0.5F));
								}
								case 3 -> {
									shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 1.0F));
									x1 = facing.getOffsetX() < 0 ? 0.5F : 0.0F;
									x2 = facing.getOffsetX() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.0F, 0.0F, x2, 0.5F, 0.5F));
									else shapes.add(Box.create(x1, 0.0F, 0.5F, x2, 0.5F, 1.0F));
								}
								case 4 -> {
									shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 1.0F));
									x1 = facing.getOffsetX() < 0 ? 0.5F : 0.0F;
									x2 = facing.getOffsetX() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetX() < 0) shapes.add(Box.create(x1, 0.0F, 0.5F, x2, 0.5F, 1.0F));
									else shapes.add(Box.create(x1, 0.0F, 0.0F, x2, 0.5F, 0.5F));
								}
							}
						}
						case Z -> {
							float z1 = facing.getOffsetZ() < 0 ? 0.0F : 0.5F;
							float z2 = facing.getOffsetZ() < 0 ? 0.5F : 1.0F;
							
							switch (corner) {
								case 0 -> shapes.add(Box.create(0.0F, 0.0F, z1, 1.0F, 0.5F, z2));
								case 1 -> {
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.5F, 0.0F, z1, 1.0F, 0.5F, z2));
									else shapes.add(Box.create(0.0F, 0.0F, z1, 0.5F, 0.5F, z2));
								}
								case 2 -> {
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.0F, 0.0F, z1, 0.5F, 0.5F, z2));
									else shapes.add(Box.create(0.5F, 0.0F, z1, 1.0F, 0.5F, z2));
								}
								case 3 -> {
									shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
									z1 = facing.getOffsetZ() < 0 ? 0.5F : 0.0F;
									z2 = facing.getOffsetZ() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.5F, 0.0F, z1, 1.0F, 0.5F, z2));
									else shapes.add(Box.create(0.0F, 0.0F, z1, 0.5F, 0.5F, z2));
								}
								case 4 -> {
									shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
									z1 = facing.getOffsetZ() < 0 ? 0.5F : 0.0F;
									z2 = facing.getOffsetZ() < 0 ? 1.0F : 0.5F;
									if (facing.getOffsetZ() < 0) shapes.add(Box.create(0.0F, 0.0F, z1, 0.5F, 0.5F, z2));
									else shapes.add(Box.create(0.5F, 0.0F, z1, 1.0F, 0.5F, z2));
								}
							}
						}
					}
				}
			}
		}
		
		return shapes;
	}
}
