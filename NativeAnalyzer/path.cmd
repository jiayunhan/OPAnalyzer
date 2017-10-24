@ECHO OFF
setlocal enabledelayedexpansion
for %%i in (*.so) do (echo %%i >> idaout.txt  & idaq -B -P+ %%i & echo !time! >> idaout.txt & idaq -A -S./python/path.py %%~ni.idb & echo !time! >> idaout.txt & del %%~ni.idb & del %%~ni.asm)
