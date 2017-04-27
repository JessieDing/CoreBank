package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectMySql {
    private String driver;
    private String url;
    private String acct;
    private String pwd;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;

    public ConnectMySql() {
        Properties prop = new Properties();
        InputStream in;
        try {
            in = getClass().getResourceAsStream("/dbproperty.properties");
            prop.load(in);
            driver = prop.getProperty("driver");
            url = prop.getProperty("url");
            acct = prop.getProperty("acct");
            pwd = prop.getProperty("pwd");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, acct, pwd);
            if (connection == null) {
                System.err.println("读取配置文件失败");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ConnectMySql(String driver, String url, String acct, String pwd) {
        super();
        this.driver = driver;
        this.url = url;
        this.acct = acct;
        this.pwd = pwd;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAcct() {
        return acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    /**
     * 建立数据库连接(可连接MySql,Oracle等数据库)
     *
     * @return
     */
    public int connectMysql() {
        try {
            Class.forName(driver);// 设置驱动
            System.out.println("建立数据库连接....");
            connection = DriverManager.getConnection(url, acct, pwd);// 创建数据库连接
            System.out.println("数据库连接成功");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 该方法为关闭数据库连接方法，提供关闭ResultSet(查询)，关闭PreparedStatement(添加，删除，修改)
     * connection关闭数据库连接,resultSet关闭查询,preparedStatement.close()关闭PreparedStatement(添加，删除，修改)
     *
     * @throws Exception
     */
    public void closeConn() throws Exception {
        if (null != resultSet && !resultSet.isClosed()) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != preparedStatement && preparedStatement.isClosed()) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != connection && connection.isClosed()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 该方法为查询数据库内容方法 smt创建一个statement(表单),rs执行查询语句
     *
     * @param strSQL
     * @return
     * @throws Exception
     */
    public ResultSet doQuery(String strSQL) throws Exception {
        statement = connection.createStatement();// 创建一个表单
        resultSet = statement.executeQuery(strSQL);// 执行查询语句
        return resultSet;
    }

    /**
     * 该提供增加，删除，修改功能，执行成功返回大于0的正整数，失败返回0
     * PreparedStatement(接口)可以设置(setString()方法)sql语句中每一个属性值
     * preparedStatement(PreparedStatement)
     *
     * @param sql
     * @return
     */
    public int doUpdate(String sql) {
        int ret = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
            ret = preparedStatement.executeUpdate();// 执行sql语句，并返回结果
            System.out.println("执行了【" + ret + "】条sql命令");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*
    * 插入数据表（subAcct和detail）
    * */
    public int insertIntoDBO(ConnectMySql dbhelper, String SQL1) {
        Connection conn = dbhelper.getConnection();
        try {
            // 插入成功返回结果为1，不成功为0
            int i = dbhelper.doUpdate(SQL1.toString());
            if (i <= 0) {
                System.out.println("数据写入失败");
                return -1;
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return 0;
    }

    /*
* 增加子账户后，写入acct和operation数据表
* */
    public int insertIntoDBO(ConnectMySql dbhelper, String SQL1, String SQL2) {
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);// 将提交事务设为手动提交
            // 插入成功返回结果为1，不成功为0
            int i = dbhelper.doUpdate(SQL1.toString());
            int j = dbhelper.doUpdate(SQL2.toString());
            if (i <= 0) {
                System.out.println("insert t_sub_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    /*
* 插入acct、Operation、subAcct数据表，
* */
    public int insertIntoDBOExceptDetail(ConnectMySql dbhelper, String SQL1, String SQL2, String SQL3) {
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);// 将提交事务设为手动提交
            // 插入成功返回结果为1，不成功为0
            int i = dbhelper.doUpdate(SQL1.toString());
            int j = dbhelper.doUpdate(SQL2.toString());
            int k = dbhelper.doUpdate(SQL3.toString());
            if (i <= 0) {
                System.out.println("insert t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
            if (k <= 0) {
                System.out.println("insert t_sub_acct 表失败");
                return -1;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 主要操作有开户、销户
     * 数据库操作，事务提交传入3句SQL语句，分别插入三个表，同时成功返回结果，一条不成功，其他都不插入
     * 更改-分别插入三个表：
     * t_acct
     * t_acct_operation
     * t_acct_detail
     *
     * @param dbhelper
     * @param SQL1
     * @param SQL2
     * @param SQL3
     * @return
     */

    public int insertIntoDBO(ConnectMySql dbhelper, String SQL1, String SQL2, String SQL3) {
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);// 将提交事务设为手动提交
            // 插入成功返回结果为1，不成功为0
            int i = dbhelper.doUpdate(SQL1.toString());
            int j = dbhelper.doUpdate(SQL2.toString());
            int k = dbhelper.doUpdate(SQL3.toString());
            if (i <= 0) {
                System.out.println("insert t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
            if (k <= 0) {
                System.out.println("insert t_acct_detail 表失败");
                return -1;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 主要操作有开户、销户
     * 数据库操作，事务提交传入4句SQL语句，分别插入四个表，同时成功返回结果，一条不成功，其他都不插入
     * 更改-分别插入四个表：
     * t_acct
     * t_sub_acct
     * t_acct_operation
     * t_acct_detail
     *
     * @param dbhelper
     * @param SQL1
     * @param SQL2
     * @param SQL3
     * @return
     */
    public int insertIntoDBO(ConnectMySql dbhelper, String SQL1, String SQL2, String SQL3, String SQL4) {
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);// 将提交事务设为手动提交
            // 插入成功返回结果为1，不成功为0
            int i = dbhelper.doUpdate(SQL1.toString());
            int j = dbhelper.doUpdate(SQL2.toString());
            int k = dbhelper.doUpdate(SQL3.toString());
            int h = dbhelper.doUpdate(SQL4.toString());
            if (i <= 0) {
                System.out.println("insert t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
            if (k <= 0) {
                System.out.println("insert t_acct_detail 表失败");
                return -1;
            }
            if (h <= 0) {
                System.out.println("insert t_sub_acct 表失败");
                return -1;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 主要操作有存款，取款
     * 数据库操作，事务提交传入2句SQL语句，分别插入2个表，同时成功返回结果，一条不成功，其他都不插入
     *
     * @param dbhelper
     * @param SQL1
     * @param SQL2
     * @param SQL3
     * @return
     */
    public int insertInto(ConnectMySql dbhelper, String SQL1, String SQL2) {
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);// 将提交事务设为手动提交
            // 插入成功返回结果为1，不成功为0
            int i = dbhelper.doUpdate(SQL1.toString());
            int j = dbhelper.doUpdate(SQL2.toString());

            if (i <= 0) {
                System.out.println("insert t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_detail 表失败");
                return -1;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }
}
