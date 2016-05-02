package com.ninjaguild.dragoneggdrop;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonDeathRunnable implements Runnable {

	private DragonEggDrop plugin = null;

	private int particleAmount = 0;
	private double particleLength = 0D;
	private double particleExtra = 0D;
	private long particleInterval = 0L;
	private double oX = 0D;
	private double oY = 0D;
	private double oZ = 0D;
	private Particle particleType = null;
	
	private boolean respawnDragon = false;
	private int respawnDelay = 0;

	private World world = null;

	private boolean placeEgg = false;

	public DragonDeathRunnable(DragonEggDrop plugin, World world, boolean prevKilled) {
		this.plugin = plugin;
		this.world = world;

		this.placeEgg = prevKilled;

		particleAmount = plugin.getConfig().getInt("particle-amount", 4);
		particleLength = plugin.getConfig().getDouble("particle-length", 6.0D);
		particleExtra = plugin.getConfig().getDouble("particle-extra", 0.0D);
		particleInterval = plugin.getConfig().getLong("particle-interval", 1L);
		oX = plugin.getConfig().getDouble("particle-offset-x", 0.25D);
		oY = plugin.getConfig().getDouble("particle-offset-y", 0D);
		oZ = plugin.getConfig().getDouble("particle-offset-z", 0.25D);
		particleType = Particle.valueOf(plugin.getConfig().getString("particle-type", "FLAME").toUpperCase());
		
		respawnDragon = plugin.getConfig().getBoolean("respawn", false);
		respawnDelay = plugin.getConfig().getInt("respawn-delay", 300);//seconds
	}

	@Override
	public void run() {
		double startY = plugin.getConfig().getDouble("egg-start-y", 180D);

		new BukkitRunnable()
		{
			double currentY = startY;
			Location pLoc = new Location(world, 0.5D, currentY, 0.5D, 0f, 90f);

			@Override
			public void run() {
				currentY -= 1D;
				pLoc.setY(currentY);

				for (double d = 0; d < particleLength; d+=0.1D) {
					world.spawnParticle(particleType, pLoc.clone().add(pLoc.getDirection().normalize().multiply(d * -1)),
							particleAmount, oX, oY, oZ, particleExtra, null);
				}

				if (world.getBlockAt(pLoc).getType() == Material.BEDROCK) {
					cancel();
					
					new BukkitRunnable()
					{
						@Override
						public void run() {
							Location prevLoc = pLoc.clone().add(new Vector(0D, 1D, 0D));

							int lightningAmount = plugin.getConfig().getInt("lightning-amount", 4);
							for (int i = 0; i < lightningAmount; i++) {
								world.strikeLightningEffect(prevLoc);
							}

							if (placeEgg) {
								world.getBlockAt(prevLoc).setType(Material.DRAGON_EGG);
							}
							
							if (respawnDragon) {
								new BukkitRunnable() {
									@Override
									public void run() {
										//start respawn process
										//would be nice to have a countdown in the 
										//boss bar or action bar
										Location[] crystalLocs = new Location[] {
										    prevLoc.clone().add(3, -3, 0),
										    prevLoc.clone().add(0, -3, 3),
										    prevLoc.clone().add(-3, -3, 0),
										    prevLoc.clone().add(0, -3, -3)
										};
										for (int i = 0; i < crystalLocs.length; i++) {
											EnderCrystal crystal = (EnderCrystal)world.spawnEntity(crystalLocs[i], EntityType.ENDER_CRYSTAL);
											crystal.setShowingBottom(false);
											plugin.getEnderDragonBattleFromWorld(world).e();
										}
									}
									
								}.runTaskLater(plugin, respawnDelay * 20);
							}
						}

					}.runTask(plugin);
				}
			}

		}.runTaskTimerAsynchronously(plugin, 0L, particleInterval);
	}

}