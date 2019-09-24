package in.hocg.windmill.spring.boot.autoconfigure;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import in.hocg.windmill.spring.boot.autoconfigure.cache.AntiReplayCache;
import in.hocg.windmill.spring.boot.autoconfigure.cache.DefaultAntiReplayCache;
import in.hocg.windmill.spring.boot.autoconfigure.handle.AntiReplayHandle;
import in.hocg.windmill.spring.boot.autoconfigure.properties.WindmillProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Log
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = WindmillProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(WindmillProperties.class)
public class WindmillAutoConfiguration implements BeanFactoryPostProcessor,
        EnvironmentAware {
    
    private final static String DEFAULT_BEAN_NAME_FORMATTER = "Windmill_%s";
    private Environment environment;
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        WindmillProperties properties = getProperties();
        List<String> ignoreUrls = properties.getIgnoreUrl();
        Long antiReplayInterval = properties.getAntiReplayInterval();
        AntiReplayHandle antiReplayHandle = beanFactory.getBean(AntiReplayHandle.class);
        beanFactory.registerSingleton(String.format(DEFAULT_BEAN_NAME_FORMATTER,
                AntiReplayFilter.class.getName()),
                new AntiReplayFilter(antiReplayCache(), ignoreUrls, antiReplayInterval, antiReplayHandle));
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    @Bean
    @ConditionalOnMissingBean(AntiReplayCache.class)
    public AntiReplayCache antiReplayCache() {
        Cache<String, Long> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(getProperties().getAntiReplayInterval(), TimeUnit.SECONDS)
                .build();
        return new DefaultAntiReplayCache(cache);
    }
    
    @Bean
    @ConditionalOnMissingBean(WindmillProperties.class)
    private WindmillProperties getProperties() {
        return Binder.get(environment).bind(WindmillProperties.PREFIX, WindmillProperties.class)
                .orElse(new WindmillProperties());
    }
    
}
