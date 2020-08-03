package com.ninjaguild.dragoneggdrop.particle;

import java.util.ArrayList;
import java.util.List;

import com.ninjaguild.dragoneggdrop.particle.condition.ConditionContext;
import com.ninjaguild.dragoneggdrop.particle.condition.EquationCondition;
import com.ninjaguild.dragoneggdrop.utils.math.MathExpression;

import org.bukkit.Particle;

/**
 * Represents a pair of equations along the x and z axis as well as a list of conditions
 * that must be met to be used by an {@link AnimatedParticleSession}. Includes a set of
 * data that will be used to spawn particles at any given point in an animation.
 *
 * @author Parker Hawke - Choco
 */
public class ConditionalEquationData {

    Particle particle;
    int particleAmount;
    double particleExtra;
    float particleOffsetX, particleOffsetY, particleOffsetZ;
    int particleStreams;

    double speedMultiplier;
    int frameIntervalTicks;
    double thetaIncrement;

    private List<EquationCondition> conditions;

    private final MathExpression xExpression, zExpression;

    /**
     * Construct conditional equation data with a pair of equations.
     *
     * @param xExpression the x expression
     * @param zExpression the z expression
     */
    public ConditionalEquationData(MathExpression xExpression, MathExpression zExpression) {
        this.xExpression = xExpression;
        this.zExpression = zExpression;
    }

    /**
     * Get the mathematical expression to be used along the x axis.
     *
     * @return the x axis expression
     */
    public MathExpression getXExpression() {
        return xExpression;
    }

    /**
     * Get the mathematical expression to be used along the z axis.
     *
     * @return the z axis expression
     */
    public MathExpression getZExpression() {
        return zExpression;
    }

    /**
     * Add a condition that must be met for this equation data.
     *
     * @param condition the condition to add
     */
    public void addCondition(EquationCondition condition) {
        if (conditions == null) {
            this.conditions = new ArrayList<>();
        }

        this.conditions.add(condition);
    }

    /**
     * Check whether or not this equation data's conditions have all been met given the
     * provided context.
     *
     * @param context the context against which to check
     *
     * @return true if met, false otherwise
     */
    public boolean isMet(ConditionContext context) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        for (EquationCondition condition : conditions) {
            if (!condition.isMet(context)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the particle to be spawned for this shape definition.
     *
     * @return the particle
     */
    public Particle getParticle() {
        return particle;
    }

    /**
     * Get the amount of particles to spawn.
     *
     * @return the particle amount
     */
    public int getParticleAmount() {
        return particleAmount;
    }

    /**
     * Get the extra data for this particle definition. Most particles are not affected by
     * extra data.
     *
     * @return the particle extra data
     */
    public double getParticleExtra() {
        return particleExtra;
    }

    /**
     * Get the particle's x offset.
     *
     * @return the x offset
     */
    public float getParticleOffsetX() {
        return particleOffsetX;
    }

    /**
     * Get the particle's y offset.
     *
     * @return the y offset
     */
    public float getParticleOffsetY() {
        return particleOffsetY;
    }

    /**
     * Get the particle's z offset.
     *
     * @return the z offset
     */
    public float getParticleOffsetZ() {
        return particleOffsetZ;
    }

    /**
     * Get the amount of particle streams to generate.
     *
     * @return the stream count
     */
    public int getParticleStreams() {
        return particleStreams;
    }

    /**
     * Get the animation's speed multiplier.
     *
     * @return the speed multiplier
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Get the amount of ticks between each frame of the animation.
     *
     * @return the frame interval in ticks
     */
    public int getFrameIntervalTicks() {
        return frameIntervalTicks;
    }

    /**
     * Get the value by which theta will be incremented each tick in the animation.
     *
     * @return the theta increment
     */
    public double getThetaIncrement() {
        return thetaIncrement;
    }

}