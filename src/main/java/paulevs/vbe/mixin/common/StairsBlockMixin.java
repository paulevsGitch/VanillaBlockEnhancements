package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.util.maths.BlockPos;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.block.StairsShape;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.StairsPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(StairsBlock.class)
public class StairsBlockMixin extends Block implements StairsShape {
	@Shadow private Block template;
	
	public StairsBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(Properties.HORIZONTAL_FACING, VBEBlockProperties.STAIRS_PART);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		PlayerEntity player = context.getPlayer();
		BlockState state = getDefaultState();
		
		Direction facing = context.getSide();
		
		if (player != null && !player.isChild()) {
			BlockPos pos = context.getBlockPos();
			BlockState worldState = context.getWorld().getBlockState(pos.offset(facing.getOpposite()));
			if (worldState.getBlock() instanceof StairsBlock) {
				facing = worldState.get(Properties.HORIZONTAL_FACING);
				StairsPart part = worldState.get(VBEBlockProperties.STAIRS_PART);
				return state.with(Properties.HORIZONTAL_FACING, facing).with(VBEBlockProperties.STAIRS_PART, part);
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
		
		return state.with(Properties.HORIZONTAL_FACING, facing);
	}
	
	@Override
	public int getTexture(int side, int meta) {
		return this.template.getTexture(side, meta);
	}
	
	@Override
	public int getTexture(int side) {
		return this.template.getTexture(side);
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void vbe_onInit(int id, Block source, CallbackInfo info) {
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
