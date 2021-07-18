package net.ludocrypt.dynmus.mixin;

import java.util.Optional;
import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.dynmus.DynamicMusic;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
@Mixin(BiomeEffectSoundPlayer.class)
public class BiomeEffectSoundPlayerMixin {

	@Shadow
	private float moodPercentage;

	@Shadow
	@Final
	private ClientPlayerEntity player;

	@Shadow
	private Optional<BiomeMoodSound> moodSound = Optional.empty();

	@Shadow
	@Final
	private Random random;

	@Inject(method = "tick", at = @At("HEAD"))
	private void DYNMUSIC_tick(CallbackInfo ci) {
		this.moodSound.ifPresent((biomeMoodSound) -> {
			World world = this.player.world;
			if (DynamicMusic.isInCave(world, player.getBlockPos()) && DynamicMusic.isInPseudoMineshaft(world, player.getBlockPos())) {
				this.moodPercentage += (float) ((15 - DynamicMusic.getAverageDarkness(world, player.getBlockPos())) / (float) biomeMoodSound.getCultivationTicks());
			}
		});
	}
}
