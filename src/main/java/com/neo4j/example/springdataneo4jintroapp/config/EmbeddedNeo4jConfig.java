package com.neo4j.example.springdataneo4jintroapp.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Map;


@Configuration
public class EmbeddedNeo4jConfig implements CommandLineRunner {

    @Bean
    public SessionFactory sessionFactory() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
//                .uri("neo4j://localhost")
                .build();
        return new SessionFactory("com.jpmc.cto.data.fabric.envoy.model");
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

        Result result = graphDatabaseService.execute("CALL apoc.help('help')");
//        Result result = graphDatabaseService.execute("CREATE (n:Person)");
//        result = graphDatabaseService.execute("MATCH (n:Person) RETURN n");
        System.out.println(result.next().toString());
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

//    @Autowired
//    UserRepository userRepository;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void run(String... args) throws Exception {
        sessionFactory.openSession().query("CREATE (n:Person)", Map.of());
        System.out.println("HELLO WORLD!");
        sessionFactory.openSession().query("CALL apoc.help('help')", Map.of());
//        crudNativeNeo4j(graphDatabaseService());
//        crudSpringDataNeo4j();
//        crudOgmSessionFactory();

        System.exit(0);
    }

//    private void crudSpringDataNeo4j() {
////        userRepository.save(User.builder().firstName("Marcus").lastName("Chiu").uuid("uuid").build());
//        Optional<User> o = userRepository.findByUuid("uuid");
//        System.out.println(o.get().getFirstName());
//    }
//
//    private void crudOgmSessionFactory() {
//        var session = sessionFactory.openSession();
//        var result = session.query("CALL apoc.help(\"apoc\")", new HashMap<>());
//        List<Map<String, Object>> dataList = StreamSupport.stream(result.spliterator(), false)
//                .collect(Collectors.toList());
//        System.out.println(dataList.toString());
//    }
}
