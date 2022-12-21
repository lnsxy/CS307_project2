
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.sql.*;

public class fuck {
    private static Connection         con = null;
    //private static PreparedStatement[]  stmt = new PreparedStatement[10];
    private static boolean            verbose = false;

    private static String host="localhost";
    private static String dbname="postgres";
    private static String user="postgres";
    private static String pwd="kali";
    private static LogInfo root=new LogInfo("root",LogInfo.StaffType.SustcManager,"kali");
    fuck(String database, String root, String pass){//host?
        dbname=database;
        user=root;
        pwd=pass;
        try{
            openDB();

        }catch (Exception e){

        }


    }
    private static ResultSet sqlQuery (String s) throws SQLException {
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery(s);
        rs.next();
        return rs;
    }

    private static ResultSet sqlQuery(String s,String...parts) throws SQLException{
        PreparedStatement stmt=con.prepareStatement(s);
        for(int i=1;i<=parts.length;i++){
            stmt.setString(i,parts[i-1]);
        }
        ResultSet rs=stmt.executeQuery();
        rs.next();
        return rs;
    }
    private static void sqlUpdate(String s,String...parts) throws  SQLException{
        PreparedStatement stmt=con.prepareStatement(s);
        for(int i=1;i<=parts.length;i++){
            stmt.setString(i,parts[i-1]);
        }
        stmt.executeUpdate();
    }
    private static void openDB() {//get con//don't mind stmt, too much that I have to rewrite it
        try {
            Class.forName("org.postgresql.Driver");
        } catch(Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + host + "/" + dbname;
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pwd);
        try {
            con = DriverManager.getConnection(url, props);
            con.setAutoCommit(false);
            if (verbose) {
                System.out.println("Successfully connected to the database "
                        + dbname + " as " + user);

            }
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        openDB();

        System.out.println("fuck!");
        System.out.println(getStaffInfo(root,"Hua Hang"));

    }

    public static int getCompanyCount(LogInfo log){
        try{
            ResultSet rs=sqlQuery("select count(*) from company");
            return rs.getInt(1);
        }catch (SQLException e){
            return -1;
        }
    }

    public static int getCityCount(LogInfo log){
        try{
            ResultSet rs=sqlQuery("select count(*) from city");
            return rs.getInt(1);
        }catch (SQLException e){
            return -1;
        }
    }

    public static int getCourierCount(LogInfo log){
        try{
            ResultSet rs=sqlQuery("select count(*) from Courier");
            return rs.getInt(1);
        }catch (SQLException e){
            return -1;
        }
    }

    public static int getShipCount(LogInfo log){
        try{
            ResultSet rs=sqlQuery("select count(*) from ship");
            return rs.getInt(1);
        }catch (SQLException e){
            return -1;
        }
    }

    public static ShipInfo getShipInfo(LogInfo log, String name){//
        //String name, String owner, boolean sailing
        String owner;
        boolean sailing;
        try{
            ResultSet rs=sqlQuery("select * from ship where name =?",name);
            owner=rs.getString(2);

            rs=sqlQuery("select count(*) from item where ship=? and state=?",name,"Shipping");
            sailing= rs.getInt(1) > 0;

            return new ShipInfo(name,owner,sailing);
        }catch (SQLException e){
            return null;
        }
    }

    public static StaffInfo getStaffInfo(LogInfo log, String name){
        //String name, StaffType type, String password
        //LogInfo basicInfo, String company, String city, boolean isFemale, int age, String phoneNumber
        LogInfo basicInfo;
        String company,city,phoneNumber,password,type,gender;
        int age;
        boolean isFemale;
        try{
            ResultSet rs=sqlQuery("select * from tmp_staff where name=?",name);
            type=rs.getString(2);
            company=rs.getString(3);
            city=rs.getString(4);
            gender=rs.getString(5);
            age=rs.getInt(6);
            phoneNumber=rs.getString(7);
            password=rs.getString(8);

            isFemale= !gender.equals("male");

            switch (type) {
                case "Courier":
                    basicInfo = new LogInfo(name, LogInfo.StaffType.Courier, password);
                    break;
                case "Company Manager":
                    basicInfo = new LogInfo(name, LogInfo.StaffType.CompanyManager, password);
                    break;
                case "Seaport Officer":
                    basicInfo = new LogInfo(name, LogInfo.StaffType.SeaportOfficer, password);
                    break;
                case "SUSTC Department Manager":
                    basicInfo = new LogInfo(name, LogInfo.StaffType.SustcManager, password);
                    break;
                default:
                    return null;
            }

            return new StaffInfo(basicInfo,company,city,isFemale,age,phoneNumber);
        }catch (SQLException e){
            return null;
        }
    }

    boolean setItemState(LogInfo log, String name, ItemState s){
        String courierName;
        String state;
        ResultSet rs;

        try{
            rs=sqlQuery("select * from item where item=?",name);
            state=rs.getString(4);
        }catch (SQLException e){
            return false;
        }

        try{//retrieval check
            rs=sqlQuery("select * from retrieval where item=?",name);
            courierName=rs.getString(2);
            if(courierName.equals(log.name())){
                if(state.equals("Picking-up")&&s.equals(ItemState.ToExportTransporting)){
                    sqlUpdate("update item set state='To-Export Transporting' where name=?",name);
                    return true;
                }
                if(state.equals("To-Export Transporting")&&s.equals(ItemState.ExportChecking)){
                    sqlUpdate("update item set state='Export Checking' where name=?",name);
                    return true;
                }
            }
        }catch (SQLException e){

        }

        try{//delivery check
            rs=sqlQuery("select count(*) from delivery where item=?",name);
            if(rs.getInt(1)==0){//delivery courier is empty
                if(state.equals("From-Import Transporting")){
                    rs=sqlQuery("select * from courier where name=?", log.name());
                    String workingCity=rs.getString(7);


                    sqlUpdate("update delivery set courier=? where name=?",log.name(),name);
                }
            }
            else{

            }
        }catch (SQLException e){

        }

        return false;
    }


}






