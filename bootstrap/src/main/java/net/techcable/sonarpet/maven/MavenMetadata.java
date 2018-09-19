package net.techcable.sonarpet.maven;

import lombok.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Immutable maven metadata
 */
public class MavenMetadata {
    @Getter
    @Nullable
    private final SnapshotInfo snapshotInfo;
    private MavenMetadata(@Nullable SnapshotInfo snapshotInfo) {
        this.snapshotInfo = snapshotInfo;
    }

    /**
     * Try and fetch maven metadata from the specified http(s) URL,
     * returning null if it doesn't exist.
     *
     * @param url the url to fetch the metadata from
     * @return the maven metadata, if found
     */
    @Nullable
    public static MavenMetadata parse(URL url) throws IOException, MavenException {
        try {
            return HttpUtils.download(url, (input) -> {
                try (Reader reader = new BufferedReader(new InputStreamReader(
                        input, StandardCharsets.UTF_8))) {
                    return parse(reader);
                }
            });
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }

    public static MavenMetadata parse(Reader reader) throws IOException, MavenException {
        try {
            // NOTE: Please ignore the horror of the XML
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(reader));
            Element versioning = (Element) document.getElementsByTagName("versioning").item(0);
            NodeList snapshotInfoList = versioning.getElementsByTagName("snapshot");
            Element snapshot = snapshotInfoList.getLength() > 0 ? (Element) snapshotInfoList.item(0) : null;
            final SnapshotInfo snapshotInfo;
            if (snapshot != null) {
                String timestamp = snapshot.getElementsByTagName("timestamp").item(0).getTextContent();
                String buildNumberStr = snapshot.getElementsByTagName("buildNumber").item(0).getTextContent();
                final int buildNumber;
                try {
                    buildNumber = Integer.parseInt(buildNumberStr);
                } catch (NumberFormatException e) {
                    throw new MavenException("Invalid maven metadata: Invalid build number " + buildNumberStr);
                }
                snapshotInfo = new SnapshotInfo(timestamp, buildNumber);
            } else {
                snapshotInfo = null;
            }
            return new MavenMetadata(snapshotInfo);
        } catch (ParserConfigurationException | SAXException | ClassCastException e) {
            throw new MavenException("Invalid maven-metadata", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("MavenMetadata(");
        if (snapshotInfo != null) {
            result.append("snapshotInfo=");
            result.append(snapshotInfo);
        }
        result.append(')');
        return result.toString();
    }

    @Data
    public static class SnapshotInfo implements Comparable<SnapshotInfo> {
        private final String timestamp;
        private final int buildNumber;
        public SnapshotInfo(String timestamp, int buildNumber) {
            this.timestamp = Objects.requireNonNull(timestamp);
            this.buildNumber = buildNumber;
        }

        @Override
        public int compareTo(@NotNull SnapshotInfo other) {
            if (this.buildNumber != other.buildNumber) {
                return Integer.compare(this.buildNumber, other.buildNumber);
            }
            return this.timestamp.compareTo(other.timestamp);
        }
    }
}
