package com.volmit.fulcrum.fx;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import com.volmit.dumpster.M;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleSpellMob;

public class EffectCauldronAcceptRecipe implements Effect
{
	private ParticleSpellMob effect;
	private Audible audio;

	public EffectCauldronAcceptRecipe()
	{
		audio = new Audio().c(SoundCategory.AMBIENT).vp(1f, 1.9f).setSound(Sound.BLOCK_BREWING_STAND_BREW);
		audio.addChild(((Audio) audio).clone().p(1.4f).s(Sound.BLOCK_END_PORTAL_FRAME_FILL).d(5));
		effect = new ParticleSpellMob().setColor(Color.BLUE);
	}

	@Override
	public void play(Location l)
	{
		audio.play(l);

		new TaskLater(5)
		{
			@Override
			public void run()
			{
				for(int i = 0; i < 20; i++)
				{
					if(M.r(0.25))
					{
						effect.setColor(Color.cyan.darker().darker());
					}

					if(M.r(0.25))
					{
						effect.setColor(Color.cyan);
					}

					if(M.r(0.15))
					{
						effect.setColor(Color.BLUE.brighter());
					}

					effect.play(l.clone().add(0.05, 0, 0.05).add((Math.random() - 0.5) * 0.8, 0.56, (Math.random() - 0.5) * 0.8));
				}
			}
		};
	}

	@Override
	public void play(Player p, Location l)
	{
		audio.play(p, l);
		effect.play(l.clone().add(Math.random() / 2, 0, Math.random() / 2), p);
	}

}