:: Server batch file

cls
set %1="5000"
set %2="5001"
set %3="localhost"
del *.class ..\Dados\*.class
javac *.java ..\Dados\*.java
java ServerMain %1 %2 %3
pause