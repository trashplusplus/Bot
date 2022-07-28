package ability;

import java.util.concurrent.Callable;

public class Ability
{
	Cooldown cooldown;
	Callable action;

	public Ability(Cooldown cooldown, Callable action)
	{
		this.cooldown = cooldown;
		this.action = action;
	}

	public void use()
	{
		if (cooldown.isAvailable())
		{
			try
			{
				action.call();
				cooldown.startCooldown();
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
	}
}
