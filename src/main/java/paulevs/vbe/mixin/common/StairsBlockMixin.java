package paulevs.vbe.mixin.common;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(StairsBlock.class)
public class StairsBlockMixin extends BaseBlock implements StairsShape {
	@Unique private static final Map<Integer, List<Box>> VBE_SHAPES = new HashMap<>();
	@Shadow private BaseBlock template;
	
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
		if (facing.getAxis().isHorizontal()) {
			state = state.with(VBEBlockProperties.STAIRS_PART, StairsPart.SIDE);
			facing = Direction.fromRotation(player == null ? 0 : (player.yaw - 45.0F));
			state = state.with(VBEBlockProperties.FACING, facing);
		}
		else {
			state = state.with(
				VBEBlockProperties.STAIRS_PART,
				facing.getOffsetY() > 0 ? StairsPart.BOTTOM : StairsPart.TOP
			);
			facing = Direction.fromRotation(player == null ? 0 : player.yaw);
			state = state.with(VBEBlockProperties.FACING, facing);
		}
		
		return state;
	}
	
	@Override
	public List<Box> vbe_getStairsShape(BlockState state) {
		final Direction facing = state.get(VBEBlockProperties.FACING);
		final StairsPart part = state.get(VBEBlockProperties.STAIRS_PART);
		int index = (facing.getId() - 2) * 3 + part.ordinal();
		return VBE_SHAPES.computeIfAbsent(index, key -> {
			List<Box> shapes = new ArrayList<>();
			switch (part) {
				case BOTTOM -> {
					shapes.add(Box.create(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F));
					switch (facing.getAxis()) {
						case X -> {
							float x1 = facing.getOffsetX() < 0 ? 0.0F : 0.5F;
							float x2 = facing.getOffsetX() < 0 ? 0.5F : 1.0F;
							shapes.add(Box.create(x1, 0.5F, 0.0F, x2, 1.0F, 1.0F));
						}
						case Z -> {
							float z1 = facing.getOffsetZ() < 0 ? 0.0F : 0.5F;
							float z2 = facing.getOffsetZ() < 0 ? 0.5F : 1.0F;
							shapes.add(Box.create(0.0F, 0.5F, z1, 1.0F, 1.0F, z2));
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
			return shapes;
		});
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
		vbe_getStairsShape(level.getBlockState(x, y, z)).forEach(shape -> {
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
}
