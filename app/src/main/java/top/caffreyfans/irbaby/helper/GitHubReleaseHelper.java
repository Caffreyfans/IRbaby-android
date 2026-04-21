package top.caffreyfans.irbaby.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class GitHubReleaseHelper {

    private static final String LATEST_RELEASE_API = "https://api.github.com/repos/Caffreyfans/IRbaby-android/releases/latest";

    private GitHubReleaseHelper() {
    }

    public static ReleaseInfo fetchLatestRelease() throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(LATEST_RELEASE_API)
                .header("Accept", "application/vnd.github+json")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Failed to fetch release info: " + response.code());
        }

        JSONObject jsonObject = new JSONObject(response.body().string());
        String tagName = jsonObject.optString("tag_name");
        String htmlUrl = jsonObject.optString("html_url");
        String apkUrl = null;
        JSONArray assets = jsonObject.optJSONArray("assets");
        if (assets != null) {
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.optJSONObject(i);
                if (asset == null) {
                    continue;
                }
                String assetName = asset.optString("name");
                String browserDownloadUrl = asset.optString("browser_download_url");
                if (assetName.endsWith(".apk") && browserDownloadUrl.length() > 0) {
                    apkUrl = browserDownloadUrl;
                    break;
                }
            }
        }

        return new ReleaseInfo(tagName, htmlUrl, apkUrl);
    }

    public static boolean hasNewerRelease(String currentVersionName, String latestTagName) {
        return compareVersions(normalizeVersion(currentVersionName), normalizeVersion(latestTagName)) < 0;
    }

    private static String normalizeVersion(String version) {
        if (version == null) {
            return "";
        }
        return version.trim().replaceFirst("^[vV]", "");
    }

    private static int compareVersions(String localVersion, String remoteVersion) {
        String[] localParts = localVersion.split("\\.");
        String[] remoteParts = remoteVersion.split("\\.");
        int maxLength = Math.max(localParts.length, remoteParts.length);
        for (int i = 0; i < maxLength; i++) {
            int localPart = parseVersionPart(localParts, i);
            int remotePart = parseVersionPart(remoteParts, i);
            if (localPart != remotePart) {
                return Integer.compare(localPart, remotePart);
            }
        }
        return 0;
    }

    private static int parseVersionPart(String[] versionParts, int index) {
        if (index >= versionParts.length) {
            return 0;
        }
        String versionPart = versionParts[index].replaceAll("[^0-9]", "");
        if (versionPart.length() == 0) {
            return 0;
        }
        return Integer.parseInt(versionPart);
    }

    public static final class ReleaseInfo {
        private final String tagName;
        private final String htmlUrl;
        private final String apkUrl;

        public ReleaseInfo(String tagName, String htmlUrl, String apkUrl) {
            this.tagName = tagName;
            this.htmlUrl = htmlUrl;
            this.apkUrl = apkUrl;
        }

        public String getTagName() {
            return tagName;
        }

        public String getHtmlUrl() {
            return htmlUrl;
        }

        public String getApkUrl() {
            return apkUrl;
        }

        public String getPreferredDownloadUrl() {
            if (apkUrl != null && apkUrl.length() > 0) {
                return apkUrl;
            }
            return htmlUrl;
        }

        public boolean hasApkAsset() {
            return apkUrl != null && apkUrl.length() > 0;
        }
    }
}