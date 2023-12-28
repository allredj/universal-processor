package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassWriter {
    private static final String PATH = "/Users/allredj/IdeaProjects/UniversalProcessor/src/main/java/org/example";
   // private static final String PATH = "/Users/allredj/IdeaProjects/UniversalProcessor/src/main/java";

    public static void write(String name, String methodBody) {
        String sourceCode =
                "package org.example;\n" +
                "\n" +
                "public class " + name + " implements Binary {\n" +
                "    @Override\n" +
                "    public int op(int a, int b) {\n" +
                methodBody +
                "    }\n" +
                "}";

        try {
            // Write the source code to a .java file
            File sourceFile = new File(PATH + "/" + name + ".java");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
                writer.write(sourceCode);
            }

//            // Compile the .java file into a .class file
//            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//            compiler.run(null, null, null, sourceFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}