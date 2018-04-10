package teclan.monitor.log;

import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

public class log4jSeparation extends RollingFileAppender {
    
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        // TODO Auto-generated method stub
        return this.getThreshold().equals(priority); 
    }
}