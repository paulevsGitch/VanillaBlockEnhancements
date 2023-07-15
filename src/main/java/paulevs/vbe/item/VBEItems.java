package paulevs.vbe.item;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.DoorBlock;
import paulevs.vbe.VBE;

public class VBEItems {
	public static final VBEDoorItem OAK_DOOR = new VBEDoorItem(VBE.id("oak_door"), (DoorBlock) BaseBlock.WOOD_DOOR);
	public static final VBEDoorItem IRON_DOOR = new VBEDoorItem(VBE.id("iron_door"), (DoorBlock) BaseBlock.IRON_DOOR);
	
	public static void init() {}
}
