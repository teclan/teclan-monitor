package teclan.monitor.sigar;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarTest {
	private CpuInfo info;

	private CpuPerc perc;

	private Cpu timer;

	public SigarTest() {

	}

	public void populate(Sigar sigar) throws SigarException {

		info = sigar.getCpuInfoList()[0];

		perc = sigar.getCpuPerc();

		timer = sigar.getCpu();

	}

	public static SigarTest gather(Sigar sigar) throws SigarException {

		SigarTest data = new SigarTest();

		data.populate(sigar);

		return data;

	}
}
