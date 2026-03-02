package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BattleEngine {

    private static BattleEngine instance;

    private static final long DEFAULT_SEED = 1L;

    private Random random = new Random(DEFAULT_SEED);
    private long currentSeed = DEFAULT_SEED;

    private BattleEngine() { }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.currentSeed = seed;
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
        this.lastEncounterRounds = 0;
        setRandomSeed(DEFAULT_SEED);
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        Objects.requireNonNull(teamA, "teamA must not be null");
        Objects.requireNonNull(teamB, "teamB must not be null");

        if (teamA.isEmpty()) {
            throw new IllegalArgumentException("teamA must not be empty");
        }
        if (teamB.isEmpty()) {
            throw new IllegalArgumentException("teamB must not be empty");
        }

        for (Combatant c : teamA) {
            if (c == null) throw new IllegalArgumentException("teamA contains null combatant");
        }
        for (Combatant c : teamB) {
            if (c == null) throw new IllegalArgumentException("teamB contains null combatant");
        }


        List<Combatant> a = new ArrayList<>(teamA);
        List<Combatant> b = new ArrayList<>(teamB);


        pruneDead(a);
        pruneDead(b);

        if (a.isEmpty() && b.isEmpty()) {
            throw new IllegalArgumentException("Both teams have 0 living combatants at start");
        }
        if (a.isEmpty()) {
            return immediateResult("B", 0, "Team A has 0 living combatants at start");
        }
        if (b.isEmpty()) {
            return immediateResult("A", 0, "Team B has 0 living combatants at start");
        }

        EncounterResult result = new EncounterResult();
        result.addLog("=== Encounter start ===");
        result.addLog("Engine seed = " + currentSeed);
        result.addLog("Team A living = " + a.size() + ", Team B living = " + b.size());

        int rounds = 0;

        while (!a.isEmpty() && !b.isEmpty()) {
            rounds++;
            result.addLog("");
            result.addLog("--- Round " + rounds + " ---");

            teamAttacks(a, b, "A", "B", result);
            pruneDead(b);
            if (b.isEmpty()) break;

            teamAttacks(b, a, "B", "A", result);
            pruneDead(a);
        }

        this.lastEncounterRounds = rounds;

        String winner = a.isEmpty() ? "B" : "A";
        result.setWinner(winner);
        result.setRounds(rounds);

        result.addLog("");
        result.addLog("=== Encounter end ===");
        result.addLog("Winner: Team " + winner);
        result.addLog("Rounds: " + rounds);

        return result;
    }


    private EncounterResult immediateResult(String winner, int rounds, String reason) {
        EncounterResult r = new EncounterResult();
        r.setWinner(winner);
        r.setRounds(rounds);
        r.addLog("=== Encounter start ===");
        r.addLog("Engine seed = " + currentSeed);
        r.addLog("Immediate finish: " + reason);
        r.addLog("Winner: Team " + winner);
        return r;
    }

    private void teamAttacks(
            List<Combatant> attackers,
            List<Combatant> defenders,
            String attackersLabel,
            String defendersLabel,
            EncounterResult result
    ) {
        for (Combatant attacker : attackers) {
            if (defenders.isEmpty()) return;
            if (attacker == null || !attacker.isAlive()) continue;

            Combatant target = defenders.get(random.nextInt(defenders.size()));

            int base = Math.max(0, attacker.getAttackPower());

            int variance = random.nextInt(3);

            boolean crit = random.nextInt(100) < 20;

            int damage = base + variance;
            if (crit) damage = damage * 2;
            if (damage < 0) damage = 0;

            result.addLog(
                    "Team " + attackersLabel + ": " + attacker.getName()
                            + " attacks " + target.getName()
                            + " for " + damage + " dmg"
                            + (crit ? " (CRIT)" : "")
            );

            target.takeDamage(damage);

            if (!target.isAlive()) {
                result.addLog("Team " + defendersLabel + ": " + target.getName() + " is defeated!");
                defenders.remove(target);
            }
        }
    }

    private void pruneDead(List<Combatant> team) {
        Iterator<Combatant> it = team.iterator();
        while (it.hasNext()) {
            Combatant c = it.next();
            if (c == null || !c.isAlive()) it.remove();
        }
    }
}