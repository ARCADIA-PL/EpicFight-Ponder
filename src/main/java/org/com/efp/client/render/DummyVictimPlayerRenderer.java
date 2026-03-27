package org.com.efp.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.com.efp.entity.DummyVictimPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class DummyVictimPlayerRenderer extends HumanoidMobRenderer<DummyVictimPlayerEntity, PlayerModel<DummyVictimPlayerEntity>> {

    public DummyVictimPlayerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DummyVictimPlayerEntity pEntity) {
        if (Minecraft.getInstance().player != null) {
            return Minecraft.getInstance().player.getSkinTextureLocation();
        }
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/steve.png");
    }
}