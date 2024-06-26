package paulevs.vbe.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.level.BlockView;
import net.minecraft.level.biome.BiomeSource;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.BlockStateView;

@Environment(EnvType.CLIENT)
public class BlockViewWrapper implements BlockView, BlockStateView {
	private BlockView originalView;
	private BlockState state;
	private int x;
	private int y;
	private int z;
	
	public void setData(BlockView view, BlockState state, int x, int y, int z) {
		this.originalView = view;
		this.state = state;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockView getOriginalView() {
		return originalView;
	}
	
	@Override
	public int getBlockID(int x, int y, int z) {
		return getBlockState(x, y, z).getBlock().id;
	}
	
	@Override
	public BlockEntity getBlockEntity(int x, int y, int z) {
		return originalView.getBlockEntity(x, y, z);
	}
	
	@Override
	public float getLight(int x, int y, int z, int l) {
		return originalView.getLight(x, y, z, l);
	}
	
	@Override
	public float getBrightness(int x, int y, int z) {
		return originalView.getBrightness(x, y, z);
	}
	
	@Override
	public int getBlockMeta(int x, int y, int z) {
		return originalView.getBlockMeta(x, y, z);
	}
	
	@Override
	public Material getMaterial(int x, int y, int z) {
		return getBlockState(x, y, z).getMaterial();
	}
	
	@Override
	public boolean isFullOpaque(int x, int y, int z) {
		return getBlockState(x, y, z).getBlock().isFullOpaque();
	}
	
	@Override
	public boolean canSuffocate(int x, int y, int z) {
		return getBlockState(x, y, z).getMaterial().hasNoSuffocation();
	}
	
	@Override
	public BiomeSource getBiomeSource() {
		return originalView.getBiomeSource();
	}
	
	@Override
	public BlockState getBlockState(int x, int y, int z) {
		if (x == this.x && y == this.y && z == this.z) {
			return state;
		}
		return ((BlockStateView) originalView).getBlockState(x, y, z);
	}
}
