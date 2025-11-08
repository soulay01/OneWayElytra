package com.example.onewayelytra;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerRespawnCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OneWayElytra implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("[OneWayElytra] Mod geladen.");

        // Elytra beim Respawn geben
        PlayerRespawnCallback.EVENT.register((oldPlayer, newPlayer, alive) -> {
            giveElytra(newPlayer);
        });

        // Elytra beim ersten Login geben
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!player.getScoreboardTags().contains("hasOneWayElytra")) {
                    giveElytra(player);
                }
            }
        });

        // Entferne Elytra nach Landung
        ServerTickEvents.END_PLAYER_TICK.register(player -> {
            if (player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                if (player.isOnGround()) {
                    player.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, ItemStack.EMPTY);
                    player.sendMessage(Text.literal("§7Deine Elytra ist verschwunden!"), false);
                    player.getScoreboardTags().remove("hasOneWayElytra");
                }
            }
        });
    }

    private void giveElytra(ServerPlayerEntity player) {
        player.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
        player.addScoreboardTag("hasOneWayElytra");
        player.sendMessage(Text.literal("§eDu hast eine Start-Elytra erhalten! Sie verschwindet, sobald du landest."), false);
    }
}