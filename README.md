# ReflectionAP
 This is an application about viewing all components inside the source code of 
 a project, based on its structure (Package, Interfaces, Classes, Fields, Methods etc).
 By intergrating this application as library inside the source code, and calling it on
 the project's main function, user has the options to view its components in console, 
 in txt file, or in a database almost automatically. Txt files are automatically created 
 and archieved in the directory 'ReflectionTxtFiles' (which is inside the parent directory 
 of the working project that user needs to test) immediately after application starts
 executing. As follows, user is asked to type his/her account's informations about RDBMS
 program he/she uses so that application can have access to the RDBMS. After that user can
 can view program's components at DBMS on a database with the same name as project's.
 
 User needs to have an account at MySQL and import in his this source code, as external library,
 the ReflectionAP.jar file. After this, in the main() function of source code, user 
 imports as the last line in the main() this line: 
 "  Reflection.FullReflection();  "  .


 Enjoy!
