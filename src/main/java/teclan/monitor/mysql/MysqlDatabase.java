package teclan.monitor.mysql;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * mysql 数据库工具类，含读取配置文件初始化数据库连接，需在 config/ 目录下添加
 * 
 * 配置文件 application.conf，mysql的配置项如下:</br>
 * </br>
 * 
 * &nbsp;config {</br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;mysql {</br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;driver="com.mysql.jdbc.Driver"</br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;url="jdbc:mysql://10.0.0.222:3306/jie_jing_ji?useUnicode=true&characterEncoding=UTF-8"</br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user="root" </br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password="root"</br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;connectionName="default"</br>
 * </br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}</br>
 * }
 * 
 * @author dev
 *
 */
public class MysqlDatabase {
	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDatabase.class);

	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	private static String connectionName;

	private static Map<String, HikariDataSource> DATA_SOURCES = new HashMap<String, HikariDataSource>();

	private static DB db;

	static {
		// 加载配置文件
		File file = new File("config/application.conf");
		Config root = ConfigFactory.parseFile(file);
		Config config = root.getConfig("config.mysql");

		driver = config.getString("driver");
		url = config.getString("url");
		user = config.getString("user");
		password = config.getString("password");
		connectionName = config.getString("connectionName");

		DATA_SOURCES.put(connectionName, generateHikariDataSource());

	}

	private static HikariDataSource generateHikariDataSource() {

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(password);

		return new HikariDataSource(config);
	}

	public static boolean isReachable() {

		boolean reachable = openDatabase();
		closeDatabase();

		if (reachable) {

		}
		return reachable;

	}

	public static boolean openDatabase() {
		try {
			if (!getDb().hasConnection()) {
				getDb().open(DATA_SOURCES.get(getConnectionName()));
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}
	}

	public static boolean closeDatabase() {
		try {
			if (getDb().hasConnection()) {
				getDb().close();
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	public static DB getDb() {
		if (db == null) {
			db = new DB(connectionName);
		}
		return db;
	}

	public static String getConnectionName() {
		return connectionName;
	}
}
