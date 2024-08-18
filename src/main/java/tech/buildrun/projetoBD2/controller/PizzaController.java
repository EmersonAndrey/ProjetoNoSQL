package tech.buildrun.projetoBD2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import tech.buildrun.projetoBD2.entity.Pizza;

@RestController
@RequestMapping("/pizza")
public class PizzaController {

	@Autowired
	private static DynamoDbTemplate dynamoDbTeamplate;
	
	
	public PizzaController(DynamoDbTemplate dynamoDbTeamplate) {
		this.dynamoDbTeamplate = dynamoDbTeamplate;
	}

	@PostMapping("/save")
	public static ResponseEntity<Pizza> salvarPizza(@RequestBody Pizza pizza) {
		Pizza p = Pizza.addID(pizza);
		dynamoDbTeamplate.save(p);
		return ResponseEntity.ok().body(p);
	}

	@GetMapping("/getAll/{sabor}")
	public List<Pizza> getAll(@PathVariable String sabor) {
		var key = Key.builder().partitionValue(sabor).build();
		var condition = QueryConditional.keyEqualTo(key);
		var query = QueryEnhancedRequest.builder().queryConditional(condition).build();
		
		var history = dynamoDbTeamplate.query(query, Pizza.class);	
		
	 
	    return history.items().stream().toList();
		
	}	

	@GetMapping("/findByFlavor/{sabor}/{pizzaId}")
	public ResponseEntity<Pizza> findByFlavor(@PathVariable("sabor") String sabor,@PathVariable("pizzaId") String pizzaId) {
		var entity = dynamoDbTeamplate.load(
				Key.builder()
				.partitionValue(sabor)
				.sortValue(pizzaId)
				.build(), Pizza.class);
		
		return entity == null ? ResponseEntity.status(HttpStatus.NOT_FOUND).build(): ResponseEntity.ok(entity);
	}
	
	@PutMapping("/update/{sabor}/{pizzaId}")
	public ResponseEntity<Pizza> updatePizza(@PathVariable String sabor,@PathVariable String pizzaId, @RequestBody Pizza pizza) {
		var entity = dynamoDbTeamplate.load(
				Key.builder()
				.partitionValue(sabor)
				.sortValue(pizzaId).build(),
				Pizza.class);
		
		if(entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		entity.setTamanho(pizza.getTamanho());;
		entity.setPreco(pizza.getPreco());;
		
		dynamoDbTeamplate.save(entity);
		
		return ResponseEntity.ok(entity);
	}
	
	
	@DeleteMapping("delete/{sabor}/{pizzaId}")
	public void deletePizza(@PathVariable String sabor, @PathVariable String pizzaId) {
		var entity = dynamoDbTeamplate.load(
				Key.builder()
				.partitionValue(sabor)
				.sortValue(pizzaId).build(),
				Pizza.class);
		
		if(entity != null) {
			dynamoDbTeamplate.delete(entity);
		}
	}
	
	
	public static Pizza addInfoPizza(Pizza pizza) {
		return Pizza.addID(pizza);
	}
	
	
	public static List<Pizza> getAll(@PathVariable String sabor, DynamoDbTemplate dynamoDbTeamplate) {
		var key = Key.builder().partitionValue(sabor).build();
		var condition = QueryConditional.keyEqualTo(key);
		var query = QueryEnhancedRequest.builder().queryConditional(condition).build();
		
		var history = dynamoDbTeamplate.query(query, Pizza.class);	
		
		
		return history.items().stream().toList();
		
	}
	
	public static ResponseEntity<Pizza> salvarPizza(@RequestBody Pizza pizza, DynamoDbTemplate dynamoDbTeamplate) {
		Pizza p = Pizza.addID(pizza);
		dynamoDbTeamplate.save(p);
		return ResponseEntity.ok().body(p);
	}
}
