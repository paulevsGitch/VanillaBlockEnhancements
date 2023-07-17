package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.client.render.block.FoliageColor;
import net.minecraft.item.ItemStack;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.Identifier;

import java.util.Collections;
import java.util.List;

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
		return LEAVES.getTextureForSide(side, this.meta);
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
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		int count = LEAVES.getDropCount(level.random);
		if (count == 0) return Collections.emptyList();
		return Collections.singletonList(new ItemStack(BaseBlock.SAPLING.id, count, this.meta));
	}
}
