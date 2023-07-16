package paulevs.vbe.block;

import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.StringIdentifiable;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;

public class VBEBlockProperties {
	public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class, dir -> dir.getAxis().isHorizontal());
	public static final EnumProperty<Direction> DIRECTION = EnumProperty.of("direction", Direction.class);
	public static final EnumProperty<StairsPart> STAIRS_PART = EnumProperty.of("part", StairsPart.class);
	public static final EnumProperty<ChestPart> CHEST_PART = EnumProperty.of("part", ChestPart.class);
	public static final EnumProperty<TopBottom> TOP_BOTTOM = EnumProperty.of("part", TopBottom.class);
	public static final EnumProperty<Axis> AXIS = EnumProperty.of("axis", Axis.class);
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	public static final BooleanProperty NATURAL = BooleanProperty.of("natural");
	public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
	public static final BooleanProperty OPENED = BooleanProperty.of("opened");
	
	public enum ChestPart implements StringIdentifiable {
		SINGLE("single"), LEFT("left"), RIGHT("right");
		
		final String name;
		
		ChestPart(String name) {
			this.name = name;
		}
		
		@Override
		public String asString() {
			return name;
		}
	}
	
	public enum TopBottom implements StringIdentifiable {
		TOP("top"), BOTTOM("bottom");
		
		final String name;
		
		TopBottom(String name) {
			this.name = name;
		}
		
		@Override
		public String asString() {
			return name;
		}
	}
	
	public enum StairsPart implements StringIdentifiable {
		BOTTOM("bottom"), SIDE("side"), TOP("top");
		
		final String name;
		
		StairsPart(String name) {
			this.name = name;
		}
		
		@Override
		public String asString() {
			return name;
		}
	}
}
