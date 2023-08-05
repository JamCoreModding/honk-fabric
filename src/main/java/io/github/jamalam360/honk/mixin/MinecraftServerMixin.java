package io.github.jamalam360.honk.mixin;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.registry.HonkS2CNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.quiltmc.loader.api.minecraft.DedicatedServerOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@DedicatedServerOnly
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	public abstract PlayerManager getPlayerManager();

	@Inject(at = @At("RETURN"), method = "reloadResources")
	private void reloadResources(Collection<String> dataPacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		CompletableFuture<Void> future = cir.getReturnValue();
		if (future != null) {
			future.thenRun(() -> {
				HonkInit.LOGGER.info("Sending " + HonkType.ENTRIES.size() + " honk types to connected players");

				for (ServerPlayerEntity player : this.getPlayerManager().getPlayerList()) {
					HonkS2CNetwork.HONK_TYPES.send(player, HonkS2CNetwork::writeHonkTypePacket);
				}
			});
		}
	}
}
