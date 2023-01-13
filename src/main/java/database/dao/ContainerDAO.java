package database.dao;


import main.Container;
import main.Item;
import main.Player;

import java.sql.*;

public class ContainerDAO {
    private Connection connection;
    private CachedItemDAO itemDAO;

    public ContainerDAO(Connection connection) {
        this.connection = connection;
        itemDAO = new CachedItemDAO(connection);
    }

    public ContainerDAO() {
        //this(DriverManager.getConnection("jdbc:sqlite:data.db"));
    }

    public void put(long player_id, Container container)
    {
        for (int i = 0; i < container.getContainerSize(); i++)
        {
            Item item = container.getItem(i);
            try
            {
                PreparedStatement ps = connection.prepareStatement("insert into container (player_id, item_id) values (?, ?);");
                ps.setLong(1, player_id);
                ps.setLong(2, item.getId());
                ps.execute();
            }
            catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        }
    }

    public void putItem(long player_id, long item_id)
    {
        String query = "insert into container (player_id, item_id) values (?, ?)";
        try
        {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, player_id);
            ps.setLong(2, item_id);
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public Container get(long player_id)
    {
        Container container = new Container();
        try
        {
            PreparedStatement ps = connection.prepareStatement("select * from container where player_id = ?;");
            ps.setLong(1, player_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                long item_id = rs.getLong("item_id");
                container.putItem(itemDAO.get_by_id(item_id));
            }
            rs.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return container;
    }

    public int size()
    {
        String query = "select count(*) from container;";
        try
        {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("SQL Exception", e);
        }
    }


    public void delete(long player_id, long item_id, int count)
    {
        String query = "delete from container where id in (select id from container where player_id = ? and item_id = ? limit ?);";
        try
        {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, player_id);
            ps.setLong(2, item_id);
            ps.setInt(3, count);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void delete(Player player, long item_id, int count)
    {
        String query = "delete from container where id in (select id from container where player_id = ? and item_id = ? limit ?);";
        try
        {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, player.getId());
            ps.setLong(2, item_id);
            ps.setInt(3, count);
            int res = ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


}
