package net.ludocrypt.dynmus;

import java.util.LinkedHashMap;
import java.util.Map;

import net.fabricmc.api.ClientModInitializer;
import net.ludocrypt.dynmus.config.DynamicMusicConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DynamicMusic implements ClientModInitializer {

	private static final Map<Identifier, SoundEvent> SOUND_EVENTS = new LinkedHashMap<>();

	public static final SoundEvent MUSIC_COLD = add("music.cold");
	public static final SoundEvent MUSIC_HOT = add("music.hot");
	public static final SoundEvent MUSIC_CAVE = add("music.cave");

	public static final SoundEvent MUSIC_NICE = add("music.nice");
	public static final SoundEvent MUSIC_DOWN = add("music.down");

	public static final SoundEvent MUSIC_COLD_CREATIVE = add("music.cold.creative");
	public static final SoundEvent MUSIC_HOT_CREATIVE = add("music.hot.creative");
	public static final SoundEvent MUSIC_CAVE_CREATIVE = add("music.cave.creative");

	public static final SoundEvent MUSIC_NICE_CREATIVE = add("music.nice.creative");
	public static final SoundEvent MUSIC_DOWN_CREATIVE = add("music.down.creative");

	public static final SoundEvent MUSIC_END_CREATIVE = add("music.end.creative");
	public static final SoundEvent MUSIC_END_BOSS = add("music.end.boss");

	private static SoundEvent add(String id) {
		Identifier realId = id(id);
		SoundEvent sound = new SoundEvent(realId);
		SOUND_EVENTS.put(realId, sound);
		return sound;
	}

	@Override
	public void onInitializeClient() {
		DynamicMusicConfig.init();
		for (Identifier id : SOUND_EVENTS.keySet()) {
			Registry.register(Registry.SOUND_EVENT, id, SOUND_EVENTS.get(id));
		}
	}

	public static Identifier id(String id) {
		return new Identifier("dynmus", id);
	}

	public static boolean isInCave(World world, BlockPos pos) {
		int searchRange = DynamicMusicConfig.getInstance().searchRange;

		if (searchRange >= 1 && !world.isSkyVisible(pos)) {
			int darkBlocks = 0;
			int stoneBlocks = 0;
			int airBlocks = 0;

			for (int x = -searchRange; x < searchRange; x++) {
				for (int y = -searchRange; y < searchRange; y++) {
					for (int z = -searchRange; z < searchRange; z++) {
						BlockPos offsetPos = pos.add(x, y, z);
						if (world.isAir(offsetPos)) {
							airBlocks++;
							if (world.getLightLevel(offsetPos) <= DynamicMusicConfig.getInstance().darknessCap) {
								darkBlocks++;
							}
						}
						if (world.getBlockState(offsetPos).getMaterial() == Material.LAVA) {
							darkBlocks++;
						}
						if (world.getBlockState(offsetPos).getMaterial() == Material.STONE) {
							stoneBlocks++;
						}
					}
				}
			}

			double blockCount = Math.pow(searchRange * 2, 3);

			double stonePercentage = ((double) stoneBlocks) / (blockCount);
			double darkPercentage = ((double) darkBlocks) / ((double) airBlocks);

			if (darkPercentage >= DynamicMusicConfig.getInstance().darknessPercent) {
				return stonePercentage >= DynamicMusicConfig.getInstance().stonePercent;
			}
		}
		return false;
	}

	public static double getAverageDarkness(World world, BlockPos pos) {
		int searchRange = DynamicMusicConfig.getInstance().searchRange;

		if (searchRange >= 1) {
			int airBlocks = 0;
			int lightTogether = 0;

			for (int x = -searchRange; x < searchRange; x++) {
				for (int y = -searchRange; y < searchRange; y++) {
					for (int z = -searchRange; z < searchRange; z++) {
						BlockPos offsetPos = pos.add(x, y, z);
						if (world.isAir(offsetPos)) {
							airBlocks++;
							lightTogether += world.getLightLevel(offsetPos);
						}
					}
				}
			}

			return (((double) lightTogether) / ((double) airBlocks));

		}
		return 15;
	}

	public static boolean isInPseudoMineshaft(World world, BlockPos pos) {
		int searchRange = DynamicMusicConfig.getInstance().pseudoMineshaftSearchRange;

		if (searchRange >= 1) {

			int pseudoMineshaftBlocks = 0;
			int airBlocks = 0;

			for (int x = -searchRange; x < searchRange; x++) {
				for (int y = -searchRange; y < searchRange; y++) {
					for (int z = -searchRange; z < searchRange; z++) {
						BlockPos offsetPos = pos.add(x, y, z);

						if (world.getBlockState(offsetPos).getMaterial() == Material.WOOD || world.getBlockState(offsetPos).getBlock() == Blocks.RAIL || world.getBlockState(offsetPos).getMaterial() == Material.COBWEB) {
							pseudoMineshaftBlocks++;
						}

						if (world.isAir(offsetPos)) {
							airBlocks++;
						}

					}
				}
			}

			double mineshaftPercentage = ((double) pseudoMineshaftBlocks) / ((double) airBlocks);

			return mineshaftPercentage >= DynamicMusicConfig.getInstance().pseudoMineshaftPercent;
		}

		return false;
	}

}
