package com.pb.sawdust.meta;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

/**
 * The {@code RepoShell} ...
 *
 * @author crf
 *         Started 4/4/12 6:24 AM
 */
public class RepoShell {

    private class DirectoryEntry {
        private final boolean isRoot;
        private final Set<DirectoryEntry> children;
        private final String name;
        private final DirectoryEntry parent;
        private boolean containsGitRoot = false;

        private DirectoryEntry(String name, DirectoryEntry parent) {
            this.name = name == null ? null : name.replace("/",""); //no dir separator in name
            children = new HashSet<>();
            this.parent = parent;
            isRoot = parent == null;
        }

        public DirectoryEntry addChild(String childName) {
            DirectoryEntry child = new DirectoryEntry(childName,this);
            children.add(child);
            return child;
        }

        public String getName() {
            return name;
        }

        public Set<DirectoryEntry> getChildren() {
            return Collections.unmodifiableSet(children);
        }

        public String getEntryName() {
            if (isRoot)
                return "";
            else
                return parent.getEntryName() + name + "/";
        }

        public void gitRootFound() {
            containsGitRoot = true;
            if (!isRoot)
                parent.gitRootFound();
        }

        public void cleanNonGitRoots() {
            Iterator<DirectoryEntry> childrenIterator = children.iterator();
            while(childrenIterator.hasNext()) {
                DirectoryEntry child = childrenIterator.next();
                if (!child.containsGitRoot)
                    childrenIterator.remove();
                else
                    child.cleanNonGitRoots();
            }

        }
    }

    private DirectoryEntry buildTree(Path startingPath) {
        return buildTree(startingPath,new DirectoryEntry(null,null));
    }

    private DirectoryEntry buildTree(Path startingPath, DirectoryEntry root) {
        Set<Path> dirs = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(startingPath)) {
            for (Path d : stream)
                if (Files.isDirectory(d))
                    dirs.add(d);
            for (Path d : dirs) {
                if (d.getFileName().toString().equals(".git"))  {//repository start, don't collect any of these folders...
                    root.gitRootFound();
                    return root;
                }
            }
            for (Path d : dirs) {
                DirectoryEntry child = root.addChild(d.getFileName().toString());
                buildTree(d,child);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        return root;
    }

    private void extractTree(DirectoryEntry root, Path physicalRoot) {
        for (DirectoryEntry entry : root.getChildren()) {
            Path newPhysicalRoot = physicalRoot.resolve(entry.getName());
            try {
                if (!Files.exists(newPhysicalRoot))
                    newPhysicalRoot = Files.createDirectory(newPhysicalRoot);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
            extractTree(entry,newPhysicalRoot);
        }
    }

    private void zipEntry(FileSystem fs, DirectoryEntry entry) throws IOException {
        if (!entry.isRoot)
            Files.createDirectory(fs.getRootDirectories().iterator().next().resolve(entry.getEntryName()));
        for (DirectoryEntry child : entry.getChildren())
            zipEntry(fs,child);
    }

    private void zipTree(DirectoryEntry root, Path zipFile) throws IOException {
        if (Files.exists(zipFile))
            Files.delete(zipFile);
        URI uri = URI.create("jar:" + zipFile.toUri().toASCIIString());
        Map<String, String> env = new HashMap<>();
        env.put("create","true");
        FileSystem fs = null;
        try {
            zipEntry(fs = FileSystems.newFileSystem(uri,env),root);
        } finally {
            if (fs != null)
                fs.close();
        }
    }

    private void printTree(DirectoryEntry root) {
        for (DirectoryEntry entry : root.getChildren()) {
            System.out.println(entry.getEntryName());
            printTree(entry);
        }
    }

    private static String getUsage() {
        StringBuilder usage = new StringBuilder("usage: com.pb.sawdust.meta.RepoShell repository_directory push/pull").append(FileUtil.getLineSeparator());
        usage.append("    repository_path - the path to the current repository root");
        usage.append("    push - push current structure into shell zip file").append(FileUtil.getLineSeparator());
        usage.append("    pull - pull current structure from zip file to repository").append(FileUtil.getLineSeparator());
        return usage.toString();
    }

    private static void error(String message) {
        System.out.println(message);
        System.out.println(getUsage());
        System.exit(1);
    }

    public static void main(String ... args) throws IOException {
        if (args.length ==0)
            error("No repository path argument");
        String mainRepository = args[0];
        Path repoPath = Paths.get(mainRepository);
        if (!Files.exists(repoPath))
            error("Main repository path does not exist: " + repoPath);
        if (args.length == 1)
            error("Missing arguments");
        args = Arrays.copyOfRange(args,1,args.length);
        boolean push = false;
        boolean pull = false;
        for (String arg : args) {
            arg = arg.toLowerCase();
            switch (arg) {
                case "push" : push = true; break;
                case "pull" : pull = true; break;
                default : error("invalid argument: " + arg);
            }
        }

        Path shellPath = repoPath.resolve("repo/repository_shell.zip");
        RepoShell rs = new RepoShell();

        if (pull) { //always do pull first to resolve
            FileSystem fs = FileSystems.newFileSystem(shellPath,null);
            DirectoryEntry de = rs.buildTree(fs.getRootDirectories().iterator().next());
            System.out.println("Pulling tree:");
            rs.printTree(de);
            rs.extractTree(de,repoPath);
        }

        if (push) {
            DirectoryEntry de = rs.buildTree(repoPath);
            de.cleanNonGitRoots();
            System.out.println("Pushing tree:");
            rs.printTree(de);
            rs.zipTree(de,shellPath);
        }
    }
}
