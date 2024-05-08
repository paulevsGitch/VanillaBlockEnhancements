package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitType;
import net.minecraft.util.maths.BlockPos;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.utils.CreativeUtil;
import paulevs.vbe.utils.LevelUtil;

import java.util.ArrayList;
import java.util.function.Function;

public class VBEHalfSlabBlock extends TemplateBlock {
	private final Function<Integer, Integer> textureGetter;
	private Block fullBlock;
	
	public VBEHalfSlabBlock(Identifier id, Material material) {
		super(id, material);
		setTranslationKey(id);
		this.textureGetter = side -> this.texture;
		NO_AMBIENT_OCCLUSION[this.id] = true;
	}
	
	public VBEHalfSlabBlock(Identifier id, Block source) {
		super(id, source.material);
		setTranslationKey(id);
		Block.EMITTANCE[this.id] = Block.EMITTANCE[source.id] / 2;
		setHardness(source.getHardness() * 0.5F);
		setSounds(source.sounds);
		this.textureGetter = source::getTexture;
		NO_AMBIENT_OCCLUSION[this.id] = true;
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.DIRECTION);
	}
	
	public void setFullBlock(Block fullBlock) {
		this.fullBlock = fullBlock;
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Level level = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Direction face = context.getSide().getOpposite();
		BlockState state = level.getBlockState(pos.offset(face));
		if (state.getBlock() instanceof VBEHalfSlabBlock) {
			Direction facing = state.get(VBEBlockProperties.DIRECTION);
			if (facing.getAxis() != face.getAxis()) {
				PlayerEntity player = context.getPlayer();
				if (player != null && !player.isChild()) {
					return getDefaultState().with(VBEBlockProperties.DIRECTION, facing);
				}
			}
		}
		return getDefaultState().with(VBEBlockProperties.DIRECTION, face);
	}
	
	@Override
	public void updateBoundingBox(BlockView view, int x, int y, int z) {
		if (!(view instanceof BlockStateView bsView)) {
			this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			return;
		}
		
		BlockState state = bsView.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		Direction facing = state.get(VBEBlockProperties.DIRECTION);
		
		int dx = facing.getOffsetX();
		int dy = facing.getOffsetY();
		int dz = facing.getOffsetZ();
		
		float minX = dx == 0 ? 0 : dx > 0 ? 0.5F : 0;
		float minY = dy == 0 ? 0 : dy > 0 ? 0.5F : 0;
		float minZ = dz == 0 ? 0 : dz > 0 ? 0.5F : 0;
		float maxX = dx == 0 ? 1 : dx > 0 ? 1 : 0.5F;
		float maxY = dy == 0 ? 1 : dy > 0 ? 1 : 0.5F;
		float maxZ = dz == 0 ? 1 : dz > 0 ? 1 : 0.5F;
		
		this.setBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public void doesBoxCollide(Level level, int x, int y, int z, Box box, ArrayList list) {
		updateBoundingBox(level, x, y, z);
		super.doesBoxCollide(level, x, y, z, box, list);
		this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isFullOpaque() {
		return false;
	}
	
	@Override
	public boolean canUse(Level level, int x, int y, int z, PlayerEntity player) {
		ItemStack stack = player.getHeldItem();
		if (stack == null) return false;
		
		Item item = stack.getType();
		if (!(item instanceof BlockItem blockItem)) return false;
		
		if (blockItem.getBlock() != this) return false;
		
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return false;
		
		Direction facing = state.get(VBEBlockProperties.DIRECTION);
		
		HitResult hit = LevelUtil.raycast(level, player);
		if (hit == null || hit.type != HitType.BLOCK) return false;
		
		double dx = hit.pos.x - x;
		double dy = hit.pos.y - y;
		double dz = hit.pos.z - z;
		
		if (dx < 0 || dx > 1 || dy < 0 || dy > 1 || dz < 0 || dz > 1) return false;
		
		Axis axis = facing.getAxis();
		
		if (axis == Axis.X && Math.abs(dx - 0.5) > 0.0001) return false;
		if (axis == Axis.Y && Math.abs(dy - 0.5) > 0.0001) return false;
		if (axis == Axis.Z && Math.abs(dz - 0.5) > 0.0001) return false;
		
		BlockState fullBlock = this.fullBlock.getDefaultState();
		
		if (fullBlock.getProperties().contains(VBEBlockProperties.AXIS)) {
			fullBlock = fullBlock.with(VBEBlockProperties.AXIS, facing.getAxis());
		}
		else if (fullBlock.getProperties().contains(VBEBlockProperties.DIRECTION)) {
			fullBlock = fullBlock.with(VBEBlockProperties.DIRECTION, facing);
		}
		
		level.setBlockState(x, y, z, fullBlock);
		level.playSound(x + 0.5, y + 0.5, z + 0.5, this.sounds.getWalkSound(), 1.0F, 1.0F);
		level.updateBlock(x, y, z);
		
		if (!CreativeUtil.isCreative(player)) {
			stack.count--;
		}
		
		return true;
	}
	
	@Override
	@Environment(value = EnvType.CLIENT)
	public boolean isSideRendered(BlockView view, int x, int y, int z, int side) {
		if (!(view instanceof BlockStateView bsView)) {
			return super.isSideRendered(view, x, y, z, side);
		}
		
		Direction face = Direction.byId(side);
		BlockState selfState = bsView.getBlockState(x, y, z);
		
		if (selfState.getBlock() instanceof VBEHalfSlabBlock) {
			Direction selfDir = selfState.get(VBEBlockProperties.DIRECTION);
			if (face == selfDir || face == selfDir.getOpposite()) {
				return super.isSideRendered(view, x, y, z, side);
			}
		}
		
		BlockState sideState = bsView.getBlockState(x - face.getOffsetX(), y - face.getOffsetY(), z - face.getOffsetZ());
		
		if (sideState.getBlock() instanceof VBEHalfSlabBlock && selfState.getBlock() instanceof VBEHalfSlabBlock) {
			Direction slab2 = selfState.get(VBEBlockProperties.DIRECTION);
			Direction slab1 = sideState.get(VBEBlockProperties.DIRECTION);
			return slab1 != slab2;
		}
		
		return super.isSideRendered(view, x, y, z, side);
	}
	
	// Item Bounding Box
	@Override
	@Environment(EnvType.CLIENT)
	public void updateRenderBounds() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}
	
	@Override
	public int getTexture(int side) {
		return textureGetter.apply(side);
	}
}
