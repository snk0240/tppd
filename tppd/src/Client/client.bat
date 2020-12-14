:: Client batch file

cls
set p1="localhost"
set p2="5000"
del *.class ..\Dados\*.class
javac *.java ..\Dados\*.java
java ServerMain.java p1 p2
pause