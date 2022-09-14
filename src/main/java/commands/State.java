package commands;

public abstract class State
{
	public State previous;
	public BaseState base;
	public String hint;

	public State()
	{}

	//public State(State previous, State base, String hint)
	//{
	//	this.previous = previous;
	//	this.base = base;
	//	this.hint = hint;
	//}

	abstract public void process(String arg);
}
