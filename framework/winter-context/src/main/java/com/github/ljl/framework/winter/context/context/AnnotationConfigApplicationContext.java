package com.github.ljl.framework.winter.context.context;

import com.github.ljl.framework.winter.context.annotation.*;
import com.github.ljl.framework.winter.context.beans.BeanDefinition;
import com.github.ljl.framework.winter.context.beans.BeanPostProcessor;
import com.github.ljl.framework.winter.context.env.Environment;
import com.github.ljl.framework.winter.context.env.StandEnvironment;
import com.github.ljl.framework.winter.context.exception.*;
import com.github.ljl.framework.winter.context.io.PropertyResolver;
import com.github.ljl.framework.winter.context.io.ResourceResolver;
import com.github.ljl.framework.winter.context.utils.AnnotationUtils;
import com.github.ljl.framework.winter.context.utils.ApplicationContextUtils;
import com.github.ljl.framework.winter.context.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 10:56
 **/

public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    private Environment environment;

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    private Set<String> creatingBeanNames;

    private final PropertyResolver propertyResolver;

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public AnnotationConfigApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
        getEnvironment();
        register(configClass);
    }

    public Environment getEnvironment() {
        if (Objects.nonNull(environment)) {
            return environment;
        }
        Constructor<StandEnvironment> constructor = null;
        try {
            constructor = StandEnvironment.class.getConstructor(ApplicationContext.class, PropertyResolver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        environment = new StandEnvironment(this, propertyResolver);
        BeanDefinition beanDefinition = new BeanDefinition(
                "environment",
                Environment.class,
                constructor,
                0,
                false,
                null,
                null,
                null,
                null);
        beanDefinition.setInstance(environment);
        this.beanDefinitions.put("environment", beanDefinition);
        return environment;
    }

    private AnnotationConfigApplicationContext register(Class<?> configClass) {
        ApplicationContextUtils.setApplicationContext(this);

        // 扫描获取所有Bean的Class类型:
        Set<String> contextBeanClassNames = scanForClassNames(configClass);


        // 创建Bean的定义:
        // 包括了configurationBean的解析
        this.beanDefinitions.putAll(createBeanDefinitions(contextBeanClassNames));

        // 创建BeanName检测循环依赖:
        this.creatingBeanNames = new HashSet<>();

        // 创建@Configuration类型的Bean:
        final List<String> configurationBeanNames = beanDefinitions.values().stream()
                // 过滤出@Configuration:
                .filter(this::isConfigurationDefinition).sorted().map(definition -> {
                    // 创建Bean实例:
                    createBeanAsEarlySingleton(definition);
                    return definition.getName();
                }).collect(Collectors.toList());

        // 创建BeanPostProcessor类型的Bean:
        List<BeanPostProcessor> processors = beanDefinitions.values().stream()
                // 过滤出BeanPostProcessor:
                .filter(this::isBeanPostProcessorDefinition)
                // 排序:
                .sorted()
                // instantiate and collect:
                .map(def -> (BeanPostProcessor) createBeanAsEarlySingleton(def))
                .collect(Collectors.toList());

        this.beanPostProcessors.addAll(processors);

        // 创建其他普通Bean:
        List<BeanDefinition> defs = this.beanDefinitions.values().stream()
                // 过滤出instance==null的BeanDefinition:
                .filter(def -> def.getInstance() == null)
                .filter(def -> !isConfigurationDefinition(def))
                .sorted().collect(Collectors.toList());

        // 依次创建Bean实例:
        defs.forEach(def -> {
            // 如果Bean未被创建(可能在其他Bean的构造方法注入前被创建):
            if (def.getInstance() == null) {
                // 创建Bean:
                createBeanAsEarlySingleton(def);
            }
        });


        // 通过字段和set方法注入依赖:
        this.beanDefinitions.values().forEach(this::injectBean);

        // 调用init方法:
        this.beanDefinitions.values().forEach(this::initBean);

        this.beanDefinitions.values().stream().sorted().forEach(beanDefinition -> {
            logger.debug("bean initialized: {}", beanDefinition);
        });

        return this;
    }

    /**
     * 注入依赖但不调用init方法
     */
    private void injectBean(BeanDefinition def) {
        // 获取Bean实例，或被代理的原始实例:
        final Object beanInstance = getProxiedInstance(def);
        try {
            injectProperties(def, def.getBeanClass(), beanInstance);
        } catch (ReflectiveOperationException e) {
            throw new BeanCreationException(e);
        }
    }

    /**
     * 注入属性
     */
    private void injectProperties(BeanDefinition def, Class<?> clazz, Object bean) throws ReflectiveOperationException {
        // 在当前类查找Field和Method并注入:
        for (Field f : clazz.getDeclaredFields()) {
            tryInjectProperties(def, clazz, bean, f);
        }
        for (Method m : clazz.getDeclaredMethods()) {
            tryInjectProperties(def, clazz, bean, m);
        }
        // 在父类查找Field和Method并注入:
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            injectProperties(def, superClazz, bean);
        }
    }

    /**
     * 注入单个属性
     */
    private void tryInjectProperties(BeanDefinition def, Class<?> clazz, Object bean, AccessibleObject acc) throws ReflectiveOperationException {
        Value value = acc.getAnnotation(Value.class);
        Autowired autowired = acc.getAnnotation(Autowired.class);
        Resource resourced = acc.getAnnotation(Resource.class);

        final List<Annotation> collect = Arrays.stream(acc.getAnnotations())
                .filter(annotation -> annotation instanceof Value || annotation instanceof Resource | annotation instanceof Autowired)
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            return;
        }
        if (collect.size() > 1) {
            String message = String.format("Field or Method can have at most 1 annotation of {@Value, @Resource, @Autowire}  %s.%s for bean  %s",
                    clazz.getSimpleName(), def.getName(), def.getBeanClass().getName());
            throw new ExcessiveAnnotationException(message);
        }

        Annotation annotation = collect.get(0);


        Field field = null;
        Method method = null;
        if (acc instanceof Field) {
            Field f = (Field) acc;
            checkFieldOrMethod(f);
            acc.setAccessible(true);
            field = f;
        }
        if (acc instanceof Method) {
            Method m = (Method) acc;
            checkFieldOrMethod(m);
            if (m.getParameters().length != 1) {
                throw new BeanDefinitionException(
                        String.format("Cannot inject a non-setter method %s for bean '%s': %s", m.getName(), def.getName(), def.getBeanClass().getName()));
            }
            m.setAccessible(true);
            method = m;
        }

        if (Objects.isNull(method) && Objects.isNull(field)) {
            throw new BeanDefinitionException("Neither method nor field when injecting project");
        }

        String accessibleName = field != null ? field.getName() : method.getName();
        Class<?> accessibleType = field != null ? field.getType() : method.getParameterTypes()[0];

        // @Value注入:Property
        if (annotation instanceof Value) {
            Object propValue = this.propertyResolver.getRequiredProperty(value.value(), accessibleType);
            if (field != null) {
                logger.debug("Field injection: {}.{} = {}", def.getBeanClass().getName(), accessibleName, propValue);
                field.set(bean, propValue);
            }
            if (method != null) {
                logger.debug("Method injection: {}.{} ({})", def.getBeanClass().getName(), accessibleName, propValue);
                method.invoke(bean, propValue);
            }
        }

        // @Resource 注入，优先按name查找
        if (annotation instanceof Resource) {
            String name = resourced.name();
            if ("".equals(name)) {
                if (Objects.nonNull(field)) {
                    name = field.getName();
                } else {
                    name = method.getName();
                }
            }
            Object depends = findBean(name);
            if (Objects.isNull(depends)) {
                depends = findBean(accessibleType);
            }
            if (Objects.nonNull(depends)) {
                if (Objects.nonNull(field)) {
                    field.set(bean, depends);
                    logger.debug("Field injection: {}.{} = {}", def.getBeanClass().getName(), accessibleName, depends);
                }
                if (Objects.nonNull(method)) {
                    logger.debug("Field injection: {}.{} ({})", def.getBeanClass().getName(), accessibleName, depends);
                    method.invoke(bean, depends);
                }
            }
        }

        // @Autowired注入: 按照类型
        if (annotation instanceof Autowired) {
            String name = autowired.name();
            boolean required = autowired.value();
            Object depends = name.isEmpty() ? findBean(accessibleType) : findBean(name, accessibleType);
            if (required && depends == null) {
                throw new UnsatisfiedDependencyException(String.format("Dependency bean not found when inject %s.%s for bean '%s': %s", clazz.getSimpleName(),
                        accessibleName, def.getName(), def.getBeanClass().getName()));
            }
            if (depends != null) {
                if (field != null) {
                    logger.debug("Field injection: {}.{} = {}", def.getBeanClass().getName(), accessibleName, depends);
                    field.set(bean, depends);
                }
                if (method != null) {
                    logger.debug("Mield injection: {}.{} ({})", def.getBeanClass().getName(), accessibleName, depends);
                    method.invoke(bean, depends);
                }
            }
        }
    }

    // findXxx与getXxx类似，但不存在时返回null
    @SuppressWarnings("unchecked")
    protected <T> T findBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(name, requiredType);
        if (Objects.isNull(beanDefinition)) {
            return null;
        }
        return (T) beanDefinition.getRequiredInstance();
    }

    @SuppressWarnings("unchecked")
    protected <T> T findBean(Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(requiredType);
        if (Objects.isNull(beanDefinition)) {
            return null;
        }
        return (T) beanDefinition.getRequiredInstance();
    }
    protected <T> T findBean(String name) {
        BeanDefinition beanDefinition = findBeanDefinition(name);
        if (Objects.isNull(beanDefinition)) {
            return null;
        }
        return (T) beanDefinition.getRequiredInstance();
    }


    @SuppressWarnings("unchecked")
    protected <T> List<T> findBeans(Class<T> requiredType) {
        return findBeanDefinitions(requiredType).stream().map(def -> (T) def.getRequiredInstance()).collect(Collectors.toList());
    }

    private void checkFieldOrMethod(Member m) {
        int mod = m.getModifiers();
        if (Modifier.isStatic(mod)) {
            throw new BeanDefinitionException("Cannot inject static field: " + m);
        }
        if (Modifier.isFinal(mod)) {
            if (m instanceof Field) {
                throw new BeanDefinitionException("Cannot inject final field: " + m);
            }
            if (m instanceof Method) {
                logger.warn("Inject final method should be careful because it is not called on target bean when bean is proxied and may cause NullPointerException.");
            }
        }
    }

    /**
     * 调用init方法
     */
    private void initBean(BeanDefinition def) {
        // 获取Bean实例，或被代理的原始实例:
        final Object beanInstance = getProxiedInstance(def);

        // 调用init方法: @Bean(initMethod=...)
        callMethod(beanInstance, def.getInitMethod(), def.getInitMethodName());

        // 调用BeanPostProcessor.postProcessAfterInitialization():
        beanPostProcessors.forEach(beanPostProcessor -> {
            Object processedInstance = beanPostProcessor.postProcessAfterInitialization(def.getInstance(), def.getName());
            if (processedInstance != def.getInstance()) {
                logger.debug("BeanPostProcessor {} return different bean from {} to {}.", beanPostProcessor.getClass().getSimpleName(),
                        def.getInstance().getClass().getName(), processedInstance.getClass().getName());
                def.setInstance(processedInstance);
            }
        });
    }

    private void callMethod(Object beanInstance, Method method, String namedMethod) {
        // 调用init/destroy方法:
        if (method != null) {
            try {
                method.invoke(beanInstance);
            } catch (ReflectiveOperationException e) {
                throw new BeanCreationException(e);
            }
        } else if (namedMethod != null) {
            // 查找initMethod/destroyMethod="xyz"，注意是在实际类型中查找:
            Method named = ClassUtils.getNamedMethod(beanInstance.getClass(), namedMethod);
            named.setAccessible(true);
            try {
                named.invoke(beanInstance);
            } catch (ReflectiveOperationException e) {
                throw new BeanCreationException(e);
            }
        }
    }

    private Object getProxiedInstance(BeanDefinition def) {
        Object beanInstance = def.getInstance();
        // 如果Proxy改变了原始Bean，又希望注入到原始Bean，则由BeanPostProcessor指定原始Bean:
        List<BeanPostProcessor> reversedBeanPostProcessors = new ArrayList<>(this.beanPostProcessors);
        Collections.reverse(reversedBeanPostProcessors);
        for (BeanPostProcessor beanPostProcessor : reversedBeanPostProcessors) {
            Object restoredInstance = beanPostProcessor.postProcessOnSetProperty(beanInstance, def.getName());
            if (restoredInstance != beanInstance) {
                logger.debug("BeanPostProcessor {} specified injection from {} to {}.", beanPostProcessor.getClass().getSimpleName(),
                        beanInstance.getClass().getSimpleName(), restoredInstance.getClass().getSimpleName());
                beanInstance = restoredInstance;
            }
        }
        return beanInstance;
    }

    private boolean isConfigurationDefinition(BeanDefinition def) {
        return AnnotationUtils.findAnnotation(def.getBeanClass(), Configuration.class) != null;
    }

    private Map<String, BeanDefinition> createBeanDefinitions(Set<String> classNameSet) {
        Map<String, BeanDefinition> defs = new HashMap<>();
        for (String className : classNameSet) {
            // 获取Class:
            Class<?> clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(e);
            }
            // 注解类不被管理
            if (clazz.isAnnotation()) {
                continue;
            }
            // 是否标注@Component?
            Component component = AnnotationUtils.findAnnotation(clazz, Component.class);
            if (component != null) {
                // 获取Bean的名称:
                String beanName = ClassUtils.getBeanName(clazz);
                BeanDefinition beanDefinition = new BeanDefinition(
                        beanName, clazz, getSuitableConstructor(clazz),
                        getOrder(clazz), clazz.isAnnotationPresent(Primary.class),
                        // init/destroy方法名称:
                        null, null,
                        // 查找@PostConstruct方法:
                        ClassUtils.findAnnotationMethod(clazz, PostConstruct.class),
                        // 查找@PreDestroy方法:
                        ClassUtils.findAnnotationMethod(clazz, PreDestroy.class));
                addBeanDefinitions(defs, beanDefinition);
                // 查找是否有@Configuration:
                Configuration configuration = AnnotationUtils.findAnnotation(clazz, Configuration.class);
                if (configuration != null) {
                    // 查找@Bean方法:
                    scanFactoryMethods(beanName, clazz, defs);
                }
            }
        }
        return defs;
    }

    /**
     * Get public constructor or non-public constructor as fallback.
     */
    private Constructor<?> getSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] cons = clazz.getConstructors();
        if (cons.length == 0) {
            cons = clazz.getDeclaredConstructors();
            if (cons.length != 1) {
                throw new BeanDefinitionException("More than one constructor found in class " + clazz.getName() + ".");
            }
        }
        if (cons.length != 1) {
            throw new BeanDefinitionException("More than one public constructor found in class " + clazz.getName() + ".");
        }
        return cons[0];
    }

    /**
     * Scan factory method that annotated with @Bean:
     *
     * <code>
     * @Configuration
     * public class Hello {
     *     @Bean
     *     ZoneId createZone() {
     *         return ZoneId.of("Z");
     *     }
     * }
     * </code>
     */
    private void scanFactoryMethods(String factoryBeanName, Class<?> clazz, Map<String, BeanDefinition> definitions) {
        for (Method method : clazz.getDeclaredMethods()) {
            Bean bean = method.getAnnotation(Bean.class);
            if (bean != null) {
                int mod = method.getModifiers();
                if (Modifier.isAbstract(mod)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be abstract.");
                }
                if (Modifier.isFinal(mod)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be final.");
                }
                if (Modifier.isPrivate(mod)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be private.");
                }
                Class<?> beanClass = method.getReturnType();
                if (beanClass.isPrimitive()) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return primitive type.");
                }
                if (beanClass == void.class || beanClass == Void.class) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return void.");
                }
                BeanDefinition definition = new BeanDefinition(ClassUtils.getBeanName(method), beanClass, factoryBeanName, method, getOrder(method),
                        method.isAnnotationPresent(Primary.class),
                        // init method:
                        bean.initMethod().isEmpty() ? null : bean.initMethod(),
                        // destroy method:
                        bean.destroyMethod().isEmpty() ? null : bean.destroyMethod(),
                        // @PostConstruct / @PreDestroy method:
                        null,
                        null);
                addBeanDefinitions(definitions, definition);
                logger.debug("define bean: {}", definition);
            }
        }
    }

    /**
     * Check and add bean definitions.
     */
    private void addBeanDefinitions(Map<String, BeanDefinition> definitions, BeanDefinition def) {
        if (definitions.put(def.getName(), def) != null) {
            throw new BeanDefinitionException("Duplicate bean name: " + def.getName());
        }
    }

    /**
     * Get order by:
     *
     * <code>
     * @Order(100)
     * @Component
     * public class Hello {}
     * </code>
     */
    private int getOrder(Class<?> clazz) {
        Order order = clazz.getAnnotation(Order.class);
        return order == null ? Integer.MAX_VALUE : order.value();
    }

    private int getOrder(Method method) {
        Order order = method.getAnnotation(Order.class);
        return order == null ? Integer.MAX_VALUE : order.value();
    }

    protected Set<String> scanForClassNames(Class<?> configClass) {
        return scanForClassNameRecursive(new HashSet<>(), configClass, new HashSet<>());
    }

    private Set<String> scanForClassNameRecursive(Set<String>result, Class<?> configClass, Set<String> visited) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Set<String> classList = scan(configClass);

        classList.forEach(className -> {
            try {
                Class<?> clazz = classLoader.loadClass(className);
                // 注解本身
                if (!clazz.isAnnotation()) {
                    // 检查是否有 @ComponentScan 注解，递归扫描
                    if (AnnotationUtils.findAnnotation(clazz, ComponentScan.class) != null) {
                        if (!visited.contains(className)) {
                            visited.add(className);
                            scanForClassNameRecursive(result, clazz, visited);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                logger.error("Class not found: {}", className, e);
            }
        });

        result.addAll(classList);

        // try to add jdbcTemplate



        return result;
    }
    /**
     * Do component scan and return class names.
     */
    protected Set<String> scan(Class<?> configClass) {
        /**
         * 需要预先尝试加载的内部bean
         * //  TODO: 放到配置文件
         */
        String[] preparedConfigs = {
                configClass.getName(),
                "com.github.ljl.framework.winter.jdbc.config.JdbcConfiguration",
        };
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<String> scanPackages = new HashSet<>();
        for (String configNames: preparedConfigs) {
            try {
                Class<?> clazz = classLoader.loadClass(configNames);
                scanPackages.add(clazz.getPackage().getName());
            } catch (ClassNotFoundException ignored) {
            }
        }

        List<ComponentScan> scanList = AnnotationUtils.findAnnotations(configClass, ComponentScan.class);
        if (!scanList.isEmpty()) {
            scanPackages.addAll(scanList.stream()
                    .map(componentScan ->
                        componentScan.value().length == 0 ?
                                // TODO
                                new String[]{configClass.getPackage().getName()}
                                : componentScan.value()
                    )
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toSet()));
        }

        logger.debug("component scan in packages: {}", Arrays.toString(scanPackages.toArray(new String[0])));

        Set<String> classNameSet = new HashSet<>();
        for (String pkg : scanPackages) {
            // 扫描package:
            logger.debug("scan package: {}", pkg);
            ResourceResolver rr = new ResourceResolver(pkg);
            List<String> classList = rr.scan(res -> {
                String name = res.getName();
                if (name.endsWith(".class")) {
                    return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
                }
                return null;
            });
            classList.forEach((className) -> {
                logger.debug("class found by component scan: {}", className);
            });
            classNameSet.addAll(classList);
        }

        // 查找@Import(包括组合的Import, Class<?>, 获取所有的values):

        final List<Import> imports = AnnotationUtils.findAnnotations(configClass, Import.class);
        imports.stream()
                .map(Import::value)
                .flatMap(Arrays::stream)
                .distinct()
                .forEach(importConfigClass -> {
                    String importClassName = importConfigClass.getName();
                    if (classNameSet.contains(importClassName)) {
                        logger.warn("ignore import: " + importClassName + " for it is already been scanned.");
                    } else {
                        logger.debug("class found by import: {}", importClassName);
                        classNameSet.add(importClassName);
                    }
                });

        return classNameSet;
    }

    // 根据Name查找BeanDefinition，如果Name不存在，返回null

    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }

    /**
     * 根据Type查找若干个BeanDefinition，返回0个或多个。
     */
    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return this.beanDefinitions.values().stream()
                // filter by type and sub-type:
                .filter(def -> type.isAssignableFrom(def.getBeanClass()))
                // 排序:
                .sorted().collect(Collectors.toList());
    }

    /**
     * 根据Type查找某个BeanDefinition
     * 如果不存在返回null，如果存在多个返回@Primary标注的一个
     * 如果有多个@Primary标注，或没有@Primary标注但找到多个
     * 均抛出NoUniqueBeanDefinitionException
     */

    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> definitions = findBeanDefinitions(type);
        if (definitions.isEmpty()) {
            return null;
        }
        if (definitions.size() == 1) {
            return definitions.get(0);
        }
        // more than 1 beans, require @Primary:
        List<BeanDefinition> primaryDefinitions = definitions.stream().filter(def -> def.isPrimary()).collect(Collectors.toList());
        if (primaryDefinitions.size() == 1) {
            return primaryDefinitions.get(0);
        }
        if (primaryDefinitions.isEmpty()) {
            throw new NoUniqueBeanDefinitionException(String.format("Multiple bean with type '%s' found, but no @Primary specified.", type.getName()));
        } else {
            throw new NoUniqueBeanDefinitionException(String.format("Multiple bean with type '%s' found, and multiple @Primary specified.", type.getName()));
        }
    }

    /**
     * 创建一个Bean，然后使用BeanPostProcessor处理
     * 但不进行字段和方法级别的注入。如果创建的Bean不是Configuration或BeanPostProcessor，
     * 则在构造方法中注入的依赖Bean会自动创建。
     */
    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition beanDefinition) {
        logger.debug("Try create bean '{}' as early singleton: {}", beanDefinition.getName(), beanDefinition.getBeanClass().getName());
        if (!this.creatingBeanNames.add(beanDefinition.getName())) {
            throw new UnsatisfiedDependencyException(String.format("Circular dependency detected when create bean '%s'", beanDefinition.getName()));
        }

        // 创建方式：构造方法或工厂方法:
        Executable createFn = null;
        if (beanDefinition.getFactoryName() == null) {
            // by constructor:
            createFn = beanDefinition.getConstructor();
        } else {
            // by factory method:
            createFn = beanDefinition.getFactoryMethod();
        }

        // 创建参数:
        final Parameter[] parameters = createFn.getParameters();
        final Annotation[][] parametersAnnos = createFn.getParameterAnnotations();
        Object[] args = new Object[parameters.length];
        // 参数列表
        for (int i = 0; i < parameters.length; i++) {
            final Parameter param = parameters[i];
            final Annotation[] paramAnnos = parametersAnnos[i];
            final Value value = ClassUtils.getAnnotation(paramAnnos, Value.class);
            final Autowired autowired = ClassUtils.getAnnotation(paramAnnos, Autowired.class);

            // @Configuration类型的Bean是工厂，不允许使用@Autowired创建:
            final boolean isConfiguration = isConfigurationDefinition(beanDefinition);
            if (isConfiguration && autowired != null) {
                throw new BeanCreationException(
                        String.format("Cannot specify @Autowired when create @Configuration bean '%s': %s.", beanDefinition.getName(), beanDefinition.getBeanClass().getName()));
            }

            // BeanPostProcessor不能依赖其他Bean，不允许使用@Autowired创建:
            final boolean isBeanPostProcessor = isBeanPostProcessorDefinition(beanDefinition);
            if (isBeanPostProcessor && autowired != null) {
                throw new BeanCreationException(
                        String.format("Cannot specify @Autowired when create BeanPostProcessor '%s': %s.", beanDefinition.getName(), beanDefinition.getBeanClass().getName()));
            }

            // 参数需要@Value或@Autowired两者之一:
            if (value != null && autowired != null) {
                throw new BeanCreationException(
                        String.format("Cannot specify both @Autowired and @Value when create bean '%s': %s.", beanDefinition.getName(), beanDefinition.getBeanClass().getName()));
            }
            // 参数类型:
            final Class<?> type = param.getType();
            if (value != null) {
                // 参数是@Value:
                args[i] = this.propertyResolver.getRequiredProperty(value.value(), type);
            }
            else if (autowired != null) {
                // 参数是@Autowired:
                String name = autowired.name();
                boolean required = autowired.value();
                // 依赖的BeanDefinition:
                BeanDefinition dependsOnDef = name.isEmpty() ? findBeanDefinition(type) : findBeanDefinition(name, type);
                // 检测required==true?
                if (required && dependsOnDef == null) {
                    throw new BeanCreationException(String.format("Missing autowired bean with type '%s' when create bean '%s': %s.", type.getName(),
                            beanDefinition.getName(), beanDefinition.getBeanClass().getName()));
                }
                if (dependsOnDef != null) {
                    // 获取依赖Bean:
                    Object autowiredBeanInstance = dependsOnDef.getInstance();
                    if (autowiredBeanInstance == null && !isConfiguration && !isBeanPostProcessor) {
                        // 当前依赖Bean尚未初始化，递归调用初始化该依赖Bean:
                        autowiredBeanInstance = createBeanAsEarlySingleton(dependsOnDef);
                    }
                    args[i] = autowiredBeanInstance;
                } else {
                    args[i] = null;
                }
            }
            else {
                throw new BeanCreationException(
                        String.format("Must specify @Autowired or @Value when create bean '%s': %s.", beanDefinition.getName(), beanDefinition.getBeanClass().getName()));
            }
        }

        // 创建Bean实例:
        Object instance = null;
        if (beanDefinition.getFactoryName() == null) {
            // 用构造方法创建:
            try {
                instance = beanDefinition.getConstructor().newInstance(args);
            } catch (Exception e) {
                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", beanDefinition.getName(), beanDefinition.getBeanClass().getName()), e);
            }
        } else {
            // 用@Bean方法创建:
            Object configInstance = getBean(beanDefinition.getFactoryName());
            try {
                instance = beanDefinition.getFactoryMethod().invoke(configInstance, args);
            } catch (Exception e) {
                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", beanDefinition.getName(), beanDefinition.getBeanClass().getName()), e);
            }
        }
        beanDefinition.setInstance(instance);

        // 调用BeanPostProcessor处理Bean:
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object processed = processor.postProcessBeforeInitialization(beanDefinition.getInstance(), beanDefinition.getName());
            if (Objects.isNull(processed)) {
                throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", beanDefinition.getName(), processor));
            }
            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用:
            if (beanDefinition.getInstance() != processed) {
                logger.debug("Bean '{}' was replaced by post processor {}.", beanDefinition.getName(), processor.getClass().getName());
                beanDefinition.setInstance(processed);
            }
        }
        return beanDefinition.getInstance();
    }
    /**
     * 根据Name和Type查找BeanDefinition
     * 如果Name不存在，返回null
     * 如果Name存在，但Type不匹配，抛出异常。
     */

    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        BeanDefinition definition = findBeanDefinition(name);
        if (Objects.isNull(definition)) {
            return null;
        }
        if (!requiredType.isAssignableFrom(definition.getBeanClass())) {
            throw new BeanNotOfRequiredTypeException(String.format("Autowire required type '%s' but bean '%s' has actual type '%s'.", requiredType.getName(),
                    name, definition.getBeanClass().getName()));
        }
        return definition;
    }

    private boolean isBeanPostProcessorDefinition(BeanDefinition definition) {
        return BeanPostProcessor.class.isAssignableFrom(definition.getBeanClass());
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanDefinitions.containsKey(name);
    }

    @Override
    public <T> T getBean(String name) {
        BeanDefinition def = this.beanDefinitions.get(name);
        if (def == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s'.", name));
        }
        return (T) def.getRequiredInstance();
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        T t = findBean(name, requiredType);
        if (Objects.isNull(t)) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s' and type '%s'.", name, requiredType));
        }
        return t;
    }

    /**
     * 通过Type查找Bean，不存在抛出NoSuchBeanDefinitionException，存在多个但缺少唯一@Primary标注抛出NoUniqueBeanDefinitionException
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        BeanDefinition definition = findBeanDefinition(requiredType);
        if (Objects.isNull(definition)) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with type '%s'.", requiredType));
        }
        return (T) definition.getRequiredInstance();
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        List<BeanDefinition> beanDefinitions = findBeanDefinitions(requiredType);
        if (beanDefinitions.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(beanDefinitions.size());
        for (BeanDefinition definition : beanDefinitions) {
            list.add((T) definition.getRequiredInstance());
        }
        return list;
    }

    @Override
    public void close() {
        logger.debug("Closing {}...", this.getClass().getName());
        this.beanDefinitions.values().forEach(definition -> {
            final Object beanInstance = getProxiedInstance(definition);
            callMethod(beanInstance, definition.getDestroyMethod(), definition.getDestroyMethodName());
        });
        this.beanDefinitions.clear();
        logger.debug("{} closed.", this.getClass().getName());
        ApplicationContextUtils.setApplicationContext(null);
    }
}
