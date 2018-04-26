package teclan.monitor.system;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teclan.monitor.handle.Handler;
import teclan.sigar.SigarUtils;
import teclan.sigar.modle.DiskLoad;
import teclan.sigar.modle.NetTraffic;
import teclan.sigar.modle.ProcessInfo;

public class SystemMonitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemMonitor.class);

	/**
	 * 监控系统资源，包括：内存，交换空间，CPU，网络流量，磁盘负载
	 * 
	 * 注：网络监控耗时 1s
	 * 
	 * @param fileSystems
	 *            慎重设值，每个文件系统需要耗时 1s
	 * @param localAddress
	 * 
	 * @@param process 监控的进程名列表
	 * 
	 * @param handler
	 */
	public static void monitor(List<String> fileSystems, String localAddress, List<String> processNames, Handler handler) {

		Mem men = SigarUtils.getMem();

		Swap swap = SigarUtils.getSwap();

		CpuPerc cpuPerc = SigarUtils.getCpuPerc();

		List<DiskLoad> diskLoads = new ArrayList<DiskLoad>();

		for (String fileSystem : fileSystems) {

			DiskLoad diskLoad = SigarUtils.getDiskLoad(fileSystem);

			if (diskLoad != null) {
				diskLoads.add(diskLoad);
			}
		}


		NetTraffic netTraffic = SigarUtils.getNetTraffic(localAddress);

		List<ProcessInfo> processInfos = SigarUtils.getProcessInfo(processNames);

		handler.handle(men, swap, cpuPerc, diskLoads, netTraffic, processInfos);

	}


}
