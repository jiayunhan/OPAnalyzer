@ECHO OFF
setlocal enabledelayedexpansion
for %%i in (*.so) do (echo %%i >> idaout.txt & echo !time! >> idaout.txt & idaq -B -P+ %%i & idaq -A -S./python/filter.py %%~ni.idb & echo !time! >> idaout.txt & del %%~ni.idb & del %%~ni.asm)
