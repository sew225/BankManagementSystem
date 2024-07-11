import java.sql.*;
import java.util.*;

public class Capacity {
    static final String URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Scanner scan = new Scanner(System.in);
        do {
            try {
                System.out.print("Enter Oracle userID: ");
                String user = scan.nextLine();
                System.out.print("Enter Oracle password: ");
                String pass = scan.nextLine();
                conn = DriverManager.getConnection(URL, user, pass);
                System.out.println("connected...");
                System.out.println("Welcome to the Bank");
                System.out.println("Select one of the following operations:");
                System.out.println("1. Bank Management \n2. Account deposit/withdrawal \n3. Payment on a loan or credit card \n4. Opening of a new account \n5. Obtaining a new or replacement credit or debit card \n6. Take out a new loan \n7. Purchases using a card \n8. Exit.");        


                String[] validSemesters = { "Spring", "Winter", "Fall", "Summer" };
                //must be first letter capitalized
                ps = conn.prepareStatement("SELECT DISTINCT year FROM section");
                //gets all valid years from section table
                rs = ps.executeQuery();
                List<String> validYears = new ArrayList<>();
                    while (rs.next()) {
                    String year = rs.getString("year");
                    validYears.add(year);
                }    

                while (true) {
                    System.out.println("Input data on the section whose classroom capacity you wish to check:");
                    System.out.println("Year(yyyy) or 0 to exit");


                    String year = scan.nextLine();
                    if (year.equals("0")) {
                        break;
                    }
                    boolean validYear = false;
                    if (!year.matches("\\d{4}")) {
                        System.out.println("Invalid year format. Please enter a 4-digit year.");
                        continue;
                    }
                    for(String valid : validYears){
                        if(valid.equals(year)){
                            validYear = true;
                                break;
                        }
                    }
                    if(!validYear){
                        System.out.println("Year not in database");
                        continue;
                    }


                    System.out.println("Semester (Spring, Winter, Fall, or Summer)");
                    String semester = scan.nextLine();

                    boolean validSemester = false;
                    for (String valid : validSemesters) {
                        if (valid.equals(semester)) {
                            validSemester = true;
                            break;
                        }
                    }
                    if (!validSemester) {
                        System.out.println(
                                "Invalid semester. Please enter one of the following: Spring, Winter, Fall, or Summer.");
                        continue;
                    }

                    System.out.println("Input course ID as a 3-digit integer: ");
                    String courseId = scan.nextLine();

                    if (!courseId.matches("\\d{3}")) {
                        System.out.println("Invalid course ID. Please enter a 3-digit integer.");
                        continue;
                    }

                    System.out.println("Input section ID as integer:");
                    String secId = scan.nextLine();
                    if (!secId.matches("\\d")) {
                        System.out.println("Invalid section ID. Please enter a 1 integer.");
                        continue;
                    }
                    ps = conn.prepareStatement(
                            "SELECT C.capacity, COUNT(T.ID) AS enrollment " +
                                    "FROM classroom C, section S, takes T " +
                                    "WHERE S.year = ? AND S.semester = ? AND S.course_id = ? AND S.sec_id = ? " +
                                    "AND C.building = S.building AND C.room_number = S.room_number " +
                                    "AND T.year = S.year AND T.semester = S.semester AND T.course_id = S.course_id AND T.sec_id = S.sec_id "
                                    +
                                    "GROUP BY C.capacity");

                    ps.setString(1, year);
                    ps.setString(2, semester);
                    ps.setString(3, courseId);
                    ps.setString(4, secId);

                    rs = ps.executeQuery();
                    System.out.println(ps);
                    boolean found = false;
                    while(rs.next()) {
                        found = true;
                        int capacity = rs.getInt("capacity");
                        int enrollment = rs.getInt("enrollment");
                        System.out.println("Classroom Capacity: " + capacity);
                        System.out.println("Section Enrollment: " + enrollment);
                        if (capacity > enrollment) {
                            int left = capacity - enrollment;
                            System.out.println("There are " + left + " seats open");
                        } else {
                            System.out.println("Class is at capacity");
                        }
                    } 
                    if(!found) {
                        System.out.println("Section not found");
                    }
                    break;

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } while (conn == null);
        scan.close();
    }

}