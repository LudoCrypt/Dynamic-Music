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
	private void DYNMUSIC_getMusicType(CallbackInfoReturnable<MusicSound> ci) {
		if (ci.getReturnValue() == MusicType.GAME || ci.getReturnValue() == MusicType.CREATIVE) {
			if (this.world != null) {
				if (DynamicMusic.isInCave(world, player.getBlockPos())) {
					DYNMUS_setReturnType(ci, DynamicMusic.MUSIC_CAVE);
				} else if ((world.getBiomeAccess().getBiome(this.player.getBlockPos()).getTemperature() < 0.15F) || (world.isRaining()) && DynamicMusicConfig.getInstance().coldMusic) {
					DYNMUS_setReturnType(ci, DynamicMusic.MUSIC_COLD);
				} else if ((world.getBiomeAccess().getBiome(this.player.getBlockPos()).getTemperature() > 0.95F) && (!world.isRaining()) && DynamicMusicConfig.getInstance().hotMusic) {
					DYNMUS_setReturnType(ci, DynamicMusic.MUSIC_HOT);
				} else if (world.getTimeOfDay() <= 12500 && DynamicMusicConfig.getInstance().niceMusic) {
					DYNMUS_setReturnType(ci, DynamicMusic.MUSIC_NICE);
				} else if (world.getTimeOfDay() > 12500 && DynamicMusicConfig.getInstance().downMusic) {
					DYNMUS_setReturnType(ci, DynamicMusic.MUSIC_DOWN);
				}
			}
		} else if (ci.getReturnValue() == MusicType.DRAGON) {
			ci.setReturnValue(MusicType.createIngameMusic(DynamicMusic.MUSIC_END_BOSS));
		} else if (ci.getReturnValue() == MusicType.END) {
			if (this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
				ci.setReturnValue(MusicType.createIngameMusic(DynamicMusic.MUSIC_END_CREATIVE));
			}
		}
	}

	@Unique
	private void DYNMUS_setReturnType(CallbackInfoReturnable<MusicSound> ci, SoundEvent e) {
		ci.setReturnValue(MusicType.createIngameMusic(new SoundEvent(ci.getReturnValue() == MusicType.CREATIVE ? e.getId() : new Identifier(String.join("", e.getId().toString(), ".creative")))));
	}
}
