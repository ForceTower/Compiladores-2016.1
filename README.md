# Compiler Project 2016.1
Building a Compiler

* Lexical: Stable
* Syntax: Stable
* Semanthic: Beta

Usage:
java -jar "program name.jar" directory

Use directory to determine which directory will be set to the analysis.
If the directory is not set, it will try to use the files inside a folder called "Entrada" at program's root folder.

Every single file fron the directory will get under analysis unless it starts with "r_Lex" or "r_Sin", it will also ignore files thar ends with ".jar"

Folders are going to be created to save the output files.

* A syntax analysis will only occur when there is no lexical error.
* A semanthic analysis will only occur if the syntax finishes with no errors
