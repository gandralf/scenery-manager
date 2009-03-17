@echo off
set CP="WEB-INF\classes"
for %%f in (WEB-INF\lib\*.jar) do call add-cp.bat %%f
java -cp %CP% br.com.devx.scenery.Main %1 %2 %3 %4 %5 %6 %7 %8