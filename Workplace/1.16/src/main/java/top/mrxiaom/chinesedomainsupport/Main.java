package top.mrxiaom.chinesedomainsupport;

import java.lang.reflect.Constructor;

import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraftforge.fml.common.Mod;

@Mod("chinesedomainsupport")
public class Main {
	// ±ª ∆» ∑¥ …‰
	public static ServerAddress newServerAddress(String ipAddress, int port) {
		try {
			Class<?> clazz = net.minecraft.client.multiplayer.ServerAddress.class;
			Constructor<?> c = clazz.getDeclaredConstructor(String.class, int.class);
			c.setAccessible(true);
			return (ServerAddress) c.newInstance(ipAddress, port);
		}catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
}
