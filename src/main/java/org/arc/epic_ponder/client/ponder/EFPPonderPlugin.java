package org.arc.epic_ponder.client.ponder;

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
import org.arc.epic_ponder.EpicFightPonder;
import org.arc.epic_ponder.compat.EFNCompat;
import org.arc.epic_ponder.compat.EFNxEFXCompat;
import org.arc.epic_ponder.compat.EFPCompatManager;
import org.arc.epic_ponder.compat.EFXCompat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.world.capabilities.item.ItemKeywordReloadListener;
import yesman.epicfight.world.item.SkillBookItem;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EFPPonderPlugin implements PonderPlugin {

    public static final Set<String> REGISTERED_SKILLS = ConcurrentHashMap.newKeySet();
    public static final Set<String> REGISTERED_WEAPONS = ConcurrentHashMap.newKeySet();

    private static final Map<Item, ResourceLocation> CACHED_WEAPON_TYPES = new ConcurrentHashMap<>();
    private static boolean isCacheBuilt = false;

    public EFPPonderPlugin() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 快捷获取 ItemStack 对应的武器 Preset ID (如 "epicfight:uchigatana")
     */
    @Nullable
    public static String getWeaponPresetId(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        if (item.getTag() != null && item.hasTag() && item.getTag().contains("efp_weapon_preset")) {
            return item.getTag().getString("efp_weapon_preset");
        }

        if (!isCacheBuilt) {
            rebuildWeaponTypeCache();
        }

        ResourceLocation presetId = CACHED_WEAPON_TYPES.get(item.getItem());
        if (presetId != null) {
            return presetId.toString();
        }

        return null;
    }

    /**
     * 获取 Ponder 所需的思索 ID
     */
    @Nullable
    public static ResourceLocation getCustomPonderId(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        if (item.getItem() instanceof SkillBookItem) {
            if (item.getTag() != null && item.hasTag() && item.getTag().contains("skill")) {
                String skillId = SkillBookItem.getContainSkill(item).toString();
                ResourceLocation rawRl = ResourceLocation.parse(skillId);
                return ResourceLocation.fromNamespaceAndPath(rawRl.getNamespace(), "skill_" + rawRl.getPath());
            }
        }

        String weaponPresetId = getWeaponPresetId(item);
        if (weaponPresetId != null) {
            ResourceLocation rawRl = ResourceLocation.parse(weaponPresetId);
            return ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, "weapon_" + rawRl.getPath());
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

        // ==== 注册技能 ====
        registerSkill(helper, "epic_fight_ponder:fallback",
                EFPSKillScenes::showcaseNoSkill);
        registerSkill(helper, "epicfight:guard",
                EFPSKillScenes::showcaseGuardSkill,
                EFPSKillScenes::showcaseGuardSkillBreak);
        registerSkill(helper, "epicfight:parrying",
                EFPSKillScenes::showcaseParrySkill);

        if (EFPCompatManager.isEFXLoaded) {
            registerSkill(helper, "epicfight:technician",
                    EFXCompat::showcaseTechnicianSkill_EFX);
            registerSkill(helper, "epicfight:step",
                    EFXCompat::showcaseStepSkill_EFX);
            registerSkill(helper, "epicfight:roll",
                    EFXCompat::showcaseRollSkill_EFX);
        } else {
            registerSkill(helper, "epicfight:technician",
                    EFPSKillScenes::showcaseTechnicianSkill);
            registerSkill(helper, "epicfight:step",
                    EFPSKillScenes::showcaseStepSkill);
            registerSkill(helper, "epicfight:roll",
                    EFPSKillScenes::showcaseRollSkill);
        }

        if (EFPCompatManager.isEFNLoaded) {
            registerSkill(helper, "efn:efn_parry",
                    EFNCompat::showcaseEFNParrySkill_First,
                    EFNCompat::showcaseEFNParrySkill_Second);
            registerSkill(helper, "efn:efn_dodge",
                    EFNCompat::showcaseRollSkill_EFN);
            registerSkill(helper, "efn:efn_step",
                    EFNCompat::showcaseStepSkill_EFN);
        }

        if (EFPCompatManager.isEFNLoaded && EFPCompatManager.isEFXLoaded) {
            registerSkill(helper, "efn:efn_parry",
                    EFNxEFXCompat::showcaseEFNParrySkill_Third);
        }

        // ==== 注册武器 ====
        if (EFPCompatManager.isEFXLoaded) {
            registerWeapon(helper, "epicfight:sword",
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo,
                    EFXCompat::showcaseSweepingEdge_EFX,
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual,
                    EFXCompat::showcaseDancingEdge_EFX);
            registerWeapon(helper, "epicfight:dagger",
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo,
                    EFXCompat::showcaseEviscerate_EFX,
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
            registerWeapon(helper, "epicfight:dagger", "epicfight_showcase_long",
                    EFXCompat::showcaseBladeRush_EFX);
            registerWeapon(helper, "epicfight:spear",
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo,
                    EFXCompat::showcaseGraspingSpire_EFX,
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand,
                    EFXCompat::showcaseHeartPiercer_EFX);
            registerWeapon(helper, "epicfight:axe",
                    EFPWeaponScenes::showcaseAxeBasicAttackCombo,
                    EFXCompat::showcaseGuillotine_EFX);
            registerWeapon(helper, "epicfight:tachi",
                    EFPWeaponScenes::showcaseTachiBasicAttackCombo,
                    EFXCompat::showcaseRushingTempo_EFX);
            registerWeapon(helper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo);
            registerWeapon(helper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFXCompat::showcaseUchigatanaBattojutsu_UnSheath_EFX);
            registerWeapon(helper, "epicfight:uchigatana",
                    EFXCompat::showcaseUchigatanaPassive_EFX,
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
            registerWeapon(helper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFXCompat::showcaseUchigatanaBattojutsu_Sheath_EFX);
            registerWeapon(helper, "epicfight:longsword",
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo,
                    EFXCompat::showcaseLongSwordBasicAttackCombo_Ochs_EFX,
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand,
                    EFXCompat::showcaseSharpStab_EFX);
            registerWeapon(helper, "epicfight:greatsword",
                    EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo,
                    EFXCompat::showcaseSteelWhirlWind_EFX);
        } else {
            registerWeapon(helper, "epicfight:sword",
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo,
                    EFPWeaponScenes::showcaseSweepingEdge,
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual,
                    EFPWeaponScenes::showcaseDancingEdge);
            registerWeapon(helper, "epicfight:dagger",
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo,
                    EFPWeaponScenes::showcaseEviscerate,
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
            registerWeapon(helper, "epicfight:dagger", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseBladeRush);
            registerWeapon(helper, "epicfight:spear",
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo,
                    EFPWeaponScenes::showcaseGraspingSpire,
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand,
                    EFPWeaponScenes::showcaseHeartPiercer);
            registerWeapon(helper, "epicfight:axe",
                    EFPWeaponScenes::showcaseAxeBasicAttackCombo,
                    EFPWeaponScenes::showcaseGuillotine);
            registerWeapon(helper, "epicfight:tachi",
                    EFPWeaponScenes::showcaseTachiBasicAttackCombo,
                    EFPWeaponScenes::showcaseRushingTempo);
            registerWeapon(helper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo);
            registerWeapon(helper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseUchigatanaBattojutsu_UnSheath);
            registerWeapon(helper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaPassive,
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
            registerWeapon(helper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseUchigatanaBattojutsu_Sheath);
            registerWeapon(helper, "epicfight:longsword",
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo,
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_Ochs,
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand,
                    EFPWeaponScenes::showcaseSharpStab);
            registerWeapon(helper, "epicfight:greatsword",
                    EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo,
                    EFPWeaponScenes::showcaseSteelWhirlWind);
        }
    }

    @SafeVarargs
    public static void registerSkill(PonderSceneRegistrationHelper<ResourceLocation> helper, String skillId, PonderSceneMethod... scenes) {
        registerSkill(helper, skillId, EpicFightPonder.MOD_ID + ":epicfight_showcase", scenes);
    }

    @SafeVarargs
    public static void registerSkill(PonderSceneRegistrationHelper<ResourceLocation> helper, String skillId, String structure, PonderSceneMethod... scenes) {
        REGISTERED_SKILLS.add(skillId);

        var component = helper.withKeyFunction((String s) -> {
            ResourceLocation rl = ResourceLocation.parse(s);
            return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "skill_" + rl.getPath());
        }).forComponents(skillId);

        ResourceLocation structureLocation = structure.contains(":") ? ResourceLocation.parse(structure) : ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, structure);

        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard(structureLocation, scene::build);
        }
    }

    @SafeVarargs
    public static void registerWeapon(PonderSceneRegistrationHelper<ResourceLocation> helper, String presetId, PonderSceneMethod... scenes) {
        registerWeapon(helper, presetId, EpicFightPonder.MOD_ID + ":epicfight_showcase", scenes);
    }

    @SafeVarargs
    public static void registerWeapon(PonderSceneRegistrationHelper<ResourceLocation> helper, String presetId, String structure, PonderSceneMethod... scenes) {
        REGISTERED_WEAPONS.add(presetId);

        var component = helper.withKeyFunction((String s) -> {
            ResourceLocation rl = ResourceLocation.parse(s);
            return ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, "weapon_" + rl.getPath());
        }).forComponents(presetId);

        ResourceLocation structureLocation = structure.contains(":") ? ResourceLocation.parse(structure) : ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, structure);

        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard(structureLocation, scene::build);
        }
    }

    public interface PonderSceneMethod {
        void build(SceneBuilder scene, SceneBuildingUtil util);
    }
}