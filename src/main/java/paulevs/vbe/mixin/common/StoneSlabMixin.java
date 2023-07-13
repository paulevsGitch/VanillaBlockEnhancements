package paulevs.vbe.mixin.common;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import paulevs.vbe.block.VBEBlocks;

import java.util.Collections;
import java.util.List;

@Mixin(StoneSlabBlock.class)
public class StoneSlabMixin extends BaseBlock {
	@Shadow private boolean field_2324;
	
	public StoneSlabMixin(int i, Material arg) {
		super(i, arg);
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		return Collections.singletonList(new ItemStack(VBEBlocks.getSlabByMeta(meta), this.field_2324 ? 2 : 1));
	}
}
