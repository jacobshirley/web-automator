mkdir bin

javac -cp "src" -d bin src/tech/conexus/webautomator/JxBrowserDownloader.java
java -cp "bin" tech.conexus.webautomator.JxBrowserDownloader

dir /s /B *.java > sources.txt
javac -cp "agent/asm-all-5.0.4.jar;lib/*;lib/derby/*;src" -d bin @sources.txt

echo "Compilation complete!"
pause