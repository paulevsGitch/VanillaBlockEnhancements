package paulevs.vbe.mixin.common;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.util.maths.BlockPos;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.block.StairsShape;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.StairsPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(StairsBlock.class)
public class StairsBlockMixin extends BaseBlock implements StairsShape {
	@Unique private static final List<List<Box>> VBE_SHAPES = new ArrayList<>(36);
	@Shadow private BaseBlock template;
	
	static {
		for (int i = 0; i < 36; i++) {
			VBE_SHAPES.add(null);
		}
	}
	
	public StairsBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.FACING, VBEBlockProperties.STAIRS_PART);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		PlayerBase player = context.getPlayer();
		BlockState state = getDefaultState();
		
		Direction facing = context.getSide();
		
		if (player != null && !player.isChild()) {
			BlockPos pos = context.getBlockPos();
			BlockState worldState = context.getWorld().getBlockState(pos.offset(facing.getOpposite()));
			if (worldState.getBlock() instanceof StairsBlock) {
				facing = worldState.get(VBEBlockProperties.FACING);
				StairsPart part = worldState.get(VBEBlockProperties.STAIRS_PART);
				return state.with(VBEBlockProperties.FACING, facing).with(VBEBlockProperties.STAIRS_PART, part);
			}
		}
		
		if (facing.getAxis().isHorizontal()) {
			state = state.with(VBEBlockProperties.STAIRS_PART, StairsPart.SIDE);
			facing = Direction.fromRotation(player == null ? 0 : (player.yaw - 45.0F));
		}
		else {
			state = state.with(
				VBEBlockProperties.STAIRS_PART,
				facing.getOffsetY() > 0 ? StairsPart.BOTTOM : StairsPart.TOP
			);
			facing = Direction.fromRotation(player == null ? 0 : player.yaw);
		}
		
		return state.with(VBEBlockProperties.FACING, facing);
	}
	
	@Override
	public List<Box> vbe_getStairsShape(BlockStateView view, int x, int y, int z, BlockState state) {
		final Direction facing = state.get(VBEBlockProperties.FACING);
		final StairsPart part = state.get(VBEBlockProperties.STAIRS_PART);
		
		byte corner = 0;
		
		BlockState sideState = view.getBlockState(
			x + facing.getOffsetX(),
			y + facing.getOffsetY(),
			z + facing.getOffsetZ()
		);
		
		if (sideState.getBlock() instanceof StairsBlock && sideState.get(VBEBlockProperties.STAIRS_PART) == part) {
			if (sideState.get(VBEBlockProperties.FACING) == facing.rotateClockwise(Axis.Y)) corner = 1;
			else if (sideState.get(VBEBlockProperties.FACING) == facing.rotateCounterclockwise(Axis.Y)) corner = 2;
		}
		
		if (corner == 0) {
			sideState = view.getBlockState(
				x - facing.getOffsetX(),
				y - facing.getOffsetY(),
				z - facing.getOffsetZ()
			);
			
			if (sideState.getBlock() instanceof StairsBlock && sideState.get(VBEBlockProperties.STAIRS_PART) == part) {
				if (sideState.get(VBEBlockProperties.FACING) == facing.rotateClockwise(Axis.Y)) corner = 3;
				else if (sideState.get(VBEBlockProperties.FACING) == facing.rotateCounterclockwise(Axis.Y)) corner = 4;
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
				sideState.get(VBEBlockProperties.FACING) == facing &&
				sideState.get(VBEBlockProperties.STAIRS_PART) == part
			) {
				corner = 0;
			}
		}
		
		int index = (facing.getId() - 2) * 9 + part.ordinal() * 3 + corner;
		List<Box> shapes = VBE_SHAPES.get(index);
		if (shapes == null) {
			shapes = new ArrayList<>();
			VBE_SHAPES.set(index, shapes);
			
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
							//shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
							
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
							shapes.add(Box.create(x1, 0.0F, 0.0F, x2, 0.5F, 1.0F));
						}
						case Z -> {
							float z1 = facing.getOffsetZ() < 0 ? 0.0F : 0.5F;
							float z2 = facing.getOffsetZ() < 0 ? 0.5F : 1.0F;
							shapes.add(Box.create(0.0F, 0.0F, z1, 1.0F, 0.5F, z2));
						}
					}
				}
			}
		}
		
		return shapes;
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return this.template.getTextureForSide(side, meta);
	}
	
	@Override
	public int getTextureForSide(int side) {
		return this.template.getTextureForSide(side);
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void vbe_onInit(int id, BaseBlock source, CallbackInfo info) {
		this.setLightOpacity(0);
	}
	
	@Inject(method = "afterPlaced", at = @At("HEAD"), cancellable = true)
	private void vbe_afterPlaced(Level level, int x, int y, int z, LivingEntity placer, CallbackInfo info) {
		info.cancel();
	}
	
	@SuppressWarnings("rawtypes")
	@Inject(method = "doesBoxCollide", at = @At("HEAD"), cancellable = true)
	private void vbe_doesBoxCollide(Level level, int x, int y, int z, Box box, ArrayList list, CallbackInfo info) {
		vbe_getStairsShape(level, x, y, z, level.getBlockState(x, y, z)).forEach(shape -> {
			this.minX = shape.minX;
			this.minY = shape.minY;
			this.minZ = shape.minZ;
			this.maxX = shape.maxX;
			this.maxY = shape.maxY;
			this.maxZ = shape.maxZ;
			super.doesBoxCollide(level, x, y, z, box, list);
		});
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		info.cancel();
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		return Collections.singletonList(new ItemStack(this));
	}
}
