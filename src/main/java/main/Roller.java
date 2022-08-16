package main;

import java.util.Random;

public class 	Roller<T>
{
	T[] items;
	int[] weights;
	Random random;

	// private
	int sws;

	public Roller(T[] items, int[] weights, Random random)
	{
		if (items.length != weights.length)
		{
			throw new IllegalArgumentException(String.format("items length is %d, weights length is %d, must be equal", items.length, weights.length));
		}

		if (random == null)
		{
			throw new NullPointerException("random is null");
		}

		this.items = items;
		this.weights = weights;
		this.random = random;

		sws = 0;
		for (int weight : weights)
		{
			sws += weight;
		}
	}

	public T roll()
	{
		return get(random.nextInt(sws) + 1);
	}

	T get(int id)
	{
		if (id < 1 || id > sws)
		{
			throw new IndexOutOfBoundsException("Roller id is out of bounds");
		}

		int pos = 0;
		while (true)
		{
			if ((id -= weights[pos]) > 0)
				pos++;
			else
				break;
		}

		return items[pos];
	}
}
