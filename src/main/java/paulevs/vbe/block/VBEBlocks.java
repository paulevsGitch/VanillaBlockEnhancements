package paulevs.vbe.block;

import net.minecraft.block.Block;
import paulevs.vbe.VBE;
import paulevs.vbe.utils.CreativeUtil;

public class VBEBlocks {
	public static final VBEHalfSlabBlock STONE_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("stone_slab_half"), Block.STONE);
	public static final VBEFullSlabBlock STONE_SLAB_FULL = new VBEFullSlabBlock(VBE.id("stone_slab_full"), Block.STONE);
	public static final VBEHalfSlabBlock SANDSTONE_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("sandstone_slab_half"), Block.SANDSTONE);
	public static final VBEFullSlabBlock SANDSTONE_SLAB_FULL = new VBEFullSlabBlock(VBE.id("sandstone_slab_full"), Block.SANDSTONE);
	public static final VBEHalfSlabBlock OAK_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("oak_slab_half"), Block.WOOD);
	public static final VBEFullSlabBlock OAK_SLAB_FULL = new VBEFullSlabBlock(VBE.id("oak_slab_full"), Block.WOOD);
	public static final VBEHalfSlabBlock COBBLESTONE_SLAB_HALF = new VBEHalfSlabBlock(VBE.id("cobblestone_slab_half"), Block.COBBLESTONE);
	public static final VBEFullSlabBlock COBBLESTONE_SLAB_FULL = new VBEFullSlabBlock(VBE.id("cobblestone_slab_full"), Block.COBBLESTONE);
	public static final VBELogBlock OAK_LOG = new VBELogBlock(VBE.id("oak_log"));
	public static final VBELogBlock SPRUCE_LOG = new VBELogBlock(VBE.id("spruce_log"));
	public static final VBELogBlock BIRCH_LOG = new VBELogBlock(VBE.id("birch_log"));
	public static final VBELeavesBlock OAK_LEAVES = new VanillaLeavesWrapper(VBE.id("oak_leaves"), 0);
	public static final VBELeavesBlock SPRUCE_LEAVES = new VanillaLeavesWrapper(VBE.id("spruce_leaves"), 1);
	public static final VBELeavesBlock BIRCH_LEAVES = new VanillaLeavesWrapper(VBE.id("birch_leaves"), 2);
	
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
	
	public static VBEHalfSlabBlock getHalfSlabByMeta(int meta) {
		return switch (meta & 3) {
			case 1 -> SANDSTONE_SLAB_HALF;
			case 2 -> OAK_SLAB_HALF;
			case 3 -> COBBLESTONE_SLAB_HALF;
			default -> STONE_SLAB_HALF;
		};
	}
	
	public static VBEFullSlabBlock getFullSlabByMeta(int meta) {
		return switch (meta & 3) {
			case 1 -> SANDSTONE_SLAB_FULL;
			case 2 -> OAK_SLAB_FULL;
			case 3 -> COBBLESTONE_SLAB_FULL;
			default -> STONE_SLAB_FULL;
		};
	}
	
	public static VBELogBlock getLogByMeta(int meta) {
		return switch (meta & 3) {
			case 1 -> SPRUCE_LOG;
			case 2 -> BIRCH_LOG;
			default -> OAK_LOG;
		};
	}
	
	public static VBELeavesBlock getLeavesByMeta(int meta) {
		return switch (meta & 3) {
			case 1 -> SPRUCE_LEAVES;
			case 2 -> BIRCH_LEAVES;
			default -> OAK_LEAVES;
		};
	}
}
