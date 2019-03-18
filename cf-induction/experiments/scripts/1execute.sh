#!/bin/bash
#!/bin/sh

cd ../../

for arg1 in 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0$@
do
	number=1
	echo ${arg1} >> result_pro.txt
	while [ $number -lt 21 ]
	do
		java -Xmx256m -Xss256m ki.CF experiments/materials/animal.txt -test ${arg1} -po -cp 0.2 >> result_cf.txt
		cd experiments/test
		cat animal.pl > temp.pl
		cat training.pl >> temp.pl
		echo generalise\(class\/2\)? >> temp.pl
		echo test\(test\)? >> temp.pl  
		progol temp | grep \\[Over >> ../../result_pro.txt
		cd ../..
		number=`expr $number + 1`
	done
done

cd experiments/scripts

exit
