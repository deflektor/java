@echo off
for /F "tokens=1" %%i in (delete_user_list.txt) do call :process %%i
goto :EOF
:process
set VAR1=%1
echo java -jar deleteUser.jar bo itsupport "deinPasswort" secEnterprise %VAR1%
goto :EOF
echo on