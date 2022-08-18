package database.dao;

import main.ShopItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO {
    private Connection connection;
    ItemDAO item;

    public ShopDAO(Connection connection){
        this.connection = connection;
        item = new ItemDAO(this.connection);
    }

    public void put(ShopItem shopItem){
        try{

            PreparedStatement ps = connection.prepareStatement("insert into shop(name, cost, sellerName) values (?, ?, ?);");
            ps.setString(1, shopItem.getItem().getTitle());
            ps.setInt(2, shopItem.getCost());
            ps.setString(3, shopItem.getSeller());
            ps.execute();
        }catch(SQLException e){
            System.err.println(e.getErrorCode());
            e.printStackTrace();
            throw new RuntimeException("SQL Exception in ShopDAO");
        }
    }

    public List<ShopItem> getBySellerName(String sellerName) {
        List<ShopItem> result = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("select * from shop where sellerName = ?;");
            ps.setString(1, sellerName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(form(rs));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL Exception in ShopDAO", e);
        }
    }

    public List<ShopItem> getAll(){
            List<ShopItem> result = new ArrayList<>();
            try{
                PreparedStatement ps = connection.prepareStatement("select * from shop");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    result.add(form(rs));
                }
                return result;
            }catch (SQLException e){
                e.printStackTrace();
                throw new RuntimeException("SQL Exception in ShopDAO", e);
            }
    }

    public ShopItem getByID(int index){
        ShopItem s = null;
        try{
            PreparedStatement ps = connection.prepareStatement("select * from shop where id = ?");
            ps.setInt(1, index);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                s = form(rs);
            }else{
                throw new IndexOutOfBoundsException();
            }
          
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("SQL Exception in ShopDAO", e);
        }
        return s;
    }
    /*
    public void update(ShopItem shopItem){
        try {
            PreparedStatement ps = connection.prepareStatement("update shop set cost = ?, where id = ?");
            ps.setString(1, shopItem.getItem().getTitle());
            ps.setInt(2, shopItem.getCost());
            ps.setString(3, shopItem.getSeller());


        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    */
    public void delete(int id){
        try
        {
            PreparedStatement ps = connection.prepareStatement("delete from shop where id = ?;");
            ps.setInt(1, id);
            ps.execute();
        }
        catch (SQLException e)
        {
            System.err.println(e.getErrorCode());
            e.printStackTrace();
            throw new RuntimeException("SQL Exception in ShopDAO");
        }
    }

    private ShopItem form(ResultSet rs) throws SQLException{

        int id = rs.getInt(1);
        String name = rs.getString(2);
        int cost = rs.getInt(3);
        String sellerName = rs.getString(4);
        return new ShopItem(item.getByName(name), cost, sellerName, id);
    }
}
