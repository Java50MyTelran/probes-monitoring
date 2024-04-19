package telran.probes;

import java.util.function.Consumer;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;

@Slf4j
@SpringBootApplication

public class AvgPopulatorAppl {
	private static final String ID = "id";
	private static final String TIMESTAMP = "timestamp";
	private static final String VALUE = "value";
	AmazonDynamoDB client ;
	DynamoDB dynamo;
	Table table ;
	@Value("${app.aws.dynamo.table}")
	String tableName;
	public static void main(String[] args) {
		SpringApplication.run(AvgPopulatorAppl.class, args);

	}
	@Bean
	Consumer<ProbeData> avgPopulatorConsumer() {
		return this::probeDataPopulation;
	}
	void probeDataPopulation(ProbeData probeData) {
		log.debug("received probeData: {}", probeData);
		Map<String, Object> mapItem = getMap(probeData);
		table.putItem(new PutItemSpec().withItem(Item.fromMap(mapItem)));
		log.debug("item {} has been saved to Database", mapItem);
	}
	private Map<String, Object> getMap(ProbeData probeData) {
		Map<String, Object> mapResult = new HashMap<>();
		mapResult.put(ID, probeData.id());
		mapResult.put(TIMESTAMP, probeData.timestamp());
		mapResult.put(VALUE, probeData.value());
		return mapResult;
	}
	@PostConstruct
	void setDynamoDB() {
		client = AmazonDynamoDBClientBuilder.defaultClient();
		dynamo = new DynamoDB(client);
		table = dynamo.getTable(tableName);
	}

}
