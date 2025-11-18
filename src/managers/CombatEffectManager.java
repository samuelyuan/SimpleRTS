package managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import entities.GameUnit;
import graphics.Color;
import graphics.IGraphics;
import graphics.Point;

/**
 * Manages visual combat effects including damage numbers, attack animations,
 * death effects, and particle systems.
 */
public class CombatEffectManager {
    private final List<DamageNumber> damageNumbers;
    private final List<AttackAnimation> attackAnimations;
    private final List<DeathAnimation> deathAnimations;
    private final List<CombatParticle> particles;
    private final Random random;
    
    public CombatEffectManager() {
        this.damageNumbers = new ArrayList<>();
        this.attackAnimations = new ArrayList<>();
        this.deathAnimations = new ArrayList<>();
        this.particles = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Creates a damage number effect at the specified position.
     */
    public void createDamageNumber(Point position, int damage, boolean isCritical) {
        damageNumbers.add(new DamageNumber(position, damage, isCritical));
    }
    
    /**
     * Creates an attack animation for a unit.
     */
    public void createAttackAnimation(GameUnit unit) {
        attackAnimations.add(new AttackAnimation(unit));
    }
    
    /**
     * Creates a death animation for a unit.
     */
    public void createDeathAnimation(GameUnit unit) {
        deathAnimations.add(new DeathAnimation(unit));
    }
    
    /**
     * Creates combat particles at the specified position.
     */
    public void createCombatParticles(Point position, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new CombatParticle(position, random));
        }
    }
    
    /**
     * Updates all combat effects.
     */
    public void update() {
        // Update damage numbers
        Iterator<DamageNumber> damageIter = damageNumbers.iterator();
        while (damageIter.hasNext()) {
            DamageNumber damage = damageIter.next();
            damage.update();
            if (damage.isFinished()) {
                damageIter.remove();
            }
        }
        
        // Update attack animations
        Iterator<AttackAnimation> attackIter = attackAnimations.iterator();
        while (attackIter.hasNext()) {
            AttackAnimation attack = attackIter.next();
            attack.update();
            if (attack.isFinished()) {
                attackIter.remove();
            }
        }
        
        // Update death animations
        Iterator<DeathAnimation> deathIter = deathAnimations.iterator();
        while (deathIter.hasNext()) {
            DeathAnimation death = deathIter.next();
            death.update();
            if (death.isFinished()) {
                deathIter.remove();
            }
        }
        
        // Update particles
        Iterator<CombatParticle> particleIter = particles.iterator();
        while (particleIter.hasNext()) {
            CombatParticle particle = particleIter.next();
            particle.update();
            if (particle.isFinished()) {
                particleIter.remove();
            }
        }
    }
    
    /**
     * Renders all combat effects.
     */
    public void render(IGraphics g, int cameraX, int cameraY) {
        // Render damage numbers
        for (DamageNumber damage : damageNumbers) {
            damage.render(g, cameraX, cameraY);
        }
        
        // Render attack animations
        for (AttackAnimation attack : attackAnimations) {
            attack.render(g, cameraX, cameraY);
        }
        
        // Render death animations
        for (DeathAnimation death : deathAnimations) {
            death.render(g, cameraX, cameraY);
        }
        
        // Render particles
        for (CombatParticle particle : particles) {
            particle.render(g, cameraX, cameraY);
        }
    }
    
    /**
     * Represents a floating damage number.
     */
    private static class DamageNumber {
        private Point position;
        private int damage;
        private boolean isCritical;
        private int life;
        private int maxLife;
        private float alpha;
        
        public DamageNumber(Point position, int damage, boolean isCritical) {
            this.position = new Point(position.x, position.y);
            this.damage = damage;
            this.isCritical = isCritical;
            this.maxLife = isCritical ? 60 : 45; // Reduced duration to be less distracting
            this.life = maxLife;
            this.alpha = 1.0f;
        }
        
        public void update() {
            life--;
            position = new Point(position.x, position.y - 1); // Float upward
            alpha = (float) life / maxLife;
        }
        
        public boolean isFinished() {
            return life <= 0;
        }
        
        public void render(IGraphics g, int cameraX, int cameraY) {
            if (alpha <= 0) return;
            
            String text = "-" + damage;
            Color color = isCritical ? new Color(255, 0, 0) : new Color(255, 255, 255);
            
            int screenX = position.x - cameraX;
            int screenY = position.y - cameraY;
            
            // Draw with outline for better visibility
            g.setColor(new Color(0, 0, 0));
            g.drawString(text, screenX + 1, screenY + 1);
            g.setColor(color);
            g.drawString(text, screenX, screenY);
        }
    }
    
    /**
     * Represents an attack animation for a unit.
     */
    private static class AttackAnimation {
        private GameUnit unit;
        private int duration;
        private int maxDuration;
        private Color originalColor;
        
        public AttackAnimation(GameUnit unit) {
            this.unit = unit;
            this.maxDuration = 15; // 15 frames
            this.duration = maxDuration;
        }
        
        public void update() {
            duration--;
        }
        
        public boolean isFinished() {
            return duration <= 0;
        }
        
        public void render(IGraphics g, int cameraX, int cameraY) {
            if (duration <= 0) return;
            
            // Create attack flash effect
            float intensity = (float) duration / maxDuration;
            
            Point unitPos = unit.getCurrentPosition();
            int screenX = unitPos.x - cameraX;
            int screenY = unitPos.y - cameraY;
            
            // Draw attack flash (simplified without alpha)
            if (intensity > 0.3f) { // Only show flash for first 70% of duration
                g.setColor(new Color(255, 255, 0)); // Yellow flash
                g.fillRect(screenX, screenY, 50, 50); // TILE_WIDTH, TILE_HEIGHT
            }
        }
    }
    
    /**
     * Represents a death animation for a unit.
     */
    private static class DeathAnimation {
        private Point position;
        private int duration;
        private int maxDuration;
        private float alpha;
        
        public DeathAnimation(GameUnit unit) {
            this.position = new Point(unit.getCurrentPosition().x, unit.getCurrentPosition().y);
            this.maxDuration = 60; // 1 second at 60 FPS
            this.duration = maxDuration;
            this.alpha = 1.0f;
        }
        
        public void update() {
            duration--;
            alpha = (float) duration / maxDuration;
        }
        
        public boolean isFinished() {
            return duration <= 0;
        }
        
        public void render(IGraphics g, int cameraX, int cameraY) {
            if (alpha <= 0) return;
            
            // Draw fading unit outline
            int screenX = position.x - cameraX;
            int screenY = position.y - cameraY;
            
            // Only show death effect for first 50% of duration
            if (alpha > 0.5f) {
                g.setColor(new Color(255, 0, 0)); // Red fade
                g.drawRect(screenX, screenY, 50, 50); // TILE_WIDTH, TILE_HEIGHT
                
                // Draw cross for death using rectangles
                g.setColor(new Color(255, 255, 255));
                // Draw diagonal line from top-left to bottom-right
                for (int i = 0; i < 30; i++) {
                    g.fillRect(screenX + 10 + i, screenY + 10 + i, 1, 1);
                }
                // Draw diagonal line from top-right to bottom-left
                for (int i = 0; i < 30; i++) {
                    g.fillRect(screenX + 40 - i, screenY + 10 + i, 1, 1);
                }
            }
        }
    }
    
    /**
     * Represents a combat particle effect.
     */
    private static class CombatParticle {
        private Point position;
        private Point velocity;
        private int life;
        private int maxLife;
        private float alpha;
        private Color color;
        
        public CombatParticle(Point position, Random random) {
            this.position = new Point(position.x + random.nextInt(50), position.y + random.nextInt(50));
            this.velocity = new Point(
                random.nextInt(6) - 3, // -3 to 3
                random.nextInt(6) - 3
            );
            this.maxLife = 30 + random.nextInt(30); // 30-60 frames
            this.life = maxLife;
            this.alpha = 1.0f;
            
            // Random particle color
            int colorChoice = random.nextInt(3);
            switch (colorChoice) {
                case 0: this.color = new Color(255, 255, 0); break; // Yellow
                case 1: this.color = new Color(255, 100, 0); break; // Orange
                case 2: this.color = new Color(255, 255, 255); break; // White
            }
        }
        
        public void update() {
            position = new Point(position.x + velocity.x, position.y + velocity.y);
            life--;
            alpha = (float) life / maxLife;
        }
        
        public boolean isFinished() {
            return life <= 0;
        }
        
        public void render(IGraphics g, int cameraX, int cameraY) {
            if (alpha <= 0) return;
            
            int screenX = position.x - cameraX;
            int screenY = position.y - cameraY;
            
            // Only show particles for first 70% of their life
            if (alpha > 0.3f) {
                g.setColor(color);
                g.fillRect(screenX, screenY, 2, 2); // Small particle
            }
        }
    }
}

