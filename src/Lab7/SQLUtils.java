package Lab7;

import source.ConsoleLineApp;
import source.Location;
import source.Protagonist;

import java.sql.*;
import java.util.*;

public class SQLUtils {

    private final Connection connection;

    public SQLUtils() {
        this.connection = new DBConnection().getConnection();
    }

    public String loadCollection(ConsoleLineApp app){
        try {
            Set<Protagonist> set = app.getCol();
            set.addAll(parseResultSet(Objects.requireNonNull(getAllItems())));
            return "Коллекция была загружена успешно";
        } catch (NullPointerException e){
            return "Произошла ошибка во время загрузки коллекции";
        }
    }
    public String saveCollection( Set<? extends Protagonist> set){
        try {
            createTableProtagonists();
            connection.createStatement().executeUpdate("delete from lab.protagonists.protagonists;");
            for (Protagonist pr: set) {
                insertQuery(pr);
            }
            return "Коллекция была сохранена успешно";
        } catch (SQLException ignored) {}
        return "Во время сохранения коллекции произошла ошибка";
    }

    private ResultSet getAllItems(){
        try {
            createTableProtagonists();
            return connection.createStatement().executeQuery("select * from lab.protagonists.protagonists;");
        } catch (SQLException e) {
            return null;
        }
    }

    public ResultSet getUsersTable(){
        try{
            createTableAccounts();
            return connection.createStatement().executeQuery("select * from lab.accounts.accounts;");
        } catch (SQLException e){
            return null;
        }
    }

    private void insertQuery(Protagonist pr) {
        try {
            PreparedStatement statement = connection.prepareStatement("insert into lab.protagonists.protagonists values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
            if (pr.getLocation() != null) {
                Location l = pr.getLocation();
                statement.setString(12, l.getX()+","+l.getY()+","+l.getZ());
            } else {
                statement.setNull(12, Types.VARCHAR);
            }
            statement.setString(13,pr.getOwner());
            statement.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private Set<Protagonist> parseResultSet(ResultSet rs){
        Set<Protagonist> set = new HashSet<>();
        try {
            while(rs.next()){
                Location location = null;
                if(rs.getString(12) != null){
                    String[] array = rs.getString(12).split(",");
                    try {
                        location = new Location(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
                        location = null;
                    }
                }
                set.add(new Protagonist(
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
                        location,
                        rs.getString(13)
                ));
            }
        } catch (Exception ignored) {}
        return set;
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
            connection.createStatement().executeUpdate( "create table if not exists lab.protagonists.protagonists(" +
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
                    "localDateTime timestamptz," +
                    "x int," +
                    "y int," +
                    "z int," +
                    "owner text" +
                    ");");

        } catch (SQLException ignored) {}
    }

    public void createTableAccounts() {
        try{
            connection.createStatement().executeUpdate("create table if not exists lab.accounts.accounts(" +
                    "login text," +
                    "password text);");
        } catch (SQLException ignored){
        }
    }

    public void addNewAccount(String login, String password){
        try {
            PreparedStatement st = connection.prepareStatement("insert into lab.accounts.accounts values (?, ?)");
            st.setString(1, login);
            st.setString(2, Registration.sha1Coding(password));
            st.executeUpdate();
        } catch (SQLException ignored) {}
    }

}