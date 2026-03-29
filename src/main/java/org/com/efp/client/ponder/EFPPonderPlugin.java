package org.com.efp.client.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.com.efp.EpicFightPonder;
import org.com.efp.compat.EFPCompatManager;
import org.com.efp.compat.EFXCompat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.world.capabilities.item.ItemKeywordReloadListener;
import yesman.epicfight.world.item.SkillBookItem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EFPPonderPlugin implements PonderPlugin {

    private static final Map<Item, ResourceLocation> CACHED_WEAPON_TYPES = new ConcurrentHashMap<>();
    private static boolean isCacheBuilt = false;

    public EFPPonderPlugin() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nullable
    public static ResourceLocation getCustomPonderId(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        if (item.getItem() instanceof SkillBookItem) {
            if (item.getTag() != null && item.hasTag() && item.getTag().contains("skill")) {
                String skillId = item.getTag().getString("skill");
                ResourceLocation rawRl = ResourceLocation.parse(skillId);
                return ResourceLocation.fromNamespaceAndPath(rawRl.getNamespace(), "skill_" + rawRl.getPath());
            }
        }

        if (!isCacheBuilt) {
            rebuildWeaponTypeCache();
        }

        ResourceLocation presetId = CACHED_WEAPON_TYPES.get(item.getItem());
        if (presetId != null) {
            return ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, "weapon_" + presetId.getPath());
        }

        return null;
    }

    private static synchronized void rebuildWeaponTypeCache() {
        CACHED_WEAPON_TYPES.clear();

        ItemCapabilityReloadListener.getWeaponDataStream().forEach(tag -> {
            if (tag.contains("type")) {
                Item item = Item.byId(tag.getInt("id"));
                ResourceLocation presetId = ResourceLocation.parse(tag.getString("type"));
                CACHED_WEAPON_TYPES.put(item, presetId);
            }
        });

        Map<ResourceLocation, ItemKeywordReloadListener.ItemRegex> regexes = ItemKeywordReloadListener.getRegexes();

        for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
            Item item = entry.getValue();

            if (CACHED_WEAPON_TYPES.containsKey(item) || item instanceof BlockItem) {
                continue;
            }

            ResourceLocation registryName = entry.getKey().location();
            boolean matched = false;

            for (Map.Entry<ResourceLocation, ItemKeywordReloadListener.ItemRegex> regexEntry : regexes.entrySet()) {
                if (regexEntry.getValue().matchesAny(registryName.toString())) {
                    CACHED_WEAPON_TYPES.put(item, regexEntry.getKey());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                Class<?> clazz = item.getClass();
                while (clazz != null) {
                    if (SwordItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:sword"));
                        break;
                    } else if (AxeItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:axe"));
                        break;
                    } else if (PickaxeItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:pickaxe"));
                        break;
                    } else if (ShovelItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:shovel"));
                        break;
                    } else if (HoeItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:hoe"));
                        break;
                    } else if (BowItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:bow"));
                        break;
                    } else if (CrossbowItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:crossbow"));
                        break;
                    } else if (ShieldItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:shield"));
                        break;
                    }
                    clazz = clazz.getSuperclass();
                }
            }
        }

        isCacheBuilt = true;
    }

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        isCacheBuilt = false;
    }

    @Override
    public @NotNull String getModId() {
        return EpicFightPonder.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {

        PonderSceneRegistrationHelper<String> skillHelper = helper.withKeyFunction(
                skillString -> {
                    ResourceLocation rl = ResourceLocation.parse(skillString);
                    return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "skill_" + rl.getPath());
                }
        );

        PonderSceneRegistrationHelper<String> weaponHelper = helper.withKeyFunction(
                presetId -> {
                    ResourceLocation rl = ResourceLocation.parse(presetId);
                    return ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, "weapon_" + rl.getPath());
                }
        );

        // ==== 注册技能 ====
        registerPreset(skillHelper, "epic_fight_ponder:fallback",
                EFPSKillScenes::showcaseNoSkill);
        registerPreset(skillHelper, "epicfight:guard",
                EFPSKillScenes::showcaseGuardSkill,
                EFPSKillScenes::showcaseGuardSkillBreak);
        registerPreset(skillHelper, "epicfight:parrying",
                EFPSKillScenes::showcaseParrySkill);
        registerPreset(skillHelper, "epicfight:step",
                EFPSKillScenes::showcaseStepSkill);
        registerPreset(skillHelper, "epicfight:roll",
                EFPSKillScenes::showcaseRollSkill);

        if (EFPCompatManager.isEFXLoaded) {
            registerPreset(skillHelper, "epicfight:technician",
                    EFXCompat::showcaseTechnicianSkill_EFX);
        } else {
            registerPreset(skillHelper, "epicfight:technician",
                    EFPSKillScenes::showcaseTechnicianSkill);
        }

        // ==== 根据WeaponCapPreset的ID注册武器 ====
        if (EFPCompatManager.isEFXLoaded) {
            registerPreset(weaponHelper, "epicfight:tachi",
                    EFPWeaponScenes::showcaseTachiBasicAttackCombo,
                    EFXCompat::showcaseRushingTempo_EFX);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFXCompat::showcaseUchigatanaBattojutsu_UnSheath_EFX);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFXCompat::showcaseUchigatanaPassive_EFX,
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFXCompat::showcaseUchigatanaBattojutsu_Sheath_EFX);
        } else {
            registerPreset(weaponHelper, "epicfight:tachi",
                    EFPWeaponScenes::showcaseTachiBasicAttackCombo,
                    EFPWeaponScenes::showcaseRushingTempo);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseUchigatanaBattojutsu_UnSheath);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaPassive,
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseUchigatanaBattojutsu_Sheath);
        }

        registerPreset(weaponHelper, "epicfight:greatsword",
                EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo);
        registerPreset(weaponHelper, "epicfight:longsword",
                EFPWeaponScenes::showcaseLongSwordBasicAttackCombo,
                EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_Ochs,
                EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand);
        registerPreset(weaponHelper, "epicfight:dagger",
                EFPWeaponScenes::showcaseDaggerBasicAttackCombo,
                EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
        registerPreset(weaponHelper, "epicfight:spear",
                EFPWeaponScenes::showcaseSpearBasicAttackCombo,
                EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand);
        registerPreset(weaponHelper, "epicfight:sword",
                EFPWeaponScenes::showcaseSwordBasicAttackCombo,
                EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual);
    }

    private void registerPreset(PonderSceneRegistrationHelper<String> helper, String presetOrSkillId, PonderSceneMethod... scenes) {
        var component = helper.forComponents(presetOrSkillId);
        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard("epicfight_showcase", scene::build);
        }
    }

    private void registerPreset(PonderSceneRegistrationHelper<String> helper, String presetOrSkillId, String structure, PonderSceneMethod... scenes) {
        var component = helper.forComponents(presetOrSkillId);
        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard(structure, scene::build);
        }
    }

    public interface PonderSceneMethod {
        void build(SceneBuilder scene, SceneBuildingUtil util);
    }
}