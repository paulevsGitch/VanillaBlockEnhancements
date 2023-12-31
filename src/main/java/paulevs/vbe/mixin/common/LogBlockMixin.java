package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.utils.LevelUtil;

import java.util.Collections;
import java.util.List;

@Mixin(LogBlock.class)
public class LogBlockMixin extends Block {
	public LogBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public void onBlockPlaced(Level level, int x, int y, int z, BlockState replacedState) {
		int meta = level.getBlockMeta(x, y, z);
		BlockState state = VBEBlocks.getLogByMeta(meta).getDefaultState();
		LevelUtil.setBlockSilent(level, x, y, z, state);
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		return Collections.singletonList(new ItemStack(VBEBlocks.getLogByMeta(meta)));
	}
}
