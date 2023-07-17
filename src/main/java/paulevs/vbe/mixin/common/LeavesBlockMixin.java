package paulevs.vbe.mixin.common;

import net.minecraft.block.LeavesBaseBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.utils.LevelUtil;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends LeavesBaseBlock {
	@Shadow public abstract int getDropId(int i, Random random);
	
	public LeavesBlockMixin(int id, int texture, Material material, boolean bl) {
		super(id, texture, material, bl);
	}
	
	@Override
	public void onBlockPlaced(Level level, int x, int y, int z, BlockState replacedState) {
		int meta = level.getBlockMeta(x, y, z);
		BlockState state = VBEBlocks.getLeavesByMeta(meta).getDefaultState();
		LevelUtil.setBlockSilent(level, x, y, z, state);
	}
}
