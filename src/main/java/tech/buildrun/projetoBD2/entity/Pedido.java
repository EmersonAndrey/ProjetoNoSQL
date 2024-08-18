package tech.buildrun.projetoBD2.entity;

import java.util.UUID;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Pedido {

	private String nomeCliente;
	private UUID pedidoId;
	private Double valor;
	private Pizza pizza;
	
	
	public static Pedido addPedido(Pedido pedido, Pizza pizza) {
		Pedido entity = new Pedido();
		
		entity.setNomeCliente(pedido.getNomeCliente());
		entity.setPedidoId(UUID.randomUUID());
		entity.setValor(pizza.getPreco() * 1.10);
		entity.setPizza(pizza);
		
		return entity;
	}
	
	
	@DynamoDbPartitionKey
	@DynamoDbAttribute("nomeCliente")
	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	@DynamoDbSortKey
	@DynamoDbAttribute("pedidoId")
	public UUID getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(UUID pedidoId) {
		this.pedidoId = pedidoId;
	}

	@DynamoDbAttribute("valor")
	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@DynamoDbAttribute("pizza")
	public Pizza getPizza() {
		return pizza;
	}

	public void setPizza(Pizza pizza) {
		this.pizza = pizza;
	}
	
}
