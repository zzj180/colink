@echo off

if exist build (
    echo --- Removing build folder...
    rd build /s /q
)

set "lj=%~p0"
set "lj=%lj:\= %"
for %%a in (%lj%) do set wjj=%%a

echo %1|findstr "^release" >nul
if %errorlevel% equ 0 (
    @echo on
    @echo --- Building %wjj% release....
    gradle assembleRelease
) else (
    @echo on
    @echo --- Building %wjj% debug....
    gradle assembleDebug
)
