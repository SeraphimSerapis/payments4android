package com.paypal.example.android.mpl.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Pizza implements Serializable {
	private static final long	serialVersionUID	= 8389725829329090393L;

	public enum Size {
		Small("4.00"), Medium("5.00"), Large("6.00");

		private String	price;

		private Size(String price) {
			this.price = price;
		}

		public String getPrice() {
			return this.price;
		}
	}

	public enum Topping {
		Tuna("1.00"), Ham("1.00"), Salami("0.50"), Aspergus("1.00"), Shrimps(
				"1.50"), Garlic("0.50"), Mushrooms("0.50"), Spinach("0.50"), Paprika(
				"0.50"), Onions("0.50"), Chilli("0.50"), Pineapple("0.50");

		private String	price;

		private Topping(String price) {
			this.price = price;
		}

		public String getPrice() {
			return this.price;
		}
	}

	public enum Preset {
		TonnoECipolla(new Topping[] {
				Topping.Tuna, Topping.Garlic, Topping.Onions
		}), Diavolo(new Topping[] {
				Topping.Paprika, Topping.Salami, Topping.Chilli
		}), Boscaiola(new Topping[] {
				Topping.Ham, Topping.Mushrooms
		}), Hawaii(new Topping[] {
				Topping.Ham, Topping.Pineapple
		});

		private Topping[]	toppings;

		private Preset(Topping[] toppings) {
			this.toppings = toppings;
		}

		public Topping[] getToppings() {
			return this.toppings;
		}
	}

	private Size			size;
	private List<Topping>	toppings;

	/**
	 * Creates a new instance of {@link Pizza}
	 */
	public Pizza() {
		this.toppings = new ArrayList<Topping>();
	}

	/**
	 * Creates a new instance of {@link Pizza}
	 * 
	 * @param size
	 */
	public Pizza(Size size) {
		this.size = size;
		this.toppings = new ArrayList<Topping>();
	}

	/**
	 * Creates a new instance of {@link Pizza}
	 * 
	 * @param size
	 * @param toppings
	 */
	public Pizza(Size size, List<Topping> toppings) {
		this.size = size;
		this.toppings = toppings;
	}

	/**
	 * @return the size
	 */
	public Size getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	/**
	 * @return the toppings
	 */
	public List<Topping> getToppings() {
		return toppings;
	}

	/**
	 * @param toppings
	 *            the toppings to set
	 */
	public void setToppings(List<Topping> toppings) {
		this.toppings = toppings;
	}

	/**
	 * @param topping
	 *            the topping to add
	 */
	public void addTopping(Topping topping) {
		if (!toppings.contains(topping)) {
			toppings.add(topping);
		}
	}

	/**
	 * @param topping
	 *            the topping to remove
	 */
	public void removeTopping(Topping topping) {
		if (toppings.contains(topping)) {
			toppings.remove(topping);
		}
	}

	/**
	 * Calculates the {@link Pizza}'s total price
	 * 
	 * @return the {@link Pizza}'s price
	 */
	public String getPrice() {
		BigDecimal totalPrice = new BigDecimal(0);
		totalPrice = totalPrice.add(new BigDecimal(size.price));

		for (Topping topping : toppings) {
			totalPrice = totalPrice.add(new BigDecimal(topping.price));
		}

		return totalPrice.toString();
	}

	/**
	 * Creates a description of the {@link Size} and differnt {@link Topping}s
	 * 
	 * @return the {@link Pizza}'s description
	 */
	public String getDescription() {
		final StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append(size.toString());

		if (toppings != null && toppings.size() > 0) {
			descriptionBuilder.append(", ");

			int nextToLast = toppings.size() - 1;
			if (toppings.size() > 1) {
				for (int i = 0; i < nextToLast; i++) {
					descriptionBuilder.append(toppings.get(i).toString())
							.append(", ");
				}
			}
			descriptionBuilder.append(toppings.get(nextToLast).toString());
		}

		return descriptionBuilder.toString();
	}
}
