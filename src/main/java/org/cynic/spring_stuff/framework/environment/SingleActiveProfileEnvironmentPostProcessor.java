package org.cynic.spring_stuff.framework.environment;

import java.util.Arrays;
import java.util.List;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public final class SingleActiveProfileEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(final ConfigurableEnvironment environment, final SpringApplication application) {
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());

        if (activeProfiles.size() > 1) {
            throw new ApplicationException("error.multiple.profiles.active", activeProfiles);
        }
    }
}
