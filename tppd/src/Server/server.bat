:: Server batch file

cls
set p1="5000"
set p2="5001"
set p3="localhost"
del *.class ..\Dados\*.class
javac *.java ..\Dados\*.java
java ServerMain.java p1 p2 p3
pause