package teclan.monitor.es.sys;

import java.util.ArrayList;
import java.util.List;

import teclan.sigar.SigarUtils;
import teclan.sigar.modle.ProcessInfo;

public class SystemTest {

	public static void main(String[] args) {

		List<String> process = new ArrayList<String>();

		process.add("123.exe");

		ProcessInfo list = SigarUtils.getProcessInfo(100, process);

		System.out.println(list.toString());


		// for (ProcessInfo processInfo : list) {
		// System.out.println(processInfo);
		// }
		//
		// try {
		// Ps.getInfo(SigarUtils.getInstance(), 100);
		// } catch (SigarException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }


	}

}
