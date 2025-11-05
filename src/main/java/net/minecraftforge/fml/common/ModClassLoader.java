/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.ModAPITransformer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import top.outlands.foundation.TransformerDelegate;
import top.outlands.foundation.boot.ActualClassLoader;

import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A simple delegating class loader used to load mods into the system
 *
 *
 * @author cpw
 *
 */
public class ModClassLoader extends URLClassLoader
{
    private static final List<String> STANDARD_LIBRARIES = ImmutableList.of("jinput.jar", "lwjgl.jar", "lwjgl_util.jar", "rt.jar");
    private LaunchClassLoader mainClassLoader;
    private final List<File> sources;
    private List<URL> parentURLs = null;

    public ModClassLoader(ClassLoader parent) {
        super(new URL[0], null);
        this.sources = Lists.newArrayList();

        if (parent instanceof LaunchClassLoader) {
            this.mainClassLoader = (LaunchClassLoader) parent;
            this.applyClassLoaderInclusions();

            File customLibFolder = new File("./customize_libraries");
            if (!customLibFolder.exists()) customLibFolder.mkdir();
            this.loadCustomizeLibraries(customLibFolder);
        }
    }

    private void loadCustomizeLibraries(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File lib : files) {
                    this.loadCustomizeLibraries(lib);
                }
            }
        } else {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try {
                    this.addFile(file);
                    FMLLog.log.info("Loaded custom library {}", file.getName());
                } catch (MalformedURLException e) {
                    FMLLog.log.error("Unable to add custom lib file {} to the mod classloader", file.getAbsolutePath(), e);
                }
            }
        }
    }

    // CREF - fix for new foundation
    private void applyClassLoaderInclusions() {
        ActualClassLoader.classLoaderInclusions.put("catserver.", true);
        ActualClassLoader.classLoaderInclusions.put("org.bukkit.", true);
        ActualClassLoader.classLoaderInclusions.put("org.spigotmc.", true);
        ActualClassLoader.classLoaderInclusions.put("com.destroystokyo.paper.", true);
    }

    public void addFile(File modFile) throws MalformedURLException {
        this.mainClassLoader.addURL(modFile.getAbsoluteFile().toURI().toURL());
        this.sources.add(modFile);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.mainClassLoader.loadClass(name);
    }

    public File[] getParentSources() {
        try {
            List<File> files = new ArrayList<File>();
            for(URL url : this.mainClassLoader.getSources()) {
                URI uri = url.toURI();
                if(uri.getScheme().equals("file")) {
                    files.add(new File(uri));
                }
            }
            return files.toArray(new File[]{});
        }
        catch (URISyntaxException e) {
            FMLLog.log.error("Unable to process our input to locate the minecraft code", e);
            throw new LoaderException(e);
        }
    }

    public List<String> getDefaultLibraries()
    {
        return STANDARD_LIBRARIES;
    }

    public boolean isDefaultLibrary(File file)
    {
        String home = System.getProperty("java.home"); // Nullcheck just in case some JVM decides to be stupid
        if (home != null && file.getAbsolutePath().startsWith(home)) return true;
        // Should really pull this from the json somehow, but we dont have that at runtime.
        String name = file.getName();
        if (!name.endsWith(".jar")) return false;
        String[] prefixes =
        {
            "asm-",
            "foundation-",
            "maven-artifact-",
            "patchy-",
            "text2speech-",
            "mixin-",
            "launchwrapper-",
            "asm-all-",
            "akka-actor_2.11-",
            "config-",
            "scala-",
            "jopt-simple-",
            "lzma-",
            "realms-",
            "httpclient-",
            "httpcore-",
            "vecmath-",
            "trove4j-",
            "icu4j-core-mojang-",
            "codecjorbis-",
            "codecwav-",
            "libraryjavawound-",
            "librarylwjglopenal-",
            "soundsystem-",
            "netty-all-",
            "guava-",
            "commons-lang3-",
            "commons-compress-",
            "commons-logging-",
            "commons-io-",
            "commons-codec-",
            "jinput-",
            "jutils-",
            "gson-",
            "authlib-",
            "log4j-api-",
            "log4j-core-",
            "lwjgl-",
            "lwjgl_util-",
            "lwjgl3-",
            "twitch-",
            "jline-",
            "jna-",
            "platform-",
            "oshi-core-",
            "netty-",
            "libraryjavasound-",
            "fastutil-"
        };
        for (String s : prefixes)
        {
            if (name.startsWith(s)) return true;
        }
        return false;
    }

    public void clearNegativeCacheFor(Set<String> classList)
    {
        this.mainClassLoader.clearNegativeEntries(classList);
    }

    public ModAPITransformer addModAPITransformer(ASMDataTable dataTable)
    {
        ModAPITransformer modAPI = new ModAPITransformer();
        TransformerDelegate.registerTransformer(modAPI);
        modAPI.initTable(dataTable);
        return modAPI;
    }

    public boolean containsSource(File source)
    {
        if (this.parentURLs == null) {
            this.parentURLs = Arrays.asList(this.mainClassLoader.getURLs());
        }
        try
        {
            return this.parentURLs.contains(source.toURI().toURL());
        } catch (MalformedURLException e)
        {
            // shouldn't happen
            return false;
        }
    }
}
