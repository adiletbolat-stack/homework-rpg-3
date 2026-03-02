package com.narxoz.rpg.adapter;

import com.narxoz.rpg.battle.Combatant;
import com.narxoz.rpg.enemy.Enemy;

import java.util.Objects;

public class EnemyCombatantAdapter implements Combatant {

    private final Enemy enemy;

    public EnemyCombatantAdapter(Enemy enemy) {
        this.enemy = Objects.requireNonNull(enemy, "Enemy must not be null");
    }

    @Override
    public String getName() {
        return enemy.getTitle();
    }

    @Override
    public int getAttackPower() {
        int damage = enemy.getDamage();

        return Math.max(0, damage);
    }

    @Override
    public void takeDamage(int amount) {
        enemy.applyDamage(amount);
    }

    @Override
    public boolean isAlive() {
        return !enemy.isDefeated();
    }

    public Enemy getWrapped() {
        return enemy;
    }
}