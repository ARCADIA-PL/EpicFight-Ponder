package org.arc.efp.compat;

import com.google.common.base.Strings;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipClientServices;
import net.createmod.ponder.Ponder;
import net.createmod.ponder.enums.PonderKeybinds;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.arc.efp.client.ponder.EFPPonderPlugin;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.item.EpicFightItems;

@OnlyIn(Dist.CLIENT)
public class EpicSkillsPonderCompat {

    public static final String HOLD_TO_PONDER = PonderLocalization.UI_PREFIX + "hold_to_ponder";
    private static final LerpedFloat holdKeyProgress = LerpedFloat.linear().startWithValue(0);

    public enum TargetType { SKILL, WEAPON }

    private static String hoveredTargetThisFrame = null;
    private static TargetType hoveredTargetTypeThisFrame = null;

    private static String trackingTarget = null;
    private static TargetType trackingTargetType = null;

    public static void setHoveredSkill(Skill skill) {
        if (skill != null) {
            hoveredTargetThisFrame = skill.toString();
            hoveredTargetTypeThisFrame = TargetType.SKILL;
        }
    }

    public static void setHoveredWeapon(String presetId) {
        if (!Strings.isNullOrEmpty(presetId)) {
            hoveredTargetThisFrame = presetId;
            hoveredTargetTypeThisFrame = TargetType.WEAPON;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft instance = Minecraft.getInstance();
            if (instance.screen == null) return;

            if (hoveredTargetThisFrame == null) {
                holdKeyProgress.setValue(Math.max(0, holdKeyProgress.getValue() - 0.05f));
                if (holdKeyProgress.getValue() == 0 && trackingTarget != null) {
                    trackingTarget = null;
                    trackingTargetType = null;
                }
                return;
            }

            if (!hoveredTargetThisFrame.equals(trackingTarget)) {
                trackingTarget = hoveredTargetThisFrame;
                trackingTargetType = hoveredTargetTypeThisFrame;
                holdKeyProgress.startWithValue(0);
            }

            float value = holdKeyProgress.getValue();

            if (isDown()) {
                if (value >= 1) {
                    ItemStack fakeItem;
                    if (trackingTargetType == TargetType.SKILL) {
                        fakeItem = new ItemStack(EpicFightItems.SKILLBOOK.get());
                        fakeItem.getOrCreateTag().putString("skill", trackingTarget);
                    } else {
                        fakeItem = new ItemStack(Items.WOODEN_SWORD);
                        fakeItem.getOrCreateTag().putString("efp_weapon_preset", trackingTarget);
                    }

                    ScreenOpener.transitionTo(PonderUI.of(fakeItem));

                    holdKeyProgress.startWithValue(0);
                    hoveredTargetThisFrame = null;
                    hoveredTargetTypeThisFrame = null;
                    trackingTarget = null;
                    trackingTargetType = null;
                    return;
                }
                holdKeyProgress.setValue(Math.min(1, value + Math.max(.25f, value) * .25f));
            } else {
                holdKeyProgress.setValue(Math.max(0, value - .05f));
            }

            hoveredTargetThisFrame = null;
            hoveredTargetTypeThisFrame = null;
        }
    }

    public static Component getPonderTooltip() {
        float progress = Math.min(1, holdKeyProgress.getValue(Minecraft.getInstance().getFrameTime()) * 8 / 7f);

        MutableComponent holdW = Ponder.lang()
                .translate(HOLD_TO_PONDER, message().copy()
                        .withStyle(ChatFormatting.GRAY))
                .style(ChatFormatting.DARK_GRAY)
                .component();

        if (progress > 0) {
            Font fontRenderer = Minecraft.getInstance().font;
            float charWidth = fontRenderer.width("|");
            float tipWidth = fontRenderer.width(holdW);

            int total = (int) (tipWidth / charWidth);
            int current = (int) (progress * total);

            String bars = ChatFormatting.DARK_AQUA + Strings.repeat("|", current)
                    + ChatFormatting.DARK_GRAY + Strings.repeat("|", Math.max(0, total - current));
            return Component.literal(bars);
        }

        return holdW;
    }

    public static boolean hasPonderScene(Skill skill) {
        if (skill == null) return false;
        return EFPPonderPlugin.REGISTERED_SKILLS.contains(skill.toString());
    }

    public static boolean hasPonderScene(String presetId) {
        if (Strings.isNullOrEmpty(presetId)) return false;
        return EFPPonderPlugin.REGISTERED_WEAPONS.contains(presetId);
    }

    public static boolean isDown() {
        return CatnipClientServices.CLIENT_HOOKS.isKeyPressed(PonderKeybinds.PONDER.getKeybind());
    }

    public static Component message() {
        return PonderKeybinds.PONDER.getKeybind().getTranslatedKeyMessage();
    }
}