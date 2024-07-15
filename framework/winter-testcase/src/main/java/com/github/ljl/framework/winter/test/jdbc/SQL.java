package com.github.ljl.framework.winter.test.jdbc;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 13:22
 **/

public class SQL {
    // @Value("${winter.datasource.url}")
    public static String url = "jdbc:mysql://localhost:3306/winter?useSSL=false&autoReconnect=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

    // @Value("${winter.datasource.username}")
    public static String username = "root";

    // @Value("${winter.datasource.password}")
    public static String password = "root";

    public static String createUserTableSQL = "CREATE TABLE IF NOT EXISTS User (\n" +
            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    username VARCHAR(50) NOT NULL,\n" +
            "    email VARCHAR(100) NOT NULL,\n" +
            "    age INT\n" +
            ");";
    public static String insertUserSQL = "INSERT INTO User (username, email, age) VALUES\n" +
            "('Alice', 'alice@example.com', 25),\n" +
            "('Bob', 'bob@example.com', 30),\n" +
            "('Charlie', 'charlie@example.com', 28),\n" +
            "('David', 'david@example.com', 32),\n" +
            "('Eve', 'eve@example.com', 27),\n" +
            "('Frank', 'frank@example.com', 29),\n" +
            "('Grace', 'grace@example.com', 31),\n" +
            "('Hannah', 'hannah@example.com', 26),\n" +
            "('Ivy', 'ivy@example.com', 33),\n" +
            "('Jack', 'jack@example.com', 29);";

    public static String updateUserSQL = "UPDATE User SET age = 26 WHERE username = 'Alice';";

    public static String selectUserSQL = "SELECT * FROM User WHERE username = 'Alice';";

    public static String deleteUserSQL = "DELETE FROM User WHERE age > 30;";

    public static String selectAllSQL = "SELECT * FROM User";

    public static String selectEmailSQL = "SELECT email FROM User";

    public static String selectSingleAge = "SELECT AGE FROM User where id = 10";

    public static String selectSingleAgeFail = "SELECT AGE FROM USER WHERE 0=1";

    public static String dropTableSQL = "DROP TABLE IF EXISTS User";
}
