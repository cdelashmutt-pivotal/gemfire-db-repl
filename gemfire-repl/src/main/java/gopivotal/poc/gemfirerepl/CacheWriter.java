/**
 * 
 */
package gopivotal.poc.gemfirerepl;

import java.util.Map;

import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;
import org.springframework.integration.annotation.ServiceActivator;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;

/**
 * @author cdelashmutt
 *
 */
public class CacheWriter 
{
	
	private ClientCache cache;

	@ServiceActivator
	public void writeMessage(@Payload Map<String,Object> content, @Header("region") String region)
	{
		Region<Object,Object> gfRegion = cache.getRegion(region);
		String operation = content.get("operation").toString();
		content.remove("operation");
		
		if("U".equals(operation) || "I".equals(operation))
		{
			gfRegion.put(content.get("id"), content);
		}
		else if("D".equals(operation))
		{
			gfRegion.remove(content.get("id"));
		}
		else
		{
			throw new RuntimeException(String.format("Unsupported operation value detected: '%s'.  Check the queue table for %s for bad data.", operation, region));
		}
	}

	public ClientCache getCache() {
		return cache;
	}

	public void setCache(ClientCache cache) {
		this.cache = cache;
	}
}
