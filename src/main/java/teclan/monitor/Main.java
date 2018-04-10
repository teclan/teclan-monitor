package teclan.monitor;

import java.util.List;

import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teclan.monitor.sigar.SigarInfoEntity;
import teclan.monitor.sigar.SigarUtils;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		// 内存使用率

		try {
			List<SigarInfoEntity> memoryInfos = SigarUtils.getMemoryInfos();
			for (SigarInfoEntity info : memoryInfos) {
				LOGGER.info("{}", info);
			}

		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
