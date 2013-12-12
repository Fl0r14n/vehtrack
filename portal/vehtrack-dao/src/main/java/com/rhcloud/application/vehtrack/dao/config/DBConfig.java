package com.rhcloud.application.vehtrack.dao.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.rhcloud.application.vehtrack.dao.repository"})
public class DBConfig {

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {               
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        {
//            dataSource.setUrl("jdbc:postgresql://" + System.getenv("OPENSHIFT_DB_HOST") + ":" + System.getenv("OPENSHIFT_DB_PORT") + "/" + System.getenv("OPENSHIFT_APP_NAME"));
//            dataSource.setUsername(System.getenv("OPENSHIFT_DB_USERNAME"));
//            dataSource.setPassword(System.getenv("OPENSHIFT_DB_PASSWORD"));

            dataSource.setUrl("jdbc:postgresql://localhost:5432/vehtrack");
            dataSource.setUsername("postgres");
            dataSource.setPassword("sergtsop");
            
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setTestOnBorrow(true);
            dataSource.setTestOnReturn(true);
            dataSource.setTestWhileIdle(true);
            dataSource.setTimeBetweenEvictionRunsMillis(1800000);
            dataSource.setNumTestsPerEvictionRun(3);
            dataSource.setMinEvictableIdleTimeMillis(1800000);
            dataSource.setValidationQuery("SELECT 1");
        }
        return dataSource;
    }
    
    @Bean(name="entityManagerFactory") 
    @DependsOn({"dataSource"})
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        {
            vendorAdapter.setDatabase(Database.POSTGRESQL);
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(true);
        }
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        {
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPersistenceUnitName("vehtrack");
            factory.setPersistenceXmlLocation("classpath:/META-INF/persistence.xml");
            factory.setDataSource(dataSource());
            factory.afterPropertiesSet();
        }
        return factory.getObject();
    }

    @Bean
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }
}
