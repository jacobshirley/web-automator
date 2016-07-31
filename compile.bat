mkdir bin

dir /s /B *.java > sources.txt
javac -cp "agent/asm-all-5.0.4.jar;lib/*;lib/derby/*;src" -d bin @sources.txt
pause