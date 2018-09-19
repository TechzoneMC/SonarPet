package net.techcable.sonarpet.maven;

import lombok.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nullable;

import net.techcable.sonarpet.utils.SSLUtils;

import static java.util.Objects.*;

public class ResolvedMavenArtifact extends MavenArtifact {
    private final MavenRepository repository;
    private final URL location;
    private final boolean secure;
    @Nullable
    private final Path fileLocation;

    @SneakyThrows(URISyntaxException.class)
    /* package */ ResolvedMavenArtifact(MavenRepository repository, String groupId, String artifactId, String version, String extension, URL location) {
        super(groupId, artifactId, version, extension);
        this.repository = requireNonNull(repository);
        this.location = requireNonNull(location);
        String protocol = location.getProtocol();
        boolean secure = false;
        switch (protocol) {
            case "https":
                secure = true;
                // fallthrough
            case "http":
                fileLocation = null;
                break;
            case "file":
                fileLocation = Paths.get(location.toURI()).normalize();
                secure = true;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
        }
        this.secure = secure;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isLocal() {
        return fileLocation != null;
    }

    @Nullable
    public Path getFileLocation() {
        return fileLocation;
    }

    public URL getLocation() {
        return location;
    }

    public MavenRepository getRepository() {
        return repository;
    }

    public void downloadTo(Path dest) throws IOException, MavenException {
        requireNonNull(dest);
        if (fileLocation != null) {
            Files.copy(fileLocation, dest);
        } else {
            try (OutputStream out = Files.newOutputStream(dest, StandardOpenOption.CREATE_NEW)) {
                HttpUtils.<IOException, Void>download(this.location, (in) -> {
                    byte[] buffer = new byte[4096];
                    int readBytes;
                    while ((readBytes = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, readBytes);
                    }
                    return null;
                });
            } catch (FileNotFoundException ignored) {
                throw new MavenException(this + " doesn't exist");
            }
        }
    }

    public void verifyExistence() throws IOException, MavenException {
        if (!checkExistence()) {
            throw new MavenException(this + " doesn't exist");
        }
    }

    private int exists = -1;
    public boolean checkExistence() throws IOException {
        // Cached existence check
        int exists = this.exists;
        if (exists < 0) {
            this.exists = exists = checkExistence0() ? 1 : 0;
        }
        assert exists >= 0;
        return exists != 0;
    }
    private boolean checkExistence0() throws IOException {
        if (fileLocation != null) {
            return Files.exists(fileLocation);
        } else {
            return HttpUtils.checkExistence(this.location);
        }
    }

    @Override
    public String toString() {
        return getSpecifier() + "@" + (fileLocation != null ? fileLocation.toString() : location.toString());
    }
}
