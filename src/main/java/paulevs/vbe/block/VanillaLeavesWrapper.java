package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.FoliageColor;
import net.minecraft.item.ItemStack;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.level.biome.BiomeSource;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Collections;
import java.util.List;

public class VanillaLeavesWrapper extends VBELeavesBlock {
	private final int meta;
	
	public VanillaLeavesWrapper(Identifier id, int meta) {
		super(id);
		this.meta = meta;
	}
	
	@Override
	public int getTexture(int side, int meta) {
		return LEAVES.getTexture(side, this.meta);
	}
	
	@Override
	public int getTexture(int side) {
		return LEAVES.getTexture(side, this.meta);
	}
	
	@Override
	@Environment(value= EnvType.CLIENT)
	public int getBaseColor(int meta) {
		return LEAVES.getBaseColor(this.meta);
	}
	
	@Environment(value= EnvType.CLIENT)
	public int getColorMultiplier(BlockView view, int x, int y, int z) {
		if ((meta & 1) == 1) return FoliageColor.getSpruceColor();
		if ((meta & 2) == 2) return FoliageColor.getBirchColor();
		// getBiomeSource, not remapped in V2
		BiomeSource source = view.method_1781();
		source.getBiomes(x, z, 1, 1);
		double t = source.temperatureNoises[0];
		double w = source.rainfallNoises[0];
		return FoliageColor.getFoliageColor(t, w);
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		int count = LEAVES.getDropCount(level.random);
		if (count == 0) return Collections.emptyList();
		return Collections.singletonList(new ItemStack(Block.SAPLING.id, count, this.meta));
	}
}
