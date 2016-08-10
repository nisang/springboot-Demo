package io.dandan.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

/**
 * 
 */
@Configuration
@AutoConfigureAfter(DataSourceConfiguration.class)
public class MyBatisConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    Logger logger = LoggerFactory.getLogger(MyBatisConfiguration.class);

    private static RelaxedPropertyResolver propertyResolver = null;

    Environment environment;

    static String DATASOURCE_BEAN_NAME_SUFFIX = "DataSource";
    static String SQL_SESSION_FACTORY_BEAN_NAME_SUFFIX = "SqlSessionFactory";
    static String TRANSACTION_MANAGER_BEAN_NAME_SUFFIX = "TransactionManager";
    static String MAPPER_SCANNERE_CONFIGURATION_BEAN_NAME_SUFFIX = "MapperScannerConfigurer";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            propertyResolver = new RelaxedPropertyResolver(environment,   "carmall.");
            String profile = "";

            try {

                String dataSourceBeanName = profile + DATASOURCE_BEAN_NAME_SUFFIX;
                String sqlSessionFactoryBeanName = profile + SQL_SESSION_FACTORY_BEAN_NAME_SUFFIX;
                String transactionManagerBeanName = profile + TRANSACTION_MANAGER_BEAN_NAME_SUFFIX;
                String mapperSannerConfigurationbeanName = profile + MAPPER_SCANNERE_CONFIGURATION_BEAN_NAME_SUFFIX;
                String mapperLocations = propertyResolver.getProperty("mapperLocations");
                String url =  propertyResolver.getProperty("url");
                String typeAliasesPackage =  propertyResolver.getProperty("typeAliasesPackage");
                String basePackage =   propertyResolver.getProperty("basePackage");
                logger.info("mapperLocations:{}",mapperLocations);
                logger.info("url:{}",url);
                logger.info("typeAliasesPackage:{}",typeAliasesPackage);
                logger.info("basePackage:{}",basePackage);
                logger.debug("Initialization {}", dataSourceBeanName);

                BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
                dataSourceBuilder.setInitMethodName("init").setDestroyMethodName("close")
                        .addPropertyValue("url", url)
                        .addPropertyValue("username", propertyResolver.getProperty("user"))
                        .addPropertyValue("password", propertyResolver.getProperty("password"))
                        .addPropertyValue("maxActive", propertyResolver.getProperty("maxActive"));

                registry.registerBeanDefinition(dataSourceBeanName, dataSourceBuilder.getRawBeanDefinition());

                logger.debug("Initialization {}", sqlSessionFactoryBeanName);

                BeanDefinitionBuilder sqlSessionFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class);
                sqlSessionFactoryBuilder.addPropertyValue("typeAliasesPackage", typeAliasesPackage)
                        .addPropertyValue("mapperLocations", new PathMatchingResourcePatternResolver().getResources(mapperLocations))
                        .addPropertyReference("dataSource", dataSourceBeanName);

                registry.registerBeanDefinition(sqlSessionFactoryBeanName, sqlSessionFactoryBuilder.getRawBeanDefinition());


                logger.debug("Initialization {}", transactionManagerBeanName);

                BeanDefinitionBuilder transactionManagerBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
                transactionManagerBuilder.addConstructorArgReference(dataSourceBeanName);

                registry.registerBeanDefinition(transactionManagerBeanName, transactionManagerBuilder.getRawBeanDefinition());

                logger.debug("Initialization {}", mapperSannerConfigurationbeanName);

                BeanDefinitionBuilder mapperScannerBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
                mapperScannerBuilder.addPropertyValue("basePackage", basePackage)
                        .addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName)
                        .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

                registry.registerBeanDefinition(mapperSannerConfigurationbeanName, mapperScannerBuilder.getRawBeanDefinition());
            } catch (IOException e) {
               logger.error("",e);
            }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
