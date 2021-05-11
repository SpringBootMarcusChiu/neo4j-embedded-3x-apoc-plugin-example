package com.neo4j.example.springdataneo4jintroapp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class SpringDataNeo4jIntroAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataNeo4jIntroAppApplication.class, args);
	}

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private GraphDatabaseService graphDatabaseService;

	@Override
	public void run(String... args) {
		System.out.println("COMMAND LINE RUNNER");

		graphDatabaseServiceExample();
		sessionFactoryExample();

		System.exit(0);
	}

	private void graphDatabaseServiceExample() {
		var result1 = graphDatabaseService.execute("CALL apoc.help('help')");
		System.out.println("\nGraphDatabaseService\n" + result1.next().toString());
	}

	private void sessionFactoryExample() {
		var result2 = sessionFactory.openSession().query("CALL apoc.help('help')", Map.of());
		List<Map<String, Object>> dataList = StreamSupport.stream(result2.spliterator(), false)
				.collect(Collectors.toList());
		System.out.println("\nSessionFactory\n" + dataList.toString() + "\n\n");
	}
}
