package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.BaseItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitType;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;
import net.modificationstation.stationapi.api.util.math.BlockPos;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.utils.CreativeUtil;
import paulevs.vbe.utils.LevelUtil;

import java.util.ArrayList;

public class VBEHalfSlabBlock extends TemplateBlockBase {
	private BaseBlock fullBlock;
	
	public VBEHalfSlabBlock(Identifier id, Material material) {
		super(id, material);
		setTranslationKey(id.toString());
	}
	
	public VBEHalfSlabBlock(Identifier id, BaseBlock source) {
		this(id, source.material);
		setTranslationKey(id.toString());
		BaseBlock.EMITTANCE[this.id] = BaseBlock.EMITTANCE[source.id] / 2;
		setHardness(source.getHardness() * 0.5F);
		setSounds(source.sounds);
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.DIRECTION);
	}
	
	public void setFullBlock(BaseBlock fullBlock) {
		this.fullBlock = fullBlock;
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Level level = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Direction face = context.getSide().getOpposite();
		BlockState state = level.getBlockState(pos.offset(face));
		if (state.isOf(this)) {
			Direction facing = state.get(VBEBlockProperties.DIRECTION);
			if (facing.getAxis() != face.getAxis()) {
				PlayerBase player = context.getPlayer();
				if (player != null && !player.isChild()) {
					return state;
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
	public boolean canUse(Level level, int x, int y, int z, PlayerBase player) {
		ItemStack stack = player.getHeldItem();
		if (stack == null) return false;
		
		BaseItem item = stack.getType();
		if (!(item instanceof BlockItem blockItem)) return false;
		
		if (blockItem.id != this.id) return false;
		
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
		level.callAreaEvents(x, y, z);
		
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
		BlockState state1 = bsView.getBlockState(x - face.getOffsetX(), y - face.getOffsetY(), z - face.getOffsetZ());
		BlockState state2 = bsView.getBlockState(x, y, z);
		
		if (state1.getBlock() instanceof VBEHalfSlabBlock && state2.getBlock() instanceof VBEHalfSlabBlock) {
			Direction slab2 = state2.get(VBEBlockProperties.DIRECTION);
			Direction slab1 = state1.get(VBEBlockProperties.DIRECTION);
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
}
