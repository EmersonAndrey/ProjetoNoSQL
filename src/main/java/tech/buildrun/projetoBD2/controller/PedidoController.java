package tech.buildrun.projetoBD2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import tech.buildrun.projetoBD2.entity.Pedido;
import tech.buildrun.projetoBD2.entity.Pizza;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	private DynamoDbTemplate dynamoDbTeamplate;
	
	@Autowired
	private PizzaController pizzaController;
	
	
	public PedidoController(PizzaController pizzaController) {
		this.pizzaController = pizzaController;
	}

	@PostMapping("/save/{sabor}/{pizzaId}")
	public ResponseEntity<Pedido> save(@RequestBody Pedido pedido,@PathVariable("sabor") String sabor,@PathVariable("pizzaId") String pizzaId) {
		Pizza pizza = pizzaController.findByFlavor(sabor, pizzaId).getBody();
		
		if (pizza == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    
	  
	    if (pedido.getNomeCliente() == null || pedido.getNomeCliente().isEmpty()) {
	        return ResponseEntity.badRequest().build();
	    }
		
		Pedido entity = Pedido.addPedido(pedido,pizza);
		
		dynamoDbTeamplate.save(entity);
		
		return ResponseEntity.ok().body(entity);
	}
	

	@GetMapping("/findByPedido/{nomeCliente}/{pedidoId}")
	public ResponseEntity<Pedido> findByPedido(@PathVariable("nomeCliente") String nomeCliente,@PathVariable("pedidoId") String pedidoId) {
		var entity = dynamoDbTeamplate.load(
				Key.builder()
				.partitionValue(nomeCliente)
				.sortValue(pedidoId)
				.build(), Pedido.class);
		
		return entity == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(entity);
	}
	
	
	@PutMapping("/update/{nomeCliente}/{pedidoId}")
	public ResponseEntity<Pedido> updatePedido(@PathVariable("nomeCliente") String nomeCliente,@PathVariable("pedidoId") String pedidoId,@RequestBody Pizza pizzaChega) {
		
		Pedido pedidoExistente = dynamoDbTeamplate.load(
				Key.builder()
				.partitionValue(nomeCliente)
				.sortValue(pedidoId).build(),
				Pedido.class);
		
		if(pedidoExistente == null) {
			return ResponseEntity.notFound().build();
			
		}
		
		List<Pizza> pizzaList = PizzaController.getAll(pizzaChega.getSabor(), dynamoDbTeamplate);
		
		for (Pizza pizza : pizzaList) {
			if(pizzaChega.getTamanho().equals(pizza.getTamanho())) {
				pedidoExistente.setPizza(pizza);
				pedidoExistente.getPizza().setPreco(pizzaChega.getPreco());
				pedidoExistente.setValor(pizzaChega.getPreco() * 1.10);
				
				dynamoDbTeamplate.save(pedidoExistente);
				return ResponseEntity.ok(pedidoExistente);
			}
		}
		Pizza pizzaNova = PizzaController.salvarPizza(pedidoExistente.getPizza(), dynamoDbTeamplate).getBody();
		this.delete(nomeCliente, pedidoId);
		Pedido pedidoAtualizado = this.save(pedidoExistente, pizzaNova.getSabor() , pizzaNova.getPizzaId().toString()).getBody();
		
		dynamoDbTeamplate.save(pedidoAtualizado);
		return ResponseEntity.ok(pedidoAtualizado);
		
	}
	
	
	@DeleteMapping("delete/{nomeCliente}/{pedidoId}")
	public void delete(@PathVariable("nomeCliente") String nomeCliente, @PathVariable("pedidoId") String pedidoId) {
		var entity = dynamoDbTeamplate.load(
				Key.builder()
				.partitionValue(nomeCliente)
				.sortValue(pedidoId).build(),
				Pedido.class);
		
		if(entity != null) {
			dynamoDbTeamplate.delete(entity);
		}
	}
	
	
	
}
