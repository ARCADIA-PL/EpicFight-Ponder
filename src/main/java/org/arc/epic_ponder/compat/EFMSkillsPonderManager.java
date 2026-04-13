package org.arc.epic_ponder.compat;

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
import org.arc.epic_ponder.client.ponder.EFPPonderPlugin;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

@OnlyIn(Dist.CLIENT)
public class EFMSkillsPonderManager {

    public static final String HOLD_TO_PONDER = PonderLocalization.UI_PREFIX + "hold_to_ponder";
    private static final LerpedFloat holdKeyProgress = LerpedFloat.linear().startWithValue(0);

    public enum TargetType { SKILL, WEAPON }

    private static String hoveredTargetThisFrame = null;
    private static TargetType hoveredTargetTypeThisFrame = null;
    private static ItemStack hoveredItemStackThisFrame = ItemStack.EMPTY;

    private static String trackingTarget = null;
    private static TargetType trackingTargetType = null;
    private static ItemStack trackingItemStack = ItemStack.EMPTY;

    public static void setHoveredSkill(Skill skill) {
        if (skill != null) {
            hoveredTargetThisFrame = skill.toString();
            hoveredTargetTypeThisFrame = TargetType.SKILL;
            hoveredItemStackThisFrame = ItemStack.EMPTY;
        }
    }

    public static void setHoveredWeapon(String presetId) {
        if (!Strings.isNullOrEmpty(presetId)) {
            hoveredTargetThisFrame = presetId;
            hoveredTargetTypeThisFrame = TargetType.WEAPON;
            hoveredItemStackThisFrame = ItemStack.EMPTY;
        }
    }

    /**
     * 传入 ItemStack
     */
    public static void setHoveredItem(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            String presetId = EFPPonderPlugin.getWeaponPresetId(stack);
            if (!Strings.isNullOrEmpty(presetId)) {
                hoveredTargetThisFrame = presetId;
                hoveredTargetTypeThisFrame = TargetType.WEAPON;
                hoveredItemStackThisFrame = stack;
            } else if (stack.getItem() instanceof SkillBookItem && stack.getTag() != null && stack.getTag().contains("skill")) {
                hoveredTargetThisFrame = SkillBookItem.getContainSkill(stack).toString();
                hoveredTargetTypeThisFrame = TargetType.SKILL;
                hoveredItemStackThisFrame = stack;
            }
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
                    trackingItemStack = ItemStack.EMPTY;
                }
                return;
            }

            if (!hoveredTargetThisFrame.equals(trackingTarget) || !ItemStack.matches(hoveredItemStackThisFrame, trackingItemStack)) {
                trackingTarget = hoveredTargetThisFrame;
                trackingTargetType = hoveredTargetTypeThisFrame;
                trackingItemStack = hoveredItemStackThisFrame.copy();
                holdKeyProgress.startWithValue(0);
            }

            float value = holdKeyProgress.getValue();

            if (isDown()) {
                if (value >= 1) {
                    ItemStack uiItem;

                    if (!trackingItemStack.isEmpty()) {
                        uiItem = trackingItemStack.copy();
                    } else if (trackingTargetType == TargetType.SKILL) {
                        uiItem = new ItemStack(EpicFightItems.SKILLBOOK.get());
                        uiItem.getOrCreateTag().putString("skill", trackingTarget);
                    } else {
                        uiItem = new ItemStack(Items.WOODEN_SWORD);
                        uiItem.getOrCreateTag().putString("efp_weapon_preset", trackingTarget);
                    }

                    ScreenOpener.transitionTo(PonderUI.of(uiItem));

                    holdKeyProgress.startWithValue(0);
                    hoveredTargetThisFrame = null;
                    hoveredTargetTypeThisFrame = null;
                    hoveredItemStackThisFrame = ItemStack.EMPTY;
                    trackingTarget = null;
                    trackingTargetType = null;
                    trackingItemStack = ItemStack.EMPTY;
                    return;
                }
                holdKeyProgress.setValue(Math.min(1, value + Math.max(.25f, value) * .25f));
            } else {
                holdKeyProgress.setValue(Math.max(0, value - .05f));
            }

            hoveredTargetThisFrame = null;
            hoveredTargetTypeThisFrame = null;
            hoveredItemStackThisFrame = ItemStack.EMPTY;
        }
    }

    public static Component getPonderTooltip() {
        return getPonderTooltip(ChatFormatting.DARK_AQUA, ChatFormatting.DARK_GRAY);
    }

    /**
     * @param activeColor 进度条加载中（已完成）的颜色
     * @param inactiveColor 进度条未加载（剩余部分）的颜色
     */
    public static Component getPonderTooltip(ChatFormatting activeColor, ChatFormatting inactiveColor) {
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

            String bars = activeColor.toString() + Strings.repeat("|", current)
                    + inactiveColor.toString() + Strings.repeat("|", Math.max(0, total - current));
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

    /**
     * 判断某个具体的 ItemStack 是否能打开思索界面
     */
    public static boolean hasPonderScene(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        if (stack.getItem() instanceof SkillBookItem) {
            if (stack.getTag() != null && stack.getTag().contains("skill")) {
                return hasPonderScene(SkillBookItem.getContainSkill(stack));
            }
            return false;
        }

        String presetId = EFPPonderPlugin.getWeaponPresetId(stack);
        return hasPonderScene(presetId);
    }

    public static boolean isDown() {
        return PonderKeybinds.PONDER.isDown();
    }

    public static Component message() {
        return PonderKeybinds.PONDER.message();
    }
}