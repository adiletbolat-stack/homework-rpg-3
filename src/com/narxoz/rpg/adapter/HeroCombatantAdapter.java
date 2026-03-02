package com.narxoz.rpg.adapter;

import com.narxoz.rpg.battle.Combatant;
import com.narxoz.rpg.hero.Hero;

import java.util.Objects;

public class HeroCombatantAdapter implements Combatant {

    private final Hero hero;

    public HeroCombatantAdapter(Hero hero) {
        this.hero = Objects.requireNonNull(hero, "Hero must not be null");
    }

    @Override
    public String getName() {
        return hero.getName();
    }

    @Override
    public int getAttackPower() {
        int power = hero.getPower();

        return Math.max(0, power);
    }

    @Override
    public void takeDamage(int amount) {
        hero.receiveDamage(amount);
    }

    @Override
    public boolean isAlive() {
        return hero.isAlive();
    }

    public Hero getWrapped() {
        return hero;
    }
}