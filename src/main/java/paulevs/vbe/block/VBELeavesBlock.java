package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.LeavesBaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.BlockTemplate;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.utils.FloodFillSearch;
import paulevs.vbe.utils.LevelUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VBELeavesBlock extends LeavesBaseBlock implements BlockTemplate {
	private static final Map<Integer, FloodFillSearch> SEARCH_CACHE = new HashMap<>();
	private final FloodFillSearch search;
	private final int maxDistance;
	
	public VBELeavesBlock(Identifier id) {
		this(id, Material.LEAVES, 7);
	}
	
	public VBELeavesBlock(Identifier id, Material material, int maxDistance) {
		super(BlockTemplate.getNextId(), 0, material, false);
		BlockTemplate.onConstructor(this, id);
		setTranslationKey(id.toString());
		setSounds(GRASS_SOUNDS);
		disableStat();
		disableNotifyOnMetaDataChange();
		this.maxDistance = maxDistance;
		this.search = SEARCH_CACHE.computeIfAbsent(maxDistance, radius -> new FloodFillSearch(
			radius,
			state -> state.isIn(VBEBlockTags.LOGS),
			state -> state.isIn(VBEBlockTags.LEAVES)
		));
		setDefaultState(getDefaultState().with(VBEBlockProperties.NATURAL, true).with(VBEBlockProperties.ACTIVE, false));
		setLightOpacity(4);
		setTicksRandomly(true);
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.NATURAL, VBEBlockProperties.ACTIVE);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(VBEBlockProperties.NATURAL, false);
	}
	
	@Override
	public void onAdjacentBlockUpdate(Level level, int x, int y, int z, int blockID) {
		if (blockID == 0) checkLeaves(level, x, y, z, true);
	}
	
	@Override
	public void onScheduledTick(Level level, int x, int y, int z, Random random) {
		checkLeaves(level, x, y, z, false);
	}
	
	@Override
	@Environment(value= EnvType.CLIENT)
	public boolean isSideRendered(BlockView view, int x, int y, int z, int side) {
		if (view instanceof BlockStateView blockStateView) {
			BlockState state = blockStateView.getBlockState(x, y, z);
			if (state.getBlock() instanceof LeavesBaseBlock || !state.isOpaque()) {
				return true;
			}
			return super.isSideRendered(view, x, y, z, side);
		}
		return super.isSideRendered(view, x, y, z, side);
	}
	
	private void checkLeaves(Level level, int x, int y, int z, boolean force) {
		if (level.isClientSide) return;
		BlockState state = level.getBlockState(x, y, z);
		
		if (!state.isOf(this)) return;
		if (!state.get(VBEBlockProperties.NATURAL)) return;
		
		boolean active = state.get(VBEBlockProperties.ACTIVE);
		if (!force && !active) return;
		
		LevelUtil.setBlockSilent(level, x, y, z, state.with(VBEBlockProperties.ACTIVE, false));
		
		int radius = search.search(level, x, y, z);
		if (radius > 0 && radius <= maxDistance) return;
		
		if (force && !active) {
			for (int dx = -maxDistance; dx <= maxDistance; dx++) {
				for (int dz = -maxDistance; dz <= maxDistance; dz++) {
					for (int dy = -maxDistance; dy <= maxDistance; dy++) {
						state = level.getBlockState(x + dx, y + dy, z + dz);
						if (!(state.getBlock() instanceof VBELeavesBlock)) continue;
						if (!state.get(VBEBlockProperties.NATURAL)) continue;
						if (state.get(VBEBlockProperties.ACTIVE)) continue;
						LevelUtil.setBlockSilent(
							level,
							x + dx,
							y + dy,
							z + dz,
							state.with(VBEBlockProperties.ACTIVE, true)
						);
					}
				}
			}
		}
		
		drop(level, x, y, z, 0);
		level.setBlockState(x, y, z, States.AIR.get());
		level.callAreaEvents(x, y, z);
		
		for (byte i = 0; i < 6; i++) {
			Direction side = Direction.byId(i);
			int px = x + side.getOffsetX();
			int py = y + side.getOffsetY();
			int pz = z + side.getOffsetZ();
			state = level.getBlockState(px, py, pz);
			if (state.getBlock() instanceof VBELeavesBlock) {
				level.scheduleTick(px, py, pz, state.getBlock().id, 10 + level.random.nextInt(20));
			}
		}
	}
	
	static {
		SEARCH_CACHE.put(7, new FloodFillSearch(
			7,
			state -> state.isIn(VBEBlockTags.LOGS),
			state -> state.isIn(VBEBlockTags.LEAVES)
		));
	}
}
