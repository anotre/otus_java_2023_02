package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.HwCache;
import ru.otus.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.List;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
///
        HwCache<String, Client> cache = new MyCache<>(1000, log);

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, cache);
        var startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            var clientSecond = dbServiceClient.saveClient(new Client(String.valueOf(i)));
            var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
            dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), String.valueOf(i),new Address(null, "AnyStreet"), List.of(new Phone(null, "13-555-22"),
                    new Phone(null, "14-666-333"))));
            var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        }
        var endTime = System.nanoTime();
        System.out.printf("Execution time: %s", endTime - startTime);
    }
}
