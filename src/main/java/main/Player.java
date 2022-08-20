package main;

public class Player
{
	private final long id;
	private final Inventory inventory;
	public int balance;
	public long last_fia;
	public long last_pockets;
	private String username;
	private int level;
	private int xp;
	private State state;

	// < commands args
	public Player payment_acceptor = null;
	public Integer to_place_item = null;
	// > commands args


	public Player(long id)
	{
		this(id, 0, 1, "player" + id, 0, State.awaitingNickname, new Inventory(), 0L, 0L);
	}

	public Player(long id, int xp, int level, String username, int balance, State state, Inventory inventory, long last_fia, long last_pockets)
	{
		this.id = id;
		this.username = username;
		this.balance = balance;
		this.state = state;
		this.inventory = inventory;
		this.last_fia = last_fia;
		this.last_pockets = last_pockets;
		this.xp = xp;
		this.level = level;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public long getId()
	{
		return id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Inventory getInventory()
	{
		return inventory;
	}

	public int getMoney()
	{
		return balance;
	}

	public void levelUp()
	{
		level++;
		xp %= 10;

		// back-notify the owner
	}

	public void addXp(int xp)
	{
		this.xp += xp;
		if (this.xp > 10)
			levelUp();
	}

	public int getLevel()
	{
		return level;
	}

	public int getXp()
	{
		return xp;
	}

	public enum State
	{
		awaitingNickname,
		awaitingSellArguments,
		awaitingCommands,
		awaitingChangeNickname,
		coinDash,
		shopPlaceGood_awaitingID,
		shopPlaceGood_awaitingCost,
		payAwaitingNickname,
		payAwaitingAmount,
		shopBuy
	}
}
