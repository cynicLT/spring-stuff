package org.cynic.spring_stuff;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Supplier;
import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.framework.authentication.CustomAuthenticationEntryPoint;
import org.cynic.spring_stuff.framework.converter.CustomJwtAuthenticationConverter;
import org.cynic.spring_stuff.framework.decoder.CustomJwtDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootConfiguration(proxyBeanMethods = false)
@ImportAutoConfiguration({
    ConfigurationPropertiesAutoConfiguration.class,

    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    TransactionAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class,
    LiquibaseAutoConfiguration.class,

    CacheAutoConfiguration.class,

    AopAutoConfiguration.class,

    OAuth2ResourceServerAutoConfiguration.class,

    JacksonAutoConfiguration.class,

    HttpMessageConvertersAutoConfiguration.class,

    ServletWebServerFactoryAutoConfiguration.class,
    DispatcherServletAutoConfiguration.class,
    ErrorMvcAutoConfiguration.class,
    WebMvcAutoConfiguration.class
})
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
    @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})
})
@EnableScheduling
@EnableJpaRepositories
@EnableCaching
@EnableAspectJAutoProxy
@EntityScan("org.cynic.spring_stuff.domain.entity")
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    public Configuration() {
        Package pkg = getClass().getPackage();

        LOGGER.info(Constants.AUDIT_MARKER,
            "[{}-{}] STARTED",
            pkg.getImplementationTitle(),
            pkg.getImplementationVersion()
        );
    }

    @Bean
    public Clock clock() {
        return Clock.system(Constants.TIME_ZONE.toZoneId());
    }

    @Bean
    public Detector detector() {
        return TikaConfig.getDefaultConfig().getDetector();
    }

    @Bean
    public Supplier<Reader> cbrReaderSupplier(@Value("${job.cbr.file}") String cbrDailyFileTemplate, Clock clock) {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DOT,
            Locale.getDefault());
        Charset CP_1251 = Charset.forName("Cp1251");

        return () -> {
            try {
                ZonedDateTime to = ZonedDateTime.now(clock);
                ZonedDateTime from = to.minusDays(90);

                URI uri = URI.create(
                    String.format(
                        Locale.getDefault(),
                        cbrDailyFileTemplate,
                        from.format(DATE_TIME_FORMATTER),
                        to.format(DATE_TIME_FORMATTER)
                    )
                );

                return new StringReader(IOUtils.toString(uri, CP_1251));
            } catch (IOException e) {
                throw new ApplicationException("error.cbr.io", e);
            }
        };
    }

    @EnableWebSecurity
    @EnableMethodSecurity
    @SpringBootConfiguration(proxyBeanMethods = false)
    public static class SecurityAutoConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.security.token.validate", havingValue = "false")
        public JwtDecoder jwtDecoder() {
            return new CustomJwtDecoder();
        }

        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
            return new CustomAuthenticationEntryPoint(objectMapper);
        }

        @Bean
        public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
            return new CustomJwtAuthenticationConverter();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            JwtDecoder jwtDecoder,
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {
            return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(it -> it.authenticationEntryPoint(authenticationEntryPoint)
                    .jwt(t -> t.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .build();
        }
    }
}