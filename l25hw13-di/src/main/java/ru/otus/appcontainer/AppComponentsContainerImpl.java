package ru.otus.appcontainer;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.exceptions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    private final Comparator<ConfigElementMetadata> metadataComparator = new Comparator<>() {
        @Override
        public int compare(ConfigElementMetadata current, ConfigElementMetadata next) {
            return Integer.compare(current.getOrder(), next.getOrder());
        }
    };

     public AppComponentsContainerImpl(Class<?> initialConfigClass) {
         processConfigs(initialConfigClass);
     }

    public AppComponentsContainerImpl(Class<?> ...initialConfigClasses) {
            this.processConfigs(initialConfigClasses);
    }

    public AppComponentsContainerImpl(String packageUrl) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageUrl))
                        .setScanners(Scanners.TypesAnnotated));
        Set<Class<?>> configClassesSet = reflections
            .getTypesAnnotatedWith(AppComponentsContainerConfig.class);

        Class<?>[] configClases = configClassesSet.toArray(new Class<?>[0]);
        this.processConfigs(configClases);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        Object requestedComponent = null;

        for (Object component : appComponents) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                if (requestedComponent != null) {
                    throw new ContextSearchException(
                        String.format(
                            "More than one component found by %s argument", 
                            componentClass.getCanonicalName()));
                }

                requestedComponent = component;
            }
        }

        if (requestedComponent == null) {
            throw new NoSuchComponentException();
        }

        return (C) requestedComponent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        var component = this.appComponentsByName.get(componentName);

        if (component == null) {
            throw new NoSuchComponentException();
        }

        return (C) this.appComponentsByName.get(componentName);
    }

    private void processConfigs(Class<?> ...initialConfigClasses) {
        List<ConfigElementMetadata> unsortedMetadata = new ArrayList<>();

        for (var configClass : initialConfigClasses) {
            this.checkConfigClass(configClass);

            var configInstance = this.getConfigInstance(configClass);

            List<ConfigElementMetadata> configMetadata = this.getConfigElementsMetadata(configClass, configInstance);
            unsortedMetadata.addAll(configMetadata);
        }

        this.checkDoubleComponentsMetadata(unsortedMetadata);
        List<ConfigElementMetadata> sortedMetadata = this.sortMetadataList(unsortedMetadata);

        for (ConfigElementMetadata metadataItem : sortedMetadata) {
            Object[] args = this.getComponentArgs(metadataItem.getConfigMethod());
            Object component = this.createComponent(
                metadataItem.getConfigMethod(), 
                metadataItem.getConfigInstance(), 
                args);
            this.appComponents.add(component);
            this.appComponentsByName.put(metadataItem.getComponentName(), component);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(
                String.format("Given class has no config %s", configClass.getName()));
        }
    }

    private List<ConfigElementMetadata> getConfigElementsMetadata(Class<?> configClass, Object configInstance) {
        List<ConfigElementMetadata> metadataList = new ArrayList<>();

        for (Method method : configClass.getDeclaredMethods()) {
            AppComponent componentAnnotation = method.getDeclaredAnnotation(AppComponent.class);
            String componentName = componentAnnotation.name();

            if (componentAnnotation == null || componentName == null) {
                continue;
            }

            ConfigElementMetadata metadata = new ConfigElementMetadata(
                    componentName,
                    componentAnnotation.order(),
                    method,
                    configInstance);

            metadataList.add(metadata);
        }

        return metadataList;
    }

    private Object[] getComponentArgs(Method method) {
        List<Object> args = new ArrayList<>();
        Parameter[] parameters = method.getParameters();

        if (parameters.length == 0) {
            return new Object[0];
        }

        for (Parameter parameter : parameters) {
            args.add(this.getAppComponent(parameter.getType()));
        }

        return args.toArray();
    }

    private Object createComponent(Method method, Object configInstance, Object[] args) {
        try {
            return method.invoke(configInstance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ContextCreatingException(
                String.format("Error by creating component in %s method", method.getName()));
        }
    }

    private Object getConfigInstance(Class<?> configClass) {
        try {
            return configClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | 
                InstantiationException | 
                IllegalAccessException | 
                InvocationTargetException e) {
            throw new ConfigInstantiationException(
                String.format("Error by creating instance of %s class", configClass.getName()));
        }
    }

    private List<ConfigElementMetadata> sortMetadataList(List<ConfigElementMetadata> metadataList) {
        List<ConfigElementMetadata> metadataBuffer = new ArrayList<>(metadataList);
        metadataBuffer.sort(this.metadataComparator);

        return metadataBuffer;
    }

    private void checkDoubleComponentsMetadata(List<ConfigElementMetadata> metadataList) {
        Set<String> uniqueComponentNames = new HashSet<>();

        for (var componentMetadata : metadataList) {
            String componentName = componentMetadata.getComponentName();
            if (!uniqueComponentNames.add(componentName)) {
                throw new ContextCreatingException(
                    String.format(
                        "There was found doubles in config classes by %s name parameter", 
                        componentName));
            }
        }
    }

    class ConfigElementMetadata {
        private final String componentName;
        private final int order;
        private final Method configMethod;
        private final Object configInstance;

        private ConfigElementMetadata(String componentName, int order, Method configMethod, Object configInstance) {
            this.componentName = componentName;
            this.order = order;
            this.configMethod = configMethod;
            this.configInstance = configInstance;
        }

        public String getComponentName() {
            return this.componentName;
        }

        public int getOrder() {
            return this.order;
        }

        public Method getConfigMethod() {
            return this.configMethod;
        }

        public Object getConfigInstance() {
            return this.configInstance;
        }
    }


}
