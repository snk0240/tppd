:: Server batch file

cls
set p1="2025"
set p2="2027"
set p3="localhost"
javac ..\Dados\*.java
javac *.java
javac ..\Client\*.java
java ServerMain.java %1 %2 %3
pause