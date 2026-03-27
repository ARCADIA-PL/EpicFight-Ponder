package org.com.efp.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.api.animation.types.AttackAnimation;


public class PonderCombatEvent extends Event {
    private final LivingEntity attacker;
    private final LivingEntity target;
    private final AttackAnimation animation;
    private final AttackAnimation.Phase phase;
    private final int phaseOrder;

    public PonderCombatEvent(LivingEntity attacker, LivingEntity target, AttackAnimation animation, AttackAnimation.Phase phase, int phaseOrder) {
        this.attacker = attacker;
        this.target = target;
        this.animation = animation;
        this.phase = phase;
        this.phaseOrder = phaseOrder;
    }

    public LivingEntity getAttacker() { return attacker; }
    public LivingEntity getTarget() { return target; }
    public AttackAnimation getAnimation() { return animation; }
    public AttackAnimation.Phase getAttackPhase() { return phase; }
    public int getPhaseOrder() { return phaseOrder; }


    /**
     * 当攻击者在 Ponder 中成功命中目标时触发。
     * 这是一个可取消的事件（Cancelable）。如果取消，将不会播放命中音效、粒子，也不会触发 GotHit 事件。
     */
    @Cancelable
    public static class Hit extends PonderCombatEvent {
        public Hit(LivingEntity attacker, LivingEntity target, AttackAnimation animation, AttackAnimation.Phase phase, int phaseOrder) {
            super(attacker, target, animation, phase, phaseOrder);
        }
    }

    /**
     * 当目标在 Ponder 中被命中并且计算生效后触发。
     * 通常在这个事件里给受害者播放硬直动画等
     */
    @Cancelable
    public static class BeHit extends PonderCombatEvent {
        public BeHit(LivingEntity attacker, LivingEntity target, AttackAnimation animation, AttackAnimation.Phase phase, int phaseOrder) {
            super(attacker, target, animation, phase, phaseOrder);
        }
    }
}