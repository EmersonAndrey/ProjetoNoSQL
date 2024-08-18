package tech.buildrun.projetoBD2.entity;

import java.util.UUID;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Pizza {

	private String sabor;
	private UUID pizzaId;
	private Double preco;
	private String tamanho;
	
	
	public static Pizza addID(Pizza pizza) {
		Pizza entity = new Pizza();
		
		entity.setPreco(pizza.getPreco());
		entity.setTamanho(pizza.getTamanho());
		entity.setSabor(pizza.getSabor());
		entity.setPizzaId(UUID.randomUUID());
		
		return entity;
	}
	
	
	@DynamoDbSortKey
	@DynamoDbAttribute("pizzaId")
	public UUID getPizzaId() {
		return pizzaId;
	}

	public void setPizzaId(UUID pizzaId) {
		this.pizzaId = pizzaId;
	}
	
	@DynamoDbAttribute("tamanho")
	public String getTamanho() {
		return tamanho;
	}
	
	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}
	
	@DynamoDbAttribute("preco")
	public double getPreco() {
		return preco;
	}
	
	public void setPreco(double preco) {
		this.preco = preco;
	}
	
	@DynamoDbPartitionKey
	@DynamoDbAttribute("sabor")
	public String getSabor() {
		return sabor;
	}
	
	public void setSabor(String sabor) {
		this.sabor = sabor;
	}
	
}
