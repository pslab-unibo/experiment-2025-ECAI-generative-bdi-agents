package it.unibo.jakta.agents.bdi.engine.logging.loggers.resolvers;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverFactory;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverFactory;

@Plugin(name = "JaktaLogEventResolverFactory", category = TemplateResolverFactory.CATEGORY)
public final class JaktaLogEventResolverFactory implements EventResolverFactory {

    private static final JaktaLogEventResolverFactory INSTANCE = new JaktaLogEventResolverFactory();

    private JaktaLogEventResolverFactory() {}

    @PluginFactory
    public static JaktaLogEventResolverFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return JaktaLogEventResolver.getName();
    }

    @Override
    public JaktaLogEventResolver create(EventResolverContext context, TemplateResolverConfig config) {
        return new JaktaLogEventResolver(context, config);
    }
}
