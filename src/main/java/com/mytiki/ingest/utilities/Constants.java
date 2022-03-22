package com.mytiki.ingest.utilities;

public interface Constants {
    String MODULE_DOT_PATH = "com.mytiki.ingest";
    String MODULE_SLASH_PATH = "com/mytiki/ingest";

    String SLICE_FEATURES = "features";
    String SLICE_LATEST = "latest";

    String PACKAGE_FEATURES_LATEST_DOT_PATH = MODULE_DOT_PATH + "." + SLICE_FEATURES + "." + SLICE_LATEST;
    String PACKAGE_FEATURES_LATEST_SLASH_PATH = MODULE_SLASH_PATH + "/" + SLICE_FEATURES + "/" + SLICE_LATEST;
}
