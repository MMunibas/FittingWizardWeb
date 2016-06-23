package ch.unibas.fitting.shared.javaextensions;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Created by mhelmer on 23.06.2016.
 */
public class Version {
    public static String getManifestVersion() {
        Class clazz = Version.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            // Class not from JAR
            return "no-version";
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                "/META-INF/MANIFEST.MF";
        Manifest manifest;
        try {
            URL url = new URL(manifestPath);
            manifest = new Manifest(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Attributes attr = manifest.getMainAttributes();
        String value = attr.getValue("Version");
        return value;
    }
}
