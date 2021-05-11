package com.neo4j.example.springdataneo4jintroapp.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class EmbeddedNeo4jConfig {

    @Bean
    public SessionFactory sessionFactory(GraphDatabaseService graphDatabaseService) {
        var configuration = new org.neo4j.ogm.config.Configuration.Builder()
//                .uri("neo4j://localhost")
                .build();
        var driver = new EmbeddedDriver(graphDatabaseService, configuration);
        return new SessionFactory(driver, "com.jpmc.cto.data.fabric.envoy.model");
    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
//        return new Neo4jTransactionManager(sessionFactory);
//    }

    @Bean
    public GraphDatabaseService graphDatabaseService() throws KernelException {
        var graphDatabaseService = new GraphDatabaseFactory()
                .newEmbeddedDatabase(new File("embedded-neo4j-db"));

        registerProcedure(graphDatabaseService,
                apoc.coll.Coll.class,
                apoc.map.Maps.class,
                apoc.convert.Json.class,
                apoc.create.Create.class,
                apoc.date.Date.class,
                apoc.lock.Lock.class,
                apoc.load.LoadJson.class,
                apoc.load.LoadCsv.class,
                apoc.load.Xml.class,
                apoc.path.PathExplorer.class,
                apoc.meta.Meta.class,
                apoc.refactor.GraphRefactoring.class,
                apoc.help.Help.class,
                apoc.periodic.Periodic.class);

        return graphDatabaseService;
    }

    private static void registerProcedure(GraphDatabaseService db, Class<?>... procedures) throws KernelException {
        var procedureServices = ((GraphDatabaseAPI) db)
                .getDependencyResolver()
                .resolveDependency(Procedures.class);

        for (Class<?> procedure : procedures) {
            procedureServices.registerProcedure(procedure, true);
            procedureServices.registerFunction(procedure, true);
            procedureServices.registerAggregationFunction(procedure, true);
        }
    }
}
