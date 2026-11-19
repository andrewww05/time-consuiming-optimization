#!/bin/sh
rm -rf out && javac -d out $(find src -name '*.java') || exit 1
for v in v1 v2 v3 v4; do
  for i in 1 2 3 4 5; do
    java -cp out chnu.edu.sorcerstone.Main $v | tail -1
  done
done
