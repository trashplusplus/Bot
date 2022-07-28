package ability;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Cooldown
{
	private long cooldown;
	private Runnable onCooldown;
	private boolean running;

	private static final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	private ScheduledFuture sf;

	public Cooldown(long cooldown, Runnable onCooldown)
	{
		this.cooldown = cooldown;
		this.onCooldown = onCooldown;
		running = false;
	}

	public Cooldown(long cooldown)
	{
		this(cooldown, null);
	}

	public long getCooldown()
	{
		return cooldown;
	}

	public void setCooldown(long cooldown)
	{
		this.cooldown = cooldown;
	}

	public void setOnCooldown(Runnable onCooldown)
	{
		this.onCooldown = onCooldown;
	}

	public void startCooldown()
	{
		if (!running)
		{
			sf = stpe.schedule(new InnerOnCooldown(this), cooldown, TimeUnit.SECONDS);
			running = true;
		}
	}

	public long cdTimerLeft()
	{
		return running ? sf.getDelay(TimeUnit.SECONDS) : 0L;
	}

	public boolean isAvailable()
	{
		return !running;
	}

	private class InnerOnCooldown implements Runnable
	{
		Cooldown cooldown;

		InnerOnCooldown(Cooldown cooldown)
		{
			this.cooldown = cooldown;
		}

		@Override
		public void run()
		{
			cooldown.running = false;
			if (cooldown.onCooldown != null)
			{
				cooldown.onCooldown.run();
			}
		}
	}
}
