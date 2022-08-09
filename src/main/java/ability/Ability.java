package ability;

import java.util.concurrent.Callable;

public class Ability<T>
{
	Cooldown cooldown;
	Callable<T> action;

	public Ability(Cooldown cooldown, Callable<T> action)
	{
		this.cooldown = cooldown;
		this.action = action;
	}

	public T use()
	{
		if (cooldown.isAvailable())
		{
			try
			{
				cooldown.startCooldown();
				return action.call();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("Cant use ability");
			}
		}
		else
		{
			System.out.printf("%d", cooldown.cdTimerLeft());
		}
		return null;
	}

	public boolean isUsable()
	{
		return cooldown.isAvailable();
	}

	public long getCDTimer()
	{
		return cooldown.cdTimerLeft();
	}
}
