for pid in `ps -ef|grep java|grep batch|grep -v grep|awk '{print $2}'`;
do
	echo "kill $pid ..."
        kill -9 $pid
done
