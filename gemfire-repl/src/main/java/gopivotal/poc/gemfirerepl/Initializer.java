package gopivotal.poc.gemfirerepl;

import java.nio.charset.Charset;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Component
public class Initializer implements InitializingBean
{
	Logger log = LoggerFactory.getLogger(Initializer.class);
	
	@Value("${dbinitializer.schema}")
	private String schema;
	
	@Value("${dbinitializer.tables}")
	private String tables;
	
	@Resource
	private DataSource db;
	
	@Resource
	private ApplicationContext parentContext;
	
	private org.springframework.core.io.Resource scriptResource = new ClassPathResource("schema-template.sql");

	@Override
	public void afterPropertiesSet() throws Exception 
	{
		
		JdbcTemplate template = new JdbcTemplate(db);
		
		String script = StreamUtils.copyToString(scriptResource.getInputStream(), Charset.defaultCharset());
		
		String schemaScript = StringUtils.replace(script, "${schema}", schema );
		for( String table : tables.split(","))
		{
			//Set up the Triggers and Queue tables
			String tableScript = StringUtils.replace(schemaScript, "${table}", table );
			template.execute(tableScript);
			
			log.info("Creating poller channel for [{}].[{}]", schema, table);
			
			//Create child context for listener
			ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
					new String[] { "inbound-integration.xml" },
					false,
					parentContext);
			setEnvironmentForTable(ctx, schema, table);
			
			ctx.refresh();
		}
		
	}

	private void setEnvironmentForTable(ConfigurableApplicationContext ctx,
			String schema, String table) {
		StandardEnvironment env = new StandardEnvironment();
		Properties props = new Properties();
		// populate properties for customer
		props.setProperty("schema", schema);
		props.setProperty("table", table);
		PropertiesPropertySource pps = new PropertiesPropertySource("tableprops", props);
		env.getPropertySources().addLast(pps);
		ctx.setEnvironment(env);
	}
}
