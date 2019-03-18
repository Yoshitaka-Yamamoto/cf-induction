#!/bin/bash
#!/bin/sh

cd ../../

for arg1 in 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0$@
do
	number=1

	while [ $number -lt 11 ]
	do
		java -Xmx1024m -Xss1024m ki.CF experiments/materials/plus.txt -test ${arg1} -cp 0.4 -ip 100 -onInd >> result_is_wt.txt
			
	number=`expr $number + 1`

	done

done


for arg1 in 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0$@
do
	number=1

	while [ $number -lt 11 ]
	do
		java -Xmx1024m -Xss1024m ki.CF experiments/materials/plus.txt -test ${arg1} -tn 0 -cp 0.4 -ip 100 -onInd >> result_is_wot.txt
			
	number=`expr $number + 1`

	done

done

cd experiments/scripts

exit
