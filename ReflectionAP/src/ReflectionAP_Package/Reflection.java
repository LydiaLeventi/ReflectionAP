/*
 *
 * Copyright (c) 2020. Lydia Vasiliki Leventi .
 * All rights reserved .
 *
 */

package ReflectionAP_Package;

import com.mysql.cj.protocol.Resultset;
import javax.xml.transform.Result;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Reflection {

    public static void main(String[] args){

        Reflection rf = new Reflection();
        rf.FullReflection();

    }


//all functions bellow are mounted in calling order

    //the main function of the ReflectionProject. It prints the Reflection's results.
    public static final void FullReflection(){

        //Creating a folder with Reflection results for txt files, if it doesn't already exist!
        Path PathOfReflectionResultsFolder = Paths.get("../ReflectionTxtFiles");
        if (Files.notExists(PathOfReflectionResultsFolder)) {
            new File("../ReflectionTxtFiles").mkdirs();
        }

        PrintStream originalOut = System.out; //declaring the standard output and put it in a variable

        Package[] pack = Package.getPackages(); //getting all packages , libraries of program
        String MainPackage = pack[pack.length - 2].getName(); // the name of our main package

        Class[] AllInterfaces = getInterfacesInPackage(MainPackage); //getting all interfaces of the main package
        Class[] AllClasses = getClassesInPackage(MainPackage); //getting all classes of the main package



        originalOut.print("\n\n\n\n\n");
        originalOut.println("                                                             ReflectionAP");
        originalOut.println("------------------------------------------------------------------------------------------------------------------------------------------" + "\n");
        originalOut.println("------------------------------------------------------------------------------------------------------------------------------------------" + "\n\n");

        originalOut.println("/********************************************************************************************/");
        originalOut.println("/********************************************************************************************/");
        originalOut.println(" A Few Words About ....");
        originalOut.println(" ______________________");
        originalOut.println(" This is an application about viewing all components inside the source code of \n" +
                            " a project, based on its structure (Package, Interfaces, Classes, Fields, Methods etc).\n"+
                            " By intergrating this application as library inside the source code, and calling it on\n" +
                            " the project's main function, user has the options to view its components in console, \n" +
                            " in txt file, or in a database almost automatically. Txt files are automatically created \n" +
                            " and archieved in the directory 'ReflectionTxtFiles' (which is inside the parent directory \n" +
                            " of the working project that user needs to test) immediately after application starts\n" +
                            " executing. As follows, user is asked to type his/her account's informations about RDBMS\n" +
                            " program he/she uses so that application can have access to the RDBMS. After that user can\n" +
                            " can view program's components at DBMS on a database with the same name as project's.\n" +
                            " So.....................         We start !         Good Luck !!           :)  :)  :)");
        originalOut.println("/********************************************************************************************/");
        originalOut.println("/********************************************************************************************/\n\n\n");

        originalOut.println("...................................Txt File Loading...................................");
        try {
            PrintStream fileOut = new PrintStream("../ReflectionTxtFiles/Project__" + MainPackage + "__ReflectionResults.txt");
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        TextFile_PrintFullReflection(MainPackage, AllInterfaces,AllClasses);
        originalOut.println("                           Txt file is loaded succesfully!");
        originalOut.println("....................................................................................\n\n\n");

        originalOut.println("..............................DataBase Loading......................................");
        String DataBaseName = CreatingTablesInDataBase(originalOut,AllClasses,AllInterfaces);
        originalOut.println("\n          DataBase '"+DataBaseName+"'is loaded succesfully!");
        originalOut.println("....................................................................................\n\n\n\n");

        originalOut.println("........................Reflection Results In Console!..............................");
        originalOut.println("....................................................................................\n\n\n");
        //Reflection Results In Console!

        //Introspection for packages
        originalOut.println("PACKAGES:");
        originalOut.println("-----------" + "\n");
        originalOut.println(" Libraries");
        originalOut.println("-----------");

        for (int i = 0; i < pack.length; i++) { // for every package
            if(pack[i].getName().equals("ReflectionAP_Package")){ // Ignore the package of the ReflectionAP (because we want only the using used libraries of the project we test!
                continue;
            }else{
                if (i == pack.length - 2) { // In Java Reflection always the penultimate item of getPackages() is the main package. All items above it , are libraries.

                    originalOut.println("\n" + "--------------");
                    originalOut.println(" Main Package");
                    originalOut.println("--------------");
                    originalOut.println(pack[i].getName());
                } else {
                    originalOut.println(pack[i].getName());//Printing all libraries
                }
            }

        }

        originalOut.print("\n\n");


        //Introspection for Interfaces
        originalOut.println("INTERFACES:"); // printing  all interfaces
        originalOut.println("-----------");
        originalOut.println("#  |  Identifier  |  Methods" + "\n");
        String InterfaceMethods = ""; // printing all methods of every interface
        for (int i = 0; i < AllInterfaces.length; i++) { //for every interface
            InterfaceMethods = getAllInterfaceMethods(AllInterfaces[i]); //getting all methods of every interface
            originalOut.println((i + 1) + "  |  " + AllInterfaces[i].getSimpleName() + "  |  " + InterfaceMethods);
        }
        originalOut.print("\n\n");

        //Results of Introspection for List Of Classes
        originalOut.println("CLASSES:");
        originalOut.println("--------");
        originalOut.println("#  |  Identifier  |  Modifiers  |  Implements  |  SuperClass  |  SubClasses  |  Hierarchy Tree" + "\n");
        for (int i = 0; i < AllClasses.length; i++) { // for every class

            Class cls = AllClasses[i];

            String Cname; // class name
            String Cmod;  // class modifier
            String ClassInterfaces;
            String SuperClass;
            String SubClasses;
            String HierarchyTree;

            Cname = cls.getSimpleName();

            Cmod = Modifier.toString(cls.getModifiers());
            if (cls.getModifiers() == 0) { // if class has no modifiers
                Cmod = "---";  // print this
            }

            Class[] Intf = cls.getInterfaces(); // getting all interfaces that  the class implements
            ClassInterfaces = "";
            if (Intf.length == 0) {  //if class doesn't implement any interface
                ClassInterfaces = "---";  // print this
            } else {
                for (int j = 0; j < Intf.length; j++) {  // for every interface that the class implements
                    if (j == Intf.length - 1) {  // if this is the last item of the array of interfaces
                        ClassInterfaces = ClassInterfaces + Intf[j].getSimpleName();
                    } else {
                        ClassInterfaces = ClassInterfaces + Intf[j].getSimpleName() + " , ";
                    }
                }
            }

            SuperClass = cls.getSuperclass().getSimpleName();  // name of supperclass of the class

            Class[] SubClAsSeS = getSubClasses(cls, AllClasses); // getting all sublclasses of the class
            SubClasses = "";
            if (SubClAsSeS.length == 0) {  // if class doesn;t have any subclass
                SubClasses = "---";  // print this
            } else {
                for (int j = 0; j < SubClAsSeS.length; j++) {  // for every sublcass of the class
                    if (j == SubClAsSeS.length - 1) { // if this is the last item of the array of subclasses
                        SubClasses = SubClasses + SubClAsSeS[j].getSimpleName();
                    } else {
                        SubClasses = SubClasses + SubClAsSeS[j].getSimpleName() + " , ";
                    }
                }
            }

            HierarchyTree = "";
            HierarchyTree = getHierarchyTree(cls); // getting the hierarchy tree of the class
            originalOut.println((i + 1) + "  |  " + Cname + " | " + Cmod + " | " + ClassInterfaces + "  |  " + SuperClass + "  |  " + SubClasses + "  |  " + HierarchyTree);
        }
        originalOut.println("\n\n");

        // Asking user if he/she wants program to print all classes or a specific one

        Scanner Dialog = new Scanner(System.in);

        String UserDecision;
        boolean YesOrNo = true;

        System.setOut(originalOut); // we declare again the originalOut as standard input in cosnole to be able again to write System.out for cosnole!

        while (YesOrNo) {
            System.out.println("Do you want me to print all my classes? --Say Yes/yes or No/no-- ");
            UserDecision = Dialog.nextLine();  // user's prompt

            switch (UserDecision) {
                case "Yes":     //  if user types "yes/Yes"
                case "yes":
                    YesOrNo = false;
                    //Printing ALL Classes Components!!!
                    for (int i = 0; i < AllClasses.length; i++) {
                        System.out.println("\n\n\n");
                        //Introspection in every class
                        Class cl = AllClasses[i];
                        String Cname = AllClasses[i].getSimpleName();

                        System.out.println("------------------------------------------");
                        System.out.println((i + 1) + ") CLASS " + Cname + " CONTAINS..");
                        System.out.println("------------------------------------------");

                        if (Modifier.isAbstract(cl.getModifiers())) {
                            Class[] subclasses = getSubClasses(cl, AllClasses);
                            Class Subcl = subclasses[0];
                            Constructor<?> constructor = null;
                            try {
                                constructor = Subcl.getDeclaredConstructor();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                            constructor.setAccessible(true);
                            Object obj = null;
                            try {
                                obj = (Object) constructor.newInstance();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            Console_FieldsIntrospectionForAbstract(obj);
                            Console_MethodsIntrospectionForAbstract(obj);

                        } else {
                            Constructor<?> constructor = null;
                            try {
                                constructor = cl.getDeclaredConstructor();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                            constructor.setAccessible(true);
                            Object obj = null;
                            try {
                                obj = (Object) constructor.newInstance();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }

                            Console_FieldsIntrospection(obj);
                            Console_MethodsIntrospection(obj);
                        }

                    }

                    break;

                case "No":     // if user types "no/No"
                case "no":
                    YesOrNo = false;
                    for (int i = 0; i < AllClasses.length; i++) {  // for every class
                        Scanner dialog = new Scanner(System.in);  // create new dialog for the user asking him/her for pritninh the class
                        String userDecision;
                        boolean yesOrno = true;
                        while (yesOrno) {
                            System.out.println();
                            System.out.println("Do you want me to print class " + AllClasses[i].getSimpleName() + " ? --Say Yes/yes or No/no-- ");
                            userDecision = dialog.nextLine();

                            switch (userDecision) {
                                case "Yes":    // if user types "Yes/yes"
                                case "yes":
                                    yesOrno = false;
                                    System.out.println("\n\n\n");
                                    //Introspection in every class
                                    Class cl = AllClasses[i];
                                    String Cname = AllClasses[i].getSimpleName();  // class name
                                    System.out.println("------------------------------------------");
                                    System.out.println((i + 1) + ") CLASS " + Cname + " CONTAINS..");
                                    System.out.println("------------------------------------------");

                                    if (Modifier.isAbstract(cl.getModifiers())) {  // if class is abstract
                                        Class[] subclasses = getSubClasses(cl, AllClasses);
                                        Class Subcl = subclasses[0];
                                        Constructor<?> constructor = null;
                                        try {
                                            constructor = Subcl.getDeclaredConstructor();
                                        } catch (NoSuchMethodException e) {
                                            e.printStackTrace();
                                        }
                                        constructor.setAccessible(true);
                                        Object obj = null; // creating an object of its subclass
                                        try {
                                            obj = (Object) constructor.newInstance();
                                        } catch (InstantiationException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }

                                        Console_FieldsIntrospectionForAbstract(obj);  // invoking function for fields introspection
                                        Console_MethodsIntrospectionForAbstract(obj);  // invoking function for methods introspection
                                    } else {
                                        Constructor<?> constructor = null;
                                        try {
                                            constructor = cl.getDeclaredConstructor();
                                        } catch (NoSuchMethodException e) {
                                            e.printStackTrace();
                                        }
                                        constructor.setAccessible(true);
                                        Object obj = null;  //creating an object of this class
                                        try {
                                            obj = (Object) constructor.newInstance();
                                        } catch (InstantiationException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }

                                        Console_FieldsIntrospection(obj);  // invoking function for fields introspection
                                        Console_MethodsIntrospection(obj);  // invoking function for methods introspection
                                    }
                                    break;

                                case "No":  // if user types "No/no"
                                case "no":
                                    yesOrno = false; // asking user for the next class
                                    break;

                                default:
                                    System.out.println("Invalid Input. Please enter again.. ");
                                    yesOrno = true; // asking user again for the same class

                            }
                        }
                    }

                    break;

                default:
                    System.out.println("\n"+ "Invalid Input. Please enter again.. "+ "\n");
                    YesOrNo = true; // asking user again same question (about printing all classes or not)
            }
        }
        Dialog.close();

        System.out.println("\n");
        System.out.println("~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.");
        System.out.println("~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~." + "\n");
    }

    //getting all interfaces of java files and jar files inside the working project
    private static final Class[] getInterfacesInPackage(String packageName){

        String path = packageName.replaceAll("\\.", File.separator); // for java files
        String[] classPathEntries = System.getProperty("java.class.path").split(System.getProperty("path.separator")); // for jar files

        List<Class<?>> interfaces = new ArrayList<>(); // all interfaces of java files and jar files inside the project

        String name; // name of every class java file inside java files
        int count = 0;  // number of all classes (of java and jar files)

        for (String classpathEntry : classPathEntries) {  //for every path of every file inside the project

            if( classpathEntry.contains("mysql-connector-java") || classpathEntry.contains("protobuf-java-") || classpathEntry.contains("mchange-commons-") || classpathEntry.contains("jboss-as-connector-") || classpathEntry.contains("c3p0") || classpathEntry.contains("ReflectionAP")){
                continue;
            }

            if (classpathEntry.endsWith(".jar")){  // if path has the .jar ending
                System.out.println("\n\n" +classpathEntry);
                File jar = new File(classpathEntry);  // it is a jar file and we must create it as a File
                try {
                    JarInputStream JarFile = new JarInputStream(new FileInputStream(jar));
                    JarEntry en;
                    while (true) {  // As long as there is context in the jar file
                        en = JarFile.getNextJarEntry();
                        if (en == null) {  // if there is no more context in the jar file
                            break;
                        }
                        if ((en.getName().endsWith(".class"))) {  //if context is a class file
                            String className = en.getName().replaceAll("/", "\\.");  // getting the class name
                            String myClass = className.substring(0, className.lastIndexOf('.'));
                            interfaces.add(Class.forName(myClass));  // adding it to the intefaces list
                            if (!interfaces.get(count).isInterface()) {  // if this is interface and so its not a class
                                interfaces.remove(count);  // remove it from the ArrayList
                                continue;
                            }
                            count++;
                        }
                    }

                }catch (Exception e) {
                    System.out.println("Oops.. Encounter an issue while parsing jar file " + e.toString());
                }
            }else{  // in every other occasion (a java file)
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);  // list of java files
                    for (File file : base.listFiles()) {  // for every context inside the java file
                        name = file.getName();  // getting context's name
                        if (name.endsWith(".class")) {  // if context is a class
                            name = name.substring(0, name.length() - 6);
                            interfaces.add(Class.forName(packageName + "." + name));  // adding it to the intefaces list
                            if (!interfaces.get(count).isInterface()) {  // if this is interface and so its not a class
                                interfaces.remove(count);  // remove it from the ArrayList
                                continue;
                            }
                            count++;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Oops.. Encounter an issue while parsing java file" + ex.toString());
                }
            }



        }


        Class[] Realinterfaces = new Class[count];  // creating an array of type Class with all the classes
        for (int i = 0; i < count; i++) {  // for every interface
            try {
                Realinterfaces[i] = (Class) Class.forName("java.lang.Class").cast(interfaces.get(i));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return Realinterfaces;
    }

    //getting all classes of java files and jar files inside the working project
    private static final Class[] getClassesInPackage(String packageName){
        String path = packageName.replaceAll("\\.", File.separator); // for java files
        String[] classPathEntries = System.getProperty("java.class.path").split(System.getProperty("path.separator")); // for jar files

        List<Class<?>> classes = new ArrayList<>(); // all classes of java files and jar files inside the project

        String name;  // java file's name
        int count = 0;  // number of all classes

        for (String classpathEntry : classPathEntries) { // for every path of every file

            if (classpathEntry.endsWith(".jar")){  // if path is .jar at the end
                if( classpathEntry.contains("mysql-connector-java") || classpathEntry.contains("protobuf-java-") || classpathEntry.contains("mchange-commons-") || classpathEntry.contains("jboss-as-connector-") || classpathEntry.contains("c3p0") || classpathEntry.contains("ReflectionAP")){
                    continue;
                }

                File jar = new File(classpathEntry);  // create the jar file
                try {
                    JarInputStream JarFile = new JarInputStream(new FileInputStream(jar));
                    JarEntry en;
                    while (true) { // As long as there are jar files
                        en = JarFile.getNextJarEntry();
                        if (en == null) {  //if there is no more jar files
                            break;
                        }
                        if ((en.getName().endsWith(".class"))) { // if context in jar file has .class ending
                            String className = en.getName().replaceAll("/", "\\.");
                            String myClass = className.substring(0, className.lastIndexOf('.'));
                            classes.add(Class.forName(myClass));  // add this .class file to the classes
                            if (classes.get(count).isInterface()) {  // if this is interface and so its not a class
                                classes.remove(count);  // remove it from the ArrayList
                                continue;
                            }
                            count++;
                        }
                    }

                }catch (Exception e) {
                    System.out.println("Oops.. Encounter an issue while parsing jar file " + e.toString());
                }
            }else{  // in every other ocasion (if its a java file)
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);  // create class files list inside the java file
                    for (File file : base.listFiles()) {  // for every java class file in the list
                        name = file.getName();  // getting the name of java class file inside the java file
                        if (name.endsWith(".class")) {  // if this is a class file
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name)); // adding it in all classes
                            if (classes.get(count).isInterface()) {  // if this is interface and so its not a class
                                classes.remove(count);  // remove it from the ArrayList
                                continue;
                            }
                            count++;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Oops.. Encounter an issue while parsing java file" + ex.toString());
                }
            }


        }


        Class[] Realclasses = new Class[count];  // creating an array of type Class with all the classes
        for (int i = 0; i < count; i++) {  // for every class
            try {
                Realclasses[i] = (Class) Class.forName("java.lang.Class").cast(classes.get(i));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return Realclasses;
    }

    //printing the Introduction to the the reflection results in a text file
    private static final void TextFile_PrintFullReflection(String MainPackage, Class[] AllInterfaces, Class[] AllClasses){

        // Creating a new stream and declare it as standard output for text file!
        try {
            PrintStream fileOut = new PrintStream("../ReflectionTxtFiles/Project__" + MainPackage + "__ReflectionResults.txt");
            System.setOut(fileOut); // declare System.out to be the file stream!
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.print("\n\n");
        System.out.println(" ---------- Reflection of Package " + MainPackage + "---------- ");
        System.out.println("----------------------------------------------------------------" + "\n\n");
        System.out.println("\n" + "--------------");
        System.out.println(" Main Package");
        System.out.println("--------------");
        System.out.println(MainPackage);
        System.out.print("\n\n");

        //Introspection for Interfaces
        System.out.println("INTERFACES:"); // printing  all interfaces
        System.out.println("-----------");
        System.out.println("#  |  Identifier  |  Methods" + "\n");
        String InterfaceMethods = ""; // printing all methods of every interface
        for (int i = 0; i < AllInterfaces.length; i++) { //for every interface
            InterfaceMethods = getAllInterfaceMethods(AllInterfaces[i]); //getting all methods of every interface
            System.out.println((i + 1) + "  |  " + AllInterfaces[i].getSimpleName() + "  |  " + InterfaceMethods);
        }
        System.out.print("\n\n");
        System.out.println("CLASSES:");
        System.out.println("--------");
        System.out.println("#  |  Identifier  |  Modifiers  |  Implements  |  SuperClass  |  SubClasses  |  Hierarchy Tree" + "\n");
        for (int i = 0; i < AllClasses.length; i++) { // for every class

            Class cls = AllClasses[i];

            String Cname; // class name
            String Cmod;  // class modifier
            String ClassInterfaces;
            String SuperClass;
            String SubClasses;
            String HierarchyTree;

            Cname = cls.getSimpleName();

            Cmod = Modifier.toString(cls.getModifiers());
            if (cls.getModifiers() == 0) { // if class has no modifiers
                Cmod = "---";  // print this
            }

            Class[] Intf = cls.getInterfaces(); // getting all interfaces that  the class implements
            ClassInterfaces = "";
            if (Intf.length == 0) {  //if class doesn't implement any interface
                ClassInterfaces = "---";  // print this
            } else {
                for (int j = 0; j < Intf.length; j++) {  // for every interface that the class implements
                    if (j == Intf.length - 1) {  // if this is the last item of the array of interfaces
                        ClassInterfaces = ClassInterfaces + Intf[j].getSimpleName();
                    } else {
                        ClassInterfaces = ClassInterfaces + Intf[j].getSimpleName() + " , ";
                    }
                }
            }

            SuperClass = cls.getSuperclass().getSimpleName();  // name of supperclass of the class

            Class[] SubClAsSeS = getSubClasses(cls, AllClasses); // getting all sublclasses of the class
            SubClasses = "";
            if (SubClAsSeS.length == 0) {  // if class doesn;t have any subclass
                SubClasses = "---";  // print this
            } else {
                for (int j = 0; j < SubClAsSeS.length; j++) {  // for every sublcass of the class
                    if (j == SubClAsSeS.length - 1) { // if this is the last item of the array of subclasses
                        SubClasses = SubClasses + SubClAsSeS[j].getSimpleName();
                    } else {
                        SubClasses = SubClasses + SubClAsSeS[j].getSimpleName() + " , ";
                    }
                }
            }

            HierarchyTree = "";
            HierarchyTree = getHierarchyTree(cls); // getting the hierarchy tree of the class

            System.out.println((i + 1) + "  |  " + Cname + " | " + Cmod + " | " + ClassInterfaces + "  |  " + SuperClass + "  |  " + SubClasses + "  |  " + HierarchyTree);
        }
        System.out.println("\n\n");
        TextFile_PrintReflectionOfAllClasses(AllClasses);


    }

    //getting all methods of a specific interface
    private static final String getAllInterfaceMethods(Class cl){
        String MethodsNames = "";  // all methods names of every interface
        Method[] methods = cl.getDeclaredMethods();  // getting all declared methods of every class
        for(int i=0; i<methods.length; i++){  // for every declared method
            if(i == methods.length -1)  // if this is the last item of the array of all declared methods
                MethodsNames = MethodsNames + methods[i].getName() + "()";
            else
                MethodsNames = MethodsNames + methods[i].getName() + "()" + " , ";
        }
        return MethodsNames;
    }

    //getting all subclasses of a specific class
    private static final Class[] getSubClasses(Class cl, Class[] classes){

        List subClasses = new LinkedList();

        for(int i=0; i<classes.length; i++){  // for every class
            if( cl.isAssignableFrom(classes[i]) && (cl != classes[i]) ){  // if this class is child of cl AND cl is not equal to this class
                subClasses.add(classes[i]);  // add this class to the array of subclasses
            }
        }
        Class[] SubClasses = (Class[]) subClasses.toArray(new Class[subClasses.size()]);  // creating array of type class by converting the  created LinkedList
        return SubClasses;
    }

    //getting the hierarchy tree of a specific class (from its point until Object class)
    private static final String getHierarchyTree(Class cl){
        String HierarchyTree = "";  // the hierarchy tree of class cl (cl and its all superclasses until Object)

        while(cl != null){           // for every class in the hierarchy tree of cl (until there is no other class)
            if(cl == Object.class)   // if cl is the Object class (the root of all classes)
                HierarchyTree = cl.getSimpleName() + HierarchyTree;
            else
                HierarchyTree = "  <--  " + cl.getSimpleName() + HierarchyTree;

            cl = cl.getSuperclass();  //  repeat for its superclass
        }
        return HierarchyTree;
    }

    //printing all classes components  of console in a text file
    private static final void TextFile_PrintReflectionOfAllClasses(Class[] AllClasses){

        //Printing ALL Classes Components!!!
        for (int i = 0; i < AllClasses.length; i++) {
            System.out.println("\n\n\n");
            //Introspection in every class
            Class cl = AllClasses[i];
            String Cname = AllClasses[i].getSimpleName();
            System.out.println("------------------------------------------");
            System.out.println((i + 1) + ") CLASS " + Cname + " CONTAINS..");
            System.out.println("------------------------------------------");

            if (Modifier.isAbstract(cl.getModifiers())) {
                Class[] subclasses = getSubClasses(cl, AllClasses);
                Class Subcl = subclasses[0];
                Constructor<?> constructor = null;
                try {
                    constructor = Subcl.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                constructor.setAccessible(true);
                Object obj = null;
                try {
                    obj = (Object) constructor.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                Console_FieldsIntrospectionForAbstract(obj);
                System.out.println("\n");
                Console_MethodsIntrospectionForAbstract(obj);

            } else {
                Constructor<?> constructor = null;
                try {
                    constructor = cl.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                constructor.setAccessible(true);
                Object obj = null;
                try {
                    obj = (Object) constructor.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                Console_FieldsIntrospection(obj);
                System.out.println("\n");
                Console_MethodsIntrospection(obj);
            }

        }

    }

    //printing Field's introspection results for a specific class (if its an abstract) in console
    private static final void Console_FieldsIntrospectionForAbstract(Object o) {


        PrintStream originalOut = System.out; //declaring the standard output and put it in a variable

        Field[] fields = getAllFieldsForAbstract(o);    // invoking function to get all fields (declared and inherited both) of the object of the child class of the abstract class we want
        Class cls = o.getClass().getSuperclass();  // By getting the superclass of this, we finally manage to take all fields of the abstract class.
        String Cname = cls.getSimpleName();  // class name


        originalOut.println();
        originalOut.println("FIELDS OF CLASS " + Cname + ":");
        originalOut.println("--------------------------------");
        originalOut.println("#  |  Identifier  |  Of Class  |  Modifiers  |  Type  |  Value" + "\n");

        String FieldName;
        String FieldClassName;
        String FieldMod;  // field's modifier
        String FieldType;
        Object FieldValue = null;

        for (int c = 0; c < fields.length; c++) {  // for every field
            FieldName = fields[c].getName();
            FieldClassName = fields[c].getDeclaringClass().getSimpleName();
            FieldMod = Modifier.toString(fields[c].getModifiers());  //Field's modifiers
            if (fields[c].getModifiers() == 0) {  //If field has no modifiers
                FieldMod = "---";
            }
            FieldType = fields[c].getType().getSimpleName();  //Field's type

            originalOut.print((c + 1) + "  |  " + FieldName + "  |  " + FieldClassName + "  |  " + FieldMod + "  |  " + FieldType  + "  |  " /* + FieldValue*/);

            //printing field's value
            try {
                FieldValue = fields[c].get(o); // each field's value
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(fields[c].getType().isArray()) {  // if field's value is an array
                getContentFromArray(FieldValue);  // getting all components of this array and pritning them
                originalOut.println();
            }
            else{  //if field is everything else but array
                originalOut.println(FieldValue);  //printing field's value
            }

        }
    }

    //getting all fields (declared and inherited both) of a specific class (if it is an abstract class)
    private static final Field[] getAllFieldsForAbstract(Object o) {
        Class cls = o.getClass().getSuperclass(); // o is the object of the child class of the abstract class. By getting its superclass , we finally take the abstract class
        List accum = new LinkedList();  // List of all fields of class (declared and inherited both)
        while (cls != null) {   // loop for getting fields of all superclasses in the hierarchy tree of cls (including cls)
            Field[] f = cls.getDeclaredFields();  // getting all fields of each class
            for (int i = 0; i < f.length; i++) {  // for every field of each class
                accum.add(f[i]);  // add it to the List
                f[i].setAccessible(true);  // set the field accessible so that later we can get its value
            }
            cls = cls.getSuperclass();  // repeat for its superclass
        }
        Field[] AbstractFields = (Field[]) accum.toArray(new Field[accum.size()]);  // convert the List of all fields to an array of type Field
        return AbstractFields;
    }

    //printing the content of an array (of any dimension and size)
    private static void getContentFromArray(Object array) {

        String ArrayValue ="";  //Array's actual value
        boolean GoOn =true;     //we use this boolean variable to manage the right appearance of multidimensional arrays
        Class<?> componentType = array.getClass().getComponentType();   //getting the type of array's components
        int size = Array.getLength(array);   //getting the size of array

        for (int i = 0; i < size; i++) {    //for very element of the array
            Object value = Array.get(array, i);    //getting the element of array

            if (i == 0) {     //if this is the first element of the array
                System.out.print("[ ");
            }

            if (value.getClass().isArray()) {    //if the element of the array is an element too
                getContentFromArray(value);      //recursive calling to "build" the element as an array too
                GoOn = false;                    //we put it "false" so that we prevent the program from printing this element's value, because this element is an array and it's actual value in printing will be messy
            }
            if (GoOn == true) {
                ArrayValue = value + " ";                   // setting the  printing value of this element, if element is everything else but array
            }
            System.out.print(ArrayValue);
            if (i == Array.getLength(array) - 1) {         //if this is the last element of the array
                System.out.print("] ");
            }
        }

    }

    //printing Method's introspection results for a class (if its an abstract) in console
    private static final void Console_MethodsIntrospectionForAbstract(Object o){

        //Creating a folder with Reflection results if it doesn't already exist!
        Path PathOfReflectionResultsFolder = Paths.get("../ReflectionTxtFiles");
        if (Files.notExists(PathOfReflectionResultsFolder)) {
            new File("../ReflectionTxtFiles").mkdirs();
        }
        PrintStream originalOut = System.out; //declaring the standard output and put it in a variable

        //Introspection for Class methods
        Method[] methods = getAllMethodsForAbstract(o);  // getting all methods (both declared and inherited)
        String Cname = o.getClass().getSuperclass().getSimpleName();  // o  is the object of the child class of the abstract class we want. By taking its superclass, we mean the abstract.

        originalOut.println();
        originalOut.println("METHODS OF CLASS " + Cname +" :");
        originalOut.println("#  |  Identifier  |  OfClass  |  Modifiers  |  Return Type  |  Parameters");
        originalOut.println();

        String Mname;  // method's name
        String MClass;  // method's class
        String Mmod;  // method's modifier
        String MRtype;  // method's returned type
        Class[] MPtypes;  // method's parameters types
        String MParams;  // methods's parameters's types

        for(int i=0; i<methods.length; i++){  //for every method in class Demo:

            MParams = "";

            Mname = methods[i].getName();

            MClass = methods[i].getDeclaringClass().getSimpleName();

            Mmod =  Modifier.toString(methods[i].getModifiers());  //method's modifier
            if(methods[i].getModifiers() == 0){  //if method has no modifiers
                Mmod = "---";
            }

            MRtype = methods[i].getReturnType().getSimpleName();  //method's return type

            MPtypes = methods[i].getParameterTypes();  //getting all parameters of method
            int MPcount = methods[i].getParameterCount();  // number of parameters
            if(MPcount == 0) {  //if method has no parameters
                MParams = "---";
            } else{//if method has parameters
                for(int MPc=0; MPc< MPcount; MPc++){  //for every parameter in the method
                    if(MPc == (MPcount -1) ){  //if this is the last parameter in the order of method's parameters
                        MParams = MParams + MPtypes[MPc].getSimpleName();
                        break;
                    }
                    MParams = MParams + MPtypes[MPc].getSimpleName() + ", ";  //if this is NOT the last parameter in the order of method's parameters
                }
            }

            originalOut.println((i+1) + "  |  " + Mname + "  |  " + MClass + "  |  " + Mmod + "  |  " + MRtype + "  |  " + MParams);
        }

    }

    //getting all methods (declared and inherited both) of a specific class (if it is an abstract class)
    private static final Method[] getAllMethodsForAbstract(Object o){

        Class cls = o.getClass().getSuperclass(); // o is the object of the child class of the absract class. By getting the superclass of child class, we re getting the abstract
        List accum = new LinkedList();  // creating List for all methods (declared and inherited both)

        while ( (cls != null) && (cls != Object.class) ) {  // loop for all superclasses of cls in the hierarchy tree of cls , except the root (Object)
            Method[] methods = cls.getDeclaredMethods();  // getting all methods for the specific class
            for (int i = 0; i < methods.length; i++) {  // for every method
                accum.add(methods[i]);  // add it in the List
                methods[i].setAccessible(true);  // make method accessible
            }
            cls = cls.getSuperclass();  // repeat for its superclass ((of cls))
        }
        Method[] AbstractMethods = (Method[]) accum.toArray(new Method[accum.size()]);  // creating an array of type Method by converting the List of all methods
        return AbstractMethods;
    }

    //printing Field's introspection results for a specific class (by its object if its NOT an abstract) in console
    private static final void Console_FieldsIntrospection(Object o) {

        PrintStream originalOut = System.out; //declaring the standard output and put it in a variable

        Field[] fields = getAllFields(o);  // invoking function to get all fields (declared and inherited both)
        String Cname = o.getClass().getSimpleName();  // class name

        originalOut.println();
        originalOut.println("FIELDS OF CLASS " + Cname + ":");
        originalOut.println("--------------------------------");
        originalOut.println("#  |  Identifier  |  Of Class  |  Modifiers  |  Type  |  Value" + "\n");

        String FieldName;
        String FieldClassName;
        String FieldMod;  // field's modifier
        String FieldType;
        Object FieldValue = null;

        for (int c = 0; c < fields.length; c++) {  // for every field
            FieldName = fields[c].getName();
            FieldClassName = fields[c].getDeclaringClass().getSimpleName();
            FieldMod = Modifier.toString(fields[c].getModifiers());//Field's modifiers
            if (fields[c].getModifiers() == 0) {  //If field has no modifiers
                FieldMod = "---";
            }
            FieldType = fields[c].getType().getSimpleName();//Field's type

            originalOut.print((c + 1) + "  |  " + FieldName + "  |  " + FieldClassName + "  |  " + FieldMod + "  |  " + FieldType  + "  |  " /* + FieldValue*/);

            // Printing the value of field
            try {
                FieldValue = fields[c].get(o);  // field's value
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(fields[c].getType().isArray()) {  // if field's value is an array
                getContentFromArray(FieldValue);  // invoking function for pritning the content of an array object
                originalOut.println();
            }
            else{  //if field is everything else but array
                originalOut.println(FieldValue);  //printing field's value
            }

        }

    }

    //getting all fields(declared and inherited both) of a specific class (by it's object if it is NOT an abstract class)
    private static final Field[] getAllFields(Object o) {

        Class cls = o.getClass();  // class of o
        List accum = new LinkedList();  // List of all fields of class (declared and inherited both)

        while (cls != null) {  // loop for getting fields of all superclasses in the hierarchy tree of cls (including cls)
            Field[] f = cls.getDeclaredFields();  // getting all fields of each class
            for (int i = 0; i < f.length; i++) {  // for every field of each class
                accum.add(f[i]);  // add it to the List
                f[i].setAccessible(true);  // set the field accessible so that later we can get its value
            }
            cls = cls.getSuperclass();  // repeat for its superclass
        }
        Field[] allFields = (Field[]) accum.toArray(new Field[accum.size()]);  // convert the List of all fields to an array of type Field
        return allFields;
    }

    //printing Method's introspection results for a class (by its object if its NOT an abstract) in console
    private static final void Console_MethodsIntrospection(Object o) {


        //Creating a folder with Reflection results if it doesn't already exist!
        Path PathOfReflectionResultsFolder = Paths.get("../ReflectionTxtFiles");
        if (Files.notExists(PathOfReflectionResultsFolder)) {
            new File("../ReflectionTxtFiles").mkdirs();
        }
        PrintStream originalOut = System.out; //declaring the standard output and put it in a variable

        //Introspection for Class methods
        Method[] methods = getAllMethods(o);  // getting all methods (declared and inherited both)
        String Cname = o.getClass().getSimpleName();  // class name

        originalOut.println();
        originalOut.println("METHODS OF CLASS " + Cname + " :");
        originalOut.println("#  |  Identifier  |  OfClass  |  Modifiers  |  Return Type  |  Parameters");
        originalOut.println();

        String Mname;  // method's name
        String MClass;  // method's class
        String Mmod;  // method's modifier
        String MRtype;  // method's returned type
        Class[] MPtypes;  // method's parameter's types
        String MParams;  // method's parameters (their types and their number) in String type

        for (int i = 0; i < methods.length; i++) {  //for every method in class Demo:

            MParams = "";

            Mname = methods[i].getName();

            MClass = methods[i].getDeclaringClass().getSimpleName();

            Mmod = Modifier.toString(methods[i].getModifiers());  //method's modifier
            if (methods[i].getModifiers() == 0) {  //if method has no modifiers
                Mmod = "---";
            }

            MRtype = methods[i].getReturnType().getSimpleName();  //method's return type

            MPtypes = methods[i].getParameterTypes();  //getting all parameters of method
            int MPcount = methods[i].getParameterCount();  // number of parameters
            if (MPcount == 0) {  //if method has no parameters
                MParams = "---";
            } else {  //if method has parameters
                for (int MPc = 0; MPc < MPcount; MPc++) {  //for every parameter in the method
                    if (MPc == (MPcount - 1)) {  //if this is the last parameter in the order of method's parameters
                        MParams = MParams + MPtypes[MPc].getSimpleName();
                        break;
                    }
                    MParams = MParams + MPtypes[MPc].getSimpleName() + ", ";  //if this is NOT the last parameter in the order of method's parameters
                }
            }

            originalOut.println((i + 1) + "  |  " + Mname + "  |  " + MClass + "  |  " + Mmod + "  |  " + MRtype + "  |  " + MParams);
        }

    }

    //getting all methods(declared and inherited both) of a specific class (by it's object if it is NOT an abstract class)
    private static final Method[] getAllMethods(Object o){

        Class cls = o.getClass();  // class of o
        List accum = new LinkedList();  // creating List for all methods (declared and inherited both)

        while ( (cls != null) && (cls != Object.class) ) {  // loop for all superclasses of cls in the hierarchy tree of cls , except the root (Object)
            Method[] methods = cls.getDeclaredMethods();  // getting all methods for the specific class
            for (int i = 0; i < methods.length; i++) {  // for every method
                accum.add(methods[i]);  // add it in the List
                methods[i].setAccessible(true);  // make method accessible
            }
            cls = cls.getSuperclass();  // repeat for its superclass ((of cls))
        }
        Method[] allMethods = (Method[]) accum.toArray(new Method[accum.size()]);  // creating an array of type Method by converting the List of all methods
        return allMethods;
    }

    //creating all tables of the database we need to put reflection results in
    private static final String CreatingTablesInDataBase(PrintStream originalOut,Class[] AllClasses, Class[] AllInterfaces) {

        originalOut.println();
        //getting the project's name by using user's directory's path
        String userDir = System.getProperty("user.dir");
        Path path = Paths.get(userDir);
        String ProjectName = path.getFileName().toString().replaceAll("\\s","_"); // project's name
        String DataBaseName = "";  //DataBase's name

        //Staring the dialog between program and user
        originalOut.println("To start .. We need your permission to connect ReflectionAP with your RDBMS. ");
        originalOut.println("So we ask you to give us important informations about your RDBMS account as follows. \n");

        while(true){
            Scanner scnf = new Scanner(System.in);
            originalOut.print("Please, type here your Username: ");
            String UserName = scnf.next();
            originalOut.print("Please, type here your Password: ");
            String Password1= scnf.next();
            originalOut.print("For your security and our calmness, please confirm here your Password again: ");
            String Password2= scnf.next();
            originalOut.println("");

            //preparing connection between user and RDBMS according to his/ her previous inputs to the system (username and password)
            Properties connectionProps = new Properties();
            connectionProps.put("user", UserName);
            connectionProps.put("password", Password1);
            Connection conn = null;

            try {
                String url_s = "jdbc:mysql://localhost:3306/";  //getting the path of RDBMS
                conn = DriverManager.getConnection(url_s, connectionProps);  //connecting to RDBMS
                Statement stmt = conn.createStatement();  // creating connection to sql queries

                //getting valid username and password of every account for all users of RDBMS
                String sql = "Select * from mysql.user ";
                ResultSet rs = stmt.executeQuery(sql);  // executing the sql query
                if (rs.next()) {  // if there is an account with same username and password as user typed in console

                    if (Password1.equals(Password2)) {  // if the tha values about password ,that user typed in console, are equal to each other
                        DataBaseName = ("db_Project_" + ProjectName).toLowerCase();  // creating the new database's name

                        //creating the database
                        try{
                            String query_Start = "CREATE DATABASE " +DataBaseName+";" ;
                            stmt.executeUpdate(query_Start);
                        }catch (SQLException ee) {
                            originalOut.println("Error1097 : " + ee.getMessage());
                        }

                        //creating all tables of the new database(for classes and interfaces)
                        try{
                            String[] queries = new String[ 2 + (2*AllClasses.length) ];
                            queries[0] = "CREATE TABLE "+DataBaseName+".Interfaces (Count int NOT NULL, Identifier varchar(255), Methods varchar(255), PRIMARY KEY (Count) );";  //query about creating table for Interfaces
                            queries[1] = "CREATE TABLE "+DataBaseName+".Classes(Count int NOT NULL, Identifier varchar(255), Modifiers varchar(255), Implements varchar(255), SuperClass varchar(255), SubClasses varchar(255), Hierarchy_Tree varchar(255), PRIMARY KEY (Count) );" ;  //query about creating table for the list of all classes
                            for (int i = 0; i < AllClasses.length; i++) {  //for every class(abstract and non-abstract
                                String Cname = AllClasses[i].getSimpleName();
                                queries[i+2] = "CREATE TABLE "+DataBaseName+".Fields_Of_Class_" + Cname + " (Count int NOT NULL, Identifier varchar(255), OfClass varchar(255), Modifiers varchar(255), Type varchar(255), Value varchar(255), PRIMARY KEY (Count) );" ;  //query about creating table of fields for every class
                                queries[AllClasses.length+i+2] = "CREATE TABLE "+DataBaseName+".Methods_Of_Class_" + Cname + " (Count int NOT NULL, Identifier varchar(255), OfClass varchar(255), Modifiers varchar(255), Return_Type varchar(255), Parameters varchar(255), PRIMARY KEY (Count) );" ;  //query about creating table of methods for every class
                            }

                            //creating all tables
                            for(int j=0; j<queries.length; j++){ //
                                stmt.addBatch(queries[j]);  //adding all queries statements
                            }
                            stmt.executeBatch();  //executing all queries (creating all tables)

                            ImportingDataInDataBase(conn,  originalOut,DataBaseName,AllInterfaces, AllClasses); // importing all informations in the created tables

                        }catch (SQLException exx) {
                            originalOut.println("Error1109: " + exx.getMessage());
                        }finally {
                            try {
                                if (conn != null) {
                                    conn.close(); // ending connection to RDBMS
                                }
                            } catch (SQLException ex) {
                                originalOut.println("Error1127 : " + ex.getMessage());
                            }
                        }

                        break;


                    } else {  // if the tha values about password ,that user typed in console, are NOT equal to each other
                        originalOut.println("We warn you that you did not type twice the correct password.  :(  Please try again!  :)\n\n");
                    }
                }

            }catch(SQLException excpt){  // if there is NO account with same username and password as user typed in console
                originalOut.println("Error1140 : " + excpt.getMessage());
                originalOut.println("Invalid username / password.  :(  Please try again!  :)\n\n");
            }

        }

        return DataBaseName;

    }

    //importing all reflection results as data inside the created tables of database
    private static final void ImportingDataInDataBase(Connection conn, PrintStream originalOut,String DataBaseName,Class[] AllInterfaces, Class[] AllClasses){

        String InterfaceMethods=""; // all methods of a specific interface(separating by comma in a string)
        Statement stmt = null;
        String[] I_queries = new String[AllInterfaces.length];  //sql queries about imports in the table of Interfaces
        String[] C_queries = new String[AllClasses.length];  //sql queries about imports in the table of Classes

        for (int i = 0; i < AllInterfaces.length; i++) {  // for every interface
            InterfaceMethods = getAllInterfaceMethods(AllInterfaces[i]);  // getting all methods of this interface
            I_queries[i] ="INSERT INTO "+DataBaseName+".Interfaces VALUES(" + (i+1) + ", '" + AllInterfaces[i].getSimpleName() + "', '" + InterfaceMethods + "');" ;  //import value of column "methods" in teach row of table of interfaces

            //executing queries for interfaces table
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(I_queries[i]);
            }catch (SQLException exx) {
                originalOut.println("Error1167 : " + exx.getMessage());
            }

        }

        //importing data for all tables of every class(abstract and NON-abstract)
        for (int i = 0; i < AllClasses.length; i++) {
            Class cls = AllClasses[i];

            String Cname = cls.getSimpleName(); // class name
            String Cmod;  // class modifier
            String ClassInterfaces;
            String SuperClass;
            String SubClasses;
            String HierarchyTree = getHierarchyTree(cls);;

            Cmod = Modifier.toString(cls.getModifiers());
            if (cls.getModifiers() == 0) { // if class has no modifiers
                Cmod = "---";  // print this
            }

            Class[] Intf = cls.getInterfaces(); // getting all interfaces that  the class implements
            ClassInterfaces = "";
            if (Intf.length == 0) {  //if class doesn't implement any interface
                ClassInterfaces = "---";  // print this
            } else {
                for (int j = 0; j < Intf.length; j++) {  // for every interface that the class implements
                    if (j == Intf.length - 1) {  // if this is the last item of the array of interfaces
                        ClassInterfaces = ClassInterfaces + Intf[j].getSimpleName();
                    } else {
                        ClassInterfaces = ClassInterfaces + Intf[j].getSimpleName() + " , ";
                    }
                }
            }

            SuperClass = cls.getSuperclass().getSimpleName();  // name of supperclass of the class

            Class[] SubClAsSeS = getSubClasses(cls, AllClasses); // getting all sublclasses of the class
            SubClasses = "";
            if (SubClAsSeS.length == 0) {  // if class doesn;t have any subclass
                SubClasses = "---";  // print this
            } else {
                for (int j = 0; j < SubClAsSeS.length; j++) {  // for every sublcass of the class
                    if (j == SubClAsSeS.length - 1) { // if this is the last item of the array of subclasses
                        SubClasses = SubClasses + SubClAsSeS[j].getSimpleName();
                    } else {
                        SubClasses = SubClasses + SubClAsSeS[j].getSimpleName() + " , ";
                    }
                }
            }

            //sql query for importing data about all fields and methods of every class
            C_queries[i] = "INSERT INTO "+DataBaseName+".Classes VALUES(" + (i + 1) + ", '"+Cname+"', '"+Cmod+"', '" +ClassInterfaces+ "', '"+SuperClass+"', '"+SubClasses+"', '"+HierarchyTree+"');" ;
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(C_queries[i]);
            }catch (SQLException ee) {
                originalOut.println("Error1224 : " + ee.getMessage());
            }

            // if class is Abstract
            if (Modifier.isAbstract(cls.getModifiers())) {
                Class[] subclasses = getSubClasses(cls, AllClasses);
                Class Subcl = subclasses[0];
                Constructor<?> constructor = null;
                try {
                    constructor = Subcl.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                constructor.setAccessible(true);
                Object obj = null;
                try {
                    obj = (Object) constructor.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                ImportingDataOfFieldsInTableOfAbstractClass(conn,originalOut,DataBaseName,obj);  // importing data in the table about fields
                ImportingDataOfMethodsInTableOfAbstractClass(conn, originalOut,DataBaseName,obj);  // importing data in the table about methods

            } else {  //if class is NON-abstract
                Constructor<?> constructor = null;
                try {
                    constructor = cls.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                constructor.setAccessible(true);
                Object obj = null;
                try {
                    obj = (Object) constructor.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                ImportingDataOfFieldsInTableOfClass(conn, originalOut,DataBaseName,obj);  // importing data in the table about fields
                ImportingDataOfMethodsInTableOfClass(conn, originalOut,DataBaseName,obj);  // importing data in the table about methods
            }


        }

    }

    //importing in tables for fields of abstract classes their data- fields
    private static final void ImportingDataOfFieldsInTableOfAbstractClass(Connection conn, PrintStream originalOut,String DataBaseName,Object o){
        Statement stmt = null;
        Field[] fields = getAllFieldsForAbstract(o);
        String Cname = o.getClass().getSimpleName();

        String FieldName;
        String FieldClassName;
        String FieldMod;  // field's modifier
        String FieldType;
        Object FieldValue = null;

        for (int c = 0; c < fields.length; c++) {  // for every field of the class
            FieldName = fields[c].getName();
            FieldClassName = fields[c].getDeclaringClass().getSimpleName();
            FieldMod = Modifier.toString(fields[c].getModifiers());//Field's modifiers
            if (fields[c].getModifiers() == 0) {  //If field has no modifiers
                FieldMod = "---";
            }
            FieldType = fields[c].getType().getSimpleName();//Field's type

            //importing all informations(except value) about the field inside the table of fields of the class
            int count = c+1;
            String query1 ="INSERT INTO "+DataBaseName+".Fields_Of_Class_"+Cname+" (Count, Identifier, OfClass, Modifiers, Type) VALUES("+count+", '"+FieldName+"', '"+FieldClassName+"', '"+FieldMod+"', '"+FieldType+"');" ;
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(query1);
            }catch (SQLException exx) {
                originalOut.println("Error1308 : " + exx.getMessage());
            }

            // Importing the value of field
            try {
                FieldValue = fields[c].get(o);  // field's value
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(fields[c].getType().isArray()) {  // if field's value is an array
                //sql query about changing-updating the column by name 'value' from null(by default) to '', because otherwise if it remains null table cannot get updated
                String query2 = "UPDATE "+DataBaseName+".Fields_Of_Class_" +Cname+ "  SET Value = '' WHERE Count = "+count+" ;" ;
                try {
                    stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(query1);
                }catch (SQLException exx) {
                    originalOut.println("Error1325 : " + exx.getMessage());
                }
                ImportingContentFromArray(conn, originalOut,DataBaseName  ,count, Cname, FieldValue);  //calling this function for importing correct the value of an array

            }
            else{  //if field is everything else but array
                String query2 = "UPDATE "+DataBaseName+".Fields_Of_Class_" +Cname+ "  SET Value = '"+FieldValue+"' WHERE Count = "+count+" ;" ;  //sql query about changing-updating the column by name 'value' with the actual value of the field
                try {
                    stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(query2);
                }catch (SQLException exx) {
                    originalOut.println("Error1336 : " + exx.getMessage());
                }
            }
        }
    }

    //using this method for importing value of a data-field if its value is an array
    private static void ImportingContentFromArray(Connection conn,  PrintStream originalOut,String DataBaseName, int count, String Cname,  Object array) {
        Statement stmt = null;


        String ArrayValue ="";  //Array's actual value
        boolean GoOn =true;     //we use this boolean variable to manage the right appearance of multidimensional arrays
        Class<?> componentType = array.getClass().getComponentType();   //getting the type of array's components
        int size = Array.getLength(array);   //getting the size of array

        for (int i = 0; i < size; i++) {    //for very element of the array
            Object value = Array.get(array, i);    //getting the element of array

            if (i == 0) {     //if this is the first element of the array
                //updating the existed table of fields by changing the value of field to '[ '
                String query1 = "UPDATE "+DataBaseName+".Fields_Of_Class_" +Cname+ "  SET Value = CONCAT(Value, '[ ') WHERE Count = "+count+" ;" ;
                try {
                    stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(query1);
                }catch (SQLException exc) {
                    originalOut.println("Error1363 : " + exc.getMessage());
                }
            }

            if (value.getClass().isArray()) {    //if the element of the array is an element too
                ImportingContentFromArray(conn, originalOut,DataBaseName, count, Cname,value);      //recursive calling to "build" the element as an array too
                GoOn = false;                    //we put it "false" so that we prevent the program from printing this element's value, because this element is an array and it's actual value in printing will be messy
            }
            if (GoOn == true) {
                ArrayValue = value + " ";      // setting the imported value of this element, if element is everything else but array
            }

            //updating the existed table of fields by adding to the value of field the actual value of the array
            String query2 = "UPDATE "+DataBaseName+".Fields_Of_Class_" +Cname+ "  SET Value = CONCAT(Value, '"+ArrayValue+"')  WHERE Count = "+count+" ;" ;
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(query2);
            }catch (SQLException exc) {
                originalOut.println("Error1380 : " + exc.getMessage());
            }

            if (i == Array.getLength(array) - 1) {         //if this is the last element of the array
                //updating the existed table of fields by adding to the actual value of field the ' ]'
                String query3 = "UPDATE "+DataBaseName+".Fields_Of_Class_" +Cname+ "  SET Value = CONCAT(Value, '] ') WHERE Count = "+count+" ;" ;
                try {
                    stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(query3);
                }catch (SQLException exc) {
                    originalOut.println("Error1390 : " + exc.getMessage());
                }
            }
        }

    }

    //importing in tables for methods of abstract classes their data- methods
    private static final void ImportingDataOfMethodsInTableOfAbstractClass(Connection conn, PrintStream originalOut,String DataBaseName,Object o){
        Statement stmt = null;
        Method[] methods = getAllMethodsForAbstract(o);
        String Cname = o.getClass().getSimpleName();

        String Mname;  // method's name
        String MClass;  // method's class
        String Mmod;  // method's modifier
        String MRtype;  // method's returned type
        Class[] MPtypes;  // method's parameter's types
        String MParams;  // method's parameters (their types and their number) in String type

        for (int i = 0; i < methods.length; i++) {  //for every method of the Abstract class

            MParams = "";

            Mname = methods[i].getName();

            MClass = methods[i].getDeclaringClass().getSimpleName();

            Mmod = Modifier.toString(methods[i].getModifiers());  //method's modifier
            if (methods[i].getModifiers() == 0) {  //if method has no modifiers
                Mmod = "---";
            }

            MRtype = methods[i].getReturnType().getSimpleName();  //method's return type

            MPtypes = methods[i].getParameterTypes();  //getting all parameters of method
            int MPcount = methods[i].getParameterCount();  // number of parameters
            if (MPcount == 0) {  //if method has no parameters
                MParams = "---";
            } else {  //if method has parameters
                for (int MPc = 0; MPc < MPcount; MPc++) {  //for every parameter in the method
                    if (MPc == (MPcount - 1)) {  //if this is the last parameter in the order of method's parameters
                        MParams = MParams + MPtypes[MPc].getSimpleName();
                        break;
                    }
                    MParams = MParams + MPtypes[MPc].getSimpleName() + ", ";  //if this is NOT the last parameter in the order of method's parameters
                }
            }

            int count = i+1;
            //importing all data about the method of the class in the table of all the methods of this class
            String query ="INSERT INTO "+DataBaseName+".Methods_Of_Class_"+Cname+" VALUES("+count+", '"+Mname+"', '"+MClass+"', '"+Mmod+"', '"+MRtype+"', '"+MParams+"');" ;
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(query);
            }catch (SQLException exx) {
                originalOut.println("Error1446 : " + exx.getMessage());
            }
        }


    }

    //importing in tables for fields of NON-abstract classes their data- fields
    private static final void ImportingDataOfFieldsInTableOfClass(Connection conn, PrintStream originalOut,String DataBaseName,Object o){
        Statement stmt = null;
        Field[] fields = getAllFields(o);
        String Cname = o.getClass().getSimpleName();

        String FieldName;
        String FieldClassName;
        String FieldMod;  // field's modifier
        String FieldType;
        Object FieldValue = null;

        for (int c = 0; c < fields.length; c++) {  // for every field of the NON-abstract class
            FieldName = fields[c].getName();
            FieldClassName = fields[c].getDeclaringClass().getSimpleName();
            FieldMod = Modifier.toString(fields[c].getModifiers());//Field's modifiers
            if (fields[c].getModifiers() == 0) {  //If field has no modifiers
                FieldMod = "---";
            }
            FieldType = fields[c].getType().getSimpleName();//Field's type

            //sql query about inserting all informations (except value) for every field in the table of fields of the specific class
            int count = c+1;
            String query1 ="INSERT INTO "+DataBaseName+".Fields_Of_Class_"+Cname+" (Count, Identifier, OfClass, Modifiers, Type) VALUES("+count+", '"+FieldName+"', '"+FieldClassName+"', '"+FieldMod+"', '"+FieldType+"');" ;
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(query1);
            }catch (SQLException exx) {
                originalOut.println("Error1481 : " + exx.getMessage());
            }

            // Importing the value of field
            try {
                FieldValue = fields[c].get(o);  // field's value
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(fields[c].getType().isArray()) {  // if field's value is an array
                //sql query about changing-updating the column value from null(by default) to '',because otherwise it cannot happen the later update
                String query3 = "UPDATE "+DataBaseName+".Fields_Of_Class_"+Cname+ "  SET Value = '' WHERE Count = "+count+" ;" ;
                try {
                    stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(query3);
                }catch (SQLException exx) {
                    originalOut.println("Error1498 : " + exx.getMessage());
                }
                ImportingContentFromArray(conn, originalOut,DataBaseName,count, Cname, FieldValue);  //calling this method for importing the value of the array

            }
            else{  //if field is everything else but array
                //sql query about changing-updating the column by name 'value' from null(by default) to its actual value
                String query2 = "UPDATE "+DataBaseName+".Fields_Of_Class_"+Cname+ "  SET Value = '"+FieldValue+"' WHERE Count = "+count+" ;" ;
                try {
                    stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(query2);
                }catch (SQLException exx) {
                    originalOut.println("Error1510 : " + exx.getMessage());
                }
            }
        }
    }

    //importing in tables for methods of NON-abstract classes their data- methods
    private static final void ImportingDataOfMethodsInTableOfClass(Connection conn, PrintStream originalOut,String DataBaseName, Object o){
        Statement stmt = null;
        Method[] methods = getAllMethods(o);
        String Cname = o.getClass().getSimpleName();

        String Mname;  // method's name
        String MClass;  // method's class
        String Mmod;  // method's modifier
        String MRtype;  // method's returned type
        Class[] MPtypes;  // method's parameter's types
        String MParams;  // method's parameters (their types and their number) in String type

        for (int i = 0; i < methods.length; i++) {  //for every method of this NON-abstract class

            MParams = "";

            Mname = methods[i].getName();

            MClass = methods[i].getDeclaringClass().getSimpleName();

            Mmod = Modifier.toString(methods[i].getModifiers());  //method's modifier
            if (methods[i].getModifiers() == 0) {  //if method has no modifiers
                Mmod = "---";
            }

            MRtype = methods[i].getReturnType().getSimpleName();  //method's return type

            MPtypes = methods[i].getParameterTypes();  //getting all parameters of method
            int MPcount = methods[i].getParameterCount();  // number of parameters
            if (MPcount == 0) {  //if method has no parameters
                MParams = "---";
            } else {  //if method has parameters
                for (int MPc = 0; MPc < MPcount; MPc++) {  //for every parameter in the method
                    if (MPc == (MPcount - 1)) {  //if this is the last parameter in the order of method's parameters
                        MParams = MParams + MPtypes[MPc].getSimpleName();
                        break;
                    }
                    MParams = MParams + MPtypes[MPc].getSimpleName() + ", ";  //if this is NOT the last parameter in the order of method's parameters
                }
            }

            int count = i+1;
            //importing all data about the method of the class in the table of all the methods of this class
            String query ="INSERT INTO "+DataBaseName+".Methods_Of_Class_"+Cname+" VALUES("+count+", '"+Mname+"', '"+MClass+"', '"+Mmod+"', '"+MRtype+"', '"+MParams+"');" ;
            try {
                stmt = conn.createStatement();
                int rs = stmt.executeUpdate(query);
            }catch (SQLException exx) {
                originalOut.println("Error1564 : " + exx.getMessage());
            }
        }


    }

}