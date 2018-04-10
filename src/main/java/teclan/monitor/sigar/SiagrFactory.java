package teclan.monitor.sigar;


import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.thoughtworks.xstream.XStream;

public class SiagrFactory {


	public static void main(String[] args) throws SigarException {
		System.out.println("=====");

		System.out.println(System.getProperty("java.library.path"));

		Sigar sigar = new Sigar();

		SigarTest cpuData = SigarTest.gather(sigar);

		XStream xstream = new XStream();

		xstream.alias("CpuData", SigarTest.class);

		System.out.println(xstream.toXML(cpuData));
	}

}
