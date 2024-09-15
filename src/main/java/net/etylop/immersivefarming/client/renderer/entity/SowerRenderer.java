package net.etylop.immersivefarming.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.client.renderer.IFModelLayers;
import net.etylop.immersivefarming.client.renderer.entity.model.PlowModel;
import net.etylop.immersivefarming.client.renderer.entity.model.SowerModel;
import net.etylop.immersivefarming.entity.SowerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public final class SowerRenderer extends DrawnRenderer<SowerEntity, SowerModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersiveFarming.MOD_ID, "textures/entity/plow.png");

    public SowerRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new SowerModel(renderManager.bakeLayer(IFModelLayers.SOWER)));
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final SowerEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderContents(final SowerEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        super.renderContents(entity, delta, stack, source, packedLight);
    }
}
