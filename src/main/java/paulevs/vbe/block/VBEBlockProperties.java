package paulevs.vbe.block;

import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;

public class VBEBlockProperties {
	public static final EnumProperty<Direction> DIRECTION = EnumProperty.of("direction", Direction.class);
	public static final EnumProperty<Axis> AXIS = EnumProperty.of("axis", Axis.class);
}
