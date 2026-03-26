package org.com.efp.api.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.com.efp.entity.DummyPlayerEntity;
import org.com.efp.entity.DummyPlayerEntityPatch;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class EpicFightSceneBuilder extends PonderSceneBuilder {

    public static final String PLAY_SPEED = "play_speed_in_ponder_level";

    private final EpicFightEffectInstructions effects;
    private final EpicFightWorldInstructions world;

    public EpicFightSceneBuilder(SceneBuilder baseSceneBuilder) {
        this(baseSceneBuilder.getScene());
    }

    private EpicFightSceneBuilder(PonderScene ponderScene) {
        super(ponderScene);
        effects = new EpicFightEffectInstructions();
        world = new EpicFightWorldInstructions();
    }

    @Override
    public @NotNull EpicFightSceneBuilder.EpicFightEffectInstructions effects() {
        return effects;
    }

    @Override
    public @NotNull EpicFightSceneBuilder.EpicFightWorldInstructions world() {
        return world;
    }

    public class EpicFightEffectInstructions extends PonderEffectInstructions {
    }

    public class EpicFightWorldInstructions extends PonderWorldInstructions {

        public void setPosition(ElementLink<EntityElement> link, double x, double y, double z) {
            world().modifyEntity(link, entity -> {
                if (entity instanceof DummyPlayerEntity dummyPlayerEntity) {
                    dummyPlayerEntity.setPos(x, y, z);
                }
            });
        }

        public void updateLivingMotions(ElementLink<EntityElement> link) {
            world().modifyEntity(link, entity -> {
                if (entity instanceof DummyPlayerEntity dummyPlayerEntity) {
                    EpicFightCapabilities.getUnparameterizedEntityPatch(dummyPlayerEntity, LivingEntityPatch.class).ifPresent(livingEntityPatch -> {
                        if (livingEntityPatch instanceof DummyPlayerEntityPatch<?> dummyPlayerEntityPatch) {
                            dummyPlayerEntityPatch.updateLivingMotionsForPonder();
                        }
                    });
                }
            });
        }

        public void simulateJump(ElementLink<EntityElement> link) {
            world().modifyEntity(link, entity -> {
                if (entity instanceof DummyPlayerEntity dummyPlayerEntity) {
                    dummyPlayerEntity.jumpFromGround(1.5F);
                }
            });
        }

        public void simulateSpring(ElementLink<EntityElement> link, float blocks, int duration) {
            world().modifyEntity(link, entity -> {
                if (entity instanceof DummyPlayerEntity dummyPlayerEntity) {
                    dummyPlayerEntity.sprintForward(blocks, duration);
                }
            });
        }

        public void waitForState(ElementLink<EntityElement> link, Predicate<EntityState> statePredicate) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement element = scene.resolve(link);
                    if (element != null) {
                        element.ifPresent(entity -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, LivingEntityPatch.class).ifPresent(patch -> {
                                    if (statePredicate.test(patch.getEntityState())) {
                                        complete = true;
                                    }
                                });
                            }
                        });
                    } else {
                        complete = true;
                    }
                }
            });
        }

        /**
         * 实体状态
         */
        public void waitForComboInput(ElementLink<EntityElement> link) {
            waitForState(link, state -> state.canBasicAttack() || !state.inaction());
        }

        public void waitForInaction(ElementLink<EntityElement> link) {
            waitForState(link, state -> !state.inaction());
        }

        public void waitForCanBasicAttack(ElementLink<EntityElement> link) {
            waitForState(link, EntityState::canBasicAttack);
        }

        public void waitForCanUseSkill(ElementLink<EntityElement> link) {
            waitForState(link, EntityState::canUseSkill);
        }

        /**
         * 动画播放的时间进度
         */
        public void waitForAnimationProgress(ElementLink<EntityElement> link, float targetPercentage) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement element = scene.resolve(link);
                    if (element != null) {
                        element.ifPresent(entity -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, LivingEntityPatch.class).ifPresent(patch -> {
                                    AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
                                    DynamicAnimation currentAnim = null;
                                    if (player != null) {
                                        currentAnim = player.getAnimation().get();
                                    }

                                    if (currentAnim == null) {
                                        complete = true;
                                        return;
                                    }

                                    float totalTime = currentAnim.getTotalTime();
                                    if (totalTime > 0) {
                                        float progress = player.getElapsedTime() / totalTime;
                                        if (progress >= targetPercentage) {
                                            complete = true;
                                        }
                                    } else {
                                        complete = true;
                                    }
                                });
                            }
                        });
                    } else {
                        complete = true;
                    }
                }
            });
        }

        /**
         * 动画播放的时间
         */
        public void waitForAnimationTime(ElementLink<EntityElement> link, float targetTimeSeconds) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement element = scene.resolve(link);
                    if (element != null) {
                        element.ifPresent(entity -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, LivingEntityPatch.class).ifPresent(patch -> {
                                    AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
                                    if (player != null && (player.getAnimation() == null || player.getElapsedTime() >= targetTimeSeconds)) {
                                        complete = true;
                                    }
                                });
                            }
                        });
                    } else {
                        complete = true;
                    }
                }
            });
        }

        /**
         * 等待攻击命中
         */
        public void waitForAttackHit(ElementLink<EntityElement> attackerLink, ElementLink<EntityElement> victimLink) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement attackerElem = scene.resolve(attackerLink);
                    EntityElement victimElem = scene.resolve(victimLink);

                    if (attackerElem == null || victimElem == null) {
                        complete = true;
                        return;
                    }

                    final Entity[] extracted = new Entity[2];
                    attackerElem.ifPresent(e -> extracted[0] = e);
                    victimElem.ifPresent(e -> extracted[1] = e);

                    if (!(extracted[0] instanceof LivingEntity attackerEntity) || !(extracted[1] instanceof LivingEntity victimEntity)) {
                        return;
                    }

                    LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getUnparameterizedEntityPatch(attackerEntity, LivingEntityPatch.class).orElse(null);
                    LivingEntityPatch<?> victimPatch = EpicFightCapabilities.getUnparameterizedEntityPatch(victimEntity, LivingEntityPatch.class).orElse(null);

                    if (attackerPatch == null || victimPatch == null) {
                        return;
                    }

                    AnimationPlayer player = attackerPatch.getAnimator().getPlayerFor(null);

                    if (player == null || player.isEmpty() || !(player.getAnimation() instanceof AttackAnimation attackAnim)) {
                        return;
                    }

                    float elapsedTime = player.getElapsedTime();
                    float prevElapsedTime = player.getPrevElapsedTime();

                    if (elapsedTime >= attackAnim.getTotalTime() - 0.05f) {
                        complete = true;
                        return;
                    }

                    EntityState state = attackAnim.getState(attackerPatch, elapsedTime);

                    if (state.attacking()) {
                        AttackAnimation.Phase phase = attackAnim.getPhaseByTime(elapsedTime);

                        if (phase != null) {
                            List<Entity> hits = phase.getCollidingEntities(attackerPatch, attackAnim, prevElapsedTime, elapsedTime, 1.0F);

                            if (hits.contains(victimEntity)) {
                                complete = true;

                                Vec3 hitPos = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0);

                                victimEntity.level().playSound(
                                        null, hitPos.x, hitPos.y, hitPos.z,
                                        attackerPatch.getWeaponHitSound(phase.hand), SoundSource.PLAYERS, 1.0F, 1.0F
                                );

                                try {
                                    yesman.epicfight.particle.HitParticleType particleType = attackerPatch.getWeaponHitParticle(phase.hand);
                                    if (particleType != null) {
                                        victimEntity.level().addParticle(
                                                particleType,
                                                hitPos.x, hitPos.y, hitPos.z, 0, 0, 0
                                        );
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            });
        }

        public void playGroundSlam(Vec3 pos, float radius) {
            addInstruction(PonderInstruction.simple(ponderScene -> {
                LevelUtil.circleSlamFracture(null, ponderScene.getWorld(), pos, radius);
            }));
        }

        public void playGroundSlam(Vec3 pos, float radius, boolean noSound, boolean noParticle) {
            addInstruction(PonderInstruction.simple(ponderScene -> {
                LevelUtil.circleSlamFracture(null, ponderScene.getWorld(), pos, radius, noSound, noParticle);
            }));
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void addEntityAfterImageParticle(Class<E> entityClass) {
            modifyEntities(entityClass, entity -> {
                entity.level().addParticle(EpicFightParticles.WHITE_AFTERIMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void addEntityAfterImageParticle(Class<E> entityClass, Vec3 pos) {
            modifyEntities(entityClass, entity -> {
                entity.level().addParticle(EpicFightParticles.WHITE_AFTERIMAGE.get(), pos.x(), pos.y(), pos.z(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatches(Class<E> entityClass, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntities(entityClass, livingEntity -> {
                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, type).ifPresent(entityPatchCallBack);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatchesInside(Class<E> entityClass, Selection area, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntitiesInside(entityClass, area, livingEntity -> {
                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, type).ifPresent(entityPatchCallBack);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatch(ElementLink<EntityElement> link, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntity(link, e -> {
                if (e instanceof LivingEntity livingEntity) {
                    EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, type).ifPresent(entityPatchCallBack);
                }
            });
        }

        //调速
        public <E extends LivingEntity> void modifyEntitiesPlaySpeed(Class<E> entityClass, float playSpeed) {
            super.modifyEntities(entityClass, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        public <E extends LivingEntity> void modifyEntitiesInsidePlaySpeed(Class<E> entityClass, Selection area, float playSpeed) {
            super.modifyEntitiesInside(entityClass, area, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        public void modifyEntityPlaySpeed(ElementLink<EntityElement> link, float playSpeed) {
            super.modifyEntity(link, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }


        //播动画
        public <E extends LivingEntity, A extends StaticAnimation> void playEntitiesAnimation(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, livingEntityPatch -> {
                livingEntityPatch.playAnimation(animationAccessor, transitionTimeModifier);
            });
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playEntitiesAnimation(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, livingEntityPatch -> {
                livingEntityPatch.playAnimation(animationAccessor, transitionTimeModifier);
            });
        }

        public <A extends StaticAnimation> void playEntityAnimation(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, livingEntityPatch -> {
                livingEntityPatch.playAnimation(animationAccessor, transitionTimeModifier);
            });
        }

        public <E extends LivingEntity, A extends StaticAnimation> void reserveAnimation(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.reserveAnimation(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void reserveAnimation(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.reserveAnimation(animationAccessor));
        }

        public <A extends StaticAnimation> void reserveAnimation(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.reserveAnimation(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimationInstantly(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.playAnimationInstantly(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimationInstantly(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.playAnimationInstantly(animationAccessor));
        }

        public <A extends StaticAnimation> void playAnimationInstantly(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.playAnimationInstantly(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimation(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.playAnimation(animationAccessor, transitionTimeModifier));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimation(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.playAnimation(animationAccessor, transitionTimeModifier));
        }

        public <A extends StaticAnimation> void playAnimation(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.playAnimation(animationAccessor, transitionTimeModifier));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void stopPlaying(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.stopPlaying(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void stopPlaying(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.stopPlaying(animationAccessor));
        }

        public <A extends StaticAnimation> void stopPlaying(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.stopPlaying(animationAccessor));
        }
    }
}