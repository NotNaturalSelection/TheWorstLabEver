package Lab7;

import source.Location;
import source.Protagonist;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLUtils {

    public static void main(String[] args) {
        DBConnection dbc = new DBConnection();
        SQLUtils sqlUtils = new SQLUtils(dbc);
        System.out.println(sqlUtils.parseAccountsResultSet(sqlUtils.getUsersTable()));
    }

    private Connection connection;

    public SQLUtils(DBConnection dbc) {
        this.connection = dbc.getConnection();
    }

    public boolean saveCollection( List<Protagonist> list){
        boolean result = false;
        try {
            connection.createStatement().executeUpdate("delete from protagonists;");
            for (Protagonist pr: list) {
                insertQuery(pr);
            }
            result = true;
        } catch (SQLException ignored) {}
        return result;
    }

    public ResultSet getAllItems(){
        try {
            return connection.createStatement().executeQuery("select * from protagonists;");
        } catch (SQLException e) {
            return null;
        }
    }

    public ResultSet getUsersTable(){
        try{
            return connection.createStatement().executeQuery("select * from accounts;");
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public void insertQuery(Protagonist pr) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT into protagonists values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, pr.getGender());
            statement.setString(2, pr.getName());
            statement.setFloat(3,(float)pr.getStrength());
            statement.setFloat(4,(float)pr.getAgility());
            statement.setFloat(5,(float)pr.getIntelligence());
            statement.setFloat(6,(float)pr.getLuck());
            statement.setInt(7,pr.getWealth());
            statement.setInt(8,pr.getBallCounter());
            statement.setFloat(9,(float)pr.getLevelOfPain());
            statement.setFloat(10,(float)pr.getDefence());
            statement.setTimestamp(11,Timestamp.valueOf(pr.getLocalDateTime()));
            statement.setInt(12, pr.getLocation().getX());
            statement.setInt(13, pr.getLocation().getY());
            statement.setInt(14, pr.getLocation().getZ());
            statement.setString(15,pr.getOwner());
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public List<Protagonist> parseResultSet(ResultSet rs){
        List<Protagonist> list = new ArrayList<>();
        try {
            while(rs.next()){
                list.add(new Protagonist(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getFloat(3),
                        rs.getFloat(4),
                        rs.getFloat(5),
                        rs.getFloat(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getFloat(9),
                        rs.getFloat(10),
                        rs.getTimestamp(11).toLocalDateTime(),
                        new Location(rs.getInt(12), rs.getInt(13), rs.getInt(14)),
                        rs.getString(15)
                ));
            }
        } catch (SQLException ignored) {}
        return list;
    }

    public Map<String, String> parseAccountsResultSet(ResultSet rs){
        Map<String, String> map = new HashMap<>();
        try{
            while(rs.next()){
                map.put(rs.getString(1), rs.getString(2));
            }
        }catch (SQLException ignored){}
        return map;
    }

    public void createTableProtagonists() {
        try {
            connection.createStatement().executeUpdate( "CREATE TABLE protagonists(" +
                    "gender text," +
                    "name text," +
                    "strength float ," +
                    "agility float ," +
                    "intelligence float ," +
                    "luck float ," +
                    "wealth int," +
                    "ballcounter int ," +
                    "levelOfPain float ," +
                    "defence float," +
                    "localdatetime timestamptz," +
                    "x int," +
                    "y int," +
                    "z int," +
                    "owner text" +
                    ");");

        } catch (SQLException ignored) {}
    }

    public void createTableAccounts() {
        try{
            connection.createStatement().executeUpdate("create table accounts(" +
                    "login text," +
                    "password text);");
        } catch (SQLException ignored){}
    }

    public boolean isTableOfProtagonistsExists() {
        boolean result = false;
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT table_name FROM information_schema.tables  WHERE table_schema='public';");
            while(rs.next()){
                if(rs.getString(1).equals("protagonists")){
                    result = true;
                }
            }
        } catch (SQLException ignored) {}
        return result;
    }

    public boolean isTableOfAccountsExists() {
        boolean result = false;
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT table_name FROM information_schema.tables  WHERE table_schema='public';");
            while(rs.next()){
                if(rs.getString(1).equals("accounts")){
                    result = true;
                }
            }
        } catch (SQLException ignored) {}
        return result;
    }

    public void addNewAccount(String login, String password){
        try {
            PreparedStatement st = connection.prepareStatement("insert into accounts values (?, ?)");
            st.setString(1, login);
            st.setString(2, Registration.sha1Coding(password));
            st.executeUpdate();
//            connection.prepareStatement().executeUpdate("insert into accounts values ("+login+", "+Registration.md5Apache(password)+");");
        } catch (SQLException ignored) {
        }
    }

//    String getStringOfObjectParametersFromResultSet(ResultSet rs) {
//        StringBuilder result = new StringBuilder();
//        try {
//            while (rs.next()) {
//                for (int i = 1; i < 12; i++) {
//                    result.append(rs.getString(i));
//                    result.append(" ");
//                }
//                result.append("\n");
//            }
//        } catch (SQLException e) {
////            e.printStackTrace();
//        }
//        return result.toString();
//    }
}