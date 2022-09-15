package commands;

import java.util.List;

public abstract class State
{
	public State previous;
	public BaseState base;
	public String hint;

	public State()
	{}

	abstract public void process(String arg);
}
