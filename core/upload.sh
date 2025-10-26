#!/bin/bash
#
#target/qc-v2.jar

fname="qc-v2.`date +%Y%m%d%H%M%S`.jar"

filename_src1="target/qc-v2.jar"
filename_src2="./$fname"

cp -f $filename_src1 $filename_src2

filename_dst="/data2/$fname"

echo $filename_src
echo $filename_dst

#scp $filename_src2 root@quant-1:$filename_dst
#scp $filename_src2 root@quant-2:$filename_dst
#scp $filename_src2 root@quant-3:$filename_dst
#scp $filename_src2 root@quant-4:$filename_dst

scp $filename_src2 root@quant-4:/data2/1-p5000-arb/
scp $filename_src2 root@quant-4:/data2/1-p5000-arb/
