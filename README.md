# Compiler Project 2016.1
Building a Compiler

* Lexical: Stable
* Syntax: Stable
* Semanthic: Beta

Usage:
java -jar "program name.jar" directory

Use directory to determine which directory will be set to the analysis.
If no directory is set, it will try to use the files within program's root folder.

Every single file from the directory will get under analysis unless it starts with "r_Lex_", "r_Sin_" or "r_Sem_", it will also ignore files thar ends with ".jar"

Folders are going to be created to save the output files.

* A syntax analysis will only occur when there is no lexical error.
* A semanthic analysis will only occur if the syntax finishes with no errors
