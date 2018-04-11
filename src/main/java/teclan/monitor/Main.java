package teclan.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teclan.monitor.mysql.MysqlDatabase;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		MysqlDatabase.openDatabase();

		MysqlDatabase.closeDatabase();

//		// 内存使用率
//
//		try {
//			List<SigarInfoEntity> memoryInfos = SigarUtils.getMemoryInfos();
//			for (SigarInfoEntity info : memoryInfos) {
//				LOGGER.info("{}", info);
//			}
//
//			LOGGER.info("\n\n=============================\n\n");
//
//			List<SigarInfoEntity> cpuInfos = SigarUtils.getCpuInfos();
//			for (SigarInfoEntity info : cpuInfos) {
//				LOGGER.info("{}", info);
//			}
//
//		} catch (SigarException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
		


	}

}
