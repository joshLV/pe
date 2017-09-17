#!/bin/sh

# *** set variant ***
export JAVA_EXEC_PATH=/usr/share/jdk1.7.0_79/jre/bin/java
export JAVA_VM_MODE='-server'
export JAVA_VW_MEMORY_ARGS='-Xms512M -Xmx1024M'
export JAVA_OPTIONS='-Dlogback.configurationFile=logback.xml -Dfile.encoding=UTF-8'
export JAVA_OPTIONS='-DPID='$1
export CLASSPATH=.:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar

export EXEC_DIR=/home/csiihj-pay/batch
export EXEC_JAR=batch.jar
export LIB_DIR=/home/epay/csiihj-pay/lib
export LAUNCH_CLASS=com.csii.pe.channel.stream.context.IoCContainerStart
export LAUNCH_ARG=
export DATE=`date +%Y%m%d`
export LOG_FILE=/home/csiihj-pay/batch/log/batch.log.$DATE

# *** set classpath ***
buildClassPath() {
		class_path=
		c=1
		for i in `ls $LIB_DIR/*.jar`
		do
		        if [ "$c" -eq "1" ]; then
		                class_path=${i}
		                c=2
		        else
		                class_path=${class_path}:${i}
		        fi
		done
		echo $class_path
		#return $class_path
}

export clspath=$CLASSPATH:$EXEC_DIR/$EXEC_JAR:`buildClassPath`
export CMDLINE=$clspath' '$LAUNCH_CLASS' '$LAUNCH_ARG

echoPrint() {
echo '***************************************************'
echo 'CLASSPATH is:'$clspath
echo '***************************************************'
} >>$LOG_FILE

`echoPrint`

nohup $JAVA_EXEC_PATH $JAVA_VM_MODE $JAVA_VW_MEMORY_ARGS $JAVA_OPTIONS -cp $CMDLINE 1>>$LOG_FILE 2>&1 &

#tail -f $LOG_FILE
