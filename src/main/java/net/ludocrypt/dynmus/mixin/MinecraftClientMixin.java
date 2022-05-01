package net.ludocrypt.dynmus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.dynmus.DynamicMusic;
import net.ludocrypt.dynmus.config.DynamicMusicConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Inject(method = "getMusicType", at = @At("RETURN"), cancellable = true)
	private void dynmus$getMusicType(CallbackInfoReturnable<MusicSound> ci) {
		if (ci.getReturnValue().equals(MusicType.GAME) || ci.getReturnValue().equals(MusicType.CREATIVE)) {
			if (this.world != null) {
				if (DynamicMusic.isInCave(world, player.getBlockPos())) {
					dynmus$setReturnType(ci, DynamicMusic.MUSIC_CAVE);
				} else if ((world.getBiomeAccess().getBiome(this.player.getBlockPos()).value().getTemperature() < 0.15F) || (world.isRaining()) && DynamicMusicConfig.getInstance().coldMusic) {
					dynmus$setReturnType(ci, DynamicMusic.MUSIC_COLD);
				} else if ((world.getBiomeAccess().getBiome(this.player.getBlockPos()).value().getTemperature() > 0.95F) && (!world.isRaining()) && DynamicMusicConfig.getInstance().hotMusic) {
					dynmus$setReturnType(ci, DynamicMusic.MUSIC_HOT);
				} else if (world.getTimeOfDay() <= 12500 && DynamicMusicConfig.getInstance().niceMusic) {
					dynmus$setReturnType(ci, DynamicMusic.MUSIC_NICE);
				} else if (world.getTimeOfDay() > 12500 && DynamicMusicConfig.getInstance().downMusic) {
					dynmus$setReturnType(ci, DynamicMusic.MUSIC_DOWN);
				}
			}
		} else if (ci.getReturnValue() == MusicType.DRAGON) {
			ci.setReturnValue(new MusicSound(DynamicMusic.MUSIC_END_BOSS, 0, 0, true));
		} else if (ci.getReturnValue() == MusicType.END) {
			if (this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
				ci.setReturnValue(new MusicSound(DynamicMusic.MUSIC_END_CREATIVE, 1200, 8000, true));
			}
		}
	}

	@Unique
	private void dynmus$setReturnType(CallbackInfoReturnable<MusicSound> ci, SoundEvent e) {
		ci.setReturnValue(MusicType.createIngameMusic(new SoundEvent(ci.getReturnValue() == MusicType.CREATIVE ? e.getId() : new Identifier(String.join("", e.getId().toString(), ".creative")))));
	}
}
