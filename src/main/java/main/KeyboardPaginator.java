package main;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyboardPaginator
{
	private List<KeyboardPage> pages = new ArrayList<>();
	public int size = 0;

	public List<KeyboardRow> get(int index)
	{
		return pages.get(index).markup();
	}

	public KeyboardPaginator first(String... buttons)
	{
		KeyboardPage page = KeyboardPage.builder().has_next().add_buttons(buttons).build();
		pages.add(page);

		return this;
	}

	public KeyboardPaginator then(String... buttons)
	{
		KeyboardPage page = KeyboardPage.builder().has_previous().has_next().add_buttons(buttons).build();
		pages.add(page);

		return this;
	}

	public KeyboardPaginator last(String... buttons)
	{
		KeyboardPage page = KeyboardPage.builder().has_previous().add_buttons(buttons).build();
		pages.add(page);
		size = pages.size();

		return this;
	}
}

class KeyboardPage
{
	List<KeyboardRow> markup;
	boolean has_previous = false;
	boolean has_next = false;
	int rows = 3;
	int max_columns = 3;

	KeyboardPage()
	{
	}

	List<KeyboardRow> markup()
	{
		return markup;
	}

	public static KeyboardPageBuilder builder()
	{
		return new KeyboardPageBuilder();
	}

	static class KeyboardPageBuilder
	{
		KeyboardPage page;
		List<String> buttons;

		KeyboardPageBuilder()
		{
			page = new KeyboardPage();
			buttons = new ArrayList<>();
		}

		KeyboardPage build()
		{
			page.markup = new ArrayList<>();
			boolean new_row = true;
			int current_row = 0;
			int current_column = 0;
			KeyboardRow row = null;

			int max_buttons = page.max_columns * page.rows;
			int buttons_count = buttons.size();
			if (buttons_count > max_buttons)
			{
				System.err.printf("Too many buttons provided for a page. Max: %d, actual: %d, trimmed: %d \n", max_buttons, buttons_count, buttons_count - max_buttons);
				buttons_count = max_buttons;
			}
			for (int i = 0; i < buttons_count; i++)
			{
				String button = buttons.get(i);
				if (current_row >= page.rows)
				{
					throw new Error("Button markup overflow");
				}
				if (new_row)
				{
					row = new KeyboardRow();
					new_row = false;
				}
				row.add(button);
				current_column++;
				if (i == buttons_count - 1 && current_column < page.max_columns)
				{
					page.markup.add(row);
				}
				if (current_column >= page.max_columns)
				{
					current_column = 0;
					current_row++;
					new_row = true;
					page.markup.add(row);
				}
			}


			//for (String button : buttons)
			//{
			//	if (current_row >= page.rows)
			//	{
			//		break;
			//	}
			//	if (new_row)
			//	{
			//		row = new KeyboardRow();
			//		new_row = false;
			//	}
			//	row.add(button);
			//	current_column++;
			//	if (current_column >= page.max_columns)
			//	{
			//		current_column = 0;
			//		current_row++;
			//		new_row = true;
			//		page.markup.add(row);
			//	}
			//}
			row = new KeyboardRow();
			if (page.has_previous)
			{
				//row.add("/previous");
				row.add(BotCommandProcessor.PREVIOUS_BUTTON);
			}
			if (page.has_next)
			{
				//row.add("/next");
				row.add(BotCommandProcessor.NEXT_BUTTON);
			}
			page.markup.add(row);

			return page;
		}

		KeyboardPageBuilder has_previous()
		{
			page.has_previous = true;
			return this;
		}

		KeyboardPageBuilder has_next()
		{
			page.has_next = true;
			return this;
		}

		KeyboardPageBuilder add_buttons(String... buttons)
		{
			this.buttons.addAll(Arrays.stream(buttons).collect(Collectors.toList()));
			return this;
		}
	}
}
