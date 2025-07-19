package paulevs.vbe.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class ItemConverter {
	private static final Block[] LOGS = new Block[] {
		VBEBlocks.OAK_LOG,
		VBEBlocks.SPRUCE_LOG,
		VBEBlocks.BIRCH_LOG
	};
	
	public static int getID(int itemId, int damage) {
		Item item = null;
		if (itemId == Block.STONE_SLAB.asItem().id && VBE.ENHANCED_SLABS.getValue()) item = VBEBlocks.getHalfSlabByMeta(damage).asItem();
		if (itemId == Block.LOG.asItem().id) item = VBEBlocks.getLogByMeta(damage).asItem();
		if (itemId == Block.LEAVES.asItem().id) item = VBEBlocks.getLeavesByMeta(damage).asItem();
		if (itemId == Item.woodDoor.id) item = VBEItems.OAK_DOOR.asItem();
		if (itemId == Item.ironDoor.id) item = VBEItems.IRON_DOOR.asItem();
		return item == null ? -1 : item.id;
	}
	
	public static int getDamage(int id, int damage) {
		return id == Block.STONE_SLAB.id && VBE.ENHANCED_SLABS.getValue() ? damage : 0;
	}
	
	public static boolean resetDamage(int itemId) {
		for (Block log : LOGS) {
			Item item = log.asItem();
			if (item == null) continue;
			if (itemId == item.id) return true;
		}
		return false;
	}
}
