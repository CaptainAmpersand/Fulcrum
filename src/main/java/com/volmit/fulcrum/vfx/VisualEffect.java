package com.volmit.fulcrum.vfx;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.volmit.dumpster.GList;

public interface VisualEffect
{
	public void play(Location l);

	public void play(Location l, double r);

	public void play(Location l, Player p);

	public void play(Location l, GList<Player> p);
}
