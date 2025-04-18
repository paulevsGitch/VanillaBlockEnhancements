package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.modificationstation.stationapi.api.state.State;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(State.class)
public class StateMixin {
	@ModifyArg(method = "createCodec", at = @At(
		value = "INVOKE",
		target = "Lcom/mojang/serialization/Codec;dispatch(Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
	), remap = false, index = 2)
	private static <O, S extends State<O, S>> Function<? super O, ? extends Codec<S>> vbe_fixAir(Function<? super O, ? extends Codec<S>> original, @Local(argsOnly = true) Function<O, S> ownerToStateFunction) {
		return (object) -> {
			S state = ownerToStateFunction.apply(object);
			return original.apply(object).orElse(state);
		};
	}
}
