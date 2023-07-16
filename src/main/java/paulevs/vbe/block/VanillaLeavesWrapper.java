package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.FoliageColor;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.registry.Identifier;

import java.util.Random;

public class VanillaLeavesWrapper extends VBELeavesBlock {
	private final int meta;
	
	public VanillaLeavesWrapper(Identifier id, int meta) {
		super(id);
		this.meta = meta;
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return LEAVES.getTextureForSide(side, this.meta);
	}
	
	@Override
	public int getTextureForSide(int side) {
		return LEAVES.getTextureForSide(side, meta);
	}
	
	@Override
	public int getDropCount(Random random) {
		return LEAVES.getDropCount(random);
	}
	
	@Override
	public int getDropId(int i, Random random) {
		return LEAVES.getDropId(i, random);
	}
	
	@Override
	protected int getDropMeta(int i) {
		return meta;
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
		view.getBiomeSource().getBiomes(x, z, 1, 1);
		double t = view.getBiomeSource().temperatureNoises[0];
		double w = view.getBiomeSource().rainfallNoises[0];
		return FoliageColor.getFoliageColor(t, w);
	}
}
