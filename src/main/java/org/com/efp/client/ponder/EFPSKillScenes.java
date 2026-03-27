package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import org.com.efp.api.event.PonderCombatEvent;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.entity.DummyEntityPatch;
import org.com.efp.mixin.epicfight.WeaponCapabilityAccessor;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.item.EpicFightItems;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EFPSKillScenes {

    public static void showcaseNoSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseNoSkill(baseScene, util, 11, "nothing");
    }

    public static void showcaseGuardSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_guard", "epic_fight_ponder.ponder.skill_guard.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 1.9;

        ItemStack victimWeapon = new ItemStack(EpicFightItems.UCHIGATANA.get());
        ItemStack attackerWeapon = new ItemStack(EpicFightItems.DIAMOND_TACHI.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);

        builder.idle(20);

        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.skill_guard.text_1", 40, (int)centerX, 2, (int)centerZ);
        world.playAnimation(victim, Animations.UCHIGATANA_GUARD, 0.0F);
        builder.idle(10);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions;
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(attackerWeapon);

        if (cap instanceof WeaponCapability weaponCap) {
            Map<Style, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> motionsMap = ((WeaponCapabilityAccessor) weaponCap).getAutoAttackMotions();
            comboMotions = motionsMap.getOrDefault(CapabilityItem.Styles.TWO_HAND, motionsMap.get(CapabilityItem.Styles.COMMON));
        } else {
            comboMotions = CapabilityItem.getBasicAutoAttackMotion();
        }

        int attackCount = comboMotions.size() - 2;

        for (int i = 0; i < attackCount; i++) {
            AnimationManager.AnimationAccessor<? extends AttackAnimation> currentAttack = comboMotions.get(i);

            final boolean isLastAttack = (i == attackCount - 1);

            world.playAnimation(attacker, currentAttack, 0.0F, hitEvent -> {

                hitEvent.setResult(PonderCombatEvent.AttackResult.FAIL_BLOCKED);

                EFPSceneUtils.playSoundClientSide(EpicFightSounds.CLASH.get(), 1.0F, 1.0F);

                EFPSceneUtils.spawnEfmHitParticleClientSide(
                        hitEvent.getTarget().level(),
                        EpicFightParticles.HIT_BLUNT.get(),
                        hitEvent.getAttacker(),
                        hitEvent.getTarget(),
                        HitParticleType.FRONT_OF_EYES,
                        HitParticleType.ZERO
                );

                LivingEntityPatch<?> rawPatch = EpicFightCapabilities.getEntityPatch(hitEvent.getTarget(), LivingEntityPatch.class);

                if (rawPatch instanceof DummyEntityPatch<?> victimPatch && victimPatch.getClientAnimator() != null) {

                    victimPatch.getClientAnimator().playAnimation(Animations.UCHIGATANA_GUARD_HIT, 0.0F);

                    if (!isLastAttack) {
                        victimPatch.scheduleDelayedTask((int) (Objects.requireNonNull(victimPatch.getClientAnimator().getPlayerFor(Animations.UCHIGATANA_GUARD_HIT)).getAnimation().get().getTotalTime() / 20), () -> {
                            victimPatch.getClientAnimator().playAnimation(Animations.UCHIGATANA_GUARD, 0.3F);
                        });
                    }
                }

            }, beHitEvent -> {
            });

            if (!isLastAttack) {
                world.waitForCanBasicAttack(attacker);
            } else {
                world.waitForInaction(attacker);
            }
        }

        builder.idle(30);
        builder.markAsFinished();
    }
}