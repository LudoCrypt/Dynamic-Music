package net.ludocrypt.dynmus.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "dynamicmusic")
@Config.Gui.Background(value = "minecraft:textures/block/stone.png")
public class DynamicMusicConfig implements ConfigData {

	@ConfigEntry.Gui.Tooltip()
	public int searchRange = 5;

	@BoundedDiscrete(min = 0, max = 15)
	@ConfigEntry.Gui.Tooltip()
	public int darknessCap = 8;

	@ConfigEntry.Gui.Tooltip()
	public double darknessPercent = 0.3;

	@ConfigEntry.Gui.Tooltip()
	public double stonePercent = 0.15;

	@ConfigEntry.Gui.Tooltip()
	public int pseudoMineshaftSearchRange = 2;

	@ConfigEntry.Gui.Tooltip()
	public double pseudoMineshaftPercent = 0.1;

	@ConfigEntry.Gui.Tooltip()
	public boolean coldMusic = true;

	@ConfigEntry.Gui.Tooltip()
	public boolean hotMusic = true;

	@ConfigEntry.Gui.Tooltip()
	public boolean niceMusic = true;

	@ConfigEntry.Gui.Tooltip()
	public boolean downMusic = true;

	@ConfigEntry.Gui.Tooltip()
	public boolean dynamicPitch = true;
	
	@ConfigEntry.Gui.Tooltip()
	public long dynamicPitchAnchor = 18000;

	@ConfigEntry.Gui.Tooltip()
	public boolean dynamicPitchFaster = false;
	
	public static void init() {
		AutoConfig.register(DynamicMusicConfig.class, GsonConfigSerializer::new);
	}

	public static DynamicMusicConfig getInstance() {
		return AutoConfig.getConfigHolder(DynamicMusicConfig.class).getConfig();
	}

}
