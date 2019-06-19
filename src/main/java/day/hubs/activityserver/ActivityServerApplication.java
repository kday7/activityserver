package day.hubs.activityserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static day.hubs.activityserver.Constants.APPLICATION_CONTEXT_BEAN_NAME;
import static org.springframework.boot.SpringApplication.run;

@EnableSwagger2
@SpringBootApplication
public class ActivityServerApplication /*extends SpringBootServletInitializer*/ {

	private static final Logger LOG = LoggerFactory.getLogger(ActivityServerApplication.class);

	public static ConfigurableApplicationContext springApplicationContext;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new ActivityServerConfigurer();
	}

	public static void main(String[] args) {
		processArgs(args);
		springApplicationContext = run(ActivityServerApplication.class, args);
		if (null == springApplicationContext) {
			LOG.error("Failed to create the application context");
		}
	}

	private static void processArgs(final String[] args) {
		for (int i = 0; i < args.length; i++) {
			LOG.info("Argument {}: {}", i, args[i]);
		}
	}

	public static void shutDown() {
		springApplicationContext.close();
	}

	public static day.hubs.activityserver.components.ApplicationContext getApplicationContext() {
		if (null != springApplicationContext) {
			return (day.hubs.activityserver.components.ApplicationContext) springApplicationContext.getBean(APPLICATION_CONTEXT_BEAN_NAME);
		}

		return null;
	}

	public static Object getService(final String serviceName) {
		return (springApplicationContext != null) ? springApplicationContext.getBean(serviceName) : null;
	}

	public static day.hubs.activityserver.services.HubService getHubService(final String hub) {
		if (hub.equalsIgnoreCase("ActivityHub")) {
			return (day.hubs.activityserver.activityhub.ActivityHubService) ActivityServerApplication.getService("activityHubService");
		}
		else if (hub.equalsIgnoreCase("TaskHub")) {
			return (day.hubs.activityserver.taskhub.TaskHubService) ActivityServerApplication.getService("taskHubService");
		}
		else if (hub.equalsIgnoreCase("InfoHub")) {
			return (day.hubs.activityserver.infohub.InfoHubService) ActivityServerApplication.getService("infoHubService");
		}

		return null;
	}

}
