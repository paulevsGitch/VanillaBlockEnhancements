package paulevs.vbe.item;

import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.template.item.TemplateDoorItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;
import paulevs.vbe.utils.CreativeUtil;
import paulevs.vbe.utils.LevelUtil;

public class VBEDoorItem extends TemplateDoorItem {
	private final DoorBlock door;
	
	public VBEDoorItem(Identifier id, DoorBlock door) {
		super(id, Material.WOOD);
		setTranslationKey(id);
		this.door = door;
	}
	
	@Override
	public boolean useOnBlock(ItemStack stack, PlayerEntity player, Level level, int x, int y, int z, int side) {
		Direction direction = Direction.byId(side);
		
		x += direction.getOffsetX();
		y += direction.getOffsetY();
		z += direction.getOffsetZ();
		
		if (!door.canPlaceAt(level, x, y, z) || !level.getBlockState(x, y + 1, z).getMaterial().isReplaceable()) {
			return false;
		}
		
		direction = Direction.fromRotation(player == null ? 0 : player.yaw).getOpposite();
		BlockState state = door.getDefaultState().with(VBEBlockProperties.FACING, direction);
		boolean power = level.hasRedstonePower(x, y, z) || level.hasRedstonePower(x, y + 1, z);
		state = state.with(VBEBlockProperties.OPENED, power);
		
		direction = direction.rotateClockwise(Axis.Y);
		BlockState sideState = level.getBlockState(
			x + direction.getOffsetX(),
			y + direction.getOffsetY(),
			z + direction.getOffsetZ()
		);
		
		boolean inverted = sideState.isOf(door) && !sideState.get(VBEBlockProperties.INVERTED);
		state = state.with(VBEBlockProperties.INVERTED, inverted);
		
		level.stopPhysics = true;
		LevelUtil.setBlockSilent(level, x, y, z, state.with(VBEBlockProperties.TOP_BOTTOM, TopBottom.BOTTOM));
		LevelUtil.setBlockSilent(level, x, y + 1, z, state.with(VBEBlockProperties.TOP_BOTTOM, TopBottom.TOP));
		level.stopPhysics = false;
		
		level.updateArea(x, y, z, x, y + 1, z);
		
		level.updateAdjacentBlocks(x, y, z, door.id);
		level.updateAdjacentBlocks(x, y + 1, z, door.id);
		
		if (!CreativeUtil.isCreative(player)) stack.count--;
		
		return true;
	}
}
