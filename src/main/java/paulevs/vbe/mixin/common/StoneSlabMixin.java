package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.utils.LevelUtil;

import java.util.Collections;
import java.util.List;

@Mixin(StoneSlabBlock.class)
public class StoneSlabMixin extends Block {
	@Shadow private boolean field_2324;
	
	public StoneSlabMixin(int i, Material arg) {
		super(i, arg);
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		if (!VBE.ENHANCED_SLABS.getValue()) return null;
		return Collections.singletonList(new ItemStack(VBEBlocks.getHalfSlabByMeta(meta), this.field_2324 ? 2 : 1));
	}
	
	/*@Override
	public void onBlockPlaced(Level level, int x, int y, int z, BlockState replacedState) {
	
	}*/
	
	@Inject(method = "onBlockPlaced", at = @At("HEAD"), cancellable = true)
	private void vbe_onBlockPlaced(Level level, int x, int y, int z, CallbackInfo info) {
		if (!VBE.ENHANCED_SLABS.getValue()) return;
		info.cancel();
		int meta = level.getBlockMeta(x, y, z);
		BlockState state = (this.field_2324 ? VBEBlocks.getFullSlabByMeta(meta) : VBEBlocks.getHalfSlabByMeta(meta)).getDefaultState();
		LevelUtil.setBlockSilent(level, x, y, z, state);
	}
}
