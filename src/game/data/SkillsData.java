package game.data;

import game.core.Skill;
import game.core.Profession;

/**
 * SkillsData provides skill templates for each profession.
 * Contains all player skills organized by profession.
 * 
 * BALANCED VERSION:
 * - Rebalanced damages to work with new player stats (20+5 specialization)
 * - Warriors: 25 STR baseline
 * - Rogues: 25 AGI baseline  
 * - Mages: 25 INT baseline
 * 
 * Skill Design:
 * - Each profession has 3 skills
 * - Basic Attack: Low damage, 0 cooldown (spam-able)
 * - Normal Skill: Medium damage, 2-3 cooldown
 * - Ultimate: High damage, 3-5 cooldown
 * 
 * Profession Balance:
 * - WARRIOR: Consistent damage, balanced (STR 25)
 * - MAGE: Highest burst, longest cooldowns (INT 25)
 * - ROGUE: Fast attacks, speed advantage (AGI 25)
 */
public class SkillsData {

    /**
     * Get skills for a specific profession.
     * 
     * @param profession The profession
     * @return Array of 3 skills for that profession
     */
    public static Skill[] getSkillsForProfession(Profession profession) {
        if (profession == null) {
            return getWarriorSkills(); // Default to warrior
        }

        switch (profession) {
            case WARRIOR:
                return getWarriorSkills();
            case MAGE:
                return getMageSkills();
            case ROGUE:
                return getRogueSkills();
            default:
                return getWarriorSkills();
        }
    }

    /**
     * Get a specific skill by profession and index.
     * 
     * @param profession The profession
     * @param skillIndex Skill index (0-2)
     * @return Skill at index, or null if invalid
     */
    public static Skill getSkill(Profession profession, int skillIndex) {
        Skill[] skills = getSkillsForProfession(profession);
        if (skillIndex >= 0 && skillIndex < skills.length) {
            return skills[skillIndex];
        }
        return null;
    }

    // ===== WARRIOR SKILLS =====

    /**
     * Warrior Skills - Balanced Fighter
     * 
     * Specialization: STR (+5 bonus = 25 STR)
     * Play Style: Consistent pressure, reliable damage
     * 
     * Skills:
     * 1. Strike - 8 damage, 0 cooldown (basic)
     * 2. Slash - 17 damage, 2 cooldown (normal)
     * 3. Berserker Rage - 30 damage, 3 cooldown (ultimate)
     * 
     * At 25 STR:
     * - Strike: 33 total damage
     * - Slash: 42 total damage
     * - Berserker Rage: 55 total damage
     * 
     * Total Damage Potential: 130 (8 + 17 + 30 = 55 base × 2.36 avg)
     * 
     * Strength: Consistent, reliable, high HP (135)
     * Weakness: Average speed, no special mechanics
     * Best Against: Minotaur (can tank the damage)
     */
    private static Skill[] getWarriorSkills() {
        return new Skill[] {
            new Skill("Strike", Profession.WARRIOR, 8, 0),
            new Skill("Slash", Profession.WARRIOR, 17, 2),
            new Skill("Berserker Rage", Profession.WARRIOR, 30, 3)
        };
    }

    // ===== MAGE SKILLS =====

    /**
     * Mage Skills - Burst Damage Caster
     * 
     * Specialization: INT (+5 bonus = 25 INT)
     * Play Style: High risk, high reward burst damage
     * 
     * Skills:
     * 1. Magic Bolt - 6 damage, 0 cooldown (basic)
     * 2. Fireball - 20 damage, 3 cooldown (normal)
     * 3. Meteor Strike - 42 damage, 5 cooldown (ultimate)
     * 
     * At 20 STR (no STR bonus):
     * - Magic Bolt: 26 total damage
     * - Fireball: 40 total damage
     * - Meteor Strike: 62 total damage (highest burst!)
     * 
     * Total Damage Potential: 136 (6 + 20 + 42 = 68 base × 2)
     * 
     * Special: INT 25 gives Accuracy 86 and CDR 0.5
     * 
     * Strength: Highest burst damage, good accuracy
     * Weakness: Low HP (120), long cooldowns, weakest basic attack
     * Best Against: Mindflayer (can outburst before CDR matters)
     */
    private static Skill[] getMageSkills() {
        return new Skill[] {
            new Skill("Magic Bolt", Profession.MAGE, 6, 0),
            new Skill("Fireball", Profession.MAGE, 20, 3),
            new Skill("Meteor Strike", Profession.MAGE, 42, 5)
        };
    }

    // ===== ROGUE SKILLS =====

    /**
     * Rogue Skills - Fast Attacker
     * 
     * Specialization: AGI (+5 bonus = 25 AGI)
     * Play Style: Quick burst windows, aggressive
     * 
     * Skills:
     * 1. Quick Stab - 10 damage, 0 cooldown (basic)
     * 2. Backstab - 18 damage, 2 cooldown (normal)
     * 3. Assassinate - 33 damage, 3 cooldown (ultimate)
     * 
     * At 20 STR:
     * - Quick Stab: 30 total damage
     * - Backstab: 38 total damage
     * - Assassinate: 53 total damage
     * 
     * Total Damage Potential: 122 (10 + 18 + 33 = 61 base × 2)
     * 
     * Special: AGI 25 gives Speed 7 and Evasion 9
     * 
     * Strength: Fastest (Speed 7), most turns, good evasion
     * Weakness: Middle damage, needs to dodge to survive
     * Best Against: Killer Bunny (can match speed and evade)
     */
    private static Skill[] getRogueSkills() {
        return new Skill[] {
            new Skill("Quick Stab", Profession.ROGUE, 10, 0),
            new Skill("Backstab", Profession.ROGUE, 18, 2),
            new Skill("Assassinate", Profession.ROGUE, 33, 3)
        };
    }

    // ===== SKILL INFORMATION =====

    /**
     * Get skill count for a profession.
     * Currently always 3 skills per profession.
     * 
     * @param profession The profession
     * @return Number of skills (always 3)
     */
    public static int getSkillCount(Profession profession) {
        return 3;
    }

    /**
     * Get all skill names for a profession.
     * 
     * @param profession The profession
     * @return Array of skill names
     */
    public static String[] getSkillNames(Profession profession) {
        Skill[] skills = getSkillsForProfession(profession);
        String[] names = new String[skills.length];
        
        for (int i = 0; i < skills.length; i++) {
            names[i] = skills[i].getName();
        }
        
        return names;
    }

    /**
     * Get profession description.
     * 
     * @param profession The profession
     * @return Description of profession play style
     */
    public static String getProfessionDescription(Profession profession) {
        if (profession == null) return "Unknown";

        switch (profession) {
            case WARRIOR:
                return "Balanced fighter with consistent damage and high HP";
            case MAGE:
                return "Powerful spellcaster with devastating burst damage";
            case ROGUE:
                return "Swift assassin with speed advantage and evasion";
            default:
                return "Unknown";
        }
    }

    /**
     * Get total damage potential for a profession.
     * Sum of all base damage values (before STR modifier).
     * 
     * @param profession The profession
     * @return Total base damage
     */
    public static int getTotalBaseDamage(Profession profession) {
        Skill[] skills = getSkillsForProfession(profession);
        int total = 0;
        
        for (Skill skill : skills) {
            total += skill.getBaseDamage();
        }
        
        return total;
    }

    /**
     * Compare professions by stats and playstyle.
     * 
     * @return String comparison of professions
     */
    public static String compareProfessions() {
        return "=== PROFESSION COMPARISON ===\n\n" +
               "WARRIOR (STR 25, AGI 20, INT 20):\n" +
               "  HP: 135 | Speed: 6 | Accuracy: 85\n" +
               "  Damage: 33 / 42 / 55 (total: 130)\n" +
               "  Style: Tank and consistent damage\n" +
               "  Best vs: Minotaur\n\n" +
               
               "ROGUE (STR 20, AGI 25, INT 20):\n" +
               "  HP: 120 | Speed: 7 | Accuracy: 85 | Evasion: 9\n" +
               "  Damage: 30 / 38 / 53 (total: 121)\n" +
               "  Style: Speed and evasion\n" +
               "  Best vs: Killer Bunny\n\n" +
               
               "MAGE (STR 20, AGI 20, INT 25):\n" +
               "  HP: 120 | Speed: 6 | Accuracy: 86 | CDR: 0.5\n" +
               "  Damage: 26 / 40 / 62 (total: 128)\n" +
               "  Style: Burst damage glass cannon\n" +
               "  Best vs: Mindflayer";
    }
}