package teclan.monitor.es.sys;

import java.util.ArrayList;
import java.util.List;

import teclan.sigar.SigarUtils;
import teclan.sigar.modle.ProcessInfo;

public class SystemTest {

	public static void main(String[] args) {
		
		List<String> process = new ArrayList<String>();
		
		process.add("firefox.exe");
		
		List<ProcessInfo> list = SigarUtils.getProcessInfo(process);

		for (ProcessInfo processInfo : list) {
			System.out.println(processInfo);
		}


	}

}
