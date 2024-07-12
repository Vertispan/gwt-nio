load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_library")

j2cl_library(
    name = 'gwt-nio',
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@com_google_j2cl//:jsinterop-annotations-j2cl",
        "@com_google_elemental2//:elemental2-core-j2cl",
    ],
)