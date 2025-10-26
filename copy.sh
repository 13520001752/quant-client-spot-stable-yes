#!/bin/bash
#
#target/qc-v2.jar

fname="qc-v2.`date +%Y%m%d%H%M%S`.jar"

filename_src1="target/qc-v2.jar"
filename_src2="./$fname"

cp -f $filename_src1 $filename_src2

