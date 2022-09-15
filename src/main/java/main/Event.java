package main;

import java.util.LinkedList;
import java.util.List;

public class Event<T>
{
	private final Object sender;
	private final List<Subscriber<T>> subscribers;

	public Event(Object sender)
	{
		this.sender = sender;
		subscribers = new LinkedList<>();
	}

	public void subscribe(Subscriber<T> subscriber)
	{
		subscribers.add(subscriber);
	}

	public void raise(T arg)
	{
		for (Subscriber<T> subscriber : subscribers)
		{
			subscriber.handle(sender, arg);
		}
	}

	@FunctionalInterface
	public interface Subscriber<T>
	{
		void handle(Object sender, T arg);
	}
}
