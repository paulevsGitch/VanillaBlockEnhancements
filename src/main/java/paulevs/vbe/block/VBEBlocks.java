package paulevs.vbe.block;

import net.minecraft.block.BaseBlock;
import paulevs.vbe.VBE;

public class VBEBlocks {
	public static final VBEHalfSlabBlock STONE_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("stone_slab_half"), BaseBlock.STONE);
	public static final VBEFullSlabBlock STONE_SLAB_FULL = new VBEFullSlabBlock(VBE.id("stone_slab_full"), BaseBlock.STONE);
	public static final VBEHalfSlabBlock SANDSTONE_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("sandstone_slab_half"), BaseBlock.SANDSTONE);
	public static final VBEFullSlabBlock SANDSTONE_SLAB_FULL = new VBEFullSlabBlock(VBE.id("sandstone_slab_full"), BaseBlock.SANDSTONE);
	public static final VBEHalfSlabBlock OAK_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("oak_slab_half"), BaseBlock.WOOD);
	public static final VBEFullSlabBlock OAK_SLAB_FULL = new VBEFullSlabBlock(VBE.id("oak_slab_full"), BaseBlock.WOOD);
	public static final VBEHalfSlabBlock COBBLESTONE_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("cobblestone_slab_half"), BaseBlock.COBBLESTONE);
	public static final VBEFullSlabBlock COBBLESTONE_SLAB_FULL = new VBEFullSlabBlock(VBE.id("cobblestone_slab_full"), BaseBlock.COBBLESTONE);
	
	public static void init() {
		connectSlabs(STONE_SLAB_HALF, STONE_SLAB_FULL);
		connectSlabs(SANDSTONE_SLAB_HALF, SANDSTONE_SLAB_FULL);
		connectSlabs(OAK_SLAB_HALF, OAK_SLAB_FULL);
		connectSlabs(COBBLESTONE_SLAB_HALF, COBBLESTONE_SLAB_FULL);
	}
	
	private static void connectSlabs(VBEHalfSlabBlock halfSlab, VBEFullSlabBlock fullSlab) {
		halfSlab.setFullBlock(fullSlab);
		fullSlab.setHalfBlock(halfSlab);
	}
	
	public static VBEHalfSlabBlock getSlabByMeta(int meta) {
		VBEHalfSlabBlock block = VBEBlocks.STONE_SLAB_HALF;
		switch (meta & 3) {
			case 1 -> block = VBEBlocks.SANDSTONE_SLAB_HALF;
			case 2 -> block = VBEBlocks.OAK_SLAB_HALF;
			case 3 -> block = VBEBlocks.COBBLESTONE_SLAB_HALF;
		}
		return block;
	}
}
