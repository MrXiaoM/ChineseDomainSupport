package top.mrxiaom.chinesedomainsupport.mixin;

import java.util.Hashtable;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.client.multiplayer.ServerAddress;
import top.mrxiaom.chinesedomainsupport.Main;
import top.mrxiaom.chinesedomainsupport.Punycode;

@Mixin(net.minecraft.client.multiplayer.ServerAddress.class)
public class MixinServerAddress {
	/*
	 * Added Punycode support
	 * @author MrXiaoM
	 * */
	@Overwrite
	public static ServerAddress fromString(String addrString) {
		if (addrString == null) {
			return null;
		} else {
			String[] astring = addrString.split(":");
			if (addrString.startsWith("[")) {
				int i = addrString.indexOf("]");
				if (i > 0) {
					String s = addrString.substring(1, i);
					String s1 = addrString.substring(i + 1).trim();
					if (s1.startsWith(":") && !s1.isEmpty()) {
						s1 = s1.substring(1);
						astring = new String[] { s, s1 };
					} else {
						astring = new String[] { s };
					}
				}
			}

			if (astring.length > 2) {
				astring = new String[] { addrString };
			}

			String s2 = Punycode.encodeURL(astring[0]);
			int j = astring.length > 1 ? getInt(astring[1], 25565) : 25565;
			if (j == 25565) {
				String[] astring1 = getServerAddress(s2);
				s2 = astring1[0];
				j = getInt(astring1[1], 25565);
			}
			
			return Main.newServerAddress(s2, j);
		}
	}

	/**
	 * Returns a server's address and port for the specified hostname, looking up
	 * the SRV record if possible
	 */
	private static String[] getServerAddress(String p_78863_0_) {
		try {
			Class.forName("com.sun.jndi.dns.DnsContextFactory");
			Hashtable<String, String> hashtable = new Hashtable<>();
			hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hashtable.put("java.naming.provider.url", "dns:");
			hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
			DirContext dircontext = new InitialDirContext(hashtable);
			Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + p_78863_0_, new String[] { "SRV" });
			String[] astring = attributes.get("srv").get().toString().split(" ", 4);
			return new String[] { astring[3], astring[2] };
		} catch (Throwable var6) {
			return new String[] { p_78863_0_, Integer.toString(25565) };
		}
	}

	private static int getInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value.trim());
		} catch (Exception var3) {
			return defaultValue;
		}
	}
}
