package com.narxoz.rpg.hero;

public class Mage implements Hero {
    private final String name;
    private final int power;
    private int health;

    private static final int BASE_POWER = 25;
    private static final int BASE_HEALTH = 80;

    public Mage(String name) {
        this.name = name;


        this.power = BASE_POWER;
        this.health = BASE_HEALTH;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void receiveDamage(int amount) {

        if (amount <= 0) {
            return;
        }

        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    public int getHealth() {
        return health;
    }
}