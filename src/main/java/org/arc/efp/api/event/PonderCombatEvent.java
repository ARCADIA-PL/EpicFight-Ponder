package org.arc.efp.api.event;

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

    public LivingEntity getAttacker() {
        return attacker;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public AttackAnimation getAnimation() {
        return animation;
    }

    public AttackAnimation.Phase getAttackPhase() {
        return phase;
    }

    public int getPhaseOrder() {
        return phaseOrder;
    }

    /**
     * 动作引擎的攻击结算结果。
     * 成功则触发后续的 BeHit 受击表现；失败（闪避/格挡等）则截断表现。
     */
    public enum AttackResult {
        SUCCESS(true),   // 攻击有效
        FAIL_DODGED(false), // 失败：目标处于无敌帧/闪避状态
        FAIL_BLOCKED(false),// 失败：目标成功格挡
        FAIL_PARRIED(false),// 失败：目标完美弹反
        FAIL_CUSTOM(false); // 失败：自定义原因

        private final boolean isSuccess;

        AttackResult(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    /**
     * 当攻击者在 Ponder 中成功发生物理触碰（Hitbox 接触 Hurtbox）时触发。
     * 可以被直接 Cancel（物理无效化），也可以通过 setResult() 修改结算结果（例如目标成功防御）。
     */
    @Cancelable
    public static class Hit extends PonderCombatEvent {
        private AttackResult result = AttackResult.SUCCESS;

        public Hit(LivingEntity attacker, LivingEntity target, AttackAnimation animation, AttackAnimation.Phase phase, int phaseOrder) {
            super(attacker, target, animation, phase, phaseOrder);
        }

        public AttackResult getAttackResult() {
            return result;
        }

        public void setResult(AttackResult result) {
            this.result = result;
        }
    }

    /**
     * 当攻击被判定为 SUCCESS 并且计算生效后触发。
     * 通常在这个事件里给受害者播放受击硬直动画、处决对位等。
     */
    public static class BeHit extends PonderCombatEvent {
        public BeHit(LivingEntity attacker, LivingEntity target, AttackAnimation animation, AttackAnimation.Phase phase, int phaseOrder) {
            super(attacker, target, animation, phase, phaseOrder);
        }
    }
}