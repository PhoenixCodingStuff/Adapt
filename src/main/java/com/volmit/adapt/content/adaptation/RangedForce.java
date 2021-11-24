package com.volmit.adapt.content.adaptation;

import com.volmit.adapt.api.adaptation.SimpleAdaptation;
import com.volmit.adapt.api.advancement.AdaptAdvancement;
import com.volmit.adapt.util.C;
import com.volmit.adapt.util.Element;
import com.volmit.adapt.util.Form;
import com.volmit.adapt.util.KList;
import eu.endercentral.crazy_advancements.AdvancementDisplay;
import eu.endercentral.crazy_advancements.AdvancementVisibility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class RangedForce extends SimpleAdaptation<RangedForce.Config> {
    private final KList<Integer> holds = new KList<>();

    public RangedForce() {
        super("force");
        setDescription("Shoot projectiles further, faster!");
        setIcon(Material.ARROW);
        setBaseCost(2);
        setMaxLevel(7);
        setInterval(5000);
        setInitialCost(5);
        setCostFactor(0.225);
        registerAdvancement(AdaptAdvancement.builder()
            .icon(Material.SPECTRAL_ARROW)
            .key("challenge_force_30")
            .title("Long Shot")
            .description("Land a shot from over 30 blocks away!")
            .frame(AdvancementDisplay.AdvancementFrame.CHALLENGE)
            .visibility(AdvancementVisibility.PARENT_GRANTED)
            .build());
    }

    @Override
    public void addStats(int level, Element v) {
        v.addLore(C.GREEN + "+ " + Form.pc(getSpeed(getLevelPercent(level)), 0) + C.GRAY + " Projectile Speed");
    }

    private double getSpeed(double factor) {
        return (factor * 1.135);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Projectile r && r.getShooter() instanceof Player p && hasAdaptation(p) && !getPlayer(p).getData().isGranted("challenge_force_30"))
        {
            Location a = e.getEntity().getLocation().clone();
            Location b = p.getLocation().clone();
            a.setY(0);
            b.setY(0);

            if(a.distanceSquared(b) > 10)
            {
                getPlayer(p).getAdvancementHandler().grant("challenge_force_30");
                getSkill().xp(p, 2000);
            }
        }
    }

    @EventHandler
    public void on(ProjectileLaunchEvent e) {
        if(e.getEntity().getShooter() instanceof Player) {
            Player p = ((Player) e.getEntity().getShooter());

            if(getLevel(p) > 0) {
                double factor = getLevelPercent(p);
                e.getEntity().setVelocity(e.getEntity().getVelocity().clone().multiply(1 + getSpeed(factor)));
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SNOWBALL_THROW, 0.5f + ((float) factor * 0.25f), 0.7f + (float) (factor / 2f));
            }
        }
    }

    @Override
    public void onTick() {

    }

    protected static class Config{}
}
